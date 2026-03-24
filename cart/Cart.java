package cart;

import java.util.ArrayList;
import java.util.List;

import product.Product;

public class Cart {

    private final int userId;
    private double total;
    private final List<CartLine> lines;

    public Cart(int userId) {
        this.userId = userId;
        this.total = 0.0;
        this.lines = new ArrayList<>();
    }

    public void addProduct(Product product, int quantity) {
        for (CartLine line : lines) {
            if (line.getProduct().getId().equals(product.getId())) {
                line.setQuantity(line.getQuantity() + quantity);
                calculateTotal();
                return;
            }
        }

        lines.add(new CartLine(product, quantity));
        calculateTotal();
    }

    public void removeProduct(String productId) {
        lines.removeIf(line -> line.getProduct().getId().equals(productId));
        calculateTotal();
    }

    public double calculateTotal() {
        total = 0.0;
        for (CartLine line : lines) {
            total += line.calculateSubtotal();
        }
        return total;
    }

    public void clear() {
        lines.clear();
        total = 0.0;
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public int getUserId() {
        return userId;
    }

    public double getTotal() {
        return total;
    }

    public List<CartLine> getLines() {
        return lines;
    }

    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"userId\":").append(userId).append(",");
        json.append("\"total\":").append(calculateTotal()).append(",");
        json.append("\"lines\":[");

        for (int i = 0; i < lines.size(); i++) {
            json.append(lines.get(i).toJson());
            if (i < lines.size() - 1) {
                json.append(",");
            }
        }

        json.append("]}");
        return json.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Cart for user #").append(userId).append(" ===\n");
        if (lines.isEmpty()) {
            sb.append("  (cart is empty)\n");
        } else {
            for (CartLine line : lines) {
                sb.append("  ").append(line).append("\n");
            }
        }
        sb.append("  TOTAL: ").append(String.format("%.2f", total)).append("\n");
        return sb.toString();
    }
}
