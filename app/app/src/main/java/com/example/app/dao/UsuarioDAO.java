package com.example.app.dao;

import com.example.app.modelo.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class UsuarioDAO {

    private final JdbcTemplate jdbc;

    private final RowMapper<Usuario> mapper = (rs, linha) -> {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNome(rs.getString("nome"));
        u.setEmail(rs.getString("email"));
        u.setIdGrupo(rs.getInt("id_grupo"));
        u.setNomeGrupo(rs.getString("nome_grupo"));
        u.setAdministrador(rs.getBoolean("administrador"));
        return u;
    };

    public UsuarioDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // devolve o usuário se e-mail e senha conferem; senão, null
    public Usuario autenticar(String email, String senha) {
        String sql = """
            SELECT u.id_usuario, u.nome, u.email, u.id_grupo, u.administrador,
                   g.nome AS nome_grupo
            FROM usuario u
            JOIN grupo g ON g.id_grupo = u.id_grupo
            WHERE u.email = ? AND u.senha_hash = crypt(?, u.senha_hash)
            """;
        List<Usuario> lista = jdbc.query(sql, mapper, email, senha);
        return lista.isEmpty() ? null : lista.get(0);
    }

    // todos os usuários, com grupo e perfil
    public List<Usuario> listar() {
        String sql = """
            SELECT u.id_usuario, u.nome, u.email, u.id_grupo, u.administrador,
                   g.nome AS nome_grupo
            FROM usuario u
            JOIN grupo g ON g.id_grupo = u.id_grupo
            ORDER BY u.id_usuario
            """;
        return jdbc.query(sql, mapper);
    }
}