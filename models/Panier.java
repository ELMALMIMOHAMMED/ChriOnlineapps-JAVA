package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe Panier - représente le panier d'un utilisateur
 */
public class Panier {

    private int id;
    private List<LignePanier> lignes;

    public Panier(int id) {
        this.id = id;
        this.lignes = new ArrayList<>();
    }

    // =====================
    // 🔹 Ajouter produit
    // =====================
    public void ajouterProduit(Produit produit, int quantite) {

        if (produit == null || quantite <= 0) {
            return;
        }

        for (LignePanier ligne : lignes) {
            if (ligne.getProduit().getId() == produit.getId()) {
                ligne.setQuantite(ligne.getQuantite() + quantite);
                return;
            }
        }

        // ✅ constructeur corrigé (sans prixUnitaire)
        lignes.add(new LignePanier(produit, quantite));
    }

    // =====================
    // 🔹 Supprimer produit
    // =====================
    public void supprimerProduit(int produitId) {
        lignes.removeIf(ligne -> ligne.getProduit().getId() == produitId);
    }

    // =====================
    // 🔹 Calcul total
    // =====================
    public double calculerTotal() {
        double total = 0.0;

        for (LignePanier ligne : lignes) {
            total += ligne.calculerSousTotal();
        }

        return total;
    }

    // =====================
    // 🔹 Vider panier
    // =====================
    public void viderPanier() {
        lignes.clear();
    }

    // =====================
    // Getters
    // =====================

    public int getId() {
        return id;
    }

    public List<LignePanier> getLignes() {
        return lignes;
    }

    // =====================
    // 🔹 toString
    // =====================
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("=== Panier #").append(id).append(" ===\n");

        if (lignes.isEmpty()) {
            sb.append("  (panier vide)\n");
        } else {
            for (LignePanier ligne : lignes) {
                sb.append("  ").append(ligne).append("\n");
            }
        }

        sb.append("TOTAL : ")
          .append(String.format("%.2f", calculerTotal()))
          .append(" DH\n");

        return sb.toString();
    }
}