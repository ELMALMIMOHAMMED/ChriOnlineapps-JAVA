package models;

/**
 * Classe LignePanier - représente une ligne dans le panier
 */
public class LignePanier {

    private int quantite;
    private Produit produit;

    // Constructeur simplifié
    public LignePanier(Produit produit, int quantite) {
        this.produit = produit;
        this.quantite = quantite;
    }

    // 🔹 Calcul sous-total
    public double calculerSousTotal() {
        return produit.getPrix() * quantite;
    }

    // =====================
    // Getters & Setters
    // =====================

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    @Override
    public String toString() {
        return produit.getNom()
                + " x" + quantite
                + " = " + String.format("%.2f", calculerSousTotal()) + " DH";
    }
}