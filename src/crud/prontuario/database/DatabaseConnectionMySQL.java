package crud.prontuario.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import crud.prontuario.util.ConfigLoader;

public class DatabaseConnectionMySQL implements IConnection {

    private final String USERNAME = ConfigLoader.getValor("DB_USER");
    private final String PASSWORD = ConfigLoader.getValor("DB_PASSWORD");
    private final String ADDRESS = ConfigLoader.getValor("DB_ADDRESS");
    private final String PORT = ConfigLoader.getValor("DB_PORT");
    private final String DATABASE = ConfigLoader.getValor("DB_SCHEMA");

    @Override
    public Connection getConnection() {
        try {
            String url = "jdbc:mysql://%s:%s/%s".formatted(ADDRESS, PORT, DATABASE);
            return DriverManager.getConnection(url, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void closeConnection() {
    	
    }

}
