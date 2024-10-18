package nl.saxion.conferenceroom.building;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import nl.saxion.conferenceroom.utils.JSONUtils;
import nl.saxion.conferenceroom.utils.models.Message;

import java.io.IOException;

public class BuildingResponse {

    private static Building building;
    private static Channel channel;

    public BuildingResponse(Building building, Channel channel) {
        BuildingResponse.building = building;
        BuildingResponse.channel = channel;
    }


    public static void handleResponse(String consumerTag, Delivery delivery)  {
        String jsonMessage = new String(delivery.getBody());
        Message message = JSONUtils.toJSONObject(Message.class, jsonMessage);
        System.out.println(message);

        switch (message.head()) {
            case "REQUEST_RESERVATION" -> handleReservationRequest(message);
            case "REQUEST_CANCELLATION" -> handleCancellationRequest(message);
            case "REQUEST_CONFIRM" -> handleConfirmationRequest(message);
            case "BUILDING_RESPONSE" -> System.out.println(message.body());
            default -> System.out.println("Received an unexpected message: " + message);
        }
    }

    private static void handleReservationRequest(Message message)  {
        String reservationDetails = message.body();
        String[] detailsArray = reservationDetails.split(" ");
        int requestedRooms = Integer.parseInt(detailsArray[2]);
        if (requestedRooms > 0 && requestedRooms <= building.getAvailableRooms()) {
            Reservation reservation = new Reservation(detailsArray[0], requestedRooms);
            building.addReservation(reservation);

            String confirmation ="Successful Reservation ReservationId: " + reservation.getId() + " Building: " + building.getName() + " Rooms: " + reservation.getNumOfRooms() + " ClientId: " + detailsArray[0];

            Message successResponse = new Message(building.getName(), message.sender(), "REQUEST_RESERVATION_RESPONSE", confirmation);
            sendMessageToRentalAgent(successResponse, message.sender());
            System.out.println(successResponse);
        } else {
            String errorResponse =" Unsuccessful Reservation: Not enough rooms available. ClientId: " + detailsArray[0];
            Message errorMessage = new Message(building.getName(), message.sender(), "REQUEST_RESERVATION_RESPONSE", errorResponse);
            sendMessageToRentalAgent(errorMessage, message.sender());
            System.out.println(errorMessage);
        }
    }

    private static void handleConfirmationRequest(Message response) {

        String confirmationDetails = response.body();
        String[] detailsArray = confirmationDetails.split(" ");

            String reservationId = detailsArray[2];
            String clientId = detailsArray[0];

            Reservation reservation = Building.findReservationById(reservationId);

            if (reservation != null && reservation.getClient().equals(clientId)) {
                reservation.setCheckIn(true);

                String confirmation = " Reservation: " + reservation.getId() + " has been successfully confirmed. You are now checked in." + " ClientId: " + clientId;

                Message confirmationResponse = new Message(building.getName(), response.sender(), "CONFIRM_RESERVATION_RESPONSE", confirmation);
                sendMessageToRentalAgent(confirmationResponse, response.sender());
                System.out.println(confirmation);
            } else {
                String errorMessage = "Confirmation failed for ReservationId: " + reservationId +
                        ". It does not exist or does not belong to ClientId: " + clientId;
                Message errorResponse = new Message(building.getName(), response.sender(), "CONFIRM_RESERVATION_RESPONSE", errorMessage);
                sendMessageToRentalAgent(errorResponse, response.sender());
                System.out.println(errorMessage);
            }

    }

        private static void handleCancellationRequest(Message response)  {
            String cancellationDetails = response.body();
            String[] detailsArray = cancellationDetails.split(" ");

                String reservationId = detailsArray[2];
                String clientId = detailsArray[0];

                Reservation reservation = Building.findReservationById(reservationId);

                if (reservation != null && reservation.getClient().equals(clientId)) {
                    building.removeReservation(reservation);
                    building.updateRoomAvailability(reservation.getNumOfRooms(), true);

                    String confirmation = " Reservation: " + reservation.getId() + " has been successfully cancelled." + " ClientId: " + clientId ;

                    Message cancellationResponse = new Message(building.getName(), response.sender(), "CANCEL_RESERVATION_RESPONSE", confirmation);
                    sendMessageToRentalAgent(cancellationResponse, response.sender());
                    System.out.println(confirmation);

                } else {
                    String errorMessage = "Cancellation failed for ReservationId: " + reservationId +
                            ". It does not exist or does not belong to ClientId: " + clientId ;
                    Message errorResponse = new Message(building.getName(), response.sender(), "CANCEL_RESERVATION_RESPONSE", errorMessage);
                    sendMessageToRentalAgent(errorResponse, response.sender());
                    System.out.println(errorMessage);
                }

        }


    private static void sendMessageToRentalAgent(Message message, String receiver)  {
        System.out.println("BIG TITTIES");
        try {
            byte[] bytes = JSONUtils.toJSONBytes(message);
            channel.basicPublish("direct_agent", receiver, null, bytes);
            System.out.println("Response sent to " + receiver + ": " + message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}