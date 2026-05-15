package com.agenda.agendacultural.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);
    /**
     * Registra evento de cadastro de usuário
     */
    public void logCadastroUsuario(String nome, String email) {
        String mensagem = String.format("[CADASTRO_USUARIO] Usuário: %s (%s) - Cadastro realizado com sucesso",
                nome, email);
        logger.info(mensagem);
        escreverNoArquivo(mensagem);
    }

    /**
     * Registra evento de alteração de dados do usuário
     */
    public void logAlteracaoUsuario(String nome, String email, String campoAlterado) {
        String mensagem = String.format("[ALTERACAO_USUARIO] Usuário: %s (%s) - Campo alterado: %s",
                nome, email, campoAlterado);
        logger.info(mensagem);
        escreverNoArquivo(mensagem);
    }

    /**
     * Registra evento de exclusão de usuário
     */
    public void logExclusaoUsuario(String nome, String email) {
        String mensagem = String.format("[EXCLUSAO_USUARIO] Usuário: %s (%s) - Conta excluída",
                nome, email);
        logger.warn(mensagem);
        escreverNoArquivo(mensagem);
    }

    /**
     * Registra erro de autenticação
     */
    public void logErroAutenticacao(String email, String motivo) {
        String mensagem = String.format("[ERRO_AUTENTICACAO] Tentativa de login: %s - Motivo: %s",
                email, motivo);
        logger.warn(mensagem);
        escreverNoArquivo(mensagem);
    }

    /**
     * Registra bloqueio por múltiplas tentativas
     */
    public void logBloqueioUsuario(String email, int tentativas) {
        String mensagem = String.format("[BLOQUEIO_USUARIO] Usuário: %s - Bloqueado após %d tentativas falhas",
                email, tentativas);
        logger.warn(mensagem);
        escreverNoArquivo(mensagem);
    }

    /**
     * Registra eventos gerais da aplicação
     */
    public void logEventoAplicacao(String evento, String descricao, String usuario) {
        String mensagem = String.format("[EVENTO_APLICACAO] Evento: %s - Usuário: %s - Descrição: %s",
                evento, usuario, descricao);
        logger.info(mensagem);
        escreverNoArquivo(mensagem);
    }

    /**
     * Escreve no arquivo de log (já é feito pelo SLF4J, mas mantemos para consistência)
     */
    private void escreverNoArquivo(String mensagem) {
        // O SLF4J já escreve no arquivo configurado no application.properties
        // Este método é apenas para manter o padrão
        System.out.println(mensagem);
    }
}