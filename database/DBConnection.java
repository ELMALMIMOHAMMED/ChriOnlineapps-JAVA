package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	private static final String URL =
		    "jdbc:mysql://127.0.0.1:3307/chrionline?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "ADMIN@admin2003";

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion DB réussie !");
            return conn;
        } catch (SQLException e) {
            System.out.println("❌ Erreur connexion DB !");
            e.printStackTrace();
            return null;
        }
    }
}