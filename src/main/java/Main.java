import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        ServerSocket serverSocket = null;
        int port = 6379;

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                Thread clientThread = new Thread(() -> {
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        OutputStream out = clientSocket.getOutputStream();

                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            // Redis commands sent from clients typically start with '*' (RESP Array)
                            if (inputLine.startsWith("*")) {
                                int numElements = Integer.parseInt(inputLine.substring(1));
                                List commandTokens = new ArrayList<>();

                                for (int i = 0; i < numElements; i++) {
                                    // Read the byte length line (e.g., "$4")
                                    in.readLine();
                                    // Read the actual content line (e.g., "ECHO")
                                    commandTokens.add(in.readLine());
                                }

                                if (commandTokens.isEmpty()) {
                                    continue;
                                }

                                String command = commandTokens.get(0).toUpperCase();

                                if (command.equals("PING")) {
                                    out.write("+PONG\r\n".getBytes());
                                    out.flush();
                                } else if (command.equals("ECHO")) {
                                    String message = commandTokens.get(1);
                                    String response = "$" + message.length() + "\r\n" + message + "\r\n";
                                    out.write(response.getBytes());
                                    out.flush();
                                }
                            }
                        }

                    } catch (IOException e) {
                        System.out.println("Client disconnected or error: " + e.getMessage());
                    } finally {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            System.out.println("Error closing client socket: " + e.getMessage());
                        }
                    }
                });

                clientThread.start();
            }

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }
}