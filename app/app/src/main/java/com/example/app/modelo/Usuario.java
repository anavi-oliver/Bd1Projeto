package com.example.app.modelo;

public class Usuario {
    private int idUsuario;
    private String nome;
    private String email;
    private int idGrupo;

public String getEmail() {
    return email;
}
public int getIdGrupo() {
    return idGrupo;
}
public int getIdUsuario() {
    return idUsuario;
}
public String getNome() {
    return nome;
}

public void setEmail(String email) {
    this.email = email;
}
public void setIdGrupo(int idGrupo) {
    this.idGrupo = idGrupo;
}
public void setIdUsuario(int idUsuario) {
    this.idUsuario = idUsuario;
}
public void setNome(String nome) {
    this.nome = nome;
}

}