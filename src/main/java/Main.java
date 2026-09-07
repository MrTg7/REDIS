import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        ServerSocket serverSocket = null;
        int port = 6379;
        
        try {
            serverSocket = new ServerSocket(port);
            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);
            
            while (true) {
                // Wait for connection from client.
                Socket clientSocket = serverSocket.accept();

                try {
                    java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(clientSocket.getInputStream()));
                    java.io.OutputStream out = clientSocket.getOutputStream();

                    String inputLine;
                    // Keep reading as long as the client is connected
                    while ((inputLine = in.readLine()) != null) {
                        // For now, print what the client sent so you can see the RESP format
                        System.out.println("Received: " + inputLine);

                        // If we receive the PING command
                        if (inputLine.equalsIgnoreCase("PING")) {
                            out.write("+PONG\r\n".getBytes());
                            out.flush();
                        }
                    }
                }
                
                // Close the client connection after responding
                clientSocket.close();
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