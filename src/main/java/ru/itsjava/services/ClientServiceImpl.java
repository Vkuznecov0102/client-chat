package ru.itsjava.services;

import lombok.SneakyThrows;

import java.io.PrintWriter;
import java.net.Socket;

public class ClientServiceImpl implements ClientService {

    public final static int PORT = 8001;
    public final static String HOST = "localhost";

    @SneakyThrows
    @Override
    public void start() {
        try (Socket socket = new Socket(HOST, PORT)) {

            if (socket.isConnected()) {
                new Thread(new SocketRunnable(socket)).start();

                PrintWriter serverWriter = new PrintWriter(socket.getOutputStream());

                MessageInputService messageInputService =
                        new MessageInputServiceImpl(System.in);

                System.out.println("Введите свой логин");
                String login = messageInputService.getMessage();

                System.out.println("Введите свой пароль");
                String password = messageInputService.getMessage();

                serverWriter.println("!autho!" + login + ":" + password);
                serverWriter.flush();

                while (true) {
                    String consoleMessage = messageInputService.getMessage();
                    if (consoleMessage.startsWith("exit")) {
                        System.out.println(login + " вышел из чата");
                        System.exit(0);
                    }
                    else if(consoleMessage.startsWith("1")){
                        System.out.println("Это тестовая единица");
                    }
                    else if(consoleMessage.startsWith("2")){
                        System.out.println("Это тестовая двойка");
                    }
                    else if(consoleMessage.startsWith("3")){
                        System.out.println("Это тестовая три");
                    }
                    serverWriter.println(consoleMessage);
                    serverWriter.flush();
                }
            } else {
                System.out.println("Sorry, server is unavailable");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}