package models;

public class LigneCommande {

    private int produitId;
    private String nomProduit;
    private int quantite;
    private double prixUnitaire;

    // 🔹 Constructeur vide (IMPORTANT pour JSON)
    public LigneCommande() {}

    // 🔹 Constructeur complet
    public LigneCommande(int produitId, String nomProduit, int quantite, double prixUnitaire) {
        this.produitId = produitId;
        this.nomProduit = nomProduit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    // =========================
    // 🔹 Getters
    // =========================
    public int getProduitId() {
        return produitId;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public int getQuantite() {
        return quantite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    // =========================
    // 🔹 Setters
    // =========================
    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    // =========================
    // 🔹 Calcul métier
    // =========================
    public double calculerSousTotal() {
        return quantite * prixUnitaire;
    }

    // =========================
    // 🔹 Conversion JSON simple
    // =========================
    public String toJson() {
        return "{"
                + "\"produitId\":" + produitId + ","
                + "\"nomProduit\":\"" + nomProduit + "\","
                + "\"quantite\":" + quantite + ","
                + "\"prixUnitaire\":" + prixUnitaire
                + "}";
    }

    // =========================
    // 🔹 Debug
    // =========================
    @Override
    public String toString() {
        return "LigneCommande{" +
                "produitId=" + produitId +
                ", nomProduit='" + nomProduit + '\'' +
                ", quantite=" + quantite +
                ", prixUnitaire=" + prixUnitaire +
                ", sousTotal=" + calculerSousTotal() +
                '}';
    }
}