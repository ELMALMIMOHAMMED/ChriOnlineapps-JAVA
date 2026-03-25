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
                System.out.println("3. CREATE_COMMANDE");
                System.out.println("4. GET_COMMANDES");
                System.out.println("5. VALIDER_COMMANDE");
                System.out.println("6. ANNULER_COMMANDE");
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
                        message = Message.request("LOGIN", "2", "");
                        break;

                    case "3":
                        System.out.print("User ID: ");
                        String userId = console.readLine();

                        System.out.print("Produits (ex: 1:2;3:1): ");
                        String produits = console.readLine();

                        String payloadCreate = "{\"userId\":\"" + userId + "\",\"produits\":\"" + produits + "\"}";

                        message = Message.request("CREATE_COMMANDE", "3", payloadCreate);
                        break;

                    case "4":
                        System.out.print("User ID: ");
                        String userGet = console.readLine();

                        message = Message.request("GET_COMMANDES", "4", userGet);
                        break;

                    case "5":
                        System.out.print("ID commande: ");
                        String idVal = console.readLine();

                        message = Message.request("VALIDER_COMMANDE", "5", idVal);
                        break;

                    case "6":
                        System.out.print("ID commande: ");
                        String idAnn = console.readLine();

                        message = Message.request("ANNULER_COMMANDE", "6", idAnn);
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