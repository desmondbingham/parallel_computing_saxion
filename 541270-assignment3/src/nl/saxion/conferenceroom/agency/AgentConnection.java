package nl.saxion.conferenceroom.agency;

import com.rabbitmq.client.Channel;
import nl.saxion.conferenceroom.utils.connection.RabbitMQ;

public class AgentConnection {

    private static Channel channel;
    private static final String DIRECT_EXCHANGE = "direct_agent";  // Direct exchange for agent-specific responses
    private static final String SHARED_EXCHANGE = "shared_agent_exchange";  // Shared exchange for round-robin requests
    private static final String FANOUT_EXCHANGE = "building_broadcast";  // Fanout exchange for building broadcasts
    private static final String SHARED_QUEUE_NAME = "shared_queue";  // Shared queue for round-robin distribution
    private static final String AGENCY_NAME = "Agency";
    private static Agent agent;

    public AgentConnection(Agent agent) {
        this.agent = agent;
        channel = RabbitMQ.connect();
        initConnection();
    }

    private void initConnection() {
        String uniqueQueue = agent.name() + "_" + AGENCY_NAME;

        // Set up the shared exchange and queue (round-robin for requests)
        RabbitMQ.setupDirectExchange(channel, SHARED_EXCHANGE, SHARED_QUEUE_NAME, "", RentalAgentResponse::responseHandler);

        // Set up direct exchange and unique queue (for agent-specific responses)
        RabbitMQ.setupDirectExchange(channel, DIRECT_EXCHANGE, uniqueQueue, agent.name(), RentalAgentResponse::responseHandler);

        // Set up fanout exchange to receive building broadcast (e.g., building creation)
        RabbitMQ.setupFanoutExchange(channel, FANOUT_EXCHANGE, uniqueQueue, RentalAgentResponse::responseHandler);

        System.out.println("Rental Agent running: " + agent.name());
    }

    public static Channel getChannel() {
        return channel;
    }

    public static Agent getAgent() {
        return agent;
    }
}
