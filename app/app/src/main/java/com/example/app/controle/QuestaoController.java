package com.example.app.controle;

import com.example.app.dao.QuestaoDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

//só entregar os dados pra tela
@Controller
public class QuestaoController {

    private final QuestaoDAO questaoDAO;

    public QuestaoController(QuestaoDAO questaoDAO) {
        this.questaoDAO = questaoDAO;
    }

    @GetMapping("/questoes")
    public String listar(Model model) {
        model.addAttribute("questoes", questaoDAO.listar());
        return "questoes";
    }
}