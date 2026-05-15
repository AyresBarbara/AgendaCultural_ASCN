CREATE DATABASE agenda_cultural;
USE agenda_cultural;

CREATE TABLE User (
    id_user BINARY(16) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Category (
    id_category BINARY(16) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE Event (
    id_event BINARY(16) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    location VARCHAR(200),
    date_time DATETIME,
    category_id BINARY(16),
    created_by BINARY(16),
    FOREIGN KEY (category_id) REFERENCES Category(id_category) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES User(id_user) ON DELETE CASCADE
);

CREATE TABLE Comment (
    id_comment BINARY(16) PRIMARY KEY,
    text TEXT NOT NULL,
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    user_id BINARY(16),
    event_id BINARY(16),
    FOREIGN KEY (user_id) REFERENCES User(id_user) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES Event(id_event) ON DELETE CASCADE
);

CREATE TABLE Favorite (
    id_favorite BINARY(16) PRIMARY KEY,
    user_id BINARY(16),
    event_id BINARY(16),
    favorited_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(id_user) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES Event(id_event) ON DELETE CASCADE
);

INSERT INTO User (id_user, name, email, password) VALUES 
(UNHEX('a1b2c3d4e5f611ec8ea00242ac120002'), 'Maria Silva', 'maria.silva@email.com', 'senha123'),
(UNHEX('b2c3d4e5f6a711ec8ea00242ac120003'), 'João Santos', 'joao.santos@email.com', 'senha456'),
(UNHEX('c3d4e5f6a7b811ec8ea00242ac120004'), 'Ana Oliveira', 'ana.oliveira@email.com', 'senha789'),
(UNHEX('d4e5f6a7b8c911ec8ea00242ac120005'), 'Pedro Costa', 'pedro.costa@email.com', 'senhaabc'),
(UNHEX('e5f6a7b8c9d011ec8ea00242ac120006'), 'Carla Souza', 'carla.souza@email.com', 'senhadef'),
(UNHEX('f6a7b8c9d0e111ec8ea00242ac120007'), 'Lucas Ferreira', 'lucas.ferreira@email.com', 'senha123'),
(UNHEX('a7b8c9d0e1f211ec8ea00242ac120008'), 'Mariana Lima', 'mariana.lima@email.com', 'senha456'),
(UNHEX('b8c9d0e1f2a311ec8ea00242ac120009'), 'Rafael Alves', 'rafael.alves@email.com', 'senha789'),
(UNHEX('c9d0e1f2a3b411ec8ea00242ac120010'), 'Juliana Martins', 'juliana.martins@email.com', 'senhaabc'),
(UNHEX('d0e1f2a3b4c511ec8ea00242ac120011'), 'Bruno Pereira', 'bruno.pereira@email.com', 'senhadef');

-- Inserir dados na tabela Category
INSERT INTO Category (id_category, name, description) VALUES 
(UNHEX('e1f2a3b4c5d611ec8ea00242ac120012'), 'Música', 'Eventos relacionados a shows, concertos e apresentações musicais'),
(UNHEX('f2a3b4c5d6e711ec8ea00242ac120013'), 'Teatro', 'Peças teatrais, monólogos e performances cênicas'),
(UNHEX('a3b4c5d6e7f811ec8ea00242ac120014'), 'Cinema', 'Exibições de filmes, documentários e curtas-metragens'),
(UNHEX('b4c5d6e7f8a911ec8ea00242ac120015'), 'Dança', 'Espetáculos de dança, balé e performances corporais'),
(UNHEX('c5d6e7f8a9b011ec8ea00242ac120016'), 'Exposição', 'Exposições de arte, fotografia e instalações'),
(UNHEX('d6e7f8a9b0c111ec8ea00242ac120017'), 'Literatura', 'Lançamentos de livros, saraus e feiras literárias'),
(UNHEX('e7f8a9b0c1d211ec8ea00242ac120018'), 'Gastronomia', 'Festivais gastronômicos e feiras de comida'),
(UNHEX('f8a9b0c1d2e311ec8ea00242ac120019'), 'Esporte', 'Competições esportivas e eventos de atividade física'),
(UNHEX('a9b0c1d2e3f411ec8ea00242ac120020'), 'Workshop', 'Oficinas, cursos e atividades educativas'),
(UNHEX('b0c1d2e3f4a511ec8ea00242ac120021'), 'Festival', 'Eventos que combinam múltiplas atrações e categorias');

-- Inserir dados na tabela Event
INSERT INTO Event (id_event, title, description, location, date_time, category_id, created_by) VALUES 
(UNHEX('c1d2e3f4a5b611ec8ea00242ac120022'), 'Festival de Jazz', 'Festival com apresentações dos melhores músicos de jazz da cidade', 'Parque Municipal', '2025-06-15 18:00:00', UNHEX('e1f2a3b4c5d611ec8ea00242ac120012'), UNHEX('a1b2c3d4e5f611ec8ea00242ac120002')),
(UNHEX('d2e3f4a5b6c711ec8ea00242ac120023'), 'Hamlet - O Príncipe da Dinamarca', 'Clássico de Shakespeare em montagem contemporânea', 'Teatro Municipal', '2025-06-20 20:00:00', UNHEX('f2a3b4c5d6e711ec8ea00242ac120013'), UNHEX('b2c3d4e5f6a711ec8ea00242ac120003')),
(UNHEX('e3f4a5b6c7d811ec8ea00242ac120024'), 'Mostra de Cinema Latino', 'Exibição de filmes premiados da América Latina', 'Cineteatro Central', '2025-07-05 19:00:00', UNHEX('a3b4c5d6e7f811ec8ea00242ac120014'), UNHEX('c3d4e5f6a7b811ec8ea00242ac120004')),
(UNHEX('f4a5b6c7d8e911ec8ea00242ac120025'), 'Espetáculo de Dança Contemporânea', 'Apresentação do grupo Corpo em Movimento', 'Centro Cultural', '2025-07-12 19:30:00', UNHEX('b4c5d6e7f8a911ec8ea00242ac120015'), UNHEX('d4e5f6a7b8c911ec8ea00242ac120005')),
(UNHEX('a5b6c7d8e9f011ec8ea00242ac120026'), 'Exposição Fotográfica: Olhares da Cidade', 'Mostra de fotografias urbanas de artistas locais', 'Galeria de Arte Moderna', '2025-07-20 10:00:00', UNHEX('c5d6e7f8a9b011ec8ea00242ac120016'), UNHEX('e5f6a7b8c9d011ec8ea00242ac120006')),
(UNHEX('b6c7d8e9f0a111ec8ea00242ac120027'), 'Feira do Livro', 'Evento literário com lançamentos e sessões de autógrafos', 'Praça da Liberdade', '2025-08-01 09:00:00', UNHEX('d6e7f8a9b0c111ec8ea00242ac120017'), UNHEX('f6a7b8c9d0e111ec8ea00242ac120007')),
(UNHEX('c7d8e9f0a1b211ec8ea00242ac120028'), 'Festival Gastronômico', 'Degustação de pratos típicos regionais', 'Mercado Municipal', '2025-08-10 11:00:00', UNHEX('e7f8a9b0c1d211ec8ea00242ac120018'), UNHEX('a7b8c9d0e1f211ec8ea00242ac120008')),
(UNHEX('d8e9f0a1b2c311ec8ea00242ac120029'), 'Corrida pela Saúde', 'Evento esportivo beneficente com percursos de 5km e 10km', 'Avenida Principal', '2025-08-15 07:00:00', UNHEX('f8a9b0c1d2e311ec8ea00242ac120019'), UNHEX('b8c9d0e1f2a311ec8ea00242ac120009')),
(UNHEX('e9f0a1b2c3d411ec8ea00242ac120030'), 'Workshop de Fotografia', 'Aprenda técnicas avançadas de fotografia com profissionais', 'Espaço Cultural', '2025-08-22 14:00:00', UNHEX('a9b0c1d2e3f411ec8ea00242ac120020'), UNHEX('c9d0e1f2a3b411ec8ea00242ac120010')),
(UNHEX('f0a1b2c3d4e511ec8ea00242ac120031'), 'Festival de Verão', 'Evento com música, gastronomia e atividades culturais', 'Praia Central', '2025-09-01 16:00:00', UNHEX('b0c1d2e3f4a511ec8ea00242ac120021'), UNHEX('d0e1f2a3b4c511ec8ea00242ac120011'));

-- Inserir dados na tabela Comment
INSERT INTO Comment (id_comment, text, date, user_id, event_id) VALUES 
(UNHEX('a1b2c3d4e5f611ec8ea00242ac120032'), 'Estou ansioso para este festival! Quem mais vai?', '2025-05-20 10:15:00', UNHEX('b2c3d4e5f6a711ec8ea00242ac120003'), UNHEX('c1d2e3f4a5b611ec8ea00242ac120022')),
(UNHEX('b2c3d4e5f6a711ec8ea00242ac120033'), 'Adoro Shakespeare, não vejo a hora de assistir!', '2025-05-21 14:30:00', UNHEX('c3d4e5f6a7b811ec8ea00242ac120004'), UNHEX('d2e3f4a5b6c711ec8ea00242ac120023')),
(UNHEX('c3d4e5f6a7b811ec8ea00242ac120034'), 'Alguém sabe quais filmes serão exibidos?', '2025-05-22 09:45:00', UNHEX('d4e5f6a7b8c911ec8ea00242ac120005'), UNHEX('e3f4a5b6c7d811ec8ea00242ac120024')),
(UNHEX('d4e5f6a7b8c911ec8ea00242ac120035'), 'Já vi este grupo dançar, são incríveis!', '2025-05-23 18:20:00', UNHEX('e5f6a7b8c9d011ec8ea00242ac120006'), UNHEX('f4a5b6c7d8e911ec8ea00242ac120025')),
(UNHEX('e5f6a7b8c9d011ec8ea00242ac120036'), 'As fotografias do ano passado eram maravilhosas, mal posso esperar por esta edição.', '2025-05-24 11:10:00', UNHEX('f6a7b8c9d0e111ec8ea00242ac120007'), UNHEX('a5b6c7d8e9f011ec8ea00242ac120026')),
(UNHEX('f6a7b8c9d0e111ec8ea00242ac120037'), 'Quais autores estarão presentes na feira?', '2025-05-25 16:05:00', UNHEX('a7b8c9d0e1f211ec8ea00242ac120008'), UNHEX('b6c7d8e9f0a111ec8ea00242ac120027')),
(UNHEX('a7b8c9d0e1f211ec8ea00242ac120038'), 'Adoro comida regional, não vou perder!', '2025-05-26 13:40:00', UNHEX('b8c9d0e1f2a311ec8ea00242ac120009'), UNHEX('c7d8e9f0a1b211ec8ea00242ac120028')),
(UNHEX('b8c9d0e1f2a311ec8ea00242ac120039'), 'Alguém quer formar um grupo para correr junto?', '2025-05-27 08:15:00', UNHEX('c9d0e1f2a3b411ec8ea00242ac120010'), UNHEX('d8e9f0a1b2c311ec8ea00242ac120029')),
(UNHEX('c9d0e1f2a3b411ec8ea00242ac120040'), 'Este workshop é para iniciantes também?', '2025-05-28 19:30:00', UNHEX('d0e1f2a3b4c511ec8ea00242ac120011'), UNHEX('e9f0a1b2c3d411ec8ea00242ac120030')),
(UNHEX('d0e1f2a3b4c511ec8ea00242ac120041'), 'O festival do ano passado foi incrível, este promete ser ainda melhor!', '2025-05-29 15:50:00', UNHEX('a1b2c3d4e5f611ec8ea00242ac120002'), UNHEX('f0a1b2c3d4e511ec8ea00242ac120031'));

-- Inserir dados na tabela Favorite
INSERT INTO Favorite (id_favorite, user_id, event_id) VALUES 
(UNHEX('e1f2a3b4c5d611ec8ea00242ac120042'), UNHEX('a1b2c3d4e5f611ec8ea00242ac120002'), UNHEX('c1d2e3f4a5b611ec8ea00242ac120022')),
(UNHEX('f2a3b4c5d6e711ec8ea00242ac120043'), UNHEX('b2c3d4e5f6a711ec8ea00242ac120003'), UNHEX('d2e3f4a5b6c711ec8ea00242ac120023')),
(UNHEX('a3b4c5d6e7f811ec8ea00242ac120044'), UNHEX('c3d4e5f6a7b811ec8ea00242ac120004'), UNHEX('e3f4a5b6c7d811ec8ea00242ac120024')),
(UNHEX('b4c5d6e7f8a911ec8ea00242ac120045'), UNHEX('d4e5f6a7b8c911ec8ea00242ac120005'), UNHEX('f4a5b6c7d8e911ec8ea00242ac120025')),
(UNHEX('c5d6e7f8a9b011ec8ea00242ac120046'), UNHEX('e5f6a7b8c9d011ec8ea00242ac120006'), UNHEX('a5b6c7d8e9f011ec8ea00242ac120026')),
(UNHEX('d6e7f8a9b0c111ec8ea00242ac120047'), UNHEX('f6a7b8c9d0e111ec8ea00242ac120007'), UNHEX('b6c7d8e9f0a111ec8ea00242ac120027')),
(UNHEX('e7f8a9b0c1d211ec8ea00242ac120048'), UNHEX('a7b8c9d0e1f211ec8ea00242ac120008'), UNHEX('c7d8e9f0a1b211ec8ea00242ac120028')),
(UNHEX('f8a9b0c1d2e311ec8ea00242ac120049'), UNHEX('b8c9d0e1f2a311ec8ea00242ac120009'), UNHEX('d8e9f0a1b2c311ec8ea00242ac120029')),
(UNHEX('a9b0c1d2e3f411ec8ea00242ac120050'), UNHEX('c9d0e1f2a3b411ec8ea00242ac120010'), UNHEX('e9f0a1b2c3d411ec8ea00242ac120030')),
(UNHEX('b0c1d2e3f4a511ec8ea00242ac120051'), UNHEX('d0e1f2a3b4c511ec8ea00242ac120011'), UNHEX('f0a1b2c3d4e511ec8ea00242ac120031'));
