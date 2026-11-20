package com.example.app.dao;

import com.example.app.modelo.Avaliacao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class AvaliacaoDAO {

    private final JdbcTemplate jdbc;

    public AvaliacaoDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Avaliacao> listar() {
        String sql = """
            SELECT id_avaliacao, titulo, descricao, data_aplicacao, hora_inicio, hora_fim
            FROM avaliacao
            ORDER BY data_aplicacao DESC, hora_inicio
            """;
        return jdbc.query(sql, (rs, linha) -> {
            Avaliacao a = new Avaliacao();
            a.setIdAvaliacao(rs.getInt("id_avaliacao"));
            a.setTitulo(rs.getString("titulo"));
            a.setDescricao(rs.getString("descricao"));
            a.setDataAplicacao(rs.getDate("data_aplicacao").toLocalDate());
            a.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
            a.setHoraFim(rs.getTime("hora_fim").toLocalTime());
            return a;
        });
    }

    public void inserir(Avaliacao a) {
        String sql = """
            INSERT INTO avaliacao (titulo, descricao, data_aplicacao, hora_inicio, hora_fim)
            VALUES (?, ?, ?, ?, ?)
            """;
        jdbc.update(sql, a.getTitulo(), a.getDescricao(),
                java.sql.Date.valueOf(a.getDataAplicacao()),
                java.sql.Time.valueOf(a.getHoraInicio()),
                java.sql.Time.valueOf(a.getHoraFim()));
    }
}