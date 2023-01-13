package main.java.server;

import main.java.handler.MainHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static main.java.server.PrintConstants.CLIENT_JOINED;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(new ServerSocket(1234));
        server.run();
    }

    public void run() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                MainHandler handler = new MainHandler(socket);

                new Thread(handler).start();

                System.out.println(handler.getName() + CLIENT_JOINED);
            }
        } catch (IOException e) {
            close();
        }
    }

    private void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
