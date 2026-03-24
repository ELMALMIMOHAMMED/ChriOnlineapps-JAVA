package dao;

import cart.Cart;
import cart.CartLine;
import common.BaseDonnees;
import product.Product;
import product.ProductRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CartDAO {

    private static volatile boolean initialized;

    public static Cart getCart(int userId) {
        initializeIfNeeded();

        try (Connection connection = BaseDonnees.getConnection()) {
            return getCart(connection, userId);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load cart", e);
        }
    }

    public static Cart getCart(Connection connection, int userId) {
        initializeIfNeeded();

        Cart cart = new Cart(userId);
        String query = "SELECT ci.product_id, ci.quantity, ci.unit_price, p.name, p.description, p.price, p.stock "
                + "FROM cart_item ci JOIN product p ON p.id_product = ci.product_id "
                + "WHERE ci.user_id = ? ORDER BY ci.product_id";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = new Product(
                            resultSet.getString("product_id"),
                            resultSet.getString("name"),
                            resultSet.getString("description"),
                            resultSet.getDouble("price"),
                            resultSet.getInt("stock")
                    );

                    cart.addLine(new CartLine(
                            product,
                            resultSet.getInt("quantity"),
                            resultSet.getDouble("unit_price")
                    ));
                }
            }

            return cart;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load cart", e);
        }
    }

    public static void addProduct(int userId, Product product, int quantity) {
        initializeIfNeeded();

        try (Connection connection = BaseDonnees.getConnection()) {
            addProduct(connection, userId, product, quantity);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to add product to cart", e);
        }
    }

    public static void addProduct(Connection connection, int userId, Product product, int quantity) throws SQLException {
        initializeIfNeeded();
        ensureCartExists(connection, userId);

        String query = "INSERT INTO cart_item (user_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity), unit_price = VALUES(unit_price)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setString(2, product.getId());
            statement.setInt(3, quantity);
            statement.setDouble(4, product.getPrice());
            statement.executeUpdate();
        }
    }

    public static void removeProduct(int userId, String productId) {
        initializeIfNeeded();

        try (Connection connection = BaseDonnees.getConnection()) {
            removeProduct(connection, userId, productId);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to remove product from cart", e);
        }
    }

    public static void removeProduct(Connection connection, int userId, String productId) throws SQLException {
        initializeIfNeeded();

        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM cart_item WHERE user_id = ? AND product_id = ?")) {
            statement.setInt(1, userId);
            statement.setString(2, productId);
            statement.executeUpdate();
        }
    }

    public static void clearCart(int userId) {
        initializeIfNeeded();

        try (Connection connection = BaseDonnees.getConnection()) {
            clearCart(connection, userId);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to clear cart", e);
        }
    }

    public static void clearCart(Connection connection, int userId) throws SQLException {
        initializeIfNeeded();

        try (PreparedStatement deleteItems = connection.prepareStatement("DELETE FROM cart_item WHERE user_id = ?");
             PreparedStatement deleteCart = connection.prepareStatement("DELETE FROM cart WHERE user_id = ?")) {

            deleteItems.setInt(1, userId);
            deleteItems.executeUpdate();

            deleteCart.setInt(1, userId);
            deleteCart.executeUpdate();
        }
    }

    private static void ensureCartExists(Connection connection, int userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO cart (user_id) VALUES (?)")) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    private static void initializeIfNeeded() {
        if (initialized) {
            return;
        }

        synchronized (CartDAO.class) {
            if (initialized) {
                return;
            }

            ProductRepository.initializeStorage();

            try (Connection connection = BaseDonnees.getConnection()) {
                ensureTables(connection);
                initialized = true;
            } catch (SQLException e) {
                throw new RuntimeException("Unable to initialize cart storage", e);
            }
        }
    }

    private static void ensureTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS cart ("
                    + "user_id INT PRIMARY KEY, "
                    + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                    + ")");

            statement.execute("CREATE TABLE IF NOT EXISTS cart_item ("
                    + "user_id INT NOT NULL, "
                    + "product_id VARCHAR(20) NOT NULL, "
                    + "quantity INT NOT NULL, "
                    + "unit_price DOUBLE NOT NULL, "
                    + "PRIMARY KEY (user_id, product_id), "
                    + "CONSTRAINT fk_cart_item_cart FOREIGN KEY (user_id) REFERENCES cart(user_id) ON DELETE CASCADE, "
                    + "CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id) REFERENCES product(id_product) ON DELETE CASCADE"
                    + ")");
        }
    }
}