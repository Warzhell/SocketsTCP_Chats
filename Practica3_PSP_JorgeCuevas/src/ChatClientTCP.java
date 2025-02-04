import java.io.*;
import java.net.*;

public class ChatClientTCP {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Conectado al servidor de chat.");

            // Hilo para recibir mensajes del servidor
            Thread receiveThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (SocketException e) {
                    System.out.println("Conexión cerrada por el servidor.");
                } catch (IOException e) {
                    System.out.println("Error en la conexión con el servidor.");
                }
            });
            receiveThread.start();

            // Enviar mensajes al servidor
            String userMessage;
            while ((userMessage = userInput.readLine()) != null) {
                out.println(userMessage);
                if (userMessage.equalsIgnoreCase("exit")) {
                    break; // Si el usuario escribe "exit", se desconecta
                }
            }
        } catch (IOException e) {
            System.out.println("Error en la conexión con el servidor.");
            e.printStackTrace();
        }
    }
}

