package com.example.app.controle;

import com.example.app.dao.AvaliacaoDAO;
import com.example.app.dao.AvaliacaoQuestaoDAO;
import com.example.app.dao.QuestaoDAO;
import com.example.app.modelo.Avaliacao;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class AvaliacaoController {

    private final AvaliacaoDAO avaliacaoDAO;
    private final QuestaoDAO questaoDAO;
    private final AvaliacaoQuestaoDAO avaliacaoQuestaoDAO;

    public AvaliacaoController(AvaliacaoDAO avaliacaoDAO, QuestaoDAO questaoDAO,
                               AvaliacaoQuestaoDAO avaliacaoQuestaoDAO) {
        this.avaliacaoDAO = avaliacaoDAO;
        this.questaoDAO = questaoDAO;
        this.avaliacaoQuestaoDAO = avaliacaoQuestaoDAO;
    }

    @GetMapping("/avaliacoes")
    public String listar(Model model) {
        model.addAttribute("avaliacoes", avaliacaoDAO.listar());
        return "avaliacoes";
    }

    @GetMapping("/avaliacoes/nova")
    public String formNova(Avaliacao avaliacao) {
        return "avaliacao-form";
    }

    @PostMapping("/avaliacoes")
    public String salvar(@Valid Avaliacao avaliacao, BindingResult result) {
        if (avaliacao.getHoraInicio() != null && avaliacao.getHoraFim() != null
                && !avaliacao.getHoraFim().isAfter(avaliacao.getHoraInicio())) {
            result.rejectValue("horaFim", "horario.invalido",
                    "O horário de fim deve ser depois do início");
        }
        if (result.hasErrors()) {
            return "avaliacao-form";
        }
        avaliacaoDAO.inserir(avaliacao);
        return "redirect:/avaliacoes";
    }

    @GetMapping("/avaliacoes/{id}/questoes")
    public String formQuestoes(@PathVariable("id") int id, Model model) {
        Avaliacao avaliacao = avaliacaoDAO.buscarPorId(id);
        if (avaliacao == null) {
            return "redirect:/avaliacoes";
        }
        model.addAttribute("avaliacao", avaliacao);
        model.addAttribute("questoes", questaoDAO.listar());
        model.addAttribute("selecionadas", avaliacaoQuestaoDAO.listarIdsQuestoes(id));
        model.addAttribute("bloqueada", avaliacaoQuestaoDAO.temRespostas(id));
        return "avaliacao-questoes";
    }

    @PostMapping("/avaliacoes/{id}/questoes")
    public String salvarQuestoes(@PathVariable("id") int id,
            @RequestParam(name = "idsQuestoes", required = false) List<Integer> idsQuestoes) {
        if (avaliacaoDAO.buscarPorId(id) == null || avaliacaoQuestaoDAO.temRespostas(id)) {
            return "redirect:/avaliacoes";
        }
        avaliacaoQuestaoDAO.salvarComposicao(id, idsQuestoes == null ? List.of() : idsQuestoes);
        return "redirect:/avaliacoes";
    }
}