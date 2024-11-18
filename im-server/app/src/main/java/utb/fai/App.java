package utb.fai;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class App {
    public static void main(String[] args) {
        int port = 33000, maxConnections = 2;

        if (args.length > 0) {
            port = parseArgument(args[0], port);
            if (args.length > 1) {
                maxConnections = parseArgument(args[1], maxConnections);
            }
        }

        System.out.printf("IM server listening on port %d, maximum number of connections=%d...\n", port, maxConnections);

        ExecutorService pool = Executors.newFixedThreadPool(2 * maxConnections);
        ActiveHandlers activeHandlers = new ActiveHandlers();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!pool.isShutdown()) {
                Socket clientSocket = serverSocket.accept();
                clientSocket.setKeepAlive(true);
                SocketHandler handler = new SocketHandler(clientSocket, activeHandlers);
                pool.execute(handler.inputHandler);
                pool.execute(handler.outputHandler);
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
        } finally {
            shutdownPool(pool);
        }
    }

    private static int parseArgument(String arg, int defaultValue) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            System.err.printf("Argument %s is not an integer, using default value\n", arg);
            return defaultValue;
        }
    }

    private static void shutdownPool(ExecutorService pool) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
