package services;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;

import cart.Cart;
import cart.CartLine;
import common.BaseDonnees;
import dao.CommandeDAO;
import models.Commande;
import models.LigneCommande;
import product.Product;
import product.ProductRepository;

public class CommandeService {

    // =========================
    // 🔹 créer commande simple (compatibilité)
    // =========================
    public static Commande createCommande(int userId, double total) {
        List<LigneCommande> lignes = new ArrayList<>();
        lignes.add(new LigneCommande("GLOBAL", "Commande globale", 1, total));
        return CommandeDAO.createCommande(userId, lignes);
    }

    // =========================
    // 🔥 créer commande avec produits (PRO)
    // =========================
    public static Commande createCommandeAvecProduits(int userId, String produitsData) {
        try (Connection connection = BaseDonnees.getConnection()) {
            connection.setAutoCommit(false);

            List<LigneCommande> lignes = new ArrayList<>();
            String[] produits = produitsData.split(";");

            for (String p : produits) {

                if (p == null || p.trim().isEmpty()) {
                    continue;
                }

                String[] parts = p.split(":");

                if (parts.length != 2) {
                    continue;
                }

                String produitId = parts[0].trim();
                int quantite = Integer.parseInt(parts[1]);

                Product produit = ProductRepository.findById(connection, produitId).orElse(null);
                if (produit == null) {
                    continue;
                }

                int orderedQuantity = CommandeDAO.getOrderedQuantity(connection, produitId);
                if (orderedQuantity + quantite > produit.getStock()) {
                    connection.rollback();
                    return null;
                }

                lignes.add(new LigneCommande(
                        produitId,
                        produit.getName(),
                        quantite,
                        produit.getPrice()
                ));
            }

            Commande commande = CommandeDAO.createCommande(connection, userId, lignes);
            connection.commit();
            return commande;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to create commande with products", e);
        }
    }

    public static Commande createCommandeFromCart(int userId, Cart cart) {
        List<LigneCommande> lignes = new ArrayList<>();

        for (CartLine line : cart.getLines()) {
            lignes.add(new LigneCommande(
                    line.getProduct().getId(),
                    line.getProduct().getName(),
                    line.getQuantity(),
                    line.getUnitPrice()
            ));
        }

        return CommandeDAO.createCommande(userId, lignes);
    }

    public static Commande createCommandeFromCart(Connection connection, int userId, Cart cart) {
        List<LigneCommande> lignes = new ArrayList<>();

        for (CartLine line : cart.getLines()) {
            lignes.add(new LigneCommande(
                    line.getProduct().getId(),
                    line.getProduct().getName(),
                    line.getQuantity(),
                    line.getUnitPrice()
            ));
        }

        return CommandeDAO.createCommande(connection, userId, lignes);
    }

    // =========================
    // 🔹 valider commande
    // =========================
    public static boolean validerCommande(int id) {
        Commande commande = CommandeDAO.getCommandeById(id);
        if (commande == null || !"EN_ATTENTE".equals(commande.getStatus())) {
            return false;
        }

        return CommandeDAO.updateStatus(id, "VALIDE");
    }

    // =========================
    // 🔹 annuler commande
    // =========================
    public static boolean annulerCommande(int id) {
        Commande commande = CommandeDAO.getCommandeById(id);
        if (commande == null || "VALIDE".equals(commande.getStatus())) {
            return false;
        }

        return CommandeDAO.updateStatus(id, "ANNULEE");
    }

    // =========================
    // 🔹 récupérer commandes user
    // =========================
    public static List<Commande> getCommandesByUser(int userId) {
        return CommandeDAO.getCommandesByUser(userId);
    }

    // =========================
    // 🔹 récupérer commande par ID
    // =========================
    public static Commande getCommandeById(int id) {
        return CommandeDAO.getCommandeById(id);
    }
}
