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
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));

            PrintWriter output =
                    new PrintWriter(socket.getOutputStream(), true);

            BufferedReader console =
                    new BufferedReader(
                            new InputStreamReader(System.in));

            while (true) {

                System.out.print("Enter command (PING / LOGIN / EXIT): ");
                String command = console.readLine();

                if(command.equalsIgnoreCase("EXIT")) {
                    break;
                }

                Message message = Message.request(command, "1", "");

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
