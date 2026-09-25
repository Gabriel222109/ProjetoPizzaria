package com.mycompany.pizzaria.model;

public class Produto {

    private int id;
    private String nome;
    private String catalogo;
    private double preco;

    public Produto() {
    }

    public Produto(int id, String nome, String catalogo, double preco) {
        this.id = id;
        this.nome = nome;
        this.catalogo = catalogo;
        this.preco = preco;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(String catalogo) {
        this.catalogo = catalogo;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }
}