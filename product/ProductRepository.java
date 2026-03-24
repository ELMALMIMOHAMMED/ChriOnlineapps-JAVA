package product;

import common.BaseDonnees;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository {

    private static volatile boolean initialized;

    public static void initializeStorage() {
        initializeIfNeeded();
    }

    public static List<Product> getAll() {
        initializeIfNeeded();

        List<Product> products = new ArrayList<>();
        String query = "SELECT id_product, name, description, price, stock FROM product ORDER BY id_product";

        try (Connection connection = BaseDonnees.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                products.add(mapProduct(resultSet));
            }

            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load products from database", e);
        }
    }

    public static Optional<Product> findById(String id) {
        initializeIfNeeded();

        try (Connection connection = BaseDonnees.getConnection()) {
            return findById(connection, id);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load product from database", e);
        }
    }

    public static Optional<Product> findById(Connection connection, String id) throws SQLException {
        initializeIfNeeded();

        String query = "SELECT id_product, name, description, price, stock FROM product WHERE id_product = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapProduct(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public static boolean updateStock(String id, int newStock) {
        initializeIfNeeded();

        try (Connection connection = BaseDonnees.getConnection()) {
            return updateStock(connection, id, newStock);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to update product stock", e);
        }
    }

    public static boolean updateStock(Connection connection, String id, int newStock) throws SQLException {
        initializeIfNeeded();

        String query = "UPDATE product SET stock = ? WHERE id_product = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, newStock);
            statement.setString(2, id);
            return statement.executeUpdate() > 0;
        }
    }

    public static boolean decreaseStock(String id, int quantity) {
        initializeIfNeeded();

        try (Connection connection = BaseDonnees.getConnection()) {
            return decreaseStock(connection, id, quantity);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to decrease product stock", e);
        }
    }

    public static boolean decreaseStock(Connection connection, String id, int quantity) throws SQLException {
        initializeIfNeeded();

        String query = "UPDATE product SET stock = stock - ? WHERE id_product = ? AND stock >= ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, quantity);
            statement.setString(2, id);
            statement.setInt(3, quantity);
            return statement.executeUpdate() > 0;
        }
    }

    public static boolean create(Product product) {
        initializeIfNeeded();

        String query = "INSERT INTO product (id_product, name, description, price, stock) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = BaseDonnees.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, product.getId());
            statement.setString(2, product.getName());
            statement.setString(3, product.getDescription());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getStock());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to create product", e);
        }
    }

    public static boolean update(Product product) {
        initializeIfNeeded();

        String query = "UPDATE product SET name = ?, description = ?, price = ?, stock = ? WHERE id_product = ?";

        try (Connection connection = BaseDonnees.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getStock());
            statement.setString(5, product.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to update product", e);
        }
    }

    public static boolean delete(String id) {
        initializeIfNeeded();

        String query = "DELETE FROM product WHERE id_product = ?";

        try (Connection connection = BaseDonnees.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete product", e);
        }
    }

    private static void initializeIfNeeded() {
        if (initialized) {
            return;
        }

        synchronized (ProductRepository.class) {
            if (initialized) {
                return;
            }

            try (Connection connection = BaseDonnees.getConnection()) {
                ensureTableExists(connection);
                seedProductsIfEmpty(connection);
                initialized = true;
            } catch (SQLException e) {
                throw new RuntimeException("Unable to initialize product storage", e);
            }
        }
    }

    private static void ensureTableExists(Connection connection) throws SQLException {
        String createTable = "CREATE TABLE IF NOT EXISTS product ("
                + "id_product VARCHAR(20) PRIMARY KEY, "
                + "name VARCHAR(100) NOT NULL, "
                + "description VARCHAR(255) NOT NULL, "
                + "price DOUBLE NOT NULL, "
                + "stock INT NOT NULL"
                + ")";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTable);
        }
    }

    private static void seedProductsIfEmpty(Connection connection) throws SQLException {
        String countQuery = "SELECT COUNT(*) FROM product";

        try (PreparedStatement countStatement = connection.prepareStatement(countQuery);
             ResultSet resultSet = countStatement.executeQuery()) {

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return;
            }
        }

        String insertQuery = "INSERT INTO product (id_product, name, description, price, stock) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertProduct(insertStatement, "001", "Product 1", "A basic widget", 9.99, 100);
            insertProduct(insertStatement, "002", "Product 2", "A useful gadget", 19.99, 50);
            insertProduct(insertStatement, "003", "Product 3", "An advanced doohickey", 29.99, 25);
            insertStatement.executeBatch();
        }
    }

    private static void insertProduct(PreparedStatement statement, String id, String name, String description, double price, int stock) throws SQLException {
        statement.setString(1, id);
        statement.setString(2, name);
        statement.setString(3, description);
        statement.setDouble(4, price);
        statement.setInt(5, stock);
        statement.addBatch();
    }

    private static Product mapProduct(ResultSet resultSet) throws SQLException {
        return new Product(
                resultSet.getString("id_product"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getDouble("price"),
                resultSet.getInt("stock")
        );
    }
}
