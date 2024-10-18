package nl.saxion.conferenceroom.agency;

import com.rabbitmq.client.Delivery;
import nl.saxion.conferenceroom.utils.JSONUtils;
import nl.saxion.conferenceroom.utils.models.Message;

import java.io.IOException;

public class RentalAgentResponse {
    private static String latestMessageId = null;
    private static String agentName;

    public static void responseHandler(String s, Delivery delivery) {
            Message message = JSONUtils.toJSONObject(Message.class, delivery.getBody());
            String currentMessageId = message.messageId();
            System.out.println(message);

            if (latestMessageId == null || !latestMessageId.equals(currentMessageId)) {
                latestMessageId = currentMessageId;
                switch (message.head()) {

                    case "LIST_BUILDINGS" -> listBuildings(message);

                    case "CREATE_BUILDING" -> addBuilding(message);
                    case "DELETE_BUILDING" -> deleteBuilding(message);

                    case "REQUEST_RESERVATION" -> requestReservation(message);
                    case "REQUEST_RESERVATION_RESPONSE" -> reservationResponse(message);

                    case "REQUEST_CANCELLATION" -> cancelReservation(message);
                    case "CANCEL_RESERVATION_RESPONSE" -> reservationCancelled(message);

                    case "REQUEST_CONFIRM" -> requestConfirmation(message);
                    case "CONFIRM_RESERVATION_RESPONSE" -> confirmReservation(message);
//                    case "ERROR" -> sendError();
                    default -> System.err.println("Unknown command: " + message.head());
                }
            }

    }
    private static void listBuildings(Message message)  {
        String buildings = JSONUtils.toJSONString(BuildingManager.getInstance().getBuildings());
        Message response = new Message(agentName, message.sender(), "LIST_BUILDINGS_RESPONSE", buildings);
        sendResponse(response);
    }

    public static void addBuilding(Message message) {
        String buildingName = message.body();
        BuildingManager.getInstance().addBuilding(buildingName);
    }

    public static void deleteBuilding(Message message){
        String buildingName = message.body();
        BuildingManager.getInstance().removeBuilding(buildingName);
    }

    private static void requestReservation(Message message) {
        String reservationInfo = message.body();
        String[] parts = reservationInfo.split(" ");

        String buildingName = parts[0];

        if (!BuildingManager.getInstance().buildingExists(buildingName)) {
            String errorMessage = "Building '" + buildingName + "' does not exist.";
            Message errorResponse = new Message(agentName, message.sender(), "REQUEST_RESERVATION_RESPONSE", errorMessage);
            sendResponse(errorResponse);
        }

        String numOfRooms = parts[1];
        if (!numOfRooms.matches("\\d+")) {
            String errorMessage = "Invalid input: number of rooms contains a non-numeric character";
            Message errorResponse = new Message(agentName, message.sender(), "REQUEST_RESERVATION_RESPONSE", errorMessage);
            sendResponse(errorResponse);
        }

        String response = message.sender() + " " + message.body();
        Message messageToTheBuilding = new Message(AgentConnection.getAgent().name(), buildingName, "REQUEST_RESERVATION", response);

        sendResponse(messageToTheBuilding);
    }

    public static void reservationResponse(Message message) {
        System.out.println("Reservation response: " + message.body());
        String reservationInfo = message.body();
        String[] parts = reservationInfo.split(" ");

        String client = parts[parts.length -1];

        Message messageToTheClient = new Message(agentName, client, "REQUEST_RESERVATION_RESPONSE", message.body());
        sendResponse(messageToTheClient);
    }

    private static void confirmReservation(Message message){
        System.out.println(message.body());
        String reservationInfo = message.body();
        String[] parts = reservationInfo.split(" ");
        String response = message.sender() + " " + message.body();
        Message messageToTheBuilding = new Message(agentName, parts[parts.length-1], "CONFIRM_RESERVATION_RESPONSE", response);
        sendResponse(messageToTheBuilding);
    }

    private static void requestConfirmation(Message message){
        System.out.println(message.body());
        String reservationInfo = message.body();
        String[] parts = reservationInfo.split(" ");
        String response = message.sender() + " " + message.body();
        Message messageToTheBuilding = new Message(agentName, parts[0], "REQUEST_CONFIRM", response);
        sendResponse(messageToTheBuilding);

    }

    private static void reservationCancelled(Message message){
        System.out.println("Reservation cancelled response: " + message.body());
        String reservationInfo = message.body();
        String[] parts = reservationInfo.split(" ");

        String client = parts[parts.length-1];

        Message messageToTheClient = new Message(agentName, client, "CANCEL_RESERVATION_RESPONSE", message.body());
        sendResponse(messageToTheClient);
    }

    private static void cancelReservation(Message message){
        String reservationInfo = message.body();
        String[] parts = reservationInfo.split(" ");

        String buildingId = parts[0];
        Message messageToTheBuilding = new Message(
                agentName,
                buildingId,
                "REQUEST_CANCELLATION",
                message.sender() + " " + message.body()
        );

        sendResponse(messageToTheBuilding);
    }

    private static void sendResponse(Message response)  {
        try {
            byte[] messageBytes = JSONUtils.toJSONBytes(response);
            AgentConnection.getChannel().basicPublish("", response.receiver(), null, messageBytes);
            System.out.println("Response sent to " + response.receiver() + ": " + response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void setAgentName(String agentName){
        this.agentName = agentName;
    }
}