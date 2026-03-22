package services;

import common.Message;
import common.JsonUtil;
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
        List<Product> products = ProductRepository.getAll();
        try {
            byte[] data = JsonUtil.toBinary(products);
            String payload = Base64.getEncoder().encodeToString(data);
            return new Message(
                    "PRODUCT_LIST",
                    request.getRequestId(),
                    "SUCCESS",
                    payload,
                    ""
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
    }

    /**
     * Update stock for a product.  Payload should be JSON with id and stock fields.
     */
    public static Message updateStock(Message request) {
        try {
            byte[] data = Base64.getDecoder().decode(request.getPayload());
            UpdateStockRequest usr = JsonUtil.fromBinary(data, UpdateStockRequest.class);
            boolean ok = ProductRepository.updateStock(usr.id, usr.stock);
            if (ok) {
                return new Message("STOCK_UPDATE", request.getRequestId(), "SUCCESS", "", "");
            } else {
                return new Message("STOCK_UPDATE", request.getRequestId(), "ERROR", "", "NOT_FOUND");
            }
        } catch (Exception e) {
            return new Message("STOCK_UPDATE", request.getRequestId(), "ERROR", "", "DESERIALIZATION_ERROR");
        }
    }

    // helper class for parsing stock update payload
    private static class UpdateStockRequest implements Serializable {
        public String id;
        public int stock;
    }
}
