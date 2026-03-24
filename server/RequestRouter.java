package server;

import common.Message;
import common.JsonUtil;
import services.AuthService;
import services.CartService;
import services.CommandeService;
import services.ProductService;
import models.Commande;

import java.util.List;
import java.util.Map;

public class RequestRouter {

    public static Message route(Message message) {

        // 🔒 Vérification message null
        if (message == null) {
            return new Message("ERROR", "0", "ERROR", "", "NULL_MESSAGE");
        }

        try {

            // 🔥 sécurisation du type
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
                    return new Message(
                            type,
                            message.getRequestId(),
                            "SUCCESS",
                            "PONG",
                            ""
                    );

                // =========================
                // 🔹 AUTH
                // =========================
                case "LOGIN":
                    return AuthService.login(message);

                case "REGISTER":
                    return AuthService.register(message);

                // =========================
                // 🔹 PRODUCTS
                // =========================
                case "PRODUCT_LIST":
                    return ProductService.list(message);

                case "PRODUCT_DETAILS":
                    return ProductService.details(message);

                case "PRODUCT_ORDER_STATUS":
                    return ProductService.productOrderStatus(message);

                // =========================
                // 🔹 CART
                // =========================
                case "CART_ADD":

                    try {
                        Map<String, String> data = JsonUtil.toMap(message.getPayload());
                        if (!data.containsKey("userId") || !data.containsKey("productId") || !data.containsKey("quantity")) {
                            return error(type, message, "MISSING_FIELDS");
                        }

                        int userId = Integer.parseInt(data.get("userId"));
                        String productId = data.get("productId");
                        int quantity = Integer.parseInt(data.get("quantity"));

                        String result;
                        try {
                            result = CartService.addProduct(userId, productId, quantity);
                        } catch (RuntimeException e) {
                            return error(type, message, "DATABASE_ERROR");
                        }
                        boolean ok = result.startsWith("SUCCESS");
                        return new Message(type, message.getRequestId(), ok ? "SUCCESS" : "ERROR", result, ok ? "" : "CART_ADD_FAILED");
                    } catch (Exception e) {
                        return error(type, message, "INVALID_PAYLOAD");
                    }

                case "CART_VIEW":

                    try {
                        String payload = message.getPayload();
                        if (payload == null || payload.trim().isEmpty()) {
                            return error(type, message, "EMPTY_PAYLOAD");
                        }

                        int userId = Integer.parseInt(payload.trim());
                        try {
                            return success(type, message, CartService.getCart(userId).toString());
                        } catch (RuntimeException e) {
                            return error(type, message, "DATABASE_ERROR");
                        }
                    } catch (Exception e) {
                        return error(type, message, "INVALID_PAYLOAD");
                    }

                case "CART_REMOVE":

                    try {
                        Map<String, String> data = JsonUtil.toMap(message.getPayload());
                        if (!data.containsKey("userId") || !data.containsKey("productId")) {
                            return error(type, message, "MISSING_FIELDS");
                        }

                        int userId = Integer.parseInt(data.get("userId"));
                        String productId = data.get("productId");

                        try {
                            return success(type, message, CartService.removeProduct(userId, productId));
                        } catch (RuntimeException e) {
                            return error(type, message, "DATABASE_ERROR");
                        }
                    } catch (Exception e) {
                        return error(type, message, "INVALID_PAYLOAD");
                    }

                case "CART_CLEAR":

                    try {
                        String payload = message.getPayload();
                        if (payload == null || payload.trim().isEmpty()) {
                            return error(type, message, "EMPTY_PAYLOAD");
                        }

                        int userId = Integer.parseInt(payload.trim());
                        try {
                            return success(type, message, CartService.clearCart(userId));
                        } catch (RuntimeException e) {
                            return error(type, message, "DATABASE_ERROR");
                        }
                    } catch (Exception e) {
                        return error(type, message, "INVALID_PAYLOAD");
                    }

                case "CART_CHECKOUT":

                    try {
                        String payload = message.getPayload();
                        if (payload == null || payload.trim().isEmpty()) {
                            return error(type, message, "EMPTY_PAYLOAD");
                        }

                        int userId = Integer.parseInt(payload.trim());
                        Commande cmd;
                        try {
                            cmd = CartService.checkout(userId);
                        } catch (RuntimeException e) {
                            return error(type, message, "DATABASE_ERROR");
                        }
                        if (cmd == null) {
                            return error(type, message, "CHECKOUT_FAILED");
                        }
                        return success(type, message, cmd.toJson());
                    } catch (Exception e) {
                        return error(type, message, "INVALID_PAYLOAD");
                    }

                // =========================
                // 🔹 CREATE COMMANDE
                // =========================
                case "CREATE_COMMANDE":

                    try {
                        String payload = message.getPayload();

                        if (payload == null || payload.trim().isEmpty()) {
                            return error(type, message, "EMPTY_PAYLOAD");
                        }

                        Map<String, String> data = JsonUtil.toMap(payload);

                        if (data == null || data.isEmpty()) {
                            return error(type, message, "INVALID_JSON");
                        }

                        // 🔥 VERSION AVEC PRODUITS
                        if (data.containsKey("produits")) {

                            if (!data.containsKey("userId")) {
                                return error(type, message, "MISSING_USERID");
                            }

                            int userId = Integer.parseInt(data.get("userId"));
                            String produits = data.get("produits");

                            Commande cmd;
                            try {
                                cmd = CommandeService.createCommandeAvecProduits(userId, produits);
                            } catch (RuntimeException e) {
                                return error(type, message, "DATABASE_ERROR");
                            }

                            if (cmd == null) {
                                return error(type, message, "INSUFFICIENT_STOCK");
                            }

                            return success(type, message, cmd.toJson());
                        }

                        // 🔥 VERSION SIMPLE
                        if (!data.containsKey("userId") || !data.containsKey("total")) {
                            return error(type, message, "MISSING_FIELDS");
                        }

                        int userId = Integer.parseInt(data.get("userId"));
                        double total = Double.parseDouble(data.get("total"));

                        Commande cmd;
                        try {
                            cmd = CommandeService.createCommande(userId, total);
                        } catch (RuntimeException e) {
                            return error(type, message, "DATABASE_ERROR");
                        }

                        if (cmd == null) {
                            return error(type, message, "CREATE_FAILED");
                        }

                        return success(type, message, cmd.toJson());

                    } catch (Exception e) {
                        return error(type, message, "INVALID_JSON");
                    }

                // =========================
                // 🔹 VALIDER COMMANDE
                // =========================
                case "VALIDER_COMMANDE":

                    try {
                        String payload = message.getPayload();

                        if (payload == null || payload.trim().isEmpty()) {
                            return error(type, message, "EMPTY_PAYLOAD");
                        }

                        int cmdId = Integer.parseInt(payload);

                        boolean ok;
                        try {
                            ok = CommandeService.validerCommande(cmdId);
                        } catch (RuntimeException e) {
                            return error(type, message, "DATABASE_ERROR");
                        }

                        return new Message(
                                type,
                                message.getRequestId(),
                                ok ? "SUCCESS" : "ERROR",
                                ok ? "COMMANDE_VALIDEE" : "",
                                ok ? "" : "NOT_FOUND"
                        );

                    } catch (Exception e) {
                        return error(type, message, "INVALID_PAYLOAD");
                    }

                // =========================
                // 🔹 GET COMMANDES
                // =========================
                case "GET_COMMANDES":

                    try {
                        String payload = message.getPayload();

                        if (payload == null || payload.trim().isEmpty()) {
                            return error(type, message, "EMPTY_PAYLOAD");
                        }

                        int userId = Integer.parseInt(payload);

                        List<Commande> commandes;
                        try {
                            commandes = CommandeService.getCommandesByUser(userId);
                        } catch (RuntimeException e) {
                            return error(type, message, "DATABASE_ERROR");
                        }

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
                // 🔹 ANNULER COMMANDE
                // =========================
                case "ANNULER_COMMANDE":

                    try {
                        String payload = message.getPayload();

                        if (payload == null || payload.trim().isEmpty()) {
                            return error(type, message, "EMPTY_PAYLOAD");
                        }

                        int cmdId = Integer.parseInt(payload);

                        boolean ok;
                        try {
                            ok = CommandeService.annulerCommande(cmdId);
                        } catch (RuntimeException e) {
                            return error(type, message, "DATABASE_ERROR");
                        }

                        return new Message(
                                type,
                                message.getRequestId(),
                                ok ? "SUCCESS" : "ERROR",
                                ok ? "COMMANDE_ANNULEE" : "",
                                ok ? "" : "IMPOSSIBLE"
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

    // =========================
    // 🔧 HELPERS
    // =========================
    private static Message error(String type, Message msg, String code) {
        return new Message(type, msg.getRequestId(), "ERROR", "", code);
    }

    private static Message success(String type, Message msg, String payload) {
        return new Message(type, msg.getRequestId(), "SUCCESS", payload, "");
    }
}
