package com.example.app.dao;

import com.example.app.modelo.Questao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository //objeto QuestaoDAO
public class QuestaoDAO {

    private final JdbcTemplate jdbc; //abre a conexão com o PostgreSQL, executa o SQL e fecha tudo

    public QuestaoDAO(JdbcTemplate jdbc) { //construtor
        this.jdbc = jdbc;
    }

    //listar
    public List<Questao> listar() { // 3 aspas: guardar textos em varias linhas
        String sql = """
            SELECT q.id_questao, q.enunciado, q.tipo, q.id_tema, t.nome AS nome_tema
            FROM questao q
            JOIN tema t ON t.id_tema = q.id_tema
            ORDER BY q.id_questao
            """;

        return jdbc.query(sql, (rs, linha) -> { //funcao lambda
                                                //lambda precisa dos dois parâmetros, mesmo o segundo não sendo usado.
                                                // Ela implementa o RowMapper do spring
            Questao q = new Questao();
            q.setIdQuestao(rs.getInt("id_questao"));
            q.setEnunciado(rs.getString("enunciado"));
            q.setIdTema(rs.getInt("id_tema"));
            q.setTipo(rs.getString("tipo"));
            q.setNomeTema(rs.getString("nome_tema"));
            return q;
        });
    }

    //inserir: Os ? são preenchidos pelos valores na mesma ordem
    public void inserir(Questao q) {
        String sql = "INSERT INTO questao (enunciado, tipo, id_tema) VALUES (?, ?, ?)";
        jdbc.update(sql, q.getEnunciado(), q.getTipo(), q.getIdTema());
    }
} 

