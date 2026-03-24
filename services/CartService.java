package services;

import java.util.Optional;
import java.sql.Connection;
import java.sql.SQLException;

import cart.Cart;
import cart.CartLine;
import common.BaseDonnees;
import dao.CartDAO;
import dao.CommandeDAO;
import models.Commande;
import models.LigneCommande;
import product.Product;
import product.ProductRepository;

public class CartService {

    public static Cart getCart(int userId) {
        return CartDAO.getCart(userId);
    }

    public static String addProduct(int userId, String productId, int quantity) {
        if (quantity <= 0) {
            return "ERROR: Quantity must be greater than 0.";
        }

        Optional<Product> optionalProduct = ProductRepository.findById(productId);
        if (!optionalProduct.isPresent()) {
            return "ERROR: Product not found.";
        }

        Product product = optionalProduct.get();
        Cart cart = getCart(userId);
        int orderedQuantity = CommandeDAO.getOrderedQuantity(productId);

        int currentQuantity = 0;
        for (CartLine line : cart.getLines()) {
            if (line.getProduct().getId().equals(productId)) {
                currentQuantity = line.getQuantity();
                break;
            }
        }

        int remainingCapacity = product.getStock() - orderedQuantity;
        if (remainingCapacity <= 0 || currentQuantity + quantity > remainingCapacity) {
            return "ERROR: Ordered quantity would exceed stock. Remaining capacity: " + Math.max(0, remainingCapacity);
        }

        cart.addProduct(product, quantity);
        CartDAO.addProduct(userId, product, quantity);
        return "SUCCESS: " + product.getName() + " x" + quantity + " added to cart.";
    }

    public static String removeProduct(int userId, String productId) {
        CartDAO.removeProduct(userId, productId);
        return "SUCCESS: Product removed from cart.";
    }

    public static String clearCart(int userId) {
        CartDAO.clearCart(userId);
        return "SUCCESS: Cart cleared.";
    }

    public static Commande checkout(int userId) {
        try (Connection connection = BaseDonnees.getConnection()) {
            connection.setAutoCommit(false);

            Cart cart = CartDAO.getCart(connection, userId);
            if (cart.isEmpty()) {
                connection.rollback();
                return null;
            }

            for (CartLine line : cart.getLines()) {
                Product product = ProductRepository.findById(connection, line.getProduct().getId()).orElse(null);
                int orderedQuantity = CommandeDAO.getOrderedQuantity(connection, line.getProduct().getId());
                if (product == null || orderedQuantity + line.getQuantity() > product.getStock()) {
                    connection.rollback();
                    return null;
                }
            }

            Commande commande = CommandeDAO.createCommande(connection, userId, buildOrderLines(cart));
            CartDAO.clearCart(connection, userId);
            connection.commit();
            return commande;
        } catch (SQLException e) {
            throw new RuntimeException("Checkout failed", e);
        }
    }

    private static java.util.List<LigneCommande> buildOrderLines(Cart cart) {
        java.util.List<LigneCommande> lines = new java.util.ArrayList<>();

        for (CartLine line : cart.getLines()) {
            lines.add(new LigneCommande(
                    line.getProduct().getId(),
                    line.getProduct().getName(),
                    line.getQuantity(),
                    line.getUnitPrice()
            ));
        }

        return lines;
    }
}