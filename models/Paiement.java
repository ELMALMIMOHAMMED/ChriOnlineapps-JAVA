package models;

import java.time.LocalDateTime;

public class Paiement {

    private int idPaiement;
    private int userId;
    private int idCommande;
    private double montant;
    private LocalDateTime datePaiement;
    private StatutPaiement statut;

    // 🔥 constructeur propre
    public Paiement(int idPaiement, int userId, int idCommande, double montant) {
        this.idPaiement = idPaiement;
        this.userId = userId;
        this.idCommande = idCommande;
        this.montant = montant;
        this.datePaiement = LocalDateTime.now();
        this.statut = StatutPaiement.PENDING;
    }

    public int getIdPaiement() {
        return idPaiement;
    }

    public int getUserId() {
        return userId;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public double getMontant() {
        return montant;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public StatutPaiement getStatut() {
        return statut;
    }

    public void setStatut(StatutPaiement statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Paiement{" +
                "idPaiement=" + idPaiement +
                ", userId=" + userId +
                ", idCommande=" + idCommande +
                ", montant=" + montant +
                ", statut=" + statut +
                ", datePaiement=" + datePaiement +
                '}';
    }
}