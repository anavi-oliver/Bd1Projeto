package com.example.app.controle;

import com.example.app.dao.AvaliacaoDAO;
import com.example.app.dao.AvaliacaoUsuarioDAO;
import com.example.app.modelo.Avaliacao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AvaliacaoUsuarioController {

    private final AvaliacaoDAO avaliacaoDAO;
    private final AvaliacaoUsuarioDAO avaliacaoUsuarioDAO;

    public AvaliacaoUsuarioController(AvaliacaoDAO avaliacaoDAO,
                                      AvaliacaoUsuarioDAO avaliacaoUsuarioDAO) {
        this.avaliacaoDAO = avaliacaoDAO;
        this.avaliacaoUsuarioDAO = avaliacaoUsuarioDAO;
    }

    @GetMapping("/avaliacoes/{id}/usuarios")
    public String form(@PathVariable("id") int id, Model model) {
        Avaliacao avaliacao = avaliacaoDAO.buscarPorId(id);
        if (avaliacao == null) {
            return "redirect:/avaliacoes";
        }
        model.addAttribute("avaliacao", avaliacao);
        model.addAttribute("grupos", avaliacaoUsuarioDAO.resumoPorGrupo(id));
        return "avaliacao-usuarios";
    }

    @PostMapping("/avaliacoes/{id}/usuarios")
    public String designar(@PathVariable("id") int id,
                           @RequestParam("idGrupo") int idGrupo) {
        if (avaliacaoDAO.buscarPorId(id) != null) {
            avaliacaoUsuarioDAO.designarGrupo(id, idGrupo);
        }
        return "redirect:/avaliacoes/" + id + "/usuarios";
    }
}