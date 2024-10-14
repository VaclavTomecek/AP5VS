package utb.fai;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {

    private final Socket clientSocket;
    private BufferedReader inputReader;
    private PrintWriter outputWriter;

    public ClientThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            // Inicializace vstupních a výstupních toků
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();
            inputReader = new BufferedReader(new InputStreamReader(inputStream));
            outputWriter = new PrintWriter(outputStream, true);

            String inputLine;

            // Hlavní smyčka pro zpracování příchozí komunikace
            while ((inputLine = inputReader.readLine()) != null) {
                System.out.println("Received: " + inputLine);  // Výpis zprávy na server
                if ("/QUIT".equalsIgnoreCase(inputLine)) {
                    System.out.println("Client disconnected.");
                    break;
                }

                // Odeslání zpětné vazby klientovi (echo server)
                outputWriter.println("Server: " + inputLine);
            }

        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Uzavření socketu po ukončení komunikace
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
