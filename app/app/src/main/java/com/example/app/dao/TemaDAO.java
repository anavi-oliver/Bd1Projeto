package com.example.app.dao;

import com.example.app.modelo.Tema;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class TemaDAO {

    private final JdbcTemplate jdbc;

    public TemaDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Tema> listar() {
        String sql = "SELECT id_tema, nome FROM tema ORDER BY nome";
        return jdbc.query(sql, (rs, linha) -> {
            Tema t = new Tema();
            t.setIdTema(rs.getInt("id_tema"));
            t.setNome(rs.getString("nome"));
            return t;
        });
    }
}