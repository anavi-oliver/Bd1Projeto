package com.example.app.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class RelatorioDAO {

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate named;

    public RelatorioDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.named = new NamedParameterJdbcTemplate(jdbc);
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

    // nomes dos grupos (para as caixinhas do filtro)
    public List<String> nomesDosGrupos() {
        return jdbc.queryForList("SELECT nome FROM grupo ORDER BY nome", String.class);
    }

    // Relatório 3: média de cada grupo comparada com a média geral, num período
    public List<Map<String, Object>> mediaPorGrupo(LocalDate inicio, LocalDate fim,
                                                   List<String> grupos) {
        String sql = """
            SELECT g.nome AS grupo,
                   ROUND(AVG(r.valor), 2) AS media_do_grupo,
                   (SELECT ROUND(AVG(valor), 2) FROM resposta) AS media_geral
            FROM resposta r
            JOIN usuario u   ON u.id_usuario   = r.id_usuario
            JOIN grupo g     ON g.id_grupo     = u.id_grupo
            JOIN avaliacao a ON a.id_avaliacao = r.id_avaliacao
            WHERE a.data_aplicacao BETWEEN :inicio AND :fim
              AND g.nome IN (:grupos)
            GROUP BY g.nome
            HAVING COUNT(r.valor) >= 10
            ORDER BY media_do_grupo DESC
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("inicio", java.sql.Date.valueOf(inicio))
                .addValue("fim", java.sql.Date.valueOf(fim))
                .addValue("grupos", grupos);
        return named.queryForList(sql, params);
    }

    // Relatório 4: ranking das 10 questões de escala mais bem avaliadas
    public List<Map<String, Object>> rankingQuestoes(String busca) {
        String sql = """
            SELECT RANK() OVER (ORDER BY AVG(r.valor) DESC) AS posicao,
                   q.enunciado,
                   ROUND(AVG(r.valor), 2) AS media
            FROM questao q
            JOIN resposta r ON r.id_questao = q.id_questao
            WHERE q.tipo = 'ESCALA'
              AND LOWER(q.enunciado) LIKE LOWER(:busca)
            GROUP BY q.id_questao, q.enunciado
            ORDER BY posicao
            FETCH FIRST 10 ROWS ONLY
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("busca", "%" + busca + "%");
        return named.queryForList(sql, params);
    }
}