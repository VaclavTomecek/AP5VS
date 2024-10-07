package utb.fai;

import java.io.*;
import java.net.*;

public class TelnetClient implements Runnable {

    private final String serverIp;
    private final int port;
    private Socket socket;
    private BufferedReader inputReader;
    private PrintWriter outputWriter;
    private volatile boolean running = true; 

    // Konstruktor klienta s IP adresou a portem
    public TelnetClient(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            // Připojení k serveru
            socket = new Socket(serverIp, port);

            // Nastavení vstupních a výstupních toků
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            inputReader = new BufferedReader(new InputStreamReader(System.in));
            outputWriter = new PrintWriter(outputStream, true);

            // Vytvoření vlákna pro čtení odpovědí ze serveru
            Thread readThread = new Thread(new ServerResponseReader(inputStream));
            readThread.start();

            // Hlavní vlákno: čtení vstupu z konzole a odesílání na server
            String inputLine;
            while (running && (inputLine = inputReader.readLine()) != null) {
                if ("/QUIT".equalsIgnoreCase(inputLine)) {
                    running = false; 
                    break;
                }
                outputWriter.println(inputLine); // Odeslání příkazu na server
            }

            // Uzavření socketu po ukončení komunikace
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Zajištění uzavření socketu při ukončení aplikace
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Vnitřní třída pro čtení odpovědí ze serveru v samostatném vláknu
    private class ServerResponseReader implements Runnable {
        private final InputStream inputStream;

        public ServerResponseReader(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            try {
                // Čtení dat ze serveru
                while (running) {
                    if (inputStream.available() > 0) {
                        int data = inputStream.read();
                        System.out.print((char) data); 
                    } else {
                        Thread.sleep(20); 
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
