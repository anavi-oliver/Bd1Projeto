package com.example.app.modelo;

public class Resposta {
    private int idUsuario;
    private int idAvaliacao;
    private int idQuestao;
    private Integer valor;   // nota de 1 a 5 (null nas questões de texto)
    private String texto;    // comentário (null nas questões de escala)

public int getIdAvaliacao() {
    return idAvaliacao;
}
public int getIdQuestao() {
    return idQuestao;
}
public int getIdUsuario() {
    return idUsuario;
}
public String getTexto() {
    return texto;
}
public Integer getValor() {
    return valor;
}


public void setIdAvaliacao(int idAvaliacao) {
    this.idAvaliacao = idAvaliacao;
}
public void setIdQuestao(int idQuestao) {
    this.idQuestao = idQuestao;
}
public void setIdUsuario(int idUsuario) {
    this.idUsuario = idUsuario;
}
public void setTexto(String texto) {
    this.texto = texto;
}
public void setValor(Integer valor) {
    this.valor = valor;
}

}