package dao;

import common.BaseDonnees;
import models.Commande;
import models.LigneCommande;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandeDAO {

    private static volatile boolean initialized;

    public static Commande createCommande(int userId, List<LigneCommande> lignes) {
        initializeIfNeeded();

        try (Connection connection = BaseDonnees.getConnection()) {
            return createCommande(connection, userId, lignes);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to create commande", e);
        }
    }

    public static Commande createCommande(Connection connection, int userId, List<LigneCommande> lignes) {
        initializeIfNeeded();

        String insertCommande = "INSERT INTO commande (user_id, status, total) VALUES (?, ?, ?)";
        String insertLigne = "INSERT INTO ligne_commande (commande_id, product_id, product_name, quantity, unit_price) VALUES (?, ?, ?, ?, ?)";
        double total = 0.0;

        for (LigneCommande ligne : lignes) {
            total += ligne.calculerSousTotal();
        }

        try (PreparedStatement commandeStatement = connection.prepareStatement(insertCommande, Statement.RETURN_GENERATED_KEYS)) {
            commandeStatement.setInt(1, userId);
            commandeStatement.setString(2, "EN_ATTENTE");
            commandeStatement.setDouble(3, total);
            commandeStatement.executeUpdate();

            int commandeId;
            try (ResultSet generatedKeys = commandeStatement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("No commande ID generated");
                }
                commandeId = generatedKeys.getInt(1);
            }

            try (PreparedStatement ligneStatement = connection.prepareStatement(insertLigne)) {
                for (LigneCommande ligne : lignes) {
                    ligneStatement.setInt(1, commandeId);
                    ligneStatement.setString(2, ligne.getProduitId());
                    ligneStatement.setString(3, ligne.getNomProduit());
                    ligneStatement.setInt(4, ligne.getQuantite());
                    ligneStatement.setDouble(5, ligne.getPrixUnitaire());
                    ligneStatement.addBatch();
                }
                ligneStatement.executeBatch();
            }

            return buildCommande(commandeId, userId, "EN_ATTENTE", lignes);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to create commande", e);
        }
    }

    public static boolean updateStatus(int commandeId, String status) {
        initializeIfNeeded();

        try (Connection connection = BaseDonnees.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE commande SET status = ? WHERE id = ?")) {

            statement.setString(1, status);
            statement.setInt(2, commandeId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to update commande status", e);
        }
    }

    public static List<Commande> getCommandesByUser(int userId) {
        initializeIfNeeded();
        List<Commande> commandes = new ArrayList<>();

        String query = "SELECT id, user_id, status FROM commande WHERE user_id = ? ORDER BY id";

        try (Connection connection = BaseDonnees.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    commandes.add(loadCommande(connection, resultSet.getInt("id"), resultSet.getInt("user_id"), resultSet.getString("status")));
                }
            }

            return commandes;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load commandes", e);
        }
    }

    public static Commande getCommandeById(int commandeId) {
        initializeIfNeeded();

        String query = "SELECT id, user_id, status FROM commande WHERE id = ?";

        try (Connection connection = BaseDonnees.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, commandeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return loadCommande(connection, resultSet.getInt("id"), resultSet.getInt("user_id"), resultSet.getString("status"));
                }
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load commande", e);
        }
    }

    public static int getOrderedQuantity(String productId) {
        initializeIfNeeded();

        try (Connection connection = BaseDonnees.getConnection()) {
            return getOrderedQuantity(connection, productId);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load ordered quantity", e);
        }
    }

    public static int getOrderedQuantity(Connection connection, String productId) {
        initializeIfNeeded();

        String query = "SELECT COALESCE(SUM(lc.quantity), 0) AS ordered_quantity "
                + "FROM ligne_commande lc "
                + "JOIN commande c ON c.id = lc.commande_id "
                + "WHERE lc.product_id = ? AND c.status <> 'ANNULEE'";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, productId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("ordered_quantity");
                }
            }

            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load ordered quantity", e);
        }
    }

    public static Map<String, Integer> getOrderedQuantitiesByUser(int userId) {
        initializeIfNeeded();

        String query = "SELECT lc.product_id, COALESCE(SUM(lc.quantity), 0) AS ordered_quantity "
                + "FROM ligne_commande lc "
                + "JOIN commande c ON c.id = lc.commande_id "
                + "WHERE c.user_id = ? AND c.status <> 'ANNULEE' "
                + "GROUP BY lc.product_id";

        try (Connection connection = BaseDonnees.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return readQuantityMap(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load user ordered quantities", e);
        }
    }

    public static Map<String, Integer> getOrderedQuantities() {
        initializeIfNeeded();

        String query = "SELECT lc.product_id, COALESCE(SUM(lc.quantity), 0) AS ordered_quantity "
                + "FROM ligne_commande lc "
                + "JOIN commande c ON c.id = lc.commande_id "
                + "WHERE c.status <> 'ANNULEE' "
                + "GROUP BY lc.product_id";

        try (Connection connection = BaseDonnees.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            return readQuantityMap(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load ordered quantities", e);
        }
    }

    private static Commande loadCommande(Connection connection, int id, int userId, String status) throws SQLException {
        Commande commande = new Commande(id, userId);
        commande.setStatus(status);

        String ligneQuery = "SELECT product_id, product_name, quantity, unit_price FROM ligne_commande WHERE commande_id = ? ORDER BY id";

        try (PreparedStatement statement = connection.prepareStatement(ligneQuery)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    commande.ajouterLigne(new LigneCommande(
                            resultSet.getString("product_id"),
                            resultSet.getString("product_name"),
                            resultSet.getInt("quantity"),
                            resultSet.getDouble("unit_price")
                    ));
                }
            }
        }

        return commande;
    }

    private static Commande buildCommande(int id, int userId, String status, List<LigneCommande> lignes) {
        Commande commande = new Commande(id, userId);
        commande.setStatus(status);

        for (LigneCommande ligne : lignes) {
            commande.ajouterLigne(new LigneCommande(
                    ligne.getProduitId(),
                    ligne.getNomProduit(),
                    ligne.getQuantite(),
                    ligne.getPrixUnitaire()
            ));
        }

        return commande;
    }

    private static Map<String, Integer> readQuantityMap(ResultSet resultSet) throws SQLException {
        Map<String, Integer> quantities = new HashMap<>();

        while (resultSet.next()) {
            quantities.put(resultSet.getString("product_id"), resultSet.getInt("ordered_quantity"));
        }

        return quantities;
    }

    private static void initializeIfNeeded() {
        if (initialized) {
            return;
        }

        synchronized (CommandeDAO.class) {
            if (initialized) {
                return;
            }

            try (Connection connection = BaseDonnees.getConnection()) {
                ensureTables(connection);
                initialized = true;
            } catch (SQLException e) {
                throw new RuntimeException("Unable to initialize commande storage", e);
            }
        }
    }

    private static void ensureTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS commande ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "user_id INT NOT NULL, "
                    + "status VARCHAR(50) NOT NULL, "
                    + "total DOUBLE NOT NULL, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")");

            statement.execute("CREATE TABLE IF NOT EXISTS ligne_commande ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "commande_id INT NOT NULL, "
                    + "product_id VARCHAR(20) NOT NULL, "
                    + "product_name VARCHAR(100) NOT NULL, "
                    + "quantity INT NOT NULL, "
                    + "unit_price DOUBLE NOT NULL, "
                    + "CONSTRAINT fk_ligne_commande_commande FOREIGN KEY (commande_id) REFERENCES commande(id) ON DELETE CASCADE"
                    + ")");
        }
    }
}