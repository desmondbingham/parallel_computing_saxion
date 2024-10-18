package nl.saxion.conferenceroom.utils.connection;

import com.rabbitmq.client.*;

import java.io.IOException;

import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

public class RabbitMQ {

    public static Channel connect() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            Connection connection = factory.newConnection();
            return connection.createChannel();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setupDirectExchange(Channel channel, String exchange, String queue, String routingKey, BiConsumer<String, Delivery> messageHandler ) {
        try {
            channel.exchangeDeclare(exchange, "direct");
            channel.queueDeclare(queue, true, false, false, null);
            channel.queueBind(queue, exchange, routingKey);
            channel.basicConsume(queue, true, messageHandler::accept, consumerTag -> {});
        } catch (IOException e) {
            System.err.println("Error initializing direct exchange:\n" + e);
        }
    }

    public static void setupFanoutExchange(Channel channel, String exchange, String queue, BiConsumer<String, Delivery> messageHandler ) {
        try {
            channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT, true);
            channel.queueDeclare(queue, true, false, false, null);
            channel.queueBind(queue, exchange, "");
            channel.basicConsume(queue, true, messageHandler::accept, consumerTag -> {});
        } catch (IOException e) {
            System.err.println("Error initializing fanout exchange: " + e.getMessage());

        }
    }

}
