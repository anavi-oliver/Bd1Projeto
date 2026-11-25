package com.example.app.modelo;

public class Usuario {
    private int idUsuario;
    private String nome;
    private String email;
    private int idGrupo;
    private String nomeGrupo; 
    private boolean administrador;



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
public boolean isAdministrador() { 
    return administrador; 
}
public String getNomeGrupo() {
     return nomeGrupo; 
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
public void setAdministrador(boolean administrador) { 
    this.administrador = administrador; 
}
public void setNomeGrupo(String nomeGrupo) { 
    this.nomeGrupo = nomeGrupo; 
}



}