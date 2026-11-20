package com.example.app.controle;

import com.example.app.dao.QuestaoDAO;
import com.example.app.dao.TemaDAO;
import com.example.app.modelo.Questao;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

//entregar dados pra tela
@Controller
public class QuestaoController {

    private final QuestaoDAO questaoDAO;
    private final TemaDAO temaDAO;

    public QuestaoController(QuestaoDAO questaoDAO, TemaDAO temaDAO) {
        this.questaoDAO = questaoDAO;
        this.temaDAO = temaDAO;
    }

    @GetMapping("/questoes")
    public String listar(Model model) {
        model.addAttribute("questoes", questaoDAO.listar());
        return "questoes";
    }

    @GetMapping("/questoes/nova")
    public String formNova(Questao questao, Model model) {
        model.addAttribute("temas", temaDAO.listar());
        return "questao-form";
    }

    @PostMapping("/questoes")
    public String salvar(@Valid Questao questao, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("temas", temaDAO.listar());
            return "questao-form";
        }
        questaoDAO.inserir(questao);
        return "redirect:/questoes";
    }
}