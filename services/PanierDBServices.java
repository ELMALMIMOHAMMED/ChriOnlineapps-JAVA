package services;

import database.DBConnection;
import models.PanierItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PanierDBServices {

    // =========================
    // GET PANIER
    // =========================
    public List<PanierItem> getPanier(int userId) {
        List<PanierItem> list = new ArrayList<>();

        if (userId <= 0) {
            System.out.println("❌ userId invalide !");
            return list;
        }

        try (Connection conn = DBConnection.getConnection()) {

            String sql = """
                    SELECT pa.produit_id, p.nom, p.prix, pa.quantite
                    FROM panier pa
                    JOIN produits p ON pa.produit_id = p.id
                    WHERE pa.user_id = ?
                    """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new PanierItem(
                        rs.getInt("produit_id"),
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getInt("quantite")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // AJOUT PRODUIT
    // =========================
    public boolean ajouterProduit(int userId, int produitId, int quantite) {

        if (userId <= 0) {
            System.out.println("❌ userId invalide !");
            return false;
        }

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false); // 🔥 TRANSACTION

            // 1️⃣ Vérifier stock
            String stockSql = "SELECT stock FROM produits WHERE id=?";
            PreparedStatement psStock = conn.prepareStatement(stockSql);
            psStock.setInt(1, produitId);

            ResultSet rs = psStock.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Produit inexistant !");
                return false;
            }

            int stock = rs.getInt("stock");

            if (stock < quantite) {
                System.out.println("❌ Stock insuffisant !");
                return false;
            }

            // 2️⃣ Vérifier panier
            String checkSql = "SELECT quantite FROM panier WHERE user_id=? AND produit_id=?";
            PreparedStatement psCheck = conn.prepareStatement(checkSql);
            psCheck.setInt(1, userId);
            psCheck.setInt(2, produitId);

            ResultSet rsCheck = psCheck.executeQuery();

            int result;

            if (rsCheck.next()) {

                // UPDATE
                String update = "UPDATE panier SET quantite = quantite + ? WHERE user_id=? AND produit_id=?";
                PreparedStatement psUpdate = conn.prepareStatement(update);
                psUpdate.setInt(1, quantite);
                psUpdate.setInt(2, userId);
                psUpdate.setInt(3, produitId);

                result = psUpdate.executeUpdate();

            } else {

                // INSERT
                String insert = "INSERT INTO panier(user_id, produit_id, quantite) VALUES (?, ?, ?)";
                PreparedStatement psInsert = conn.prepareStatement(insert);
                psInsert.setInt(1, userId);
                psInsert.setInt(2, produitId);
                psInsert.setInt(3, quantite);

                result = psInsert.executeUpdate();
            }

            if (result > 0) {
                conn.commit(); // ✅ SUCCESS
                return true;
            } else {
                conn.rollback(); // ❌ FAIL
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // UPDATE QUANTITE
    // =========================
    public boolean updateQuantite(int userId, int produitId, int quantite) {

        if (userId <= 0) return false;

        try (Connection conn = DBConnection.getConnection()) {

            if (quantite <= 0) {
                return supprimerProduit(userId, produitId);
            }

            String stockSql = "SELECT stock FROM produits WHERE id=?";
            PreparedStatement psStock = conn.prepareStatement(stockSql);
            psStock.setInt(1, produitId);

            ResultSet rs = psStock.executeQuery();
            if (!rs.next()) return false;

            int stock = rs.getInt("stock");
            if (stock < quantite) return false;

            String update = "UPDATE panier SET quantite=? WHERE user_id=? AND produit_id=?";
            PreparedStatement psUpdate = conn.prepareStatement(update);
            psUpdate.setInt(1, quantite);
            psUpdate.setInt(2, userId);
            psUpdate.setInt(3, produitId);

            return psUpdate.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // DELETE
    // =========================
    public boolean supprimerProduit(int userId, int produitId) {

        if (userId <= 0) return false;

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "DELETE FROM panier WHERE user_id=? AND produit_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, produitId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}