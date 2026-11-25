-- Dados de teste, que rodam logo depois do schema.sql.
-- coloca dados de teste dentro dessas tabelas, para o sistema e os gráficos terem o que mostrar.

SELECT setseed(0.42);  -- deixa o random() repetível: a carga gera sempre os mesmos dados

INSERT INTO grupo (nome) VALUES ('CC-2026'), ('Engenharia'), ('Medicina'), ('Direito');
INSERT INTO tema (nome) VALUES ('Infraestrutura'), ('Atendimento'), ('Conteúdo'), ('Organização'), ('Comunicação');
-- perfis: usuario1 e usuario2 são adms
UPDATE usuario SET administrador = TRUE WHERE id_usuario IN (1, 2);

INSERT INTO usuario (nome, email, senha_hash, id_grupo)
SELECT 'Usuário ' || n, 'usuario' || n || '@exemplo.com', crypt('123456', gen_salt('bf')), (n % 4) + 1 FROM generate_series(1, 24) AS n;

--questao 1 a 25 tabela
INSERT INTO questao (enunciado, tipo, id_tema)           
SELECT 'Pergunta de escala ' || n, 'ESCALA', ((n - 1) % 5) + 1 FROM generate_series(1, 25) AS n; 

-- questao 26 a 30, 5 linhas de texto
INSERT INTO questao (enunciado, tipo, id_tema)            
SELECT 'Comentário livre ' || n, 'TEXTO', n FROM generate_series(1, 5) AS n;

INSERT INTO avaliacao (titulo, descricao, data_aplicacao, hora_inicio, hora_fim) VALUES
('Pesquisa de março','Primeira do semestre','2026-03-20', '08:00', '22:00'),
('Pesquisa de maio', 'Segunda do semestre','2026-05-15', '08:00', '22:00'),
('Pesquisa de agosto', 'Terceira do semestre','2026-08-21', '08:00', '22:00'),
('Pesquisa de outubro','Quarta do semestre', '2026-10-16', '08:00', '22:00'),
('Pesquisa de novembro','Quinta do semestre',  '2026-11-20', '08:00', '22:00');

-- cada avaliação: 10 questões de escala (janela deslizante) + 1 de texto livre
INSERT INTO avaliacao_questao (id_avaliacao, id_questao, ordem)
SELECT a, ((a - 1) * 5 + q - 1) % 25 + 1, q
FROM generate_series(1, 5) AS a, generate_series(1, 10) AS q;
INSERT INTO avaliacao_questao (id_avaliacao, id_questao, ordem)
SELECT a, 25 + a, 11 FROM generate_series(1, 5) AS a;

-- todos os usuários devem fazer todas as avaliações
INSERT INTO avaliacao_usuario (id_avaliacao, id_usuario)
SELECT a, u FROM generate_series(1, 5) AS a, generate_series(1, 24) AS u;

-- respostas de escala: as notas sobem um pouco a cada avaliação;
-- na avaliação 5 só os usuários 1 a 12 responderam (os outros ficam pendentes)
INSERT INTO resposta (id_usuario, id_avaliacao, id_questao, valor)
SELECT au.id_usuario, aq.id_avaliacao, aq.id_questao,
       LEAST(5, 1 + floor(random() * 5 + (aq.id_avaliacao - 1) * 0.3)::int)
FROM avaliacao_usuario au
JOIN avaliacao_questao aq ON aq.id_avaliacao = au.id_avaliacao
JOIN questao q ON q.id_questao = aq.id_questao
WHERE q.tipo = 'ESCALA' AND (au.id_avaliacao < 5 OR au.id_usuario <= 12);

-- respostas de texto livre: só alguns usuários comentam
INSERT INTO resposta (id_usuario, id_avaliacao, id_questao, texto)
SELECT au.id_usuario, aq.id_avaliacao, aq.id_questao, 'Comentário do usuário ' || au.id_usuario
FROM avaliacao_usuario au
JOIN avaliacao_questao aq ON aq.id_avaliacao = au.id_avaliacao
JOIN questao q ON q.id_questao = aq.id_questao
WHERE q.tipo = 'TEXTO' AND au.id_usuario % 3 = 0 AND (au.id_avaliacao < 5 OR au.id_usuario <= 12);