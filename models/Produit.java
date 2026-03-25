package models;

public class Produit {

    private int id;
    private String nom;
    private double prix;
    private int stock;

    // =========================
    // 🔧 CONSTRUCTEUR
    // =========================
    public Produit(int id, String nom, double prix, int stock) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.stock = stock;
    }

    // =========================
    // 📌 GETTERS
    // =========================
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public double getPrix() {
        return prix;
    }

    public int getStock() {
        return stock;
    }

    // =========================
    // 🔥 SETTERS
    // =========================
    public void setStock(int stock) {
        if (stock >= 0) {
            this.stock = stock;
        }
    }

    // =========================
    // 🔥 MÉTHODE DISPONIBILITÉ
    // =========================
    public boolean estDisponible(int quantite) {
        return quantite > 0 && stock >= quantite;
    }

    // =========================
    // 🔥 MÉTHODE UTILE (BONUS)
    // =========================
    public void diminuerStock(int quantite) {
        if (quantite > 0 && stock >= quantite) {
            stock -= quantite;
        }
    }

    // =========================
    // 🔥 DEBUG / LOG
    // =========================
    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prix=" + prix +
                ", stock=" + stock +
                '}';
    }
}