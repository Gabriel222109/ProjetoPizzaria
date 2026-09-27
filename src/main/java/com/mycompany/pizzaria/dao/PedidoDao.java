package com.mycompany.pizzaria.dao;

import com.mycompany.pizzaria.model.Pedido;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PedidoDao {

    public boolean realizar(int clienteId, String produtoNome) {
        String buscarProduto = "SELECT id, preco FROM produto WHERE nome = ?";
        String inserirPedido = "INSERT INTO pedido (cliente_id, data, status, total, forma_pagamento, taxa_entrega, tempo_entrega) "
                + "VALUES (?, ?, 'Aberto', ?, ?, ?, ?)";
        String inserirItem = "INSERT INTO item_pedido (pedido_id, produto_id, quantidade, preco_unitario) "
                + "VALUES (?, ?, 1, ?)";

        try (Connection conexao = Conexao.conectar()) {
            conexao.setAutoCommit(false);

            int produtoId;
            double preco;
            try (PreparedStatement ps = conexao.prepareStatement(buscarProduto)) {
                ps.setString(1, produtoNome);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conexao.rollback();
                        return false;
                    }
                    produtoId = rs.getInt("id");
                    preco = rs.getDouble("preco");
                }
            }

            String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            int pedidoId;
            try (PreparedStatement ps = conexao.prepareStatement(inserirPedido, PreparedStatement.RETURN_GENERATED_KEYS)) {
                if (clienteId <= 0) {
                    ps.setObject(1, null);
                } else {
                    ps.setInt(1, clienteId);
                }
                ps.setString(2, data);
                ps.setDouble(3, preco);
                ps.setString(4, "PIX");
                ps.setDouble(5, 0);
                ps.setInt(6, 40);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        conexao.rollback();
                        return false;
                    }
                    pedidoId = keys.getInt(1);
                }
            }

            try (PreparedStatement ps = conexao.prepareStatement(inserirItem)) {
                ps.setInt(1, pedidoId);
                ps.setInt(2, produtoId);
                ps.setDouble(3, preco);
                ps.executeUpdate();
            }

            conexao.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao realizar pedido: " + e.getMessage());
            return false;
        }
    }

    public boolean realizar(String produtoNome) {
        return realizar(0, produtoNome);
    }

    public boolean realizar(int clienteId, String produtoNome, String formaPagamento, double taxa, int tempo) {
        if (!realizar(clienteId, produtoNome)) {
            return false;
        }

        int pedidoId = buscarUltimoId(clienteId);
        if (pedidoId <= 0) {
            return false;
        }

        String sql = "UPDATE pedido SET forma_pagamento = ?, taxa_entrega = ?, tempo_entrega = ?, total = total + ? WHERE id = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, formaPagamento);
            ps.setDouble(2, taxa);
            ps.setInt(3, tempo);
            ps.setDouble(4, taxa);
            ps.setInt(5, pedidoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao gravar pagamento: " + e.getMessage());
            return false;
        }

        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String msg = "Pedido recebido. Pagamento: " + formaPagamento
                + ". Taxa: R$ " + taxa + ". Tempo: " + tempo + " min.";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement ps = conexao.prepareStatement(
                     "INSERT INTO notificacao (pedido_id, mensagem, data, lida) VALUES (?, ?, ?, 0)")) {
            ps.setInt(1, pedidoId);
            ps.setString(2, msg);
            ps.setString(3, data);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao gravar notificacao: " + e.getMessage());
        }
        return true;
    }

    public boolean cancelarUltimo(int clienteId) {
        int pedidoId = buscarUltimoId(clienteId);
        if (pedidoId <= 0) {
            return false;
        }
        return atualizarStatus(pedidoId, "Cancelado");
    }

    public boolean atualizarStatus(int pedidoId, String status) {
        String sql = "UPDATE pedido SET status = ? WHERE id = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, pedidoId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar pedido: " + e.getMessage());
            return false;
        }
    }

    public Pedido buscarPorId(int id) {
        String sql = "SELECT id, cliente_id, data, status, total FROM pedido WHERE id = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Pedido pedido = new Pedido();
                    pedido.setId(rs.getInt("id"));
                    pedido.setClienteId(rs.getInt("cliente_id"));
                    pedido.setData(rs.getString("data"));
                    pedido.setStatus(rs.getString("status"));
                    pedido.setTotal(rs.getDouble("total"));
                    return pedido;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar pedido: " + e.getMessage());
        }
        return null;
    }

    private int buscarUltimoId(int clienteId) {
        String sql = "SELECT id FROM pedido WHERE cliente_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar ultimo pedido: " + e.getMessage());
        }
        return 0;
    }
}