package utb.fai;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class SocketHandler {
    Socket mySocket;
    String clientID;
    ActiveHandlers activeHandlers;
    ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<>(20);
    CountDownLatch startSignal = new CountDownLatch(2);

    OutputHandler outputHandler = new OutputHandler();
    InputHandler inputHandler = new InputHandler();

    volatile boolean inputFinished = false;

    String userName = null;
    Set<String> joinedRooms = ConcurrentHashMap.newKeySet();

    public SocketHandler(Socket mySocket, ActiveHandlers activeHandlers) {
        this.mySocket = mySocket;
        this.clientID = mySocket.getInetAddress() + ":" + mySocket.getPort();
        this.activeHandlers = activeHandlers;
        this.joinedRooms.add("public");
    }

    class OutputHandler implements Runnable {
        public void run() {
            try (Writer writer = new OutputStreamWriter(mySocket.getOutputStream(), "UTF-8")) {
                startSignal.countDown();
                startSignal.await();

                writer.write("\nWelcome! Please set your name.\n");
                writer.flush();

                while (!inputFinished) {
                    writer.write(messages.take() + "\r\n");
                    writer.flush();
                }
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    class InputHandler implements Runnable {
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(mySocket.getInputStream(), "UTF-8"))) {
                startSignal.countDown();
                startSignal.await();

                activeHandlers.add(SocketHandler.this);
                setName(reader.readLine());

                String request;
                while ((request = reader.readLine()) != null) {
                    if (request.startsWith("#")) {
                        handleCommand(request);
                    } else if (userName != null) {
                        activeHandlers.sendMessageToRooms(SocketHandler.this, formatMessage(request), joinedRooms);
                    } else {
                        messages.offer("Set your name with #setMyName <name>");
                    }
                }
                inputFinished = true;
                messages.offer("OutputHandler, wakeup and die!");
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                activeHandlers.remove(SocketHandler.this);
            }
        }

        private void handleCommand(String command) {
            String[] parts = command.split("\\s+", 3);
            switch (parts[0]) {
                case "#setMyName" -> setName(parts.length > 1 ? parts[1] : null);
                case "#sendPrivate" -> activeHandlers.sendPrivateMessage(SocketHandler.this, parts[1], parts[2]);
                case "#join" -> handleJoinLeave(parts, true);
                case "#leave" -> handleJoinLeave(parts, false);
                case "#groups" -> messages.offer("Your joined rooms: " + String.join(", ", joinedRooms));
                default -> messages.offer("Unknown command: " + command);
            }
        }

        private void handleJoinLeave(String[] parts, boolean isJoin) {
            if (parts.length > 1) {
                if (isJoin) {
                    joinedRooms.add(parts[1]);
                    messages.offer("Joined room: " + parts[1]);
                } else {
                    joinedRooms.remove(parts[1]);
                    messages.offer("Left room: " + parts[1]);
                }
            } else {
                messages.offer("Usage: #" + (isJoin ? "join" : "leave") + " <room_name>");
            }
        }

        private void setName(String newName) {
            if (newName != null && newName.trim().matches("\\S+")) {
                if (activeHandlers.isNameUnique(newName)) {
                    userName = newName;
                    messages.offer("Your name has been set to: " + userName);
                } else {
                    messages.offer("This name is already taken. Please choose another one.");
                }
            } else {
                messages.offer("Invalid name. Please try again.");
            }
        }

        private String formatMessage(String message) {
            return "[" + userName + "] >> " + message;
        }
    }
}
