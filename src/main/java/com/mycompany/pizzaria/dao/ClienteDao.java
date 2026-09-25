package com.mycompany.pizzaria.dao;

import com.mycompany.pizzaria.model.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteDao {

    public boolean cadastrar(Cliente cliente) {
        String sql = "INSERT INTO cliente (email, nome, senha, cpf, telefone, endereco, bairro, complemento) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, cliente.getEmail());
            ps.setString(2, cliente.getNome());
            ps.setString(3, cliente.getSenha());
            ps.setString(4, cliente.getCpf());
            ps.setString(5, cliente.getTelefone());
            ps.setString(6, cliente.getEndereco());
            ps.setString(7, cliente.getBairro());
            ps.setString(8, cliente.getComplemento());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar cliente: " + e.getMessage());
            return false;
        }
    }

    public Cliente login(String usuario, String senha) {
        String sql = "SELECT * FROM cliente "
                + "WHERE (email = ? OR nome = ?) AND senha = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, usuario);
            ps.setString(3, senha);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setId(rs.getInt("id"));
                    cliente.setEmail(rs.getString("email"));
                    cliente.setNome(rs.getString("nome"));
                    cliente.setSenha(rs.getString("senha"));
                    cliente.setCpf(rs.getString("cpf"));
                    cliente.setTelefone(rs.getString("telefone"));
                    cliente.setEndereco(rs.getString("endereco"));
                    cliente.setBairro(rs.getString("bairro"));
                    cliente.setComplemento(rs.getString("complemento"));
                    return cliente;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao fazer login do cliente: " + e.getMessage());
        }
        return null;
    }
}