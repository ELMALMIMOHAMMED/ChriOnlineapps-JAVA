package server;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

    public static void main(String[] args) {

        int port = 6000;

        try {

            ServerSocket serverSocket = new ServerSocket(port);

            System.out.println("=================================");
            System.out.println("Server started on port " + port);
            System.out.println("Waiting for client connections...");
            System.out.println("=================================");

            while(true) {

                Socket clientSocket = serverSocket.accept();

                System.out.println("Client connected : " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket);

                new Thread(handler).start();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
