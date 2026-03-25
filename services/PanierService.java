package services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import common.Message;
import models.PanierItem;
import network.Client;

import java.util.ArrayList;
import java.util.List;

public class PanierService {

    private final Client client;

    public PanierService() {
        client = new Client("127.0.0.1", 6000);
    }

    // =========================
    // GET PANIER (FIX PRINCIPAL)
    // =========================
    public List<PanierItem> getPanier() {

        List<PanierItem> list = new ArrayList<>();

        try {
            int userId = SessionService.getCurrentUserId();

            if (userId <= 0) {
                System.out.println("❌ utilisateur non connecté !");
                return list;
            }

            JsonObject data = new JsonObject();
            data.addProperty("userId", userId);

            Message request = new Message(
                    "GET_CART",
                    "REQ_GET_CART",
                    "",
                    data.toString(),
                    ""
            );

            Message response = client.sendRequest(request);

            if (response == null || !"SUCCESS".equals(response.getStatus())) {
                return list;
            }

            JsonArray array = JsonParser.parseString(response.getPayload()).getAsJsonArray();

            for (int i = 0; i < array.size(); i++) {

                JsonObject obj = array.get(i).getAsJsonObject();

                list.add(new PanierItem(
                        obj.get("produitId").getAsInt(),
                        obj.get("nom").getAsString(),
                        obj.get("prix").getAsDouble(),
                        obj.get("quantite").getAsInt()
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // UPDATE
    // =========================
    public boolean updateQuantite(int produitId, int quantite) {

        try {
            int userId = SessionService.getCurrentUserId();

            JsonObject data = new JsonObject();
            data.addProperty("userId", userId);
            data.addProperty("produitId", produitId);
            data.addProperty("quantite", quantite);

            Message request = new Message(
                    "UPDATE_CART",
                    "REQ_UPDATE",
                    "",
                    data.toString(),
                    ""
            );

            Message response = client.sendRequest(request);

            return response != null && "SUCCESS".equals(response.getStatus());

        } catch (Exception e) {
            return false;
        }
    }

    // =========================
    // DELETE
    // =========================
    public boolean supprimerProduit(int produitId) {

        try {
            int userId = SessionService.getCurrentUserId();

            JsonObject data = new JsonObject();
            data.addProperty("userId", userId);
            data.addProperty("produitId", produitId);

            Message request = new Message(
                    "REMOVE_FROM_CART",
                    "REQ_REMOVE",
                    "",
                    data.toString(),
                    ""
            );

            Message response = client.sendRequest(request);

            return response != null && "SUCCESS".equals(response.getStatus());

        } catch (Exception e) {
            return false;
        }
    }

    // =========================
    // ADD
    // =========================
    public boolean ajouterProduit(int produitId, int quantite) {

        try {
            int userId = SessionService.getCurrentUserId();

            JsonObject data = new JsonObject();
            data.addProperty("userId", userId);
            data.addProperty("produitId", produitId);
            data.addProperty("quantite", quantite);

            Message request = new Message(
                    "ADD_TO_CART",
                    "REQ_ADD",
                    "",
                    data.toString(),
                    ""
            );

            Message response = client.sendRequest(request);

            return response != null && "SUCCESS".equals(response.getStatus());

        } catch (Exception e) {
            return false;
        }
    }
}