package main.java.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private String name;

    public static final String HOST = "localhost";
    public static final int PORT = 1234;

    public Client(Socket socket, String name) {
        try {
            this.socket = socket;

            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            this.name = name;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome! Enter your name: ");
        String clientName = scanner.nextLine();

        Client client = new Client(new Socket(HOST, PORT), clientName);

        client.getMsg();
        client.sendMsg();
    }

    public void sendMsg() {
        try {
            writeAndFlush(name);

            Scanner scanner = new Scanner(System.in);

            while (socket.isConnected()) {
                String msg = scanner.nextLine();

                writeAndFlush(name + ": " + msg);
            }
        } catch (IOException e) {
            close();
        }
    }

    public void getMsg() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String msg = bufferedReader.readLine();
                    System.out.println(msg);
                } catch (IOException e) {
                    close();
                }
            }
        }).start();
    }

    private void writeAndFlush(String msg) throws IOException {
        this.bufferedWriter.write(msg);
        this.bufferedWriter.newLine();
        this.bufferedWriter.flush();
    }

    private void close() {
        try {
            bufferedReader.close();
            bufferedWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
