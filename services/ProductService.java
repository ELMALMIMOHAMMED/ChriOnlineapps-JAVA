package services;

import common.Message;
import common.JsonUtil;
import dao.CommandeDAO;
import product.Product;
import product.ProductRepository;

import java.util.Base64;
import java.util.List;
import java.io.Serializable;

public class ProductService {

    /**
     * Return a list of all products.  Response payload will contain JSON array.
     */
    public static Message list(Message request) {
        try {
            List<Product> products = ProductRepository.getAll();
            byte[] data = JsonUtil.toBinary(products);
            String payload = Base64.getEncoder().encodeToString(data);
            return new Message(
                    "PRODUCT_LIST",
                    request.getRequestId(),
                    "SUCCESS",
                    payload,
                    ""
            );
        } catch (RuntimeException e) {
            return new Message(
                "PRODUCT_LIST",
                request.getRequestId(),
                "ERROR",
                "",
                "DATABASE_ERROR"
            );
        } catch (Exception e) {
            return new Message(
                    "PRODUCT_LIST",
                    request.getRequestId(),
                    "ERROR",
                    "",
                    "SERIALIZATION_ERROR"
            );
        }
    }

    /**
     * Return details for a single product.  Expects request.payload to contain the product id.
     */
    public static Message details(Message request) {
        try {
            String id = request.getPayload();
            return ProductRepository.findById(id)
                    .map(p -> {
                        try {
                            byte[] data = JsonUtil.toBinary(p);
                            String payload = Base64.getEncoder().encodeToString(data);
                            return new Message("PRODUCT_DETAILS", request.getRequestId(), "SUCCESS", payload, "");
                        } catch (Exception e) {
                            return new Message("PRODUCT_DETAILS", request.getRequestId(), "ERROR", "", "SERIALIZATION_ERROR");
                        }
                    })
                    .orElseGet(() -> new Message("PRODUCT_DETAILS", request.getRequestId(), "ERROR", "", "NOT_FOUND"));
        } catch (RuntimeException e) {
            return new Message("PRODUCT_DETAILS", request.getRequestId(), "ERROR", "", "DATABASE_ERROR");
        }
    }

    /**
     * Update stock for a product.  Payload should be JSON with id and stock fields.
     */
    public static Message updateStock(Message request) {
        try {
            UpdateStockRequest usr;

            try {
                byte[] data = Base64.getDecoder().decode(request.getPayload());
                usr = JsonUtil.fromBinary(data, UpdateStockRequest.class);
            } catch (Exception ignored) {
                usr = null;
            }

            if (usr == null) {
                java.util.Map<String, String> values = JsonUtil.toMap(request.getPayload());
                if (values.isEmpty() || !values.containsKey("id") || !values.containsKey("stock")) {
                    return new Message("STOCK_UPDATE", request.getRequestId(), "ERROR", "", "INVALID_PAYLOAD");
                }

                usr = new UpdateStockRequest();
                usr.id = values.get("id");
                usr.stock = Integer.parseInt(values.get("stock"));
            }

            boolean ok = ProductRepository.updateStock(usr.id, usr.stock);
            if (ok) {
                return new Message("STOCK_UPDATE", request.getRequestId(), "SUCCESS", "", "");
            } else {
                return new Message("STOCK_UPDATE", request.getRequestId(), "ERROR", "", "NOT_FOUND");
            }
        } catch (RuntimeException e) {
            return new Message("STOCK_UPDATE", request.getRequestId(), "ERROR", "", "DATABASE_ERROR");
        } catch (Exception e) {
            return new Message("STOCK_UPDATE", request.getRequestId(), "ERROR", "", "DESERIALIZATION_ERROR");
        }
    }

    public static Message productOrderStatus(Message request) {
        try {
            String payload = request.getPayload() == null ? "" : request.getPayload().trim();
            if (payload.isEmpty()) {
                return new Message("PRODUCT_ORDER_STATUS", request.getRequestId(), "ERROR", "", "INVALID_PAYLOAD");
            }

            int userId = Integer.parseInt(payload);
            List<Product> products = ProductRepository.getAll();
            java.util.Map<String, Integer> userOrdered = CommandeDAO.getOrderedQuantitiesByUser(userId);
            java.util.Map<String, Integer> totalOrdered = CommandeDAO.getOrderedQuantities();

            StringBuilder payloadBuilder = new StringBuilder();
            payloadBuilder.append("=== Product Order Status for user #").append(userId).append(" ===\n");

            for (Product product : products) {
                int orderedByUser = userOrdered.getOrDefault(product.getId(), 0);
                int orderedTotal = totalOrdered.getOrDefault(product.getId(), 0);
                int remaining = Math.max(0, product.getStock() - orderedTotal);

                payloadBuilder.append(product.getId())
                        .append(" - ")
                        .append(product.getName())
                        .append(" | Stock: ")
                        .append(product.getStock())
                        .append(" | Ordered by you: ")
                        .append(orderedByUser)
                        .append(" | Total ordered: ")
                        .append(orderedTotal)
                        .append(" | Remaining capacity: ")
                        .append(remaining)
                        .append("\n");
            }

            return new Message("PRODUCT_ORDER_STATUS", request.getRequestId(), "SUCCESS", payloadBuilder.toString(), "");
        } catch (NumberFormatException e) {
            return new Message("PRODUCT_ORDER_STATUS", request.getRequestId(), "ERROR", "", "INVALID_PAYLOAD");
        } catch (RuntimeException e) {
            return new Message("PRODUCT_ORDER_STATUS", request.getRequestId(), "ERROR", "", "DATABASE_ERROR");
        }
    }

    public static Message createProduct(Message request) {
        try {
            Product product = parseProduct(request.getPayload());
            boolean created = ProductRepository.create(product);
            return new Message("CREATE_PRODUCT", request.getRequestId(), created ? "SUCCESS" : "ERROR", created ? "PRODUCT_CREATED" : "", created ? "" : "CREATE_FAILED");
        } catch (IllegalArgumentException e) {
            return new Message("CREATE_PRODUCT", request.getRequestId(), "ERROR", "", "INVALID_PAYLOAD");
        } catch (RuntimeException e) {
            return new Message("CREATE_PRODUCT", request.getRequestId(), "ERROR", "", "DATABASE_ERROR");
        }
    }

    public static Message updateProduct(Message request) {
        try {
            Product product = parseProduct(request.getPayload());
            boolean updated = ProductRepository.update(product);
            return new Message("UPDATE_PRODUCT", request.getRequestId(), updated ? "SUCCESS" : "ERROR", updated ? "PRODUCT_UPDATED" : "", updated ? "" : "NOT_FOUND");
        } catch (IllegalArgumentException e) {
            return new Message("UPDATE_PRODUCT", request.getRequestId(), "ERROR", "", "INVALID_PAYLOAD");
        } catch (RuntimeException e) {
            return new Message("UPDATE_PRODUCT", request.getRequestId(), "ERROR", "", "DATABASE_ERROR");
        }
    }

    public static Message deleteProduct(Message request) {
        try {
            String id = request.getPayload() == null ? "" : request.getPayload().trim();
            if (id.isEmpty()) {
                return new Message("DELETE_PRODUCT", request.getRequestId(), "ERROR", "", "INVALID_PAYLOAD");
            }

            boolean deleted = ProductRepository.delete(id);
            return new Message("DELETE_PRODUCT", request.getRequestId(), deleted ? "SUCCESS" : "ERROR", deleted ? "PRODUCT_DELETED" : "", deleted ? "" : "NOT_FOUND");
        } catch (RuntimeException e) {
            return new Message("DELETE_PRODUCT", request.getRequestId(), "ERROR", "", "DATABASE_ERROR");
        }
    }

    private static Product parseProduct(String payload) {
        java.util.Map<String, String> values = JsonUtil.toMap(payload);
        if (!values.containsKey("id") || !values.containsKey("name") || !values.containsKey("description")
                || !values.containsKey("price") || !values.containsKey("stock")) {
            throw new IllegalArgumentException("Missing product fields");
        }

        try {
            return new Product(
                    values.get("id"),
                    values.get("name"),
                    values.get("description"),
                    Double.parseDouble(values.get("price")),
                    Integer.parseInt(values.get("stock"))
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric value", e);
        }
    }

    // helper class for parsing stock update payload
    private static class UpdateStockRequest implements Serializable {
        public String id;
        public int stock;
    }
}
