package server;

import com.google.gson.*;
import common.Message;
import models.*;
import services.*;

import java.util.List;

public class RequestRouter {

    private static final PanierDBServices panierService = new PanierDBServices();
    private static final CommandeService commandeService = new CommandeService();
    private static final PaiementService paiementService = new PaiementService();
    private static final ProduitService produitService = new ProduitService();

    public static Message route(Message message) {

        try {
            if (message == null || message.getType() == null) {
                return error(message, "INVALID_REQUEST");
            }

            switch (message.getType()) {

                case "LOGIN":
                    return AuthService.login(message);

                case "REGISTER":
                    return AuthService.register(message);

                case "GET_PRODUCTS": {
                    List<Produit> produits = produitService.getAllProduits();
                    JsonArray array = new JsonArray();

                    for (Produit p : produits) {
                        JsonObject obj = new JsonObject();
                        obj.addProperty("id", p.getId());
                        obj.addProperty("nom", p.getNom());
                        obj.addProperty("prix", p.getPrix());
                        obj.addProperty("stock", p.getStock());
                        array.add(obj);
                    }
                    return success(message, array.toString());
                }

                case "ADD_TO_CART": {
                    JsonObject data = parsePayload(message);
                    if (data == null) return error(message, "EMPTY_PAYLOAD");

                    int userId = data.get("userId").getAsInt();
                    int produitId = data.get("produitId").getAsInt();
                    int quantite = data.get("quantite").getAsInt();

                    boolean ok = panierService.ajouterProduit(userId, produitId, quantite);
                    return ok ? success(message, "OK") : error(message, "ADD_FAILED");
                }

                case "GET_CART": {
                    JsonObject data = parsePayload(message);
                    if (data == null) return error(message, "EMPTY_PAYLOAD");

                    int userId = data.get("userId").getAsInt();
                    List<PanierItem> items = panierService.getPanier(userId);

                    JsonArray array = new JsonArray();
                    for (PanierItem i : items) {
                        JsonObject obj = new JsonObject();
                        obj.addProperty("produitId", i.getProduitId());
                        obj.addProperty("nom", i.getNom());
                        obj.addProperty("prix", i.getPrix());
                        obj.addProperty("quantite", i.getQuantite());
                        array.add(obj);
                    }

                    return success(message, array.toString());
                }

                default:
                    return error(message, "UNKNOWN_REQUEST");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return error(message, "SERVER_ERROR");
        }
    }

    // 🔥 FIX IMPORTANT
    private static JsonObject parsePayload(Message message) {
        try {
            String raw = message.getJson();
            if (raw == null || raw.isEmpty()) return null;
            return JsonParser.parseString(raw).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    private static Message success(Message req, String payload) {
        return new Message(req.getType() + "_RES", "", "SUCCESS", payload, "");
    }

    private static Message error(Message req, String err) {
        return new Message(req.getType() + "_RES", "", "FAIL", "", err);
    }
}