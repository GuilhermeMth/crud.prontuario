package crud.prontuario.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import crud.prontuario.util.ConfigLoader;

public class DatabaseInitializer {

    public static void inicializarBanco() {
        final String ADDRESS = ConfigLoader.getValor("DB_ADDRESS");
        final String PORT = ConfigLoader.getValor("DB_PORT");
        final String DATABASE = ConfigLoader.getValor("DB_SCHEMA");
        final String USERNAME = ConfigLoader.getValor("DB_USER");
        final String PASSWORD = ConfigLoader.getValor("DB_PASSWORD");

        try {
            String urlSemDB = "jdbc:mysql://%s:%s/".formatted(ADDRESS, PORT);

            try (Connection conn = DriverManager.getConnection(urlSemDB, USERNAME, PASSWORD);
                 var stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + DATABASE + "`");
            }

            String urlComDB = "jdbc:mysql://%s:%s/%s".formatted(ADDRESS, PORT, DATABASE);
            try (Connection conn = DriverManager.getConnection(urlComDB, USERNAME, PASSWORD);
                 var stmt = conn.createStatement()) {

                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS PACIENTES (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        cpf VARCHAR(14) UNIQUE NOT NULL,
                        nome VARCHAR(255) NOT NULL,
                        data_nascimento DATETIME NOT NULL
                    );
                """);

                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS EXAMES (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        descricao VARCHAR(255) NOT NULL,
                        data_exame DATE NOT NULL,
                        paciente_id BIGINT NOT NULL,
                        FOREIGN KEY (paciente_id) REFERENCES PACIENTES(id) ON DELETE CASCADE
                    );
                """);
            }

        } catch (SQLException e) {
            System.out.println("[ERRO] Erro ao inicializar o banco: " + e.getMessage());
        }
    }
}