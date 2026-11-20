package com.example.app.controle;

import com.example.app.dao.UsuarioDAO;
import com.example.app.modelo.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final UsuarioDAO usuarioDAO;

    public LoginController(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/questoes";
    }

    @GetMapping("/login")
    public String formLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String entrar(@RequestParam String email, @RequestParam String senha, HttpSession session, Model model) {
        Usuario usuario = usuarioDAO.autenticar(email, senha);
        if (usuario == null) {
            model.addAttribute("erro", "E-mail ou senha incorretos");
            return "login";
        }
        session.setAttribute("usuarioLogado", usuario);
        return "redirect:/questoes";
    }

    @GetMapping("/logout")
    public String sair(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}