package network;

import com.google.gson.Gson;
import common.Message;

import java.io.*;
import java.net.Socket;

public class Client {

    private String host;
    private int port;

    private final Gson gson = new Gson();

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // =========================
    // VERSION OBJET (PRINCIPALE)
    // =========================
    public Message sendRequest(Message request) {

        try (
                Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {

            // 🔥 convertir en JSON
            String jsonRequest = gson.toJson(request);

            System.out.println("REQUEST → " + jsonRequest);

            // envoyer
            out.println(jsonRequest);

            // recevoir
            String responseJson = in.readLine();

            System.out.println("RESPONSE → " + responseJson);

            if (responseJson == null || responseJson.isEmpty()) {
                return null;
            }

            // 🔥 convertir en objet Message
            return gson.fromJson(responseJson, Message.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // =========================
    // VERSION STRING (OPTIONNEL)
    // =========================
    public String sendRequest(String type, String payload) {

        try (
                Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {

            String requestJson = "{"
                    + "\"type\":\"" + type + "\","
                    + "\"payload\":" + payload
                    + "}";

            System.out.println("REQUEST (RAW) → " + requestJson);

            // envoyer
            out.println(requestJson);

            // recevoir
            String response = in.readLine();

            System.out.println("RESPONSE (RAW) → " + response);

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}