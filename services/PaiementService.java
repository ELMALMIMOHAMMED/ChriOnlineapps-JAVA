package services;

import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PaiementService {

    public boolean processPayment(int userId, int commandeId, String methode) {

        try (Connection conn = DBConnection.getConnection()) {

            // 1️⃣ récupérer montant de la commande
            String sqlTotal = "SELECT total FROM commandes WHERE id = ?";
            PreparedStatement psTotal = conn.prepareStatement(sqlTotal);
            psTotal.setInt(1, commandeId);

            ResultSet rs = psTotal.executeQuery();

            if (!rs.next()) {
                return false;
            }

            double montant = rs.getDouble("total");

            // 2️⃣ enregistrer paiement
            String sql = "INSERT INTO paiements (commande_id, methode, montant, statut) VALUES (?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, commandeId);
            ps.setString(2, methode);
            ps.setDouble(3, montant);
            ps.setString(4, "PAID");

            ps.executeUpdate();

            // 3️⃣ mettre commande en PAYÉ
            String update = "UPDATE commandes SET statut = 'PAYE' WHERE id = ?";
            PreparedStatement psUpdate = conn.prepareStatement(update);
            psUpdate.setInt(1, commandeId);
            psUpdate.executeUpdate();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}