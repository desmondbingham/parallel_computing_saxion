package nl.saxion.conferenceroom.client;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class ClientActor {

    private Client client;
    private ClientConnection connection;
    private static ClientRequestHandler handler;
    private Scanner s;

    public ClientActor() throws IOException, TimeoutException {
        client = new Client(String.valueOf(UUID.randomUUID()));
        connection = new ClientConnection(client);

        handler = new ClientRequestHandler(connection.getChannel(), client.USER_ID());
        s = new Scanner(System.in);

        System.out.println("Client ID:" + client.USER_ID());
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        ClientActor clientActor = new ClientActor();
        handler.startMenuLoop();
    }


}
