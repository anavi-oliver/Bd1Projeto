package com.example.app.controle;

import com.example.app.dao.AvaliacaoDAO;
import com.example.app.modelo.Avaliacao;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AvaliacaoController {

    private final AvaliacaoDAO avaliacaoDAO;

    public AvaliacaoController(AvaliacaoDAO avaliacaoDAO) {
        this.avaliacaoDAO = avaliacaoDAO;
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
        if (avaliacao.getHoraInicio() != null && avaliacao.getHoraFim() != null && !avaliacao.getHoraFim().isAfter(avaliacao.getHoraInicio())) {  //CHECK (hora_fim > hora_inicio)
            result.rejectValue("horaFim", "horario.invalido", "O horário de fim deve ser depois do início");
        }
        if (result.hasErrors()) {
            return "avaliacao-form";
        }
        avaliacaoDAO.inserir(avaliacao);
        return "redirect:/avaliacoes";
    }
}