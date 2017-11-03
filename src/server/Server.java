package server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author Markus Reichl
 * @version 2017-03-06
 */

public class Server {
    public static void main(String[] args) throws IOException {
        int port = 5050;

        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Port is not a number!");
            System.out.println("Falling back to default port!");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Not enough arguments given!");
            System.out.println("Falling back to default port!");
        }

        try (
                ServerSocket serverSocket = new ServerSocket(port)
        ) {
            System.out.println("Server running on " + serverSocket.getLocalSocketAddress());

            while (!serverSocket.isClosed()) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.out.println("Socket Error!");
            System.exit(-1);
        }
    }
}
