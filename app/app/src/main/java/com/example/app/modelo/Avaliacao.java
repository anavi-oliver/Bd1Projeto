package com.example.app.modelo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
//horario e data da aplicacao
import java.time.LocalDate; 
import java.time.LocalTime;

//cadastro das avaliacoes q nem o questoes
public class Avaliacao {

    private int idAvaliacao;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 80, message = "O título pode ter no máximo 80 caracteres")
    private String titulo;

    private String descricao;

    @NotNull(message = "Informe a data")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataAplicacao;

    @NotNull(message = "Informe o horário de início")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @NotNull(message = "Informe o horário de fim")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaFim;

public LocalDate getDataAplicacao() {
    return dataAplicacao;
}
public String getDescricao() {
    return descricao;
}
public LocalTime getHoraFim() {
    return horaFim;
}
public LocalTime getHoraInicio() {
    return horaInicio;
}
public int getIdAvaliacao() {
    return idAvaliacao;
}
public String getTitulo() {
    return titulo;
}


public void setDataAplicacao(LocalDate dataAplicacao) {
    this.dataAplicacao = dataAplicacao;
}
public void setDescricao(String descricao) {
    this.descricao = descricao;
}
public void setHoraFim(LocalTime horaFim) {
    this.horaFim = horaFim;
}
public void setHoraInicio(LocalTime horaInicio) {
    this.horaInicio = horaInicio;
}
public void setIdAvaliacao(int idAvaliacao) {
    this.idAvaliacao = idAvaliacao;
}
public void setTitulo(String titulo) {
    this.titulo = titulo;
}

}