package utb.fai;

public class App {
    public static void main(String[] args) {
        // Ověření, že argumenty jsou zadány správně
        if (args.length != 2) {
            System.out.println("Usage: java App <IP address> <port>");
            System.exit(1);
        }

        String serverIp = args[0];
        int port;

        // Převod argumentu na číslo portu
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number.");
            System.exit(1);
            return;
        }

        // Vytvoření instance Telnet klienta a spuštění v samostatném vláknu
        TelnetClient telnetClient = new TelnetClient(serverIp, port);
        Thread clientThread = new Thread(telnetClient);
        clientThread.start();
    }
}
