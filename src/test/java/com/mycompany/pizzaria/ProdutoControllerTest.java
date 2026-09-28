package com.mycompany.pizzaria;

import com.mycompany.pizzaria.controller.ProdutoController;
import com.mycompany.pizzaria.dao.Conexao;
import com.mycompany.pizzaria.model.Produto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProdutoControllerTest {

    @BeforeAll
    static void prepararBanco() {
        Conexao.inicializarBanco();
    }

    @Test
    void deveCadastrarListarEDeletarProduto() {
        ProdutoController controller = new ProdutoController();
        String nome = "Teste Pizza " + System.currentTimeMillis();

        assertTrue(controller.cadastrar(nome, "Pizza", 33.50));

        List<Produto> lista = controller.listar();
        assertTrue(lista.stream().anyMatch(p -> nome.equals(p.getNome())));

        assertTrue(controller.deletar(nome));
    }
}