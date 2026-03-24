package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.List;

import common.JsonUtil;
import common.Message;
import product.Product;

public class TestClient {

    public static void main(String[] args) {

        try {

            Socket socket = new Socket("localhost", 6000);
            System.out.println("Connected to server");

            BufferedReader input =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));

            PrintWriter output =
                    new PrintWriter(socket.getOutputStream(), true);

            BufferedReader console =
                    new BufferedReader(new InputStreamReader(System.in));

            while (true) {

                System.out.println("\n=== ChriOnline Test Client ===");
                System.out.println("1. PING");
                System.out.println("2. LOGIN");
                System.out.println("3. REGISTER");
                System.out.println("4. PRODUCT_LIST");
                System.out.println("5. PRODUCT_DETAILS");
                System.out.println("6. PRODUCT_ORDER_STATUS");
                System.out.println("7. CREATE_COMMANDE");
                System.out.println("8. GET_COMMANDES");
                System.out.println("9. VALIDER_COMMANDE");
                System.out.println("10. ANNULER_COMMANDE");
                System.out.println("11. CART_ADD");
                System.out.println("12. CART_VIEW");
                System.out.println("13. CART_REMOVE");
                System.out.println("14. CART_CHECKOUT");
                System.out.println("15. CART_CLEAR");
                System.out.println("0. EXIT");

                System.out.print("Choix: ");
                String choix = console.readLine();

                if ("0".equals(choix)) {
                    break;
                }

                Message message = buildRequest(choix, console);
                if (message == null) {
                    continue;
                }

                output.println(JsonUtil.toJson(message));

                String response = input.readLine();
                if (response == null) {
                    System.out.println("Server disconnected.");
                    break;
                }

                handleResponse(response);

                if (!promptContinueTest(console)) {
                    break;
                }
            }

            socket.close();
            System.out.println("Connection closed");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Message buildRequest(String choix, BufferedReader console) throws Exception {
        switch (choix) {
            case "1":
                return Message.request("PING", "1", "");

            case "2":
                System.out.print("Email: ");
                String email = console.readLine();
                System.out.print("Password: ");
                String password = console.readLine();
                return Message.request("LOGIN", "2", email + "," + password);

            case "3":
                System.out.print("ID: ");
                String id = console.readLine();
                System.out.print("Username: ");
                String username = console.readLine();
                System.out.print("Password: ");
                String regPassword = console.readLine();
                System.out.print("Email: ");
                String regEmail = console.readLine();
                System.out.print("Phone: ");
                String phone = console.readLine();
                System.out.print("Date (yyyy-MM-dd): ");
                String date = console.readLine();
                System.out.print("Role: ");
                String role = console.readLine();
                String registerPayload = id + "," + username + "," + regPassword + "," + regEmail + "," + phone + "," + date + "," + role;
                return Message.request("REGISTER", "3", registerPayload);

            case "4":
                return Message.request("PRODUCT_LIST", "4", "");

            case "5":
                System.out.print("Product ID: ");
                return Message.request("PRODUCT_DETAILS", "5", console.readLine());

            case "6":
                System.out.print("User ID: ");
                return Message.request("PRODUCT_ORDER_STATUS", "6", console.readLine());

            case "7":
                System.out.print("User ID: ");
                String userId = console.readLine();
                System.out.print("Produits (ex: 001:2;002:1): ");
                String produits = console.readLine();
                return Message.request("CREATE_COMMANDE", "7", "{\"userId\":\"" + userId + "\",\"produits\":\"" + produits + "\"}");

            case "8":
                System.out.print("User ID: ");
                return Message.request("GET_COMMANDES", "8", console.readLine());

            case "9":
                System.out.print("ID commande: ");
                return Message.request("VALIDER_COMMANDE", "9", console.readLine());

            case "10":
                System.out.print("ID commande: ");
                return Message.request("ANNULER_COMMANDE", "10", console.readLine());

            case "11":
                System.out.print("User ID: ");
                String cartUserId = console.readLine();
                System.out.print("Product ID: ");
                String productId = console.readLine();
                System.out.print("Quantity: ");
                String quantity = console.readLine();
                return Message.request("CART_ADD", "11", "{\"userId\":\"" + cartUserId + "\",\"productId\":\"" + productId + "\",\"quantity\":\"" + quantity + "\"}");

            case "12":
                System.out.print("User ID: ");
                return Message.request("CART_VIEW", "12", console.readLine());

            case "13":
                System.out.print("User ID: ");
                String removeUserId = console.readLine();
                System.out.print("Product ID to remove: ");
                String removeProductId = console.readLine();
                return Message.request("CART_REMOVE", "13", "{\"userId\":\"" + removeUserId + "\",\"productId\":\"" + removeProductId + "\"}");

            case "14":
                System.out.print("User ID: ");
                return Message.request("CART_CHECKOUT", "14", console.readLine());

            case "15":
                System.out.print("User ID: ");
                return Message.request("CART_CLEAR", "15", console.readLine());

            default:
                System.out.println("Choix invalide");
                return null;
        }
    }

    private static void handleResponse(String response) {
        Message msg = JsonUtil.fromJson(response);

        if ("PRODUCT_LIST".equals(msg.getType()) && "SUCCESS".equals(msg.getStatus())) {
            displayProductList(msg);
            return;
        }

        if ("PRODUCT_DETAILS".equals(msg.getType()) && "SUCCESS".equals(msg.getStatus())) {
            displayProductDetails(msg);
            return;
        }

        System.out.println("Status: " + msg.getStatus());
        if (msg.getPayload() != null && !msg.getPayload().isEmpty()) {
            System.out.println("Payload: " + msg.getPayload());
        }
        if (msg.getErrorCode() != null && !msg.getErrorCode().isEmpty()) {
            System.out.println("Error: " + msg.getErrorCode());
        }
    }

    private static void displayProductList(Message msg) {
        try {
            byte[] decodedPayload = Base64.getDecoder().decode(msg.getPayload());
            List<?> products = JsonUtil.fromBinary(decodedPayload, List.class);

            System.out.println("\n=== AVAILABLE PRODUCTS ===");
            for (Object product : products) {
                System.out.println(product);
            }
        } catch (Exception e) {
            System.out.println("Error decoding product list: " + e.getMessage());
        }
    }

    private static void displayProductDetails(Message msg) {
        try {
            byte[] decodedPayload = Base64.getDecoder().decode(msg.getPayload());
            Product product = JsonUtil.fromBinary(decodedPayload, Product.class);
            System.out.println("\n=== PRODUCT DETAILS ===");
            System.out.println(product);
        } catch (Exception e) {
            System.out.println("Error decoding product details: " + e.getMessage());
        }
    }

    private static boolean promptContinueTest(BufferedReader console) {
        try {
            System.out.print("\nPress Enter to continue test, or type 0 to exit: ");
            String answer = console.readLine();
            return answer == null || !"0".equals(answer.trim());
        } catch (Exception e) {
            return false;
        }
    }
}
