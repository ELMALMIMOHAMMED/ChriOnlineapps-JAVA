package models;

public class PanierItem {

    private int produitId;
    private String nom;
    private double prix;
    private int quantite;

    public PanierItem(int produitId, String nom, double prix, int quantite) {
        this.produitId = produitId;
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
    }

    public int getProduitId() { return produitId; }
    public String getNom() { return nom; }
    public double getPrix() { return prix; }
    public int getQuantite() { return quantite; }
}