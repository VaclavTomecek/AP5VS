package utb.fai;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class App {

    public static void main(String[] args) {
        int port = 12345;  // Výchozí port
        int maxThreads = 10;  // Maximální počet vláken

        // Zpracování parametrů příkazového řádku (volitelně)
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                if (args.length > 1) {
                    maxThreads = Integer.parseInt(args[1]);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid port or thread count. Using default values.");
            }
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            // Hlavní smyčka serveru
            int activeThreads = 0;
            while (activeThreads < maxThreads) {
                // Čekání na připojení klienta
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                // Vytvoření nového vlákna pro každého klienta
                ClientThread clientThread = new ClientThread(clientSocket);
                clientThread.start();
                activeThreads++;
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
