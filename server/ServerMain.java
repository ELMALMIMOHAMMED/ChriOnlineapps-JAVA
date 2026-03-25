package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {

    private static final int PORT = 6000;

    // 🔥 Pool de threads (optimisé)
    private static final ExecutorService threadPool =
            Executors.newFixedThreadPool(20);

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("=================================");
            System.out.println("🚀 Server started on port " + PORT);
            System.out.println("⏳ Waiting for client connections...");
            System.out.println("=================================");

            while (true) {

                Socket clientSocket = serverSocket.accept();

                String clientIP = clientSocket.getInetAddress().toString();

                System.out.println("✅ Client connecté : " + clientIP);

                ClientHandler handler = new ClientHandler(clientSocket);

                // 🔥 utiliser pool au lieu de new Thread
                threadPool.execute(handler);
            }

        } catch (Exception e) {

            System.out.println("❌ Server error");
            e.printStackTrace();

        } finally {

            System.out.println("🛑 Server shutting down...");
            threadPool.shutdown();
        }
    }
}