package product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Simple in-memory store for products.  Not thread-safe; suitable for a demo.
 */
public class ProductRepository {

    private static final List<Product> PRODUCTS = new ArrayList<>();

    static {
        // populate with some sample data
        PRODUCTS.add(new Product("001", "Widget", "A basic widget", 9.99, 100));
        PRODUCTS.add(new Product("002", "Gadget", "A useful gadget", 19.99, 50));
        PRODUCTS.add(new Product("003", "Doohickey", "An advanced doohickey", 29.99, 25));
    }

    public static List<Product> getAll() {
        return Collections.unmodifiableList(PRODUCTS);
    }

    public static Optional<Product> findById(String id) {
        return PRODUCTS.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public static boolean updateStock(String id, int newStock) {
        Optional<Product> opt = findById(id);
        if (opt.isPresent()) {
            opt.get().setStock(newStock);
            return true;
        }
        return false;
    }
}
