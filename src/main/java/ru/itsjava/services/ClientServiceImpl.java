package ru.itsjava.services;

import lombok.SneakyThrows;
import ru.itsjava.utils.Props;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.*;

public class ClientServiceImpl implements ClientService {

    public final static int PORT = 8001;
    public final static String HOST = "localhost";
    private SocketRunnable socketRunnable;
    private final Props props;
    private BufferedReader bufferedReader;

    public ClientServiceImpl(Props props) {
        this.props = props;
    }


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

                if(userExists(login,password)) {
                    printInstructions();
                    serverWriter.println("!autho!" + login + ":" + password);
                    serverWriter.flush();
                }
                else{
                    System.out.println("Вы ввели неправильный логин или пароль.");
                    printMenu(bufferedReader);
                }

                while (true) {
                    String consoleMessage = messageInputService.getMessage();
                    if (consoleMessage.startsWith("exit")) {
                        System.out.println(login + " вышел из чата");
                        System.exit(0);
                    }
                    else if(consoleMessage.startsWith("1")){
                        System.out.println("Введите будущий логин");
                        String newName=messageInputService.getMessage();
                        System.out.println("Введите будущий пароль");
                        String newPassword=messageInputService.getMessage();
                        if(createNewUser(newName,newPassword)){
                            System.out.println("Новый пользователь успешно зарегистрирован. Нажмите 2 чтобы перезайти");
                        }
                        else{
                            System.out.println("Что-то пошло не так");
                        }
                    }
                    else if(consoleMessage.startsWith("2")){
                        start();
                    }
                    else if(consoleMessage.startsWith("3")){
                        System.exit(0);
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

    @SneakyThrows
    private void printInstructions() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/instructions.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    @SneakyThrows
    private boolean createNewUser(String newName, String newPassword) {
        try(

                Connection connection = DriverManager.getConnection(
                        props.getValue("db.url"),
                        props.getValue("db.user"),
                        props.getValue("db.password"))
        ){
            PreparedStatement preparedStatement = connection
                    .prepareStatement("insert into chat_schema.users(name,password) values (?,?)");
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newPassword);

            preparedStatement.executeUpdate();
            return true;
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();
        }

        return false;
    }

    @SneakyThrows
    private boolean userExists(String login,String password) {
        try(

            Connection connection = DriverManager.getConnection(
                    props.getValue("db.url"),
                    props.getValue("db.user"),
                    props.getValue("db.password"))
        ){



            PreparedStatement preparedStatement = connection
                    .prepareStatement("select count(*) cnt from chat_schema.users where name=? and password=?;");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            int userCount = resultSet.getInt("cnt");

            if (userCount == 1) {
                return true;
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    @SneakyThrows
    private void printMenu(BufferedReader bufferedReader) {
        while (true) {
            System.out.println("Неправильные логин и пароль. Вы хотите зарегистрироваться?\n" +
                    "1)Да\n" +
                    "2)Ввести данные заново\n" +
                    "3)Выйти из приложения\n");
            if(bufferedReader == null ) break;
        }
    }
}