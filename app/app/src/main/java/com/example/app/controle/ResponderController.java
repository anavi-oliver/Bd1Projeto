package com.example.app.controle;

import com.example.app.dao.AvaliacaoDAO;
import com.example.app.dao.QuestaoDAO;
import com.example.app.dao.RespostaDAO;
import com.example.app.modelo.Questao;
import com.example.app.modelo.Resposta;
import com.example.app.modelo.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ResponderController {

    private final AvaliacaoDAO avaliacaoDAO;
    private final QuestaoDAO questaoDAO;
    private final RespostaDAO respostaDAO;

    public ResponderController(AvaliacaoDAO avaliacaoDAO, QuestaoDAO questaoDAO,
                               RespostaDAO respostaDAO) {
        this.avaliacaoDAO = avaliacaoDAO;
        this.questaoDAO = questaoDAO;
        this.respostaDAO = respostaDAO;
    }

    @GetMapping("/minhas-avaliacoes")
    public String minhas(HttpSession session, Model model) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogado");
        model.addAttribute("avaliacoes", avaliacaoDAO.listarDoUsuario(u.getIdUsuario()));
        return "minhas-avaliacoes";
    }

    @GetMapping("/responder/{id}")
    public String form(@PathVariable("id") int id, HttpSession session, Model model) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogado");
        if (!podeResponder(u, id)) {
            return "redirect:/minhas-avaliacoes";
        }
        return prepararForm(id, model);
    }

    @PostMapping("/responder/{id}")
    public String salvar(@PathVariable("id") int id,
                         @RequestParam Map<String, String> params,
                         HttpSession session, Model model) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogado");
        if (!podeResponder(u, id)) {
            return "redirect:/minhas-avaliacoes";
        }

        List<Resposta> respostas = new ArrayList<>();
        boolean completo = true;

        for (Questao q : questaoDAO.listarDaAvaliacao(id)) {
            Resposta r = new Resposta();
            r.setIdUsuario(u.getIdUsuario());
            r.setIdAvaliacao(id);
            r.setIdQuestao(q.getIdQuestao());

            if ("ESCALA".equals(q.getTipo())) {
                String texto = params.get("valor_" + q.getIdQuestao());
                try {
                    int valor = Integer.parseInt(texto);
                    if (valor < 1 || valor > 5) {
                        completo = false;
                    }
                    r.setValor(valor);
                } catch (NumberFormatException e) {   // não respondeu (ou valor inválido)
                    completo = false;
                }
            } else {
                String texto = params.get("texto_" + q.getIdQuestao());
                if (texto == null || texto.isBlank()) {
                    continue;                          // texto livre é opcional
                }
                r.setTexto(texto.trim());
            }
            respostas.add(r);
        }

        if (!completo || respostas.isEmpty()) {
            model.addAttribute("erro", "Responda todas as questões de escala (de 1 a 5).");
            return prepararForm(id, model);
        }

        respostaDAO.salvarTodas(respostas);
        return "redirect:/minhas-avaliacoes";
    }

    private boolean podeResponder(Usuario u, int idAvaliacao) {
        return respostaDAO.designado(u.getIdUsuario(), idAvaliacao)
                && !respostaDAO.jaRespondeu(u.getIdUsuario(), idAvaliacao);
    }

    private String prepararForm(int id, Model model) {
        model.addAttribute("avaliacao", avaliacaoDAO.buscarPorId(id));
        model.addAttribute("questoes", questaoDAO.listarDaAvaliacao(id));
        return "responder";
    }
}