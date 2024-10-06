package utb.fai;

import java.net.*;
import java.io.*;

public class EmailSender {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    // Konstruktor s nastavením časového limitu pro připojení a čtení
    public EmailSender(String host, int port) throws UnknownHostException, IOException {

        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 5000); 
        socket.setSoTimeout(5000); 

        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);

        readResponse();
    }

    // Metoda pro odeslání emailu
    public void send(String from, String to, String subject, String text) throws IOException {
        sendCommand("EHLO " + InetAddress.getLocalHost().getHostName());
        sendCommand("MAIL FROM:<" + from + ">");
        sendCommand("RCPT TO:<" + to + ">");
        sendCommand("DATA");
        sendCommand("Subject: " + subject + "\r\n");
        sendCommand(text + "\r\n.");
    }

    // Metoda pro ukončení spojení
    public void close() {
        try {
            sendCommand("QUIT");
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metoda pro čtení odpovědi serveru
    private void readResponse() throws IOException {
        String response = input.readLine();
        if (response == null || response.isEmpty()) {
            throw new IOException("No response from SMTP server.");
        }
        System.out.println("SMTP server response: " + response);
    }

    // Metoda pro odeslání příkazu serveru
    private void sendCommand(String command) throws IOException {
        System.out.println("Command sent: " + command);
        readResponse();
    }
}
