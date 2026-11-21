package com.example.app.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public class AvaliacaoQuestaoDAO {

    private final JdbcTemplate jdbc;

    public AvaliacaoQuestaoDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ids das questões que já compõem a avaliação
    public List<Integer> listarIdsQuestoes(int idAvaliacao) {
        String sql = "SELECT id_questao FROM avaliacao_questao WHERE id_avaliacao = ?";
        return jdbc.queryForList(sql, Integer.class, idAvaliacao);
    }

    // true se alguém já respondeu a avaliação
    public boolean temRespostas(int idAvaliacao) {
        String sql = "SELECT COUNT(*) FROM resposta WHERE id_avaliacao = ?";
        Integer total = jdbc.queryForObject(sql, Integer.class, idAvaliacao);
        return total != null && total > 0;
    }

    // apaga a composição antiga e grava a nova: tudo ou nada
    @Transactional
    public void salvarComposicao(int idAvaliacao, List<Integer> idsQuestoes) {
        jdbc.update("DELETE FROM avaliacao_questao WHERE id_avaliacao = ?", idAvaliacao);
        int ordem = 1;
        for (Integer idQuestao : idsQuestoes) {
            jdbc.update("""
                INSERT INTO avaliacao_questao (id_avaliacao, id_questao, ordem)
                VALUES (?, ?, ?)
                """, idAvaliacao, idQuestao, ordem);
            ordem++;
        }
    }
}