package server;

import common.JsonUtil;
import common.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Client handler started for: " + socket.getInetAddress());

        try (
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String rawMessage;

            while ((rawMessage = in.readLine()) != null) {
                System.out.println("Raw message received: " + rawMessage);

                Message requestMessage;
                Message responseMessage;

                try {
                    requestMessage = JsonUtil.fromJson(rawMessage);

                    if (requestMessage == null) {
                        responseMessage = new Message(
                                "ERROR",
                                "0",
                                "ERROR",
                                "",
                                "INVALID_MESSAGE"
                        );
                    } else {
                        responseMessage = RequestRouter.route(requestMessage);
                    }

                } catch (Exception e) {
                    responseMessage = new Message(
                            "ERROR",
                            "0",
                            "ERROR",
                            "",
                            "INVALID_JSON"
                    );
                }

                String jsonResponse = JsonUtil.toJson(responseMessage);
                out.println(jsonResponse);

                System.out.println("Response sent: " + jsonResponse);
            }

        } catch (Exception e) {
            System.out.println("Connection error with client: " + socket.getInetAddress());
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("Client disconnected: " + socket.getInetAddress());
            } catch (Exception e) {
                System.out.println("Error while closing socket: " + e.getMessage());
            }
        }
    }
}
