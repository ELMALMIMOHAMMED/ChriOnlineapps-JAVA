package models;

import java.util.ArrayList;
import java.util.List;

public class Commande {

    private int id;
    private int userId;
    private List<LigneCommande> lignes;
    private String status;

    // 🔹 Constructeur vide (important)
    public Commande() {
        this.lignes = new ArrayList<>();
    }

    // 🔹 Constructeur principal
    public Commande(int id, int userId) {
        this.id = id;
        this.userId = userId;
        this.lignes = new ArrayList<>();
        this.status = "EN_ATTENTE";
    }

    // =========================
    // 🔹 Getters
    // =========================
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public List<LigneCommande> getLignes() {
        return lignes;
    }

    public String getStatus() {
        return status;
    }

    // =========================
    // 🔹 Setters
    // =========================
    public void setStatus(String status) {
        this.status = status;
    }

    // =========================
    // 🔹 Gestion des lignes
    // =========================
    public void ajouterLigne(LigneCommande ligne) {
        lignes.add(ligne);
    }

    public void supprimerLigne(int produitId) {
        lignes.removeIf(l -> l.getProduitId() == produitId);
    }

    // =========================
    // 🔥 Calcul automatique du total
    // =========================
    public double calculerTotal() {
        double total = 0;

        for (LigneCommande l : lignes) {
            total += l.calculerSousTotal();
        }

        return total;
    }

    // =========================
    // 🔹 JSON simple
    // =========================
    public String toJson() {

        StringBuilder json = new StringBuilder();

        json.append("{");
        json.append("\"id\":").append(id).append(",");
        json.append("\"userId\":").append(userId).append(",");
        json.append("\"status\":\"").append(status).append("\",");
        json.append("\"total\":").append(calculerTotal()).append(",");

        json.append("\"lignes\":[");

        for (int i = 0; i < lignes.size(); i++) {
            json.append(lignes.get(i).toJson());

            if (i < lignes.size() - 1) {
                json.append(",");
            }
        }

        json.append("]}");

        return json.toString();
    }

    // =========================
    // 🔹 Debug
    // =========================
    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", userId=" + userId +
                ", total=" + calculerTotal() +
                ", status='" + status + '\'' +
                ", lignes=" + lignes +
                '}';
    }
}