package nl.saxion.conferenceroom.client;

import com.rabbitmq.client.Channel;
import nl.saxion.conferenceroom.utils.JSONUtils;
import nl.saxion.conferenceroom.utils.models.Message;

import java.io.IOException;
import java.util.Scanner;

public class ClientRequestHandler {

    private Scanner s = new Scanner(System.in);
    private final String AGENCY_EXCHANGE = "client_exchange";
    private Channel channel;
    private String clientId;

    public ClientRequestHandler(Channel channel, String clientId) {
        this.channel = channel;
        this.clientId = clientId;
    }

    public  void startMenuLoop() {
        String option = "";
        while (!option.equals("0")) {
            printMenuOptions();
            option = s.nextLine().trim();
            handleMenuOption(option);
        }
    }

    private void printMenuOptions() {
        System.out.println("Choose an option:");
        System.out.println("1. List Buildings");
        System.out.println("2. Request Reservation");
        System.out.println("3. Cancel Reservation");
        System.out.println("4. Confirm Reservation");
        System.out.println("5. See Reservations");
        System.out.println("0. Exit");
    }

    private void handleMenuOption(String option) {
        switch (option) {
            case "1" -> handleListBuildings();
            case "2" -> handleRequestRoom();
            case "3" -> handleCancelRoom();
            case "4" -> handleConfirmRoom();
            case "5" -> System.out.println(ReservationManager.getInstance().getReservations());
            case "0" -> System.out.println("Exiting...");
            default -> System.err.println("Invalid input, enter a value from the menu.");
        }
    }

    public void handleListBuildings() {
        Message message = createMessage(clientId, "LIST_BUILDINGS", "");
        sendRequestToAgent(message);
    }

    public void handleRequestRoom() {
        String buildingName = getInput("Enter the name of the building:");
        String numRooms = getInput("How many rooms would you like to reserve?");
        String messageBody = String.format(buildingName +" " + numRooms);

        Message message = createMessage(clientId, "REQUEST_RESERVATION", messageBody);
        sendRequestToAgent(message);
    }

    public void handleCancelRoom() {
        String buildingName = getInput("Enter the building name:");
        String reservationId = getInput("Enter the reservation ID to cancel:");
        Message message = createMessage(clientId, "REQUEST_CANCELLATION", buildingName + " " + reservationId);
        sendRequestToAgent(message);
    }

    public void handleConfirmRoom() {
        String buildingName = getInput("Enter the building name:");
        String reservationId = getInput("Enter the reservation ID to confirm:");
        Message message = createMessage(clientId, "REQUEST_CONFIRM", buildingName + " " + reservationId);
        sendRequestToAgent(message);
    }
    private  String getInput(String prompt) {
        System.out.println(prompt);
        return s.nextLine().trim();
    }

    private Message createMessage(String clientId, String requestType, String body) {
        return new Message(clientId, AGENCY_EXCHANGE, requestType, body);
    }

    public  void sendRequestToAgent(Message message) {
        try {
            byte[] bytes = JSONUtils.toJSONBytes(message);
            channel.basicPublish(AGENCY_EXCHANGE, "", null, bytes);

            System.out.println("Request sent: " + message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
