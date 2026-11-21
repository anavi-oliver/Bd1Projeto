package com.example.app.dao;

import com.example.app.modelo.Resposta;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public class RespostaDAO {

    private final JdbcTemplate jdbc;

    public RespostaDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // a avaliação foi designada a este usuário?
    public boolean designado(int idUsuario, int idAvaliacao) {
        String sql = "SELECT COUNT(*) FROM avaliacao_usuario WHERE id_usuario = ? AND id_avaliacao = ?";
        Integer total = jdbc.queryForObject(sql, Integer.class, idUsuario, idAvaliacao);
        return total != null && total > 0;
    }

    // o usuário já respondeu esta avaliação?
    public boolean jaRespondeu(int idUsuario, int idAvaliacao) {
        String sql = "SELECT COUNT(*) FROM resposta WHERE id_usuario = ? AND id_avaliacao = ?";
        Integer total = jdbc.queryForObject(sql, Integer.class, idUsuario, idAvaliacao);
        return total != null && total > 0;
    }

    // grava todas as respostas ou nenhuma
    @Transactional
    public void salvarTodas(List<Resposta> respostas) {
        String sql = """
            INSERT INTO resposta (id_usuario, id_avaliacao, id_questao, valor, texto)
            VALUES (?, ?, ?, ?, ?)
            """;
        for (Resposta r : respostas) {
            jdbc.update(sql, r.getIdUsuario(), r.getIdAvaliacao(), r.getIdQuestao(),
                    r.getValor(), r.getTexto());
        }
    }
}