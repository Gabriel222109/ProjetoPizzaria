package com.mycompany.pizzaria;

import com.mycompany.pizzaria.controller.ClienteController;
import com.mycompany.pizzaria.dao.Conexao;
import com.mycompany.pizzaria.dao.PedidoDao;
import com.mycompany.pizzaria.model.Cliente;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PedidoDaoTest {

    @BeforeAll
    static void prepararBanco() {
        Conexao.inicializarBanco();
    }

    @Test
    void deveRealizarCancelarENotificarPedido() {
        ClienteController clienteController = new ClienteController();
        String email = "pedido" + System.currentTimeMillis() + "@email.com";
        assertTrue(clienteController.cadastrar(
                email, "Cliente Pedido", "1234", "111", "1198",
                "Rua B", "Centro", ""
        ));

        Cliente cliente = clienteController.login(email, "1234");
        assertNotNull(cliente);

        PedidoDao dao = new PedidoDao();
        assertTrue(dao.realizar(cliente.getId(), "Mussarela", "PIX", 8.00, 40));
        assertTrue(dao.cancelarUltimo(cliente.getId()));
    }
}