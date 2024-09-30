package utb.fai;

import java.net.*;
import java.io.*;

public class EmailSender {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    /*
     * Constructor opens Socket to host/port.
     */
    public EmailSender(String host, int port) throws UnknownHostException, IOException {
        // Vytvoření socketu pro připojení k SMTP serveru
        socket = new Socket(host, port);

        // Vytvoření readeru a writeru pro komunikaci se serverem
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        // Očekáváme odpověď serveru po připojení
        readResponse();
    }

    /*
     * Sends email from an email address to an email address with some subject and
     * text.
     */
    public void send(String from, String to, String subject, String text) throws IOException {
        // Komunikace s SMTP serverem podle protokolu
        sendCommand("HELO " + InetAddress.getLocalHost().getHostName());
        readResponse();

        // Nastavení odesílatele
        sendCommand("MAIL FROM: <" + from + ">");
        readResponse();

        // Nastavení příjemce
        sendCommand("RCPT TO: <" + to + ">");
        readResponse();

        // Příprava obsahu emailu
        sendCommand("DATA");
        readResponse();

        // Odeslání emailu
        sendCommand(" ");
        sendCommand("Subject: " + subject);
        sendCommand("From: " + from);
        sendCommand("To: " + to);
        sendCommand(" ");
        sendCommand(text);
        sendCommand(" ");
        readResponse();
    }

    /*
     * Sends QUIT and closes the socket
     */
    public void close() {
        try {
            sendCommand("QUIT");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Pomocná metoda pro čtení odpovědi serveru
     */
    private void readResponse() throws IOException {
        String response = reader.readLine();
        System.out.println("SMTP server response: " + response);
    }

    /*
     * Pomocná metoda pro odeslání příkazu serveru
     */
    private void sendCommand(String command) throws IOException {
        writer.write(command + "\r\n");
        writer.flush();
        System.out.println("Command sent: " + command);
    }

    public static void main(String[] args) {
        try {
            // Nastavení SMTP serveru (například pro Gmail to nebude fungovat bez SSL)
            EmailSender emailSender = new EmailSender("smtp.gmail.com", 587);

            // Odeslání emailu
            emailSender.send("your-email@example.com", "your-email@example.com", "Testovací předmět",
                    "Toto je text emailu.");

            // Ukončení spojení
            emailSender.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}