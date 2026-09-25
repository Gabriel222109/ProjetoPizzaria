package com.mycompany.pizzaria.controller;

import com.mycompany.pizzaria.dao.ClienteDao;
import com.mycompany.pizzaria.model.Cliente;

public class ClienteController {

    private final ClienteDao dao = new ClienteDao();

    public boolean cadastrar(String email, String nome, String senha, String cpf,
                             String telefone, String endereco, String bairro, String complemento) {
        Cliente cliente = new Cliente();
        cliente.setEmail(email);
        cliente.setNome(nome);
        cliente.setSenha(senha);
        cliente.setCpf(cpf);
        cliente.setTelefone(telefone);
        cliente.setEndereco(endereco);
        cliente.setBairro(bairro);
        cliente.setComplemento(complemento);
        return dao.cadastrar(cliente);
    }

    public Cliente login(String usuario, String senha) {
        return dao.login(usuario, senha);
    }
}