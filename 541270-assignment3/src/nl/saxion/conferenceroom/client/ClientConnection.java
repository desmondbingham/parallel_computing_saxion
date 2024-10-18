package nl.saxion.conferenceroom.client;

import com.rabbitmq.client.Channel;
import nl.saxion.conferenceroom.utils.connection.RabbitMQ;

public class ClientConnection {

    private Channel channel;
    private final String DIRECT_EXCHANGE = "direct_client";

    public ClientConnection(Client client) {
        channel = RabbitMQ.connect();

        RabbitMQ.setupDirectExchange(channel, DIRECT_EXCHANGE, client.USER_ID(), "", ClientResponseHandler::handleResponse);

        System.out.println("Client Connected to Exchange. press 0 to exit");
    }

    public Channel getChannel() {
        return channel;
    }
}
