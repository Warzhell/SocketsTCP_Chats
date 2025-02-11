package Codigo;

import java.io.*;
import java.net.*;

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String userName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                out.println("Ingrese un nombre de usuario:");
                userName = in.readLine();
                synchronized (ChatServerTCP.userNames) {
                    if (!ChatServerTCP.userNames.contains(userName)) {
                        ChatServerTCP.userNames.add(userName);
                        break;
                    } else {
                        out.println("Nombre en uso, elija otro.");
                    }
                }
            }

            out.println("Bienvenido al chat, " + userName + "!");
            ChatServerTCP.broadcastMessage(userName + " se ha unido al chat.", this);

            synchronized (ChatServerTCP.clients) {
                ChatServerTCP.clients.add(this);
            }

            String message;
            while ((message = in.readLine()) != null) {
                ChatServerTCP.broadcastMessage(userName + ": " + message, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getUserName() {
        return userName;
    }

    public void disconnect() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            synchronized (ChatServerTCP.userNames) {
                ChatServerTCP.userNames.remove(userName);
            }
            synchronized (ChatServerTCP.clients) {
                ChatServerTCP.clients.remove(this);
            }
            ChatServerTCP.broadcastMessage(userName + " ha salido del chat.", this);
        }
    }
}
