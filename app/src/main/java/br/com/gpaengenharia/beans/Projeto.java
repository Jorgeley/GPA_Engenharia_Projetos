package br.com.gpaengenharia.beans;

public class Projeto implements Comparable{
    private Integer id;
    private String nome;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object another) {
        return (this.id == ((Projeto)another).getId());
    }

    @Override
    public int compareTo(Object o) {
        if (this.id == ((Projeto)o).getId())
            return 0;
        else return 1;
    }
}