# Documento Explicativo Detalhado - Projeto Agenda Cultural (Segurança Removida)

Este documento detalha as alterações realizadas no projeto Spring Boot "Agenda Cultural", focando nos requisitos solicitados e nas decisões de implementação, incluindo a remoção da funcionalidade de autenticação e autorização.

## 1. Visão Geral da Aplicação e Endpoints da API

A aplicação backend foi desenvolvida para gerenciar uma agenda cultural, permitindo o cadastro e consulta de eventos, categorias, usuários, comentários e favoritos através de uma API REST. **Toda a funcionalidade de autenticação e autorização foi removida, tornando todos os endpoints públicos.**

Os endpoints principais seguem o padrão REST e estão agrupados sob o prefixo `/api`:

*   **Usuários (`/api/users`):**
    *   `POST /api/users/register`: Registra um novo usuário.
    *   *Outros endpoints de CRUD para usuários (GET, PUT, DELETE) podem ser adicionados.*
*   **Categorias (`/api/categories`):**
    *   `GET /api/categories`: Retorna todas as categorias.
    *   `GET /api/categories/{id}`: Retorna uma categoria específica pelo ID.
    *   `POST /api/categories`: Cria uma nova categoria.
    *   `PUT /api/categories/{id}`: Atualiza uma categoria existente.
    *   `DELETE /api/categories/{id}`: Deleta uma categoria.
*   **Eventos (`/api/events`):**
    *   `GET /api/events`: Retorna todos os eventos.
    *   `GET /api/events/{id}`: Retorna um evento específico pelo ID.
    *   `GET /api/events/search?name={name}`: Busca eventos pelo nome (contendo o parâmetro `name`).
    *   `POST /api/events`: Cria um novo evento.
    *   `PUT /api/events/{id}`: Atualiza um evento existente.
    *   `DELETE /api/events/{id}`: Deleta um evento.
*   **Comentários (`/api/comments`):**
    *   `GET /api/comments/event/{eventId}`: Retorna comentários de um evento específico.
    *   `POST /api/comments`: Cria um novo comentário.
    *   `DELETE /api/comments/{id}`: Deleta um comentário.
*   **Favoritos (`/api/favorites`):**
    *   *Endpoints para adicionar/remover/listar favoritos podem ser implementados aqui.*

## 2. Base de Dados SQL

A aplicação utiliza uma base de dados relacional (SQL) para persistência de dados, gerenciada através do Spring Data JPA.

*   **Configuração:** Atualmente configurada para usar um banco de dados H2 em memória (`jdbc:h2:mem:agendadb`) para facilitar o desenvolvimento e testes. As configurações (URL, driver, usuário, senha, dialeto) estão no arquivo `src/main/resources/application.properties` e podem ser facilmente alteradas para conectar a outros bancos como MySQL, PostgreSQL, etc.
*   **Tabelas de Domínio:** O modelo de dados inclui as seguintes entidades principais (mapeadas para tabelas):
    *   `User`: Representa os usuários da aplicação (a senha é armazenada em texto plano após a remoção da segurança).
    *   `Category`: Representa as categorias dos eventos (ex: Música, Teatro).
    *   `Event`: Representa os eventos culturais, com título, descrição, data/hora, local e relacionamento com `Category`.
    *   `Comment`: Representa comentários feitos por usuários (`User`) em eventos (`Event`).
    *   `Favorite`: Representa eventos (`Event`) marcados como favoritos por usuários (`User`).
*   **Geração do Schema:** A propriedade `spring.jpa.hibernate.ddl-auto=update` no `application.properties` permite que o Hibernate atualize o schema do banco de dados automaticamente com base nas entidades JPA durante a inicialização (ideal para desenvolvimento).

## 3. Tratamento de Exceção

O tratamento de exceções foi implementado para fornecer respostas de erro padronizadas e informativas aos clientes da API.

*   **Exceções Customizadas:** Foi utilizada a exceção `ResourceNotFoundException` (no pacote `exception`) para indicar que um recurso específico (como um evento ou categoria) não foi encontrado.
*   **Manipulador Global (`GlobalExceptionHandler`):** Uma classe anotada com `@ControllerAdvice` (localizada em `exception`) intercepta exceções lançadas pelos controladores ou serviços.
    *   Possui métodos anotados com `@ExceptionHandler` para tratar tipos específicos de exceções (ex: `ResourceNotFoundException`, `MethodArgumentNotValidException` para erros de validação, e exceções genéricas).
    *   Retorna um objeto `ApiError` (definido em `exception`) contendo um status HTTP apropriado, uma mensagem de erro clara e, opcionalmente, detalhes adicionais.
*   **Validação:** Erros de validação (campos de DTOs inválidos) são automaticamente tratados pelo Spring e podem ser capturados pelo `GlobalExceptionHandler` para retornar uma resposta 400 Bad Request com detalhes dos campos inválidos.

## 4. Regras de Negócio na Camada de Serviço

A lógica de negócio da aplicação está encapsulada na camada de serviço (`service` package), garantindo a separação de responsabilidades e evitando que os controladores acessem diretamente os repositórios.

*   **Separação:** Os controladores (`controller` package) recebem as requisições HTTP, validam e extraem dados, e delegam o processamento para os serviços correspondentes.
*   **Exemplos de Regras:**
    *   **Criação de Usuário (`UserService`):** Verifica se já existe um usuário com o mesmo email antes de permitir o cadastro.
    *   **Operações de CRUD (`CategoryService`, `EventService`, etc.):** Verificam a existência de um recurso pelo ID antes de tentar atualizá-lo ou deletá-lo, lançando `ResourceNotFoundException` caso não exista.
    *   **Criação de Comentário (`CommentServiceImpl`):** Verifica se o usuário e o evento associados ao comentário existem antes de salvá-lo.
    *   **Conversão DTO/Entidade:** Os serviços são responsáveis por converter DTOs recebidos da camada de controle para entidades JPA antes de interagir com os repositórios, e converter entidades JPA para DTOs antes de retorná-los para a camada de controle. Isso foi implementado manualmente após a remoção do ModelMapper.

## 5. Separação do Código em Camadas

O projeto segue uma arquitetura em camadas bem definida para promover organização, manutenibilidade e testabilidade:

*   **Controller (`controller`):** Responsável por expor os endpoints da API REST, receber requisições HTTP, validar dados de entrada (usando DTOs), chamar os serviços apropriados e retornar respostas HTTP (geralmente com DTOs).
*   **Service (`service`):** Contém a lógica de negócio principal da aplicação. Orquestra as operações, aplica regras de negócio, realiza conversões entre DTOs e Entidades, e interage com a camada de repositório. As interfaces (ex: `EventService`) e suas implementações (ex: `EventServiceImpl`) promovem baixo acoplamento.
*   **Repository (`repository`):** Define interfaces (estendendo `JpaRepository` do Spring Data JPA) para interagir com o banco de dados. O Spring Data JPA fornece implementações automáticas para operações básicas de CRUD e permite a definição de métodos de consulta customizados.
*   **Model (`model`):** Contém as classes de entidade JPA que representam os dados da aplicação e são mapeadas para as tabelas do banco de dados.
*   **DTO (`dto`):** Contém os Data Transfer Objects usados para transferir dados entre as camadas, especialmente entre Controller e Service, e na comunicação da API. Ajuda a desacoplar a representação da API do modelo de domínio interno.
*   **Exception (`exception`):** Contém as classes para tratamento de exceções (`ResourceNotFoundException`, `GlobalExceptionHandler`, `ApiError`).
*   **Config (`com.agenda.agendacultural.config`):** Contém classes de configuração adicionais, como `OpenApiConfig` para o Swagger.

## 6. Logs

O logging foi configurado para registrar informações importantes sobre a execução da aplicação, auxiliando no monitoramento e depuração.

*   **Framework:** Utiliza SLF4J como fachada de logging, com Logback como implementação padrão fornecida pelo Spring Boot.
*   **Configuração:** Os níveis de log e formatos podem ser configurados no `application.properties`.
    *   `logging.level.root=INFO`: Define o nível de log padrão.
    *   Níveis mais específicos podem ser definidos para pacotes ou classes (ex: `logging.level.org.springframework.web=DEBUG`, `logging.level.org.hibernate.SQL=DEBUG`).
*   **Uso:** Instâncias de `Logger` (obtidas via `LoggerFactory.getLogger(...)`) são usadas nas classes de serviço (como `EventServiceImpl`) para registrar eventos como início e fim de operações, buscas, criações, atualizações, exclusões e erros.

## 7. DTOs e Validação

Data Transfer Objects (DTOs) são amplamente utilizados para a comunicação da API e entre camadas.

*   **Propósito:** DTOs (no pacote `dto`) definem a estrutura dos dados esperados nas requisições e retornados nas respostas da API. Eles ajudam a:
    *   Desacoplar a API do modelo interno de dados (entidades JPA).
    *   Expor apenas os dados necessários, ocultando detalhes internos.
    *   Facilitar a validação dos dados de entrada.
*   **Exemplos:** `CategoryDTO`, `EventDTO`, `UserCreateDto`, `UserResponseDTO`, `CommentDTO`.
*   **Validação:** A validação dos campos dos DTOs é feita usando as anotações do Bean Validation (`jakarta.validation.constraints.*` como `@NotBlank`, `@Size`, `@Email`, etc.) diretamente nos DTOs. A anotação `@Valid` nos parâmetros dos métodos dos controladores ativa essa validação. Erros de validação resultam em uma resposta 400 Bad Request, tratada pelo `GlobalExceptionHandler`.

## 8. Testes Unitários

Foram implementados testes unitários para verificar o comportamento das unidades de código isoladamente, principalmente na camada de serviço.

*   **Frameworks:** JUnit 5 e Mockito são utilizados para escrever e executar os testes.
*   **Dependências:** `spring-boot-starter-test` (inclui JUnit, Mockito, AssertJ) está configurada no `pom.xml`. A dependência `spring-security-test` foi removida.
*   **Foco:** Os testes criados (`src/test/java/.../service`) focam na camada de serviço (`CategoryServiceTest`, `UserServiceTest`, `EventServiceTest`).
*   **Mocks:** Mockito (`@Mock`, `@InjectMocks`) é usado para simular o comportamento das dependências (como os repositórios) e isolar a unidade sob teste. Mocks relacionados à segurança (como `PasswordEncoder`) foram removidos.
*   **Cobertura:** Os testes cobrem cenários de sucesso (operações CRUD bem-sucedidas) e cenários de falha (ex: recurso não encontrado, dados inválidos, email duplicado).
*   **Assertivas:** As assertivas do JUnit (`assertEquals`, `assertNotNull`, `assertThrows`, etc.) e do Mockito (`verify`, `when`, etc.) são usadas para verificar os resultados e interações.

## 9. Documentação (Swagger / OpenAPI)

A API foi documentada utilizando a especificação OpenAPI (Swagger) para facilitar a compreensão e o uso por parte dos desenvolvedores.

*   **Dependência:** `springdoc-openapi-starter-webmvc-ui` foi adicionada ao `pom.xml`.
*   **Configuração (`OpenApiConfig`):** Uma classe de configuração define informações gerais da API (título, descrição, versão). A configuração de esquemas de segurança foi removida.
*   **Anotações:** Os controladores e seus métodos foram anotados com anotações do SpringDoc OpenAPI (`@Tag`, `@Operation`, `@Parameter`, `@ApiResponse`, etc.) para descrever os endpoints, parâmetros, corpos de requisição/resposta e possíveis códigos de status.
*   **Acesso:** A documentação interativa da API está disponível na URL `/swagger-ui.html` (configurável via `application.properties`). A especificação OpenAPI em formato JSON está disponível em `/api-docs`.

## Melhorias e Próximos Passos

*   **Testes de Integração:** Adicionar testes de integração para validar o fluxo completo da aplicação, incluindo a interação entre camadas e com o banco de dados.
*   **Tratamento de Erros:** Refinar o tratamento de erros e as mensagens retornadas.
*   **Paginação e Ordenação:** Implementar paginação e ordenação nas listagens de recursos (eventos, categorias, etc.).
*   **Banco de Dados de Produção:** Configurar a conexão com um banco de dados persistente (MySQL, PostgreSQL) para produção.
*   **Externalização de Configurações:** Mover configurações sensíveis (como senhas de banco de dados) para variáveis de ambiente ou um sistema de gerenciamento de configurações.

