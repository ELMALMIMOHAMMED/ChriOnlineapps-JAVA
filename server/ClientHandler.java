package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {

        try {

            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            PrintWriter output = new PrintWriter(
                    socket.getOutputStream(), true);

            String message;

            while ((message = input.readLine()) != null) {

                System.out.println("Message received : " + message);

                // convertir JSON en objet Message
                common.Message request = common.JsonUtil.fromJson(message);

                // traiter la requête
                common.Message response = RequestRouter.route(request);

                // convertir en JSON
                String jsonResponse = common.JsonUtil.toJson(response);

                // envoyer la réponse
                output.println(jsonResponse); 
            }

        } catch (Exception e) {

            System.out.println("Client disconnected");

        }
    }
}
