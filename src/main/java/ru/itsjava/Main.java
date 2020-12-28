package ru.itsjava;

import lombok.SneakyThrows;
import ru.itsjava.services.ClientService;
import ru.itsjava.services.ClientServiceImpl;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        ClientService clientService=new ClientServiceImpl();
        clientService.start();
    }
}
