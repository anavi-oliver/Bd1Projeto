package com.example.app.controle;

import com.example.app.dao.RelatorioDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;

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

        // ---------- Relatório 3: média de cada grupo x média geral ----------
    @GetMapping("/relatorios/grupos")
    public String grupos(
            @RequestParam(name = "inicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(name = "fim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(name = "grupos", required = false) List<String> grupos,
            Model model) {

        LocalDate ini = inicio != null ? inicio : LocalDate.of(2026, 1, 1);
        LocalDate f = fim != null ? fim : LocalDate.of(2026, 12, 31);
        List<String> todos = relatorioDAO.nomesDosGrupos();
        List<String> escolhidos = (grupos == null || grupos.isEmpty()) ? todos : grupos;

        List<Map<String, Object>> linhas = relatorioDAO.mediaPorGrupo(ini, f, escolhidos);

        List<String> labels = new ArrayList<>();
        List<Object> mediaGrupo = new ArrayList<>();
        List<Object> mediaGeral = new ArrayList<>();
        for (Map<String, Object> l : linhas) {
            labels.add((String) l.get("grupo"));
            mediaGrupo.add(l.get("media_do_grupo"));
            mediaGeral.add(l.get("media_geral"));
        }

        model.addAttribute("titulo", "Média de cada grupo x média geral");
        model.addAttribute("objetivo", "Ver quais grupos estão acima ou abaixo da média geral, no período escolhido.");
        model.addAttribute("cabecalhos", List.of("Grupo", "Média do grupo", "Média geral"));
        model.addAttribute("colunas", List.of("grupo", "media_do_grupo", "media_geral"));
        model.addAttribute("linhas", linhas);
        model.addAttribute("tipo", "bar");
        model.addAttribute("labels", labels);
        model.addAttribute("series", List.of(
                Map.of("label", "Média do grupo", "data", mediaGrupo),
                Map.of("label", "Média geral", "data", mediaGeral)));
        model.addAttribute("yMax", 5);

        model.addAttribute("filtro", "grupos");
        model.addAttribute("inicio", ini);
        model.addAttribute("fim", f);
        model.addAttribute("todosGrupos", todos);
        model.addAttribute("gruposEscolhidos", escolhidos);
        return "relatorio";
    }

    // ---------- Relatório 4: ranking das questões ----------
    @GetMapping("/relatorios/ranking")
    public String ranking(@RequestParam(name = "busca", required = false, defaultValue = "") String busca,
                          Model model) {
        List<Map<String, Object>> linhas = relatorioDAO.rankingQuestoes(busca.trim());

        List<String> labels = new ArrayList<>();
        List<Object> medias = new ArrayList<>();
        for (Map<String, Object> l : linhas) {
            labels.add(l.get("posicao") + "º " + l.get("enunciado"));
            medias.add(l.get("media"));
        }

        model.addAttribute("titulo", "Ranking das questões mais bem avaliadas");
        model.addAttribute("objetivo", "As 10 questões de escala com a maior média. Use a busca para filtrar pelo enunciado.");
        model.addAttribute("cabecalhos", List.of("Posição", "Enunciado", "Média"));
        model.addAttribute("colunas", List.of("posicao", "enunciado", "media"));
        model.addAttribute("linhas", linhas);
        model.addAttribute("tipo", "bar");
        model.addAttribute("labels", labels);
        model.addAttribute("series", List.of(Map.of("label", "Média", "data", medias)));
        model.addAttribute("yMax", 5);
        model.addAttribute("horizontal", true);

        model.addAttribute("filtro", "busca");
        model.addAttribute("busca", busca);
        return "relatorio";
    }
}