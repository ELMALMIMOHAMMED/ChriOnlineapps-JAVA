package server;

import common.Message;
import common.JsonUtil;

import models.Commande;
import models.Paiement;
import models.Produit;

import services.AuthService;
import services.CommandeService;
import services.PaiementService;
import services.PanierService;

import java.util.List;
import java.util.Map;

public class RequestRouter {

    private static PaiementService paiementService = new PaiementService();
    private static PanierService panierService = new PanierService();

    public static Message route(Message message) {

        if (message == null) {
            return new Message("ERROR", "0", "ERROR", "", "NULL_MESSAGE");
        }

        try {

            String rawType = message.getType();
            if (rawType == null || rawType.trim().isEmpty()) {
                return new Message("UNKNOWN", message.getRequestId(), "ERROR", "", "EMPTY_TYPE");
            }

            String type = rawType.trim().toUpperCase();

            switch (type) {

                // =========================
                // 🔹 TEST
                // =========================
                case "PING":
                    return new Message(type, message.getRequestId(), "SUCCESS", "PONG", "");

                // =========================
                // 🔹 AUTH
                // =========================
                case "LOGIN":
                    return AuthService.login(message);

                case "REGISTER":
                    return AuthService.register(message);

                // =========================
                // 🔹 PANIER
                // =========================
                case "ADD_TO_CART":

                    try {
                        Map<String, String> data = JsonUtil.toMap(message.getPayload());

                        int userId = Integer.parseInt(data.get("userId"));
                        int produitId = Integer.parseInt(data.get("produitId"));
                        int quantite = Integer.parseInt(data.get("quantite"));

                        // 🔥 simulation produit (à remplacer plus tard par DB)
                        Produit produit = new Produit(produitId, "Produit" + produitId, 100.0, 10);

                        String result = panierService.ajouterProduitPanier(userId, produit, quantite);

                        return success(type, message, result);

                    } catch (Exception e) {
                        return error(type, message, "INVALID_JSON");
                    }

                case "GET_CART":

                    try {
                        int userId = Integer.parseInt(message.getPayload());
                        String panier = panierService.afficherPanier(userId);
                        return success(type, message, panier);

                    } catch (Exception e) {
                        return error(type, message, "INVALID_PAYLOAD");
                    }

                case "REMOVE_FROM_CART":

                    try {
                        Map<String, String> data = JsonUtil.toMap(message.getPayload());

                        int userId = Integer.parseInt(data.get("userId"));
                        int produitId = Integer.parseInt(data.get("produitId"));

                        String result = panierService.retirerProduitPanier(userId, produitId);

                        return success(type, message, result);

                    } catch (Exception e) {
                        return error(type, message, "INVALID_JSON");
                    }

                // =========================
                // 🔹 COMMANDE
                // =========================
                case "CREATE_COMMANDE":

                    try {
                        Map<String, String> data = JsonUtil.toMap(message.getPayload());

                        int userId = Integer.parseInt(data.get("userId"));
                        double total = Double.parseDouble(data.get("total"));

                        Commande cmd = CommandeService.createCommande(userId, total);

                        return success(type, message, cmd.toJson());

                    } catch (Exception e) {
                        return error(type, message, "INVALID_JSON");
                    }

                case "GET_COMMANDES":

                    try {
                        int userId = Integer.parseInt(message.getPayload());

                        List<Commande> commandes = CommandeService.getCommandesByUser(userId);

                        StringBuilder json = new StringBuilder("[");
                        for (int i = 0; i < commandes.size(); i++) {
                            json.append(commandes.get(i).toJson());
                            if (i < commandes.size() - 1) json.append(",");
                        }
                        json.append("]");

                        return success(type, message, json.toString());

                    } catch (Exception e) {
                        return error(type, message, "INVALID_PAYLOAD");
                    }

                // =========================
                // 🔥 PAIEMENT
                // =========================
                case "PAYMENT":

                    try {
                        Map<String, String> data = JsonUtil.toMap(message.getPayload());

                        int userId = Integer.parseInt(data.get("userId"));
                        int commandeId = Integer.parseInt(data.get("commandeId"));
                        double montant = Double.parseDouble(data.get("montant"));

                        Paiement paiement = paiementService.processPayment(userId, commandeId, montant);

                        return paiement != null
                                ? success(type, message, paiement.toString())
                                : error(type, message, "PAYMENT_FAILED");

                    } catch (Exception e) {
                        return error(type, message, "INVALID_JSON");
                    }

                // =========================
                // 🔁 REMBOURSEMENT
                // =========================
                case "REFUND":

                    try {
                        int paiementId = Integer.parseInt(message.getPayload());

                        Paiement p = paiementService.getPaiementById(paiementId);

                        boolean ok = paiementService.rembourserPaiement(p);

                        return new Message(
                                type,
                                message.getRequestId(),
                                ok ? "SUCCESS" : "ERROR",
                                ok ? "REFUNDED" : "",
                                ok ? "" : "REFUND_FAILED"
                        );

                    } catch (Exception e) {
                        return error(type, message, "INVALID_PAYLOAD");
                    }

                // =========================
                // 🔹 DEFAULT
                // =========================
                default:
                    return error(type, message, "UNKNOWN_REQUEST");
            }

        } catch (Exception e) {
            return error("SERVER_ERROR", message, "SERVER_ERROR");
        }
    }

    private static Message error(String type, Message msg, String code) {
        return new Message(type, msg.getRequestId(), "ERROR", "", code);
    }

    private static Message success(String type, Message msg, String payload) {
        return new Message(type, msg.getRequestId(), "SUCCESS", payload, "");
    }
}
