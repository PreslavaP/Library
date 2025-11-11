import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class LibraryClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 5678;

    public static void main(String[] args) {
        System.out.println("Connecting to " + SERVER_IP + ":" + SERVER_PORT);

        try (
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
                Scanner consoleScanner = new Scanner(System.in)
        ) {
            System.out.println("SUCCESS: Connection to the server has been established.\n");

            String serverResponse;

            while ((serverResponse = serverIn.readLine()) != null) {
                System.out.println("Server: " + serverResponse);

                if (serverResponse.toLowerCase().contains("enter a function number") ||
                        serverResponse.toLowerCase().contains("select next function")) {
                    break;
                }
            }

            while (true) {
                System.out.print("Client > ");
                String clientInput = consoleScanner.nextLine();
                serverOut.println(clientInput);

                if (clientInput.equalsIgnoreCase("exit")) {
                    break;
                }

                while ((serverResponse = serverIn.readLine()) != null) {
                    if (!serverResponse.isEmpty()) {
                        System.out.println("Server: " + serverResponse);
                    }

                    if (serverResponse.toLowerCase().contains("select next function") ||
                            serverResponse.toLowerCase().contains("enter title") ||
                            serverResponse.toLowerCase().contains("enter id") ||
                            serverResponse.toLowerCase().contains("enter name of the library")) {
                        break;
                    }
                }
            }

            System.out.println("Client closed the connection.");

        } catch (IOException e) {
            System.err.println("Error occurred while connecting with the server: " + e.getMessage());
            System.err.println("Please make sure that LibraryServer is running.");
        }
    }
}