package com.mycompany.pizzaria;

import com.mycompany.pizzaria.dao.Conexao;
import com.mycompany.pizzaria.view.TelaCadastro;

public class Pizzaria {

    public static void main(String[] args) {
        Conexao.inicializarBanco();
        java.awt.EventQueue.invokeLater(() -> new TelaCadastro().setVisible(true));
    }
}