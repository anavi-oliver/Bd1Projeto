package com.example.app.controle;

import com.example.app.dao.RelatorioDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RelatorioController {

    private final RelatorioDAO relatorioDAO;

    public RelatorioController(RelatorioDAO relatorioDAO) {
        this.relatorioDAO = relatorioDAO;
    }

    @GetMapping("/relatorios")
    public String indice() {
        return "relatorios";
    }

    // ---------- Relatório 1: média por tema em cada avaliação ----------
    @GetMapping("/relatorios/media-tema")
    public String mediaTema(Model model) {
        List<Map<String, Object>> linhas = relatorioDAO.mediaPorTema();

        // organiza para o gráfico: eixo X = temas, uma série (cor) por avaliação
        List<String> temas = new ArrayList<>();
        Map<String, Map<String, Object>> medias = new LinkedHashMap<>();   // avaliação -> (tema -> média)
        for (Map<String, Object> l : linhas) {
            String tema = (String) l.get("tema");
            String avaliacao = (String) l.get("avaliacao");
            if (!temas.contains(tema)) {
                temas.add(tema);
            }
            medias.computeIfAbsent(avaliacao, k -> new HashMap<>()).put(tema, l.get("media"));
        }
        List<Map<String, Object>> series = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> e : medias.entrySet()) {
            List<Object> dados = new ArrayList<>();
            for (String tema : temas) {
                dados.add(e.getValue().get(tema));
            }
            series.add(Map.of("label", e.getKey(), "data", dados));
        }

        model.addAttribute("titulo", "Média por tema em cada avaliação");
        model.addAttribute("objetivo", "Comparar a satisfação média de cada tema ao longo das avaliações.");
        model.addAttribute("cabecalhos", List.of("Avaliação", "Tema", "Média", "Respostas"));
        model.addAttribute("colunas", List.of("avaliacao", "tema", "media", "respostas"));
        model.addAttribute("linhas", linhas);
        model.addAttribute("tipo", "bar");
        model.addAttribute("labels", temas);
        model.addAttribute("series", series);
        model.addAttribute("yMax", 5);
        return "relatorio";
    }

    // ---------- Relatório 2: quem ainda não respondeu ----------
    @GetMapping("/relatorios/pendentes")
    public String pendentes(Model model) {
        List<Map<String, Object>> grafico = relatorioDAO.pendentesPorAvaliacao();

        List<String> labels = new ArrayList<>();
        List<Object> dados = new ArrayList<>();
        for (Map<String, Object> l : grafico) {
            labels.add((String) l.get("avaliacao"));
            dados.add(l.get("pendentes"));
        }

        model.addAttribute("titulo", "Quem ainda não respondeu");
        model.addAttribute("objetivo", "Listar os usuários designados que ainda não responderam e quantos faltam por avaliação.");
        model.addAttribute("cabecalhos", List.of("Avaliação", "Usuário", "Grupo"));
        model.addAttribute("colunas", List.of("avaliacao", "usuario", "grupo"));
        model.addAttribute("linhas", relatorioDAO.quemNaoRespondeu());
        model.addAttribute("tipo", "bar");
        model.addAttribute("labels", labels);
        model.addAttribute("series", List.of(Map.of("label", "Pendentes", "data", dados)));
        model.addAttribute("yMax", null);
        return "relatorio";
    }
}