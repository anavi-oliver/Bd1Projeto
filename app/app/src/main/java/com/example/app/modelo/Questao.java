package com.example.app.modelo;

public class Questao {

//questao tem: id (PK)= int, enunciado = string , tipo = string  e idtema = int
private int id_questao;
private String enuciado;
private String tipo;
private int id_tema;


public String getEnuciado() {
    return enuciado;
}
public void setEnuciado(String enuciado) {
    this.enuciado = enuciado;
}
public int getId_questao() {
    return id_questao;
}
public void setId_questao(int id_questao) {
    this.id_questao = id_questao;
}
public int getId_tema() {
    return id_tema;
}
public void setId_tema(int id_tema) {
    this.id_tema = id_tema;
}
public String getTipo() {
    return tipo;
}
public void setTipo(String tipo) {
    this.tipo = tipo;
}

}

