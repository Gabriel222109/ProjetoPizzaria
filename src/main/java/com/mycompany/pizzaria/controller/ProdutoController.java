package com.mycompany.pizzaria.controller;

import com.mycompany.pizzaria.dao.ProdutoDao;
import com.mycompany.pizzaria.model.Produto;
import java.util.List;

public class ProdutoController {

    private final ProdutoDao dao = new ProdutoDao();

    public boolean cadastrar(String nome, String catalogo, double preco) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setCatalogo(catalogo);
        produto.setPreco(preco);
        return dao.cadastrar(produto);
    }

    public boolean deletar(String nome) {
        return dao.deletarPorNome(nome);
    }

    public List<Produto> listar() {
        return dao.listar();
    }
}