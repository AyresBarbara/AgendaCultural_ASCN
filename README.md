# Descrição do Projeto

[cite_start]A Agenda Cultural é uma aplicação backend que oferece uma plataforma completa para gerenciamento de eventos culturais[cite: 8, 9]. [cite_start]O sistema permite o cadastro e consulta de eventos, categorias, comentários e favoritos, tudo através de uma API REST de fácil integração[cite: 10].

[cite_start]Desenvolvida com Java e Spring Boot, a aplicação utiliza o padrão de arquitetura MVC e segue as melhores práticas de desenvolvimento, com separação clara entre as camadas de controle, serviço e persistência[cite: 11].

## Histórias de Usuário

| ID | História |
| :--- | :--- |
| **US01** | Eu, como usuário interessado, quero me cadastrar na plataforma informando nome, e-mail e senha, para que eu possa ter uma conta e acessar o sistema. |
| **US02** | Eu, como usuário cadastrado, quero fazer login informando meu e-mail e senha, para que eu possa acessar as funcionalidades da aplicação. |
| **US03** | Eu, como usuário logado, quero visualizar a lista de eventos culturais disponíveis, para que eu possa conhecer as opções de lazer e entretenimento. |
| **US04** | Eu, como usuário logado, quero criar um novo evento cultural informando título, descrição, local, data/hora e categoria, para que eu possa divulgar minha iniciativa. |
| **US05** | Eu, como usuário logado, quero adicionar comentários em um evento específico, para que eu possa expressar minha opinião ou interagir com outros participantes. |

[cite_start][cite: 12, 13]

## Funcionalidades

* [cite_start]**Gerenciamento de Categorias**: Cadastro, consulta, atualização e remoção de categorias de eventos (ex: Música, Teatro)[cite: 15, 16, 17];
* [cite_start]**Gerenciamento de Eventos**: Cadastro, consulta, atualização e remoção de eventos culturais com informações detalhadas[cite: 18];
* [cite_start]**Sistema de Comentários**: Possibilidade de usuários comentarem sobre eventos específicos[cite: 19];
* [cite_start]**Favoritos**: Funcionalidade para marcar eventos como favoritos[cite: 20];
* [cite_start]**API REST Completa**: Endpoints organizados e padronizados para todas as operações[cite: 21];
* [cite_start]**Tratamento de Exceções**: Respostas de erro padronizadas e informativas[cite: 23];
* [cite_start]**Documentação com Swagger**: Endpoints documentados para fácil integração[cite: 23];
* [cite_start]**Criptografia**: Criptografia de senhas para maior segurança do usuário[cite: 24].

## Endpoints da API

[cite_start]Todos os endpoints seguem o padrão REST e estão agrupados sob o prefixo `/api`[cite: 25, 26]:

### Usuários
* [cite_start]`POST /api/users/register`: Registra um novo usuário[cite: 27, 28];
* [cite_start]Outros endpoints de CRUD para usuários (GET, PUT, DELETE)[cite: 29].

### Categorias
* [cite_start]`GET /api/categories`: Retorna todas as categorias[cite: 30, 31];
* [cite_start]`GET /api/categories/{id}`: Retorna uma categoria específica pelo ID[cite: 32];
* [cite_start]`POST /api/categories`: Cria uma nova categoria[cite: 33];
* [cite_start]`PUT /api/categories/{id}`: Atualiza uma categoria existente[cite: 34];
* [cite_start]`DELETE /api/categories/{id}`: Remove uma categoria[cite: 35].

### Eventos
* [cite_start]`GET /api/events`: Retorna todos os eventos[cite: 36, 37];
* [cite_start]`GET /api/events/{id}`: Retorna um evento específico pelo ID[cite: 38];
* [cite_start]`GET /api/events/search?name={name}`: Busca eventos pelo nome[cite: 39];
* [cite_start]`POST /api/events`: Cria um novo evento[cite: 41];
* [cite_start]`PUT /api/events/{id}`: Atualiza um evento existente[cite: 42];
* [cite_start]`DELETE /api/events/{id}`: Remove um evento[cite: 43].

### Comentários
* [cite_start]`GET /api/comments/event/{eventId}`: Retorna comentários de um evento específico[cite: 44, 46];
* [cite_start]`POST /api/comments`: Cria um novo comentário[cite: 47];
* [cite_start]`DELETE /api/comments/{id}`: Remove um comentário[cite: 48].

### Favoritos
* [cite_start]Endpoints para adicionar/remover/listar favoritos podem ser implementados[cite: 49, 50].

## Tecnologias Utilizadas

* [cite_start]**Java 17+**: Linguagem de programação principal[cite: 51, 52];
* [cite_start]**Spring Boot**: Framework para desenvolvimento de aplicações Java[cite: 53];
* [cite_start]**Spring Data JPA**: Facilita a integração com banco de dados[cite: 53];
* [cite_start]**Hibernate**: Framework ORM para mapeamento objeto-relacional[cite: 54];
* [cite_start]**MySQL**: Banco de dados relacional (configurável para outros bancos)[cite: 54];
* [cite_start]**H2 Database**: Banco de dados em memória para desenvolvimento e testes[cite: 55];
* [cite_start]**Lombok**: Biblioteca para redução de código boilerplate[cite: 55];
* [cite_start]**Swagger/OpenAPI**: Documentação da API[cite: 56];
* [cite_start]**Maven**: Gerenciamento de dependências e build[cite: 57].

## Modelo de Dados

[cite_start]O modelo de dados inclui as seguintes entidades principais[cite: 58, 59]:

* [cite_start]**User**: Representa os usuários da aplicação[cite: 60];
* [cite_start]**Category**: Representa as categorias dos eventos (ex: Música, Teatro)[cite: 61];
* [cite_start]**Event**: Representa os eventos culturais, com título, descrição, data/hora, local[cite: 61];
* [cite_start]**Comment**: Representa comentários feitos por usuários em eventos[cite: 63];
* [cite_start]**Favorite**: Representa eventos marcados como favoritos por usuários[cite: 64].

## Configuração

### Banco de Dados
[cite_start]Certifique-se de que o banco `database_cultural_agenda` esteja criado antes de rodar o projeto[cite: 65, 66, 67]:

```sql
CREATE DATABASE database_cultural_agenda;
