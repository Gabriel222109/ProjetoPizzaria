package com.mycompany.pizzaria;

import com.mycompany.pizzaria.controller.ClienteController;
import com.mycompany.pizzaria.dao.Conexao;
import com.mycompany.pizzaria.model.Cliente;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteControllerTest {

    @BeforeAll
    static void prepararBanco() {
        Conexao.inicializarBanco();
    }

    @Test
    void deveCadastrarEFazerLogin() {
        ClienteController controller = new ClienteController();
        String email = "teste" + System.currentTimeMillis() + "@email.com";

        boolean ok = controller.cadastrar(
                email, "Cliente Teste", "1234", "00000000000",
                "11999999999", "Rua A", "Centro", ""
        );
        assertTrue(ok);

        Cliente cliente = controller.login(email, "1234");
        assertNotNull(cliente);
        assertEquals("Cliente Teste", cliente.getNome());
    }

    @Test
    void loginErradoDeveFalhar() {
        ClienteController controller = new ClienteController();
        assertNull(controller.login("naoexiste", "senha"));
    }
}