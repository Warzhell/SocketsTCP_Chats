package Codigo;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServerTCP {
    private static final int PORT = 12345;
    static Set<String> userNames = new HashSet<>();
    static Set<ClientHandler> clients = new HashSet<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor TCP iniciado en el puerto " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + socket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(socket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMessage(String message, ClientHandler sender) {
        if (message.equalsIgnoreCase("/ayuda")) {
            sender.sendMessage("Comandos disponibles:\n/exit - Cerrar conexión\n/emoji - Enviar (>‿◠)✌");
            return;
        } else if (message.equalsIgnoreCase("/emoji")) {
            message = sender.getUserName() + " : (>‿◠)✌";
        } else if (message.equalsIgnoreCase("/exit")) {
            sender.disconnect();
            return;
        }

        System.out.println("[Chat] " + message);
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }
}
