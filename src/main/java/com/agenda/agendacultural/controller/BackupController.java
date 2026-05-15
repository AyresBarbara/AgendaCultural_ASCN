package com.agenda.agendacultural.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class BackupController {

    private static final Logger logger = LoggerFactory.getLogger(BackupController.class);
    private static final String BACKUP_DIR = "./backups/";

    @Value("${mysqldump.path:mysqldump}")
    private String mysqldumpPath;

    @Value("${spring.datasource.username:root}")
    private String dbUser;

    @Value("${spring.datasource.password:}")
    private String dbPassword;

    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/agenda_cultural}")
    private String dbUrl;

    /**
     * Endpoint para realizar backup do banco de dados
     */
    @PostMapping("/backup")
public ResponseEntity<?> realizarBackup() {
    try {
        logger.info("Iniciando backup do banco de dados...");
        
        File pastaBackup = new File(BACKUP_DIR);
        if (!pastaBackup.exists()) {
            pastaBackup.mkdirs();
        }

        String dbName = extractDatabaseName(dbUrl);
        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nomeArquivo = "backup_" + dbName + "_" + dataHora + ".sql";
        String caminhoCompleto = BACKUP_DIR + nomeArquivo;

        // Forçar encoding UTF-8 no comando mysqldump
        List<String> comando = new ArrayList<>();
        comando.add(mysqldumpPath);
        comando.add("--no-defaults");
        comando.add("--default-character-set=utf8mb4");  // <-- ADICIONADO
        comando.add("-u" + dbUser);
        comando.add("-p" + dbPassword);
        comando.add(dbName);

        logger.info("Comando: {}", String.join(" ", comando));

        ProcessBuilder processBuilder = new ProcessBuilder(comando);
        processBuilder.redirectOutput(new File(caminhoCompleto));
        processBuilder.redirectErrorStream(true);
        
        Process processo = processBuilder.start();
        int codigoSaida = processo.waitFor();

        if (codigoSaida == 0) {
            return ResponseEntity.ok(Map.of(
                "mensagem", "Backup realizado com sucesso!",
                "arquivo", nomeArquivo,
                "caminho", caminhoCompleto,
                "data", LocalDateTime.now().toString()
            ));
        } else {
            throw new Exception("Erro no backup. Código: " + codigoSaida);
        }

    } catch (Exception e) {
        logger.error("Erro no backup: {}", e.getMessage());
        return ResponseEntity.status(500).body(Map.of("erro", e.getMessage()));
    }
}
    /**
     * Endpoint para listar todos os backups disponíveis
     */
    @GetMapping("/backups")
    public ResponseEntity<?> listarBackups() {
        try {
            File pastaBackup = new File(BACKUP_DIR);
            if (!pastaBackup.exists()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            File[] arquivos = pastaBackup.listFiles((dir, name) -> name.endsWith(".sql"));
            List<Map<String, Object>> backups = new ArrayList<>();

            if (arquivos != null) {
                for (File arquivo : arquivos) {
                    Map<String, Object> info = new HashMap<>();
                    info.put("nome", arquivo.getName());
                    info.put("tamanho", arquivo.length() + " bytes");
                    info.put("data", new Date(arquivo.lastModified()).toString());
                    backups.add(info);
                }
            }

            // Ordenar do mais novo para o mais antigo
            backups.sort((a, b) -> b.get("nome").toString().compareTo(a.get("nome").toString()));

            return ResponseEntity.ok(backups);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * Extrai o nome do banco de dados da URL JDBC
     */
    private String extractDatabaseName(String url) {
        int lastSlash = url.lastIndexOf('/');
        if (lastSlash > 0) {
            String db = url.substring(lastSlash + 1);
            int questionMark = db.indexOf('?');
            if (questionMark > 0) {
                return db.substring(0, questionMark);
            }
            return db;
        }
        return "agenda_cultural";
    }

    /**
     * Endpoint para restaurar backup a partir de um arquivo existente
     */
    @PostMapping("/restore")
public ResponseEntity<?> restaurarBackup(@RequestBody Map<String, String> request) {
    try {
        String nomeArquivo = request.get("arquivo");
        
        if (nomeArquivo == null || nomeArquivo.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Nome do arquivo não fornecido"));
        }

        logger.info("===== INICIANDO RESTORE =====");
        logger.info("Arquivo solicitado: {}", nomeArquivo);
        
        String caminhoCompleto = BACKUP_DIR + nomeArquivo;
        File arquivoBackup = new File(caminhoCompleto);
        
        if (!arquivoBackup.exists()) {
            return ResponseEntity.status(404).body(Map.of("erro", "Arquivo de backup não encontrado: " + nomeArquivo));
        }

        // CRIAR UM ARQUIVO LIMPO (SEM BOM E SEM AVISOS)
        File arquivoLimpo = new File(BACKUP_DIR + "clean_" + nomeArquivo);
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoBackup), "UTF-8"));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivoLimpo), "UTF-8"))) {
            
            // Pular o BOM (Byte Order Mark) se existir
            reader.mark(1);
            int primeiroChar = reader.read();
            if (primeiroChar != 0xFEFF) {
                reader.reset(); // Não é BOM, volta para o início
                writer.write(primeiroChar);
            }
            
            String linha;
            while ((linha = reader.readLine()) != null) {
                // Pular linhas de aviso do mysqldump
                if (linha.startsWith("mysqldump: [Warning]") || 
                    linha.contains("Using a password on the command line")) {
                    logger.info("Pulando linha de aviso: {}", linha);
                    continue;
                }
                writer.write(linha);
                writer.newLine();
            }
        }
        
        logger.info("Arquivo limpo criado: {}", arquivoLimpo.getName());

        String dbName = extractDatabaseName(dbUrl);
        String mysqlPath = mysqldumpPath.replace("mysqldump.exe", "mysql.exe");

        List<String> comando = new ArrayList<>();
        comando.add(mysqlPath);
        comando.add("--default-character-set=utf8mb4");
        comando.add("-u" + dbUser);
        comando.add("-p" + dbPassword);
        comando.add(dbName);

        logger.info("Comando: {}", String.join(" ", comando));

        ProcessBuilder processBuilder = new ProcessBuilder(comando);
        processBuilder.redirectInput(arquivoLimpo);
        processBuilder.redirectErrorStream(true);
        
        Process processo = processBuilder.start();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(processo.getInputStream()));
        String linha;
        StringBuilder output = new StringBuilder();
        while ((linha = reader.readLine()) != null) {
            logger.info("MYSQL: {}", linha);
            output.append(linha).append("\n");
        }
        
        int codigoSaida = processo.waitFor();
        
        // Apagar arquivo limpo
        arquivoLimpo.delete();

        if (codigoSaida == 0) {
            logger.info("✅ RESTORE CONCLUÍDO!");
            return ResponseEntity.ok(Map.of(
                "mensagem", "Restore realizado com sucesso!",
                "arquivo", nomeArquivo
            ));
        } else {
            return ResponseEntity.status(500).body(Map.of(
                "erro", "Falha no restore. Código: " + codigoSaida,
                "detalhes", output.toString()
            ));
        }

    } catch (Exception e) {
        logger.error("❌ ERRO: ", e);
        return ResponseEntity.status(500).body(Map.of("erro", e.getMessage()));
    }
}

    //backup agendado
    @Scheduled(cron = "0 0 2 * * ?") // Executa todo dia às 2h
public void backupAgendado() {
    try {
        logger.info("Executando backup agendado...");
        
        File pastaBackup = new File(BACKUP_DIR);
        if (!pastaBackup.exists()) pastaBackup.mkdirs();

        String dbName = extractDatabaseName(dbUrl);
        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nomeArquivo = "backup_agendado_" + dbName + "_" + dataHora + ".sql";
        String caminhoCompleto = BACKUP_DIR + nomeArquivo;

        List<String> comando = new ArrayList<>();
        comando.add(mysqldumpPath);
        comando.add("--no-defaults");
        comando.add("-u" + dbUser);
        comando.add("-p" + dbPassword);
        comando.add(dbName);

        ProcessBuilder processBuilder = new ProcessBuilder(comando);
        processBuilder.redirectOutput(new File(caminhoCompleto));
        processBuilder.redirectErrorStream(true);
        
        Process processo = processBuilder.start();
        int codigoSaida = processo.waitFor();

        if (codigoSaida == 0) {
            logger.info("Backup agendado realizado: {}", nomeArquivo);
        } else {
            logger.error("Erro no backup agendado");
        }

    } catch (Exception e) {
        logger.error("Erro no backup agendado: {}", e.getMessage());
    }
}
@PostMapping(value = "/restore/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> restaurarBackupUpload(@RequestParam("file") MultipartFile file) {
    try {
        logger.info("Iniciando restore via upload: {}", file.getOriginalFilename());
        
        String userDir = System.getProperty("user.dir");
        String backupPath = userDir + File.separator + "backups";
        
        File pastaBackup = new File(backupPath);
        if (!pastaBackup.exists()) {
            pastaBackup.mkdirs();
        }
        
        // Salvar arquivo original temporário
        File tempFileOriginal = new File(pastaBackup, "temp_original_" + file.getOriginalFilename());
        file.transferTo(tempFileOriginal);
        
        // Criar arquivo limpo (remover BOM)
        File tempFileLimpo = new File(pastaBackup, "temp_limpo_" + file.getOriginalFilename());
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(tempFileOriginal), "UTF-8"));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFileLimpo), "UTF-8"))) {
            
            reader.mark(1);
            int primeiroChar = reader.read();
            if (primeiroChar != 0xFEFF) {
                reader.reset();
                writer.write(primeiroChar);
            }
            
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.startsWith("mysqldump: [Warning]") || 
                    linha.contains("Using a password on the command line")) {
                    continue;
                }
                writer.write(linha);
                writer.newLine();
            }
        }
        
        String dbName = extractDatabaseName(dbUrl);
        String mysqlPath = mysqldumpPath.replace("mysqldump.exe", "mysql.exe");
        
        List<String> comando = new ArrayList<>();
        comando.add(mysqlPath);
        comando.add("--default-character-set=utf8mb4");
        comando.add("-u" + dbUser);
        comando.add("-p" + dbPassword);
        comando.add(dbName);
        
        ProcessBuilder processBuilder = new ProcessBuilder(comando);
        processBuilder.redirectInput(tempFileLimpo);
        processBuilder.redirectErrorStream(true);
        
        Process processo = processBuilder.start();
        int codigoSaida = processo.waitFor();
        
        tempFileOriginal.delete();
        tempFileLimpo.delete();
        
        if (codigoSaida == 0) {
            return ResponseEntity.ok(Map.of(
                "mensagem", "Restore realizado com sucesso!",
                "arquivo", file.getOriginalFilename()
            ));
        } else {
            throw new Exception("Erro ao restaurar backup. Código: " + codigoSaida);
        }
        
    } catch (Exception e) {
        logger.error("Erro no upload: ", e);
        return ResponseEntity.status(500).body(Map.of("erro", e.getMessage()));
    }
}
}