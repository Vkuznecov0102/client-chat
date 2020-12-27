package ru.itsjava;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main {

    public final static int PORT = 8001;
    public final static String HOST = "localhost";

    @SneakyThrows
    public static void main(String[] args) {
        Socket socket = new Socket(HOST, PORT);

        if (socket.isConnected()) {
            PrintWriter serverWriter = new PrintWriter(socket.getOutputStream());
            serverWriter.println("Hi from client");
            serverWriter.flush();

            BufferedReader serverReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            while (true) {
                System.out.println(serverReader.readLine());
            }
        }
    }
}
