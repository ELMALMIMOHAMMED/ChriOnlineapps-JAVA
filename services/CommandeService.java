package services;

import java.util.ArrayList;
import java.util.List;

import cart.Cart;
import cart.CartLine;
import models.Commande;
import models.LigneCommande;
import product.Product;
import product.ProductRepository;

public class CommandeService {

    private static int compteur = 1;
    private static List<Commande> commandes = new ArrayList<>();

    // =========================
    // 🔹 créer commande simple (compatibilité)
    // =========================
    public static Commande createCommande(int userId, double total) {

        Commande cmd = new Commande(compteur++, userId);

        // ligne fictive (temporaire)
        LigneCommande ligne = new LigneCommande(
            "GLOBAL",
                "Commande globale",
                1,
                total
        );

        cmd.ajouterLigne(ligne);

        commandes.add(cmd);

        return cmd;
    }

    // =========================
    // 🔥 créer commande avec produits (PRO)
    // =========================
    public static Commande createCommandeAvecProduits(int userId, String produitsData) {

        Commande cmd = new Commande(compteur++, userId);

        try {

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

                Product produit = ProductRepository.findById(produitId).orElse(null);
                if (produit == null) {
                    continue;
                }

                LigneCommande ligne = new LigneCommande(
                        produitId,
                        produit.getName(),
                        quantite,
                        produit.getPrice()
                );

                cmd.ajouterLigne(ligne);
            }

        } catch (Exception e) {
            System.out.println("Erreur parsing produits");
        }

        commandes.add(cmd);

        return cmd;
    }

    public static Commande createCommandeFromCart(int userId, Cart cart) {

        Commande cmd = new Commande(compteur++, userId);

        for (CartLine line : cart.getLines()) {
            LigneCommande ligne = new LigneCommande(
                    line.getProduct().getId(),
                    line.getProduct().getName(),
                    line.getQuantity(),
                    line.getUnitPrice()
            );
            cmd.ajouterLigne(ligne);
        }

        commandes.add(cmd);
        return cmd;
    }

    // =========================
    // 🔹 valider commande
    // =========================
    public static boolean validerCommande(int id) {

        for (Commande c : commandes) {

            if (c.getId() == id) {

                if (!c.getStatus().equals("EN_ATTENTE")) {
                    return false;
                }

                c.setStatus("VALIDE");
                return true;
            }
        }

        return false;
    }

    // =========================
    // 🔹 annuler commande
    // =========================
    public static boolean annulerCommande(int id) {

        for (Commande c : commandes) {

            if (c.getId() == id) {

                if (c.getStatus().equals("VALIDE")) {
                    return false; // déjà validée
                }

                c.setStatus("ANNULEE");
                return true;
            }
        }

        return false;
    }

    // =========================
    // 🔹 récupérer commandes user
    // =========================
    public static List<Commande> getCommandesByUser(int userId) {

        List<Commande> result = new ArrayList<>();

        for (Commande c : commandes) {
            if (c.getUserId() == userId) {
                result.add(c);
            }
        }

        return result;
    }

    // =========================
    // 🔹 récupérer commande par ID
    // =========================
    public static Commande getCommandeById(int id) {

        for (Commande c : commandes) {
            if (c.getId() == id) {
                return c;
            }
        }

        return null;
    }
}
