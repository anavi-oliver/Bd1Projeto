package com.example.app.modelo;
/*Na lista de avaliações, cada linha ganha um link Designar. 
Ele abre uma tela com os grupos, mostrando quantos usuários cada um tem e quantos já foram designados. 
Cada grupo tem um botão Designar grupo, que insere todos os usuários dele na avaliação. 
Se clicar de novo, não duplica nada. */

public class GrupoResumo {
    private int idGrupo;
    private String nome;
    private int total;       // usuarios do grupo
    private int designados;  

public int getDesignados() {
    return designados;
}
public int getIdGrupo() {
    return idGrupo;
}
public String getNome() {
    return nome;
}
public int getTotal() {
    return total;
}


public void setDesignados(int designados) {
    this.designados = designados;
}
public void setIdGrupo(int idGrupo) {
    this.idGrupo = idGrupo;
}
public void setNome(String nome) {
    this.nome = nome;
}
public void setTotal(int total) {
    this.total = total;
}

}