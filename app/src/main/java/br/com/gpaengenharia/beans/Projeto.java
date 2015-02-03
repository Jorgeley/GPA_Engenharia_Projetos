package br.com.gpaengenharia.beans;

public class Projeto implements Comparable{
    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    /** se return 0, objetos iguais e não adiciona, se return 1 então adiciona */
    @Override
    public int compareTo(Object another) {
        return 1;
    }
}