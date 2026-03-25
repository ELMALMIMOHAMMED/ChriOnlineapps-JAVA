package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBConnection {

    private static final String ROOT_URL =
            "jdbc:mysql://127.0.0.1:3306/?useSSL=false&serverTimezone=UTC";
    private static final String URL =
            "jdbc:mysql://127.0.0.1:3306/chrionline?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
        private static final String PASSWORD = "";

    private static Connection openConnection(String url) throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", USER);
        if (PASSWORD != null && !PASSWORD.isEmpty()) {
            props.setProperty("password", PASSWORD);
        }
        return DriverManager.getConnection(url, props);
    }

    public static boolean initializeDatabase() {
        try (Connection conn = openConnection(ROOT_URL);
             Statement st = conn.createStatement()) {

            st.executeUpdate("CREATE DATABASE IF NOT EXISTS chrionline");

        } catch (SQLException e) {
            System.out.println("DB init error (database creation)");
            e.printStackTrace();
            return false;
        }

        try (Connection conn = openConnection(URL);
             Statement st = conn.createStatement()) {

            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        email VARCHAR(150) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL
                    )
                    """);

            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS produits (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        nom VARCHAR(150) NOT NULL,
                        prix DOUBLE NOT NULL,
                        stock INT NOT NULL DEFAULT 0
                    )
                    """);

            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS panier (
                        user_id INT NOT NULL,
                        produit_id INT NOT NULL,
                        quantite INT NOT NULL,
                        PRIMARY KEY (user_id, produit_id)
                    )
                    """);

            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS commandes (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT NOT NULL,
                        total DOUBLE NOT NULL,
                        statut VARCHAR(30) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);

            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS commande_items (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        commande_id INT NOT NULL,
                        produit_id INT NOT NULL,
                        quantite INT NOT NULL,
                        prix DOUBLE NOT NULL
                    )
                    """);

            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS paiements (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        commande_id INT NOT NULL,
                        methode VARCHAR(50) NOT NULL,
                        montant DOUBLE NOT NULL,
                        statut VARCHAR(30) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);

            ResultSet rs = st.executeQuery("SELECT COUNT(*) AS c FROM produits");
            if (rs.next() && rs.getInt("c") == 0) {
                st.executeUpdate("""
                        INSERT INTO produits(nom, prix, stock) VALUES
                        ('produit 1', 10.0, 100),
                        ('produit 2', 20.0, 100),
                        ('produit 3', 30.0, 100)
                        """);
                System.out.println("Seed produits OK (3 produits)");
            }

            System.out.println("DB init OK");
            return true;

        } catch (SQLException e) {
            System.out.println("DB init error (table creation)");
            e.printStackTrace();
            return false;
        }
    }

    public static Connection getConnection() {
        try {
            Connection conn = openConnection(URL);
            System.out.println("✅ Connexion DB réussie !");
            return conn;
        } catch (SQLException e) {
            System.out.println("❌ Erreur connexion DB !");
            e.printStackTrace();
            return null;
        }
    }
}