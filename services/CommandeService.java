package services;

import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Service Commande - version PRO sécurisée
 */
public class CommandeService {

    // =========================
    // 🔥 créer commande depuis panier (PRO)
    // =========================
    public int creerCommande(int userId) {

        if (userId <= 0) return -1;

        int commandeId = -1;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false); // 🔥 TRANSACTION

            // =========================
            // 🔹 récupérer panier + prix (OPTIMISÉ)
            // =========================
            String panierSql =
                    "SELECT pa.produit_id, pa.quantite, p.prix " +
                    "FROM panier pa " +
                    "JOIN produits p ON pa.produit_id = p.id " +
                    "WHERE pa.user_id=?";

            PreparedStatement panierPs = conn.prepareStatement(panierSql);
            panierPs.setInt(1, userId);

            ResultSet rs = panierPs.executeQuery();

            double total = 0;

            // 🔥 stocker temporairement
            class Item {
                int produitId;
                int quantite;
                double prix;
            }

            java.util.List<Item> items = new java.util.ArrayList<>();

            while (rs.next()) {
                Item item = new Item();
                item.produitId = rs.getInt("produit_id");
                item.quantite = rs.getInt("quantite");
                item.prix = rs.getDouble("prix");

                total += item.prix * item.quantite;
                items.add(item);
            }

            // ❌ panier vide
            if (items.isEmpty()) {
                return -1;
            }

            // =========================
            // 🔹 créer commande
            // =========================
            String insertCmd =
                    "INSERT INTO commandes(user_id, total, statut) VALUES (?, ?, ?)";

            PreparedStatement cmdPs =
                    conn.prepareStatement(insertCmd, PreparedStatement.RETURN_GENERATED_KEYS);

            cmdPs.setInt(1, userId);
            cmdPs.setDouble(2, total);
            cmdPs.setString(3, "EN_ATTENTE");

            cmdPs.executeUpdate();

            ResultSet keys = cmdPs.getGeneratedKeys();
            if (keys.next()) {
                commandeId = keys.getInt(1);
            }

            // =========================
            // 🔹 insertion items (batch)
            // =========================
            String insertItem =
                    "INSERT INTO commande_items(commande_id, produit_id, quantite, prix) VALUES (?, ?, ?, ?)";

            PreparedStatement itemPs = conn.prepareStatement(insertItem);

            for (Item item : items) {

                itemPs.setInt(1, commandeId);
                itemPs.setInt(2, item.produitId);
                itemPs.setInt(3, item.quantite);
                itemPs.setDouble(4, item.prix);

                itemPs.addBatch();
            }

            itemPs.executeBatch();

            // =========================
            // 🔹 vider panier
            // =========================
            String clearSql = "DELETE FROM panier WHERE user_id=?";
            PreparedStatement clearPs = conn.prepareStatement(clearSql);
            clearPs.setInt(1, userId);
            clearPs.executeUpdate();

            conn.commit(); // ✅ SUCCESS

        } catch (Exception e) {
            e.printStackTrace();
            // ❌ rollback automatique si erreur (connection fermée)
            return -1;
        }

        return commandeId;
    }

    // =========================
    // 🔹 valider commande
    // =========================
    public boolean validerCommande(int commandeId) {

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "UPDATE commandes SET statut='VALIDE' WHERE id=? AND statut='EN_ATTENTE'";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, commandeId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // 🔹 annuler commande
    // =========================
    public boolean annulerCommande(int commandeId) {

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "UPDATE commandes SET statut='ANNULEE' WHERE id=? AND statut='EN_ATTENTE'";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, commandeId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}