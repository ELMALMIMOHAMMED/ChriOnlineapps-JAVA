package services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import cart.Cart;
import cart.CartLine;
import models.Commande;
import product.Product;
import product.ProductRepository;

public class CartService {

    private static final Map<Integer, Cart> CARTS = new HashMap<>();

    public static Cart getCart(int userId) {
        return CARTS.computeIfAbsent(userId, Cart::new);
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

        int currentQuantity = 0;
        for (CartLine line : cart.getLines()) {
            if (line.getProduct().getId().equals(productId)) {
                currentQuantity = line.getQuantity();
                break;
            }
        }

        if (!product.isAvailable(currentQuantity + quantity)) {
            return "ERROR: Stock insuffisant. Stock disponible : " + product.getStock();
        }

        cart.addProduct(product, quantity);
        return "SUCCESS: " + product.getName() + " x" + quantity + " added to cart.";
    }

    public static String removeProduct(int userId, String productId) {
        Cart cart = getCart(userId);
        cart.removeProduct(productId);
        return "SUCCESS: Product removed from cart.";
    }

    public static String clearCart(int userId) {
        Cart cart = getCart(userId);
        cart.clear();
        return "SUCCESS: Cart cleared.";
    }

    public static Commande checkout(int userId) {
        Cart cart = getCart(userId);
        if (cart.isEmpty()) {
            return null;
        }

        for (CartLine line : cart.getLines()) {
            Product product = ProductRepository.findById(line.getProduct().getId()).orElse(null);
            if (product == null || !product.isAvailable(line.getQuantity())) {
                return null;
            }
        }

        for (CartLine line : cart.getLines()) {
            Product product = ProductRepository.findById(line.getProduct().getId()).orElse(null);
            if (product != null) {
                product.decreaseStock(line.getQuantity());
            }
        }

        Commande commande = CommandeService.createCommandeFromCart(userId, cart);
        cart.clear();
        return commande;
    }
}