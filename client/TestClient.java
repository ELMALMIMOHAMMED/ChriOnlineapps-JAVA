package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import common.Message;
import common.JsonUtil;

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

                System.out.println("\nChoisir une commande :");
                System.out.println("1. PING");
                System.out.println("2. LOGIN");
                System.out.println("3. REGISTER");
                System.out.println("4. PRODUCT_LIST");
                System.out.println("5. PRODUCT_DETAILS");
                System.out.println("6. STOCK_UPDATE");
                System.out.println("7. CREATE_COMMANDE");
                System.out.println("8. GET_COMMANDES");
                System.out.println("9. VALIDER_COMMANDE");
                System.out.println("10. ANNULER_COMMANDE");
                System.out.println("0. EXIT");

                System.out.print("Choix: ");
                String choix = console.readLine();

                if (choix.equals("0")) break;

                Message message = null;

                switch (choix) {

                    case "1":
                        message = Message.request("PING", "1", "");
                        break;

                    case "2":
                        System.out.print("Email: ");
                        String email = console.readLine();
                        System.out.print("Password: ");
                        String password = console.readLine();
                        String loginPayload = email + "," + password;
                        message = Message.request("LOGIN", "2", loginPayload);
                        break;

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
                        message = Message.request("REGISTER", "3", registerPayload);
                        break;

                    case "4":
                        message = Message.request("PRODUCT_LIST", "4", "");
                        break;

                    case "5":
                        System.out.print("Product ID: ");
                        String productId = console.readLine();
                        message = Message.request("PRODUCT_DETAILS", "5", productId);
                        break;

                    case "6":
                        System.out.print("Product ID: ");
                        String updateId = console.readLine();
                        System.out.print("New stock: ");
                        String stock = console.readLine();
                        String stockPayload = "{\"id\":\"" + updateId + "\",\"stock\":" + stock + "}";
                        message = Message.request("STOCK_UPDATE", "6", stockPayload);
                        break;

                    case "7":
                        System.out.print("User ID: ");
                        String userId = console.readLine();

                        System.out.print("Produits (ex: 1:2;3:1): ");
                        String produits = console.readLine();

                        String payloadCreate = "{\"userId\":\"" + userId + "\",\"produits\":\"" + produits + "\"}";

                        message = Message.request("CREATE_COMMANDE", "7", payloadCreate);
                        break;

                    case "8":
                        System.out.print("User ID: ");
                        String userGet = console.readLine();

                        message = Message.request("GET_COMMANDES", "8", userGet);
                        break;

                    case "9":
                        System.out.print("ID commande: ");
                        String idVal = console.readLine();

                        message = Message.request("VALIDER_COMMANDE", "9", idVal);
                        break;

                    case "10":
                        System.out.print("ID commande: ");
                        String idAnn = console.readLine();

                        message = Message.request("ANNULER_COMMANDE", "10", idAnn);
                        break;

                    default:
                        System.out.println("Choix invalide");
                        continue;
                }

                String json = JsonUtil.toJson(message);
                output.println(json);

                String response = input.readLine();
                System.out.println("Server response : " + response);
            }

            socket.close();
            System.out.println("Connection closed");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
