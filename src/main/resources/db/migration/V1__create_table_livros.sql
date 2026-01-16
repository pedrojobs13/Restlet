USE biblioteca;

CREATE TABLE usuarios (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          nome VARCHAR(100) NOT NULL,
                          email VARCHAR(150) NOT NULL UNIQUE,
                          senha VARCHAR(255) NOT NULL,
                          funcao ENUM('admin', 'user') DEFAULT 'user',
                          criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE autores (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         nome VARCHAR(150) NOT NULL,
                         bio VARCHAR(255),
                         data_nascimento DATE,
                         criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE categorias (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE livros (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        titulo VARCHAR(200) NOT NULL,
                        descricao VARCHAR(255),
                        ano_de_publicacao INT,
                        isbn VARCHAR(20) UNIQUE,
                        autor_id INT NOT NULL,
                        criado_por INT NOT NULL,
                        criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                        CONSTRAINT fk_livros_autor
                            FOREIGN KEY (autor_id)
                                REFERENCES autores(id)
                                ON DELETE RESTRICT,

                        CONSTRAINT fk_livros_usuario
                            FOREIGN KEY (criado_por)
                                REFERENCES usuarios(id)
                                ON DELETE CASCADE
);



CREATE TABLE livros_categorias (
                                   livro_id INT NOT NULL,
                                   categoria_id INT NOT NULL,
                                   PRIMARY KEY (livro_id, categoria_id),

                                   CONSTRAINT fk_lc_livro
                                       FOREIGN KEY (livro_id)
                                           REFERENCES livros(id)
                                           ON DELETE CASCADE,

                                   CONSTRAINT fk_lc_categoria
                                       FOREIGN KEY (categoria_id)
                                           REFERENCES categorias(id)
                                           ON DELETE CASCADE
);

