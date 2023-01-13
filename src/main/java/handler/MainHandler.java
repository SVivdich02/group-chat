package main.java.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static main.java.server.PrintConstants.CLIENT_JOINED;
import static main.java.server.PrintConstants.CLIENT_LEFT;

public class MainHandler implements Runnable {
    private Socket socket;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private String name;

    private static List<MainHandler> allClients = new ArrayList<>();

    public MainHandler(Socket socket) {
        try {
            this.socket = socket;

            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            this.name = bufferedReader.readLine();

            allClients.add(this);

            displayMsg(name + CLIENT_JOINED);
        } catch (IOException e) {
            closeCommunication();
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                displayMsg(bufferedReader.readLine());
            } catch (Exception e) {
                closeCommunication();
                break;
            }
        }
    }

    private void displayMsg(String msg) {
        for (var handler : allClients) {
            try {
                if (isNotSender(handler)) {
                    writeAndFlush(handler, msg);
                }
            } catch (IOException e) {
                closeCommunication();
            }
        }
    }

    private boolean isNotSender(MainHandler handler) {
        return !handler.name.equals(name);
    }

    private void writeAndFlush(MainHandler handler, String msg) throws IOException {
        handler.bufferedWriter.write(msg);
        handler.bufferedWriter.newLine();
        handler.bufferedWriter.flush();
    }

    private void closeCommunication() {
        allClients.remove(this);
        displayMsg(name + CLIENT_LEFT);

        try {
            bufferedReader.close();
            bufferedWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
