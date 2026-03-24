package cart;

import product.Product;

public class CartLine {

    private final Product product;
    private int quantity;
    private final double unitPrice;

    public CartLine(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }

    public double calculateSubtotal() {
        return quantity * unitPrice;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public String toJson() {
        return "{"
                + "\"productId\":\"" + product.getId() + "\","
                + "\"productName\":\"" + product.getName() + "\","
                + "\"quantity\":" + quantity + ","
                + "\"unitPrice\":" + unitPrice + ","
                + "\"subtotal\":" + calculateSubtotal()
                + "}";
    }

    @Override
    public String toString() {
        return product.getName()
                + " x" + quantity
                + " @ " + String.format("%.2f", unitPrice)
                + " = " + String.format("%.2f", calculateSubtotal());
    }
}