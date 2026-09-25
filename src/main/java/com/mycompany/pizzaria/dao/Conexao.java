package com.mycompany.pizzaria.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Conexao SQLite da Pizzaria.
 * Banco: Pizzaria.db (criado na pasta de execucao do projeto).
 *
 * Lembrete no NetBeans:
 * Clique com o botao direito em Bibliotecas > Adicionar JAR/Pasta
 * e inclua o sqlite-jdbc (ex.: sqlite-jdbc-3.45.x.jar).
 */
public class Conexao {

    private static final String URL = "jdbc:sqlite:Pizzaria.db";

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "Driver SQLite nao encontrado. Adicione o JAR sqlite-jdbc nas Bibliotecas do projeto.",
                e
            );
        }
        return DriverManager.getConnection(URL);
    }

    public static void inicializarBanco() {
        try (Connection conexao = conectar();
             Statement comando = conexao.createStatement()) {

            comando.execute("PRAGMA foreign_keys = ON");

                        comando.execute(
                "CREATE TABLE IF NOT EXISTS produto ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nome TEXT NOT NULL, "
                + "catalogo TEXT NOT NULL, "
                + "preco REAL NOT NULL"
                + ")"
            );

            comando.execute(
                "CREATE TABLE IF NOT EXISTS usuario ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "login TEXT NOT NULL UNIQUE, "
                + "senha TEXT NOT NULL"
                + ")"
            );

            comando.execute(
                "CREATE TABLE IF NOT EXISTS cliente ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "email TEXT NOT NULL UNIQUE, "
                + "nome TEXT NOT NULL, "
                + "senha TEXT, "
                + "cpf TEXT, "
                + "telefone TEXT, "
                + "endereco TEXT, "
                + "bairro TEXT, "
                + "complemento TEXT"
                + ")"
            );

            try {
                comando.execute("ALTER TABLE cliente ADD COLUMN senha TEXT");
            } catch (SQLException ignored) {
                // coluna ja existe
            }

            comando.execute(
                "CREATE TABLE IF NOT EXISTS pedido ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "cliente_id INTEGER, "
                + "data TEXT NOT NULL, "
                + "status TEXT NOT NULL DEFAULT 'Aberto', "
                + "total REAL NOT NULL DEFAULT 0, "
                + "FOREIGN KEY (cliente_id) REFERENCES cliente(id)"
                + ")"
            );

            comando.execute(
                "CREATE TABLE IF NOT EXISTS item_pedido ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "pedido_id INTEGER NOT NULL, "
                + "produto_id INTEGER NOT NULL, "
                + "quantidade INTEGER NOT NULL DEFAULT 1, "
                + "preco_unitario REAL NOT NULL, "
                + "FOREIGN KEY (pedido_id) REFERENCES pedido(id), "
                + "FOREIGN KEY (produto_id) REFERENCES produto(id)"
                + ")"
            );

            comando.execute(
                "INSERT INTO usuario (login, senha) "
                + "SELECT 'admin', 'admin' "
                + "WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE login = 'admin')"
            );

            migrarTabelaProduto(conexao, comando);
            inserirProdutosIniciais(comando);

            System.out.println("Banco SQLite da Pizzaria conectado!");
            System.out.println("Tabelas produto, usuario, cliente, pedido e item_pedido verificadas.");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Se a tabela produto veio de outro projeto (valor/estoque),
     * recria no formato da tela admin: nome, catalogo, preco.
     */
    private static void migrarTabelaProduto(Connection conexao, Statement comando) throws SQLException {
        boolean temCatalogo = colunaExiste(conexao, "produto", "catalogo");
        boolean temPreco = colunaExiste(conexao, "produto", "preco");
        boolean temValor = colunaExiste(conexao, "produto", "valor");
        boolean temEstoque = colunaExiste(conexao, "produto", "estoque");

        if (temCatalogo && temPreco && !temValor) {
            return;
        }

        comando.execute(
            "CREATE TABLE IF NOT EXISTS produto_novo ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "nome TEXT NOT NULL, "
            + "catalogo TEXT NOT NULL, "
            + "preco REAL NOT NULL"
            + ")"
        );

        String precoOrigem = temPreco ? "preco" : (temValor ? "valor" : "0");
        String catalogoOrigem = temCatalogo ? "catalogo" : "'Geral'";

        comando.execute(
            "INSERT INTO produto_novo (id, nome, catalogo, preco) "
            + "SELECT id, nome, COALESCE(" + catalogoOrigem + ", 'Geral'), COALESCE(" + precoOrigem + ", 0) "
            + "FROM produto"
        );

        comando.execute("DROP TABLE produto");
        comando.execute("ALTER TABLE produto_novo RENAME TO produto");
        System.out.println("Tabela produto migrada para o formato da Pizzaria (nome, catalogo, preco).");

        if (temEstoque) {
            System.out.println("Coluna estoque antiga foi descartada na migracao.");
        }
    }

    private static void inserirProdutosIniciais(Statement comando) throws SQLException {
        try (ResultSet rs = comando.executeQuery("SELECT COUNT(*) AS total FROM produto")) {
            if (rs.next() && rs.getInt("total") > 0) {
                return;
            }
        }

        comando.execute(
            "INSERT INTO produto (nome, catalogo, preco) VALUES "
            + "('Mussarela', 'Pizza', 39.90), "
            + "('Calabresa', 'Pizza', 42.90), "
            + "('Portuguesa', 'Pizza', 46.90), "
            + "('Frango com Catupiry', 'Pizza', 47.90), "
            + "('Coca-Cola 2L', 'Bebida', 12.00), "
            + "('Guarana 2L', 'Bebida', 10.00)"
        );
        System.out.println("Produtos iniciais da pizzaria inseridos.");
    }

    private static boolean colunaExiste(Connection conexao, String tabela, String coluna) throws SQLException {
        try (ResultSet rs = conexao.getMetaData().getColumns(null, null, tabela, coluna)) {
            if (rs.next()) {
                return true;
            }
        }
        try (ResultSet rs = conexao.getMetaData().getColumns(null, null, tabela.toUpperCase(), coluna)) {
            return rs.next();
        }
    }
}