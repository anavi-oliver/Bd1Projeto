package com.example.app.modelo;

public class Questao {

//questao tem: id (PK)= int, enunciado = string , tipo = string  e idtema = int
private int idQuestao;
private int idTema;
private String enunciado;
private String tipo;
private String nomeTema;


public int getIdQuestao() {
    return idQuestao;
}
public void setIdQuestao(int idQuestao) {
    this.idQuestao = idQuestao;
}

public int getIdTema() { 
    return idTema;
}
public void setIdTema(int idTema) { 
    this.idTema = idTema; 
}

public String getEnunciado() { 
    return enunciado; 
}
public void setEnunciado(String enunciado) { 
    this.enunciado = enunciado; 
}

public String getTipo() { 
    return tipo; 
}
public void setTipo(String tipo) { 
    this.tipo = tipo; 
}

public String getNomeTema() { 
    return nomeTema; 
}
public void setNomeTema(String nomeTema) { 
    this.nomeTema = nomeTema; 
}
}