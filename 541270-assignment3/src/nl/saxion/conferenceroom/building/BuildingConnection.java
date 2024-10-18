package nl.saxion.conferenceroom.building;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import nl.saxion.conferenceroom.utils.JSONUtils;
import nl.saxion.conferenceroom.utils.connection.RabbitMQ;
import nl.saxion.conferenceroom.utils.models.Message;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BuildingConnection {

    private final Channel channel;
    String DIRECT_EXCHANGE = "direct_building";
    String FANOUT_EXCHANGE = "fanout";
    private static final String AGENCY_NAME= "Agency";
    private Building building;


    public BuildingConnection(Building building) throws IOException, TimeoutException {
        this.building = building;

        channel = RabbitMQ.connect();

        String queue = building.getName() + "_" + AGENCY_NAME;
        BuildingResponse buildingResponse = new BuildingResponse(building, channel);

        RabbitMQ.setupDirectExchange(channel, DIRECT_EXCHANGE, building.getName(), "", BuildingResponse::handleResponse);

        publishToAll(building, "CREATE_BUILDING");

        System.out.println("Building Connected to Exchange. Name: " + building.getName() + ", Rooms: " + building.getTotalRooms() );

        Runtime.getRuntime().addShutdownHook(new Thread(this::deleteBuilding));
    }

    public Channel getChannel() {
        return channel;
    }

    public void publishToAll(Building building, String head) throws IOException {
        Message message = new Message(building.getName(), "", head , building.getName());
        byte[] bytes = JSONUtils.toJSONBytes(message);
        channel.basicPublish(FANOUT_EXCHANGE, "", null, bytes);
    }

    private void deleteBuilding() {
        try {
            if (channel != null && channel.isOpen()) {
                publishToAll(building, "DELETE_BUILDING");
            } else {
                System.err.println("Channel is already closed, unable to send DELETE_BUILDING message.");
            }
        } catch (IOException e) {
            System.err.println("Error deleting building: " + e.getMessage());
        }
    }

}
