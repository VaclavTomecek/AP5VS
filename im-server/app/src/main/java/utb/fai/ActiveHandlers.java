package utb.fai;

import java.util.*;
import java.util.concurrent.*;

public class ActiveHandlers {
    private final ConcurrentHashMap<String, SocketHandler> activeHandlersMap = new ConcurrentHashMap<>();

    void sendMessageToAll(SocketHandler sender, String message) {
        activeHandlersMap.forEach((id, handler) -> {
            if (handler != sender && !handler.messages.offer(message)) {
                System.err.printf("Client %s message queue is full, dropping the message!\n", handler.clientID);
            }
        });
    }

    void sendMessageToRooms(SocketHandler sender, String message, Set<String> rooms) {
        activeHandlersMap.forEach((id, handler) -> {
            if (handler != sender && !Collections.disjoint(handler.joinedRooms, rooms) 
                && !handler.messages.offer(message)) {
                System.err.printf("Client %s message queue is full, dropping the message!\n", handler.clientID);
            }
        });
    }

    void sendPrivateMessage(SocketHandler sender, String recipientName, String message) {
        activeHandlersMap.values().stream()
            .filter(handler -> recipientName.equals(handler.userName))
            .findFirst()
            .ifPresentOrElse(handler -> {
                String privateMessage = "[" + sender.userName + "] >> " + message;
                if (!handler.messages.offer(privateMessage)) {
                    System.err.printf("Client %s message queue is full, dropping the message!\n", handler.clientID);
                }
                sender.messages.offer("Private message sent to " + recipientName);
            }, () -> sender.messages.offer("User " + recipientName + " not found or not online."));
    }

    boolean add(SocketHandler handler) {
        return activeHandlersMap.putIfAbsent(handler.clientID, handler) == null;
    }

    boolean remove(SocketHandler handler) {
        return activeHandlersMap.remove(handler.clientID, handler);
    }

    boolean isNameUnique(String name) {
        return activeHandlersMap.values().stream()
                .noneMatch(handler -> name.equals(handler.userName));
    }
}
