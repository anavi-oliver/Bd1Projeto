-- As consultas de cada relatório, com um comentário dizendo qual relatório  atendem
-- guarda os SELECT que respondem perguntas sobre esses dados (média por tema, quem não respondeu). Cada um vira uma tela com tabela e gráfico.

SELECT setseed(0.42);  -- deixa o random() repetvel dai a carga gera sempre os mesmos dados

-- Relatorio 1: média por tema em cada avaliação 
SELECT a.titulo, t.nome AS tema, ROUND(AVG(r.valor), 2) AS media, COUNT(*) AS respostas
FROM resposta r
JOIN avaliacao a ON a.id_avaliacao = r.id_avaliacao
JOIN questao q ON q.id_questao = r.id_questao
JOIN tema t ON t.id_tema = q.id_tema
GROUP BY a.titulo, a.data_aplicacao, t.nome
ORDER BY a.data_aplicacao, t.nome;

-- Relatório 2: qm ainda n respondeu cada avaliação
SELECT a.titulo, u.nome AS usuario, g.nome AS grupo
FROM avaliacao_usuario au
JOIN avaliacao a ON a.id_avaliacao = au.id_avaliacao
JOIN usuario u ON u.id_usuario = au.id_usuario
JOIN grupo g  ON g.id_grupo = u.id_grupo
LEFT JOIN resposta r ON r.id_avaliacao = au.id_avaliacao AND r.id_usuario = au.id_usuario
WHERE r.id_usuario IS NULL
ORDER BY a.titulo, u.nome;

    -- Grafico
    SELECT a.titulo, COUNT(*) AS pendentes
    FROM avaliacao_usuario au
    JOIN avaliacao a ON a.id_avaliacao = au.id_avaliacao
    LEFT JOIN resposta r ON r.id_avaliacao = au.id_avaliacao AND r.id_usuario = au.id_usuario
    WHERE r.id_usuario IS NULL
    GROUP BY a.id_avaliacao, a.titulo, a.data_aplicacao
    ORDER BY a.data_aplicacao;

-- Relatório 3: média de cada grupo comparada c a média geral num período
SELECT g.nome AS grupo, ROUND(AVG(r.valor), 2) AS media_do_grupo,
       (SELECT ROUND(AVG(valor), 2) FROM resposta) AS media_geral
FROM resposta r
JOIN usuario u   ON u.id_usuario = r.id_usuario
JOIN grupo g     ON g.id_grupo = u.id_grupo
JOIN avaliacao a ON a.id_avaliacao = r.id_avaliacao
WHERE a.data_aplicacao BETWEEN '2026-01-01' AND '2026-12-31'
GROUP BY g.nome
HAVING COUNT(*) >= 10
ORDER BY media_do_grupo DESC;

-- Relatório 4: ranking das 10 questões de escala mais bem avaliadas
SELECT RANK() OVER (ORDER BY AVG(r.valor) DESC) AS posicao, q.enunciado, ROUND(AVG(r.valor), 2) AS media
FROM questao q
JOIN resposta r ON r.id_questao = q.id_questao
WHERE q.tipo = 'ESCALA' AND q.enunciado LIKE '%Pergunta%'
GROUP BY q.id_questao, q.enunciado
ORDER BY posicao
FETCH FIRST 10 ROWS ONLY;