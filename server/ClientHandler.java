package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import common.Message;
import common.JsonUtil;

public class ClientHandler implements Runnable {

    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        String clientAddress = socket.getInetAddress().toString();

        try {

            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            PrintWriter output = new PrintWriter(
                    socket.getOutputStream(), true
            );

            System.out.println("✅ Client connecté : " + clientAddress);

            String request;

            while ((request = input.readLine()) != null) {

                System.out.println("📥 Reçu : " + request);

                Message message;

                // 🔥 Protection JSON
                try {
                    message = JsonUtil.fromJson(request);
                } catch (Exception e) {

                    Message error = new Message(
                            "ERROR",
                            "0",
                            "ERROR",
                            "",
                            "INVALID_JSON"
                    );

                    output.println(JsonUtil.toJson(error));
                    continue;
                }

                // 🔹 Routing
                Message response = RequestRouter.route(message);

                String jsonResponse = JsonUtil.toJson(response);

                System.out.println("📤 Envoyé : " + jsonResponse);

                output.println(jsonResponse);
            }

        } catch (Exception e) {

            System.out.println("❌ Erreur avec client : " + clientAddress);
            e.printStackTrace();

        } finally {

            try {
                socket.close();
                System.out.println("🔌 Connexion fermée : " + clientAddress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}