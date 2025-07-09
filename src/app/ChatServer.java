package app;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class ChatServer {
    private static final int PORT = 6002;

    // Use thread-safe set for concurrent access
    private static final Set<ClientHandler> clients = new CopyOnWriteArraySet<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket);
                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;
        private DataInputStream dis;
        private DataOutputStream dos;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                System.err.println("Error setting up streams for " + socket);
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String received;
            try {
                while ((received = dis.readUTF()) != null) {
                    System.out.println("Received: " + received);
                    broadcast(received);
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + socket);
            } finally {
                cleanup();
            }
        }

        private void broadcast(String message) {
            for (ClientHandler client : clients) {
                if (client != this) {
                    try {
                        client.dos.writeUTF(message);
                        client.dos.flush();
                    } catch (IOException e) {
                        System.err.println("Error broadcasting to " + client.socket);
                        e.printStackTrace();
                    }
                }
            }
        }

        private void cleanup() {
            try {
                clients.remove(this);
                if (dis != null)
                    dis.close();
                if (dos != null)
                    dos.close();
                if (socket != null && !socket.isClosed())
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
