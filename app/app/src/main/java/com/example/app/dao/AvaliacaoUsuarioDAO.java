package com.example.app.dao;

import com.example.app.modelo.GrupoResumo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class AvaliacaoUsuarioDAO {

    private final JdbcTemplate jdbc;

    public AvaliacaoUsuarioDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // para cada grupo: quantos usuários tem e quantos já foram designados a esta avaliação
    public List<GrupoResumo> resumoPorGrupo(int idAvaliacao) {
        String sql = """
            SELECT g.id_grupo, g.nome,
                   COUNT(u.id_usuario)  AS total,
                   COUNT(au.id_usuario) AS designados
            FROM grupo g
            LEFT JOIN usuario u ON u.id_grupo = g.id_grupo
            LEFT JOIN avaliacao_usuario au ON au.id_usuario = u.id_usuario
                                          AND au.id_avaliacao = ?
            GROUP BY g.id_grupo, g.nome
            ORDER BY g.nome
            """;
        return jdbc.query(sql, (rs, linha) -> {
            GrupoResumo r = new GrupoResumo();
            r.setIdGrupo(rs.getInt("id_grupo"));
            r.setNome(rs.getString("nome"));
            r.setTotal(rs.getInt("total"));
            r.setDesignados(rs.getInt("designados"));
            return r;
        }, idAvaliacao);
    }

    // designa todos os usuários do grupo; quem já estava designado é ignorado
    public void designarGrupo(int idAvaliacao, int idGrupo) {
        String sql = """
            INSERT INTO avaliacao_usuario (id_avaliacao, id_usuario)
            SELECT CAST(? AS INTEGER), u.id_usuario
            FROM usuario u
            WHERE u.id_grupo = ?
            ON CONFLICT DO NOTHING
            """;
        jdbc.update(sql, idAvaliacao, idGrupo);
    }
}