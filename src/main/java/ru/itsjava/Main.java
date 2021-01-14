package ru.itsjava;

import lombok.SneakyThrows;
import ru.itsjava.services.ClientService;
import ru.itsjava.services.ClientServiceImpl;
import ru.itsjava.utils.Props;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        ClientService clientService=new ClientServiceImpl(new Props());
        clientService.start();
    }
}
