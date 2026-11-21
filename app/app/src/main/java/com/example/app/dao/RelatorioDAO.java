package com.example.app.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class RelatorioDAO {

    private final JdbcTemplate jdbc;

    public RelatorioDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Relatório 1: média por tema em cada avaliação
    public List<Map<String, Object>> mediaPorTema() {
        String sql = """
            SELECT a.titulo AS avaliacao, t.nome AS tema,
                   ROUND(AVG(r.valor), 2) AS media,
                   COUNT(r.valor)         AS respostas
            FROM resposta r
            JOIN avaliacao a ON a.id_avaliacao = r.id_avaliacao
            JOIN questao q   ON q.id_questao   = r.id_questao
            JOIN tema t      ON t.id_tema      = q.id_tema
            GROUP BY a.id_avaliacao, a.titulo, a.data_aplicacao, t.id_tema, t.nome
            ORDER BY a.data_aplicacao, t.nome
            """;
        return jdbc.queryForList(sql);
    }

    // Relatório 2 (tabela): quem ainda não respondeu cada avaliação
    public List<Map<String, Object>> quemNaoRespondeu() {
        String sql = """
            SELECT a.titulo AS avaliacao, u.nome AS usuario, g.nome AS grupo
            FROM avaliacao_usuario au
            JOIN avaliacao a ON a.id_avaliacao = au.id_avaliacao
            JOIN usuario u   ON u.id_usuario   = au.id_usuario
            JOIN grupo g     ON g.id_grupo     = u.id_grupo
            LEFT JOIN resposta r ON r.id_avaliacao = au.id_avaliacao
                                AND r.id_usuario   = au.id_usuario
            WHERE r.id_usuario IS NULL
            ORDER BY a.titulo, u.nome
            """;
        return jdbc.queryForList(sql);
    }

    // Relatório 2 (gráfico): quantos pendentes por avaliação
    public List<Map<String, Object>> pendentesPorAvaliacao() {
        String sql = """
            SELECT a.titulo AS avaliacao, COUNT(*) AS pendentes
            FROM avaliacao_usuario au
            JOIN avaliacao a ON a.id_avaliacao = au.id_avaliacao
            LEFT JOIN resposta r ON r.id_avaliacao = au.id_avaliacao
                                AND r.id_usuario   = au.id_usuario
            WHERE r.id_usuario IS NULL
            GROUP BY a.id_avaliacao, a.titulo, a.data_aplicacao
            ORDER BY a.data_aplicacao
            """;
        return jdbc.queryForList(sql);
    }
}