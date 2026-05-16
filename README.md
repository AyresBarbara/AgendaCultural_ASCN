# Membros

* **Bárbara Fernanda Ayres da Silva 01146760**
* **Gustavo Andrew Gomes da Silva - 01711566**
* **Isllâne Maria da Silva - 01534705**
* **João Pedro Carneiro da Silva - 01711431**
* **Silas Rafael Vieira de Albuquerque 01710966**
* **Wilson Francisco do Nascimento - 01710813**


# Descrição do Projeto

A Agenda Cultural é uma aplicação backend que oferece uma plataforma completa para gerenciamento de eventos culturais. O sistema permite o cadastro e consulta de eventos, categorias, comentários e favoritos, tudo através de uma API REST de fácil integração.

Desenvolvida com Java e Spring Boot, a aplicação utiliza o padrão de arquitetura MVC e segue as melhores práticas de desenvolvimento, com separação clara entre as camadas de controle, serviço e persistência.

# Histórias de Usuário

| ID | História |
| :--- | :--- |
| **US01** | Eu, como usuário interessado, quero me cadastrar na plataforma informando nome, e-mail e senha, para que eu possa ter uma conta e acessar o sistema. |
| **US02** | Eu, como usuário cadastrado, quero fazer login informando meu e-mail e senha, para que eu possa acessar as funcionalidades da aplicação. |
| **US03** | Eu, como usuário logado, quero visualizar a lista de eventos culturais disponíveis, para que eu possa conhecer as opções de lazer e entretenimento. |
| **US04** | Eu, como usuário logado, quero criar um novo evento cultural informando título, descrição, local, data/hora e categoria, para que eu possa divulgar minha iniciativa. |
| **US05** | Eu, como usuário logado, quero adicionar comentários em um evento específico, para que eu possa expressar minha opinião ou interagir com outros participantes. |


# Funcionalidades

* **Gerenciamento de Categorias**: Cadastro, consulta, atualização e remoção de categorias de eventos (ex: Música, Teatro);
* **Gerenciamento de Eventos**: Cadastro, consulta, atualização e remoção de eventos culturais com informações detalhadas;
* **Sistema de Comentários**: Possibilidade de usuários comentarem sobre eventos específicos;
* **Favoritos**: Funcionalidade para marcar eventos como favoritos;
* **API REST Completa**: Endpoints organizados e padronizados para todas as operações;
* **Tratamento de Exceções**: Respostas de erro padronizadas e informativas;
* **Documentação com Swagger**: Endpoints documentados para fácil integração;
* **Criptografia**: Criptografia de senhas para maior segurança do usuário.

# Endpoints da API

Todos os endpoints seguem o padrão REST e estão agrupados sob o prefixo `/api`:

## Usuários
* `POST /api/users/register`: Registra um novo usuário;
* Outros endpoints de CRUD para usuários (GET, PUT, DELETE).

## Categorias
* `GET /api/categories`: Retorna todas as categorias;
* `GET /api/categories/{id}`: Retorna uma categoria específica pelo ID;
* `POST /api/categories`: Cria uma nova categoria;
* `PUT /api/categories/{id}`: Atualiza uma categoria existente;
* `DELETE /api/categories/{id}`: Remove uma categoria.

## Eventos
* `GET /api/events`: Retorna todos os eventos;
* `GET /api/events/{id}`: Retorna um evento específico pelo ID;
* `GET /api/events/search?name={name}`: Busca eventos pelo nome;
* `POST /api/events`: Cria um novo evento;
* `PUT /api/events/{id}`: Atualiza um evento existente;
* `DELETE /api/events/{id}`: Remove um evento.

## Comentários
* `GET /api/comments/event/{eventId}`: Retorna comentários de um evento específico;
* `POST /api/comments`: Cria um novo comentário;
* `DELETE /api/comments/{id}`: Remove um comentário.

## Favoritos
* Endpoints para adicionar/remover/listar favoritos podem ser implementados.

# Tecnologias Utilizadas

* **Java 17+**: Linguagem de programação principal;
* **Spring Boot**: Framework para desenvolvimento de aplicações Java;
* **Spring Data JPA**: Facilita a integração com banco de dados;
* **Hibernate**: Framework ORM para mapeamento objeto-relacional;
* **MySQL**: Banco de dados relacional (configurável para outros bancos);
* **H2 Database**: Banco de dados em memória para desenvolvimento e testes;
* **Lombok**: Biblioteca para redução de código boilerplate;
* **Swagger/OpenAPI**: Documentação da API;
* **Maven**: Gerenciamento de dependências e build.

# Modelo de Dados

O modelo de dados inclui as seguintes entidades principais:

* **User**: Representa os usuários da aplicação;
* **Category**: Representa as categorias dos eventos (ex: Música, Teatro);
* **Event**: Representa os eventos culturais, com título, descrição, data/hora, local;
* **Comment**: Representa comentários feitos por usuários em eventos;
* **Favorite**: Representa eventos marcados como favoritos por usuários.

# Configuração

## Banco de Dados
Certifique-se de que o banco `database_cultural_agenda` esteja criado antes de rodar o projeto:

```sql
CREATE DATABASE database_cultural_agenda;
```

## Configuração para MySQL
```
spring.datasource.url=jdbc:mysql://localhost:3306/database_cultural_agenda
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha`
```

## Configuração JPA/Hibernate
```
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect`
```

## Configuração para H2 (desenvolvimento/testes)
```
#spring.datasource.url=jdbc:h2:mem:agendadb
#spring.datasource.driverClassName=org.h2.Driver
#spring.datasource.username=sa
#spring.datasource.password=
#spring.h2.console.enabled=true`
```

# Como Executar o Projeto

## Pré-requisitos

* JDK 17 ou superior
* MySQL (ou outro banco de dados configurado)
* Maven

## Passos de Execução

### Clone o repositório:
```
git clone https://github.com/eduliradossantos/AgendaCultural.git
cd AgendaCultural
```

### Configure o banco de dados:

Crie o banco de dados database_cultural_agenda
Ajuste as configurações no arquivo application.properties

### Execute o projeto:
```
./mvnw spring-boot:run
```

### Acesse a documentação da API:
```
http://localhost:8443/swagger-ui.html
```

# Estrutura do projeto
```
AgendaCultural
├── src/main/java/com/agenda/cultural
│ ├── controller # Controladores REST
│ ├── model # Entidades JPA
│ ├── repository # Interfaces de repositório
│ ├── service # Camada de serviço com regras de negócio
│ ├── dto # Objetos de transferência de dados
│ ├── exception # Classes de tratamento de exceções
│ └── config # Configurações (Swagger, etc)
├── src/main/resources
│ └── application.properties # Configurações da aplicação
└── src/test # Testes unitários e de integração
```
