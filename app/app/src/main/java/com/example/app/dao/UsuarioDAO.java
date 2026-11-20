package com.example.app.dao;

import com.example.app.modelo.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class UsuarioDAO {

    private final JdbcTemplate jdbc;

    public UsuarioDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // devolve o usuário se e-mail e senha conferem senao= null
    public Usuario autenticar(String email, String senha) {
        String sql = """
            SELECT id_usuario, nome, email, id_grupo
            FROM usuario
            WHERE email = ? AND senha_hash = crypt(?, senha_hash)
            """;
        List<Usuario> lista = jdbc.query(sql, (rs, linha) -> {
            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("id_usuario"));
            u.setNome(rs.getString("nome"));
            u.setEmail(rs.getString("email"));
            u.setIdGrupo(rs.getInt("id_grupo"));
            return u;
        }, email, senha);
        return lista.isEmpty() ? null : lista.get(0);
    }
}