-- Script que cria o banco do zero, com todas as restrições. Deve rodar sem erro num  PostgreSQL vazio
-- cria a estrutura: as tabelas, as chaves e as restrições. É o DER traduzido para SQL.
--  ( DDL )

------------------------------------------------------------------------------
-- camada1 sem fk
CREATE TABLE grupo ( 
    id_grupo INT GENERATED AS ALWAYS IDENTITY PRIMARY KEY,
    nome VARCHAR(80) NOT NULL UNIQUE
    );

CREATE TABLE tema (
    id_tema INT GENERATED AS ALWAYS IDENTITY PRIMARY KEY,
    nome VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE avaliacao(
    id_avaliacao INT GENERATED AS ALWAYS IDENTITY PRIMARY KEY,
    titulo VARCHAR(80) NOT NULL,
    descricao TEXT,
    data_aplicacao DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    CHECK (hora_fim > hora_inicio)
);

------------------------- CAMADA 2
    CREATE TABLE usuario(
    id_usuario INT GENERATED AS ALWAYS IDENTITY PRIMARY KEY,
    nome VARCHAR(80) NOT NULL,
    email VARCHAR(40) NOT NULL UNIQUE,
    id_grupo INTEGER NOT NULL,
    FOREIGN KEY (id_grupo) REFERENCES grupo(id_grupo)
    );

CREATE TABLE questao(
    id_qustao INT GENERATED AS ALWAYS IDENTITY PRIMARY KEY,
    enunciado TEXT NOT NULL,
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('multipla', 'text')),
    FOREIGN KEY id_tema REFERENCES tema(id_tema)
);

----------------------------- camada3

CREATE TABLE avaliacao_usuario(
    id_avaliacao INT NOT NULL REFERENCES avaliacao (id_avaliacao),
    id_usuario   INT NOT NULL REFERENCES usuario (id_usuario),
    PRIMARY KEY (id_avaliacao, id_usuario)
);
CREATE TABLE avaliacao_questao(
    id_avaliacao INT NOT NULL REFERENCES avaliacao (id_avaliacao),
    id_questao INT NOT NULL REFERENCES questao (id_questao),
    ordemINT NOT NULL CHECK (ordem > 0),
    PRIMARY KEY (id_avaliacao, id_questao),
    UNIQUE (id_avaliacao, ordem)
);

--------------------------------camada 4
CREATE TABLE resposta(
    id_usuario INT NOT NULL,
    id_avaliacao INT NOT NULL,
    id_questao INT NOT NULL,
    valor SMALLINT CHECK (valor BETWEEN 1 AND 5),
    texto TEXT,

    PRIMARY KEY (id_usuario, id_avaliacao, id_questao),
    FOREIGN KEY (id_avaliacao, id_usuario) REFERENCES avaliacao_usuario (id_avaliacao, id_usuario),
    FOREIGN KEY (id_avaliacao, id_questao) REFERENCES avaliacao_questao (id_avaliacao, id_questao),

    CHECK ((valor IS NOT NULL AND texto IS NULL)
        OR (valor IS NULL AND texto IS NOT NULL))
    
);
