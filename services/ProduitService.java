package services;

import database.DBConnection;
import models.Produit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService {

    // =========================
    // 📦 GET ALL PRODUITS
    // =========================
    public List<Produit> getAllProduits() {

        List<Produit> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "SELECT * FROM produits";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getInt("stock")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // 🔍 GET PRODUIT BY ID
    // =========================
    public Produit getProduitById(int id) {

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "SELECT * FROM produits WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getInt("stock")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // =========================
    // 🔄 UPDATE STOCK (PRO - TRANSACTION SAFE)
    // =========================
    public boolean updateStock(Connection conn, int produitId, int quantite) {

        try {

            String sql = "UPDATE produits SET stock = stock - ? WHERE id = ? AND stock >= ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, quantite);
            ps.setInt(2, produitId);
            ps.setInt(3, quantite);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // 🔄 RESTORE STOCK (si rollback logique)
    // =========================
    public boolean restoreStock(Connection conn, int produitId, int quantite) {

        try {

            String sql = "UPDATE produits SET stock = stock + ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, quantite);
            ps.setInt(2, produitId);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // ➕ AJOUT PRODUIT (ADMIN)
    // =========================
    public boolean ajouterProduit(Produit produit) {

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "INSERT INTO produits(nom, prix, stock) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, produit.getNom());
            ps.setDouble(2, produit.getPrix());
            ps.setInt(3, produit.getStock());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // ❌ DELETE PRODUIT
    // =========================
    public boolean supprimerProduit(int id) {

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "DELETE FROM produits WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}