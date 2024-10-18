package nl.saxion.conferenceroom.agency;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import nl.saxion.conferenceroom.utils.connection.RabbitMQ;

public class AgentConnection {

    private static Channel channel;
    private static final String DIRECT_EXCHANGE = "direct_agent";
    private static final String FANOUT_EXCHANGE = "fanout";
    private static final String SHARED_QUEUE_NAME = "rental_agents_queue";
    private static final String AGENCY_NAME= "Agency";
    private static Agent agent;

    public AgentConnection(Agent agent) {
        this.agent = agent;
        channel = RabbitMQ.connect();
        initConnection();
    }

    private void initConnection() {
        String queue = agent.name() + "_" + AGENCY_NAME;

        RabbitMQ.setupFanoutExchange(channel, FANOUT_EXCHANGE, queue, RentalAgentResponse::responseHandler);
        RabbitMQ.setupDirectExchange(channel, DIRECT_EXCHANGE, SHARED_QUEUE_NAME, agent.name(), RentalAgentResponse::responseHandler);

        System.out.println("Rental Agent running: " + agent.name());
    }

    public static Channel getChannel() {
        return channel;
    }

    public static Agent getAgent() {
        return agent;
    }
}