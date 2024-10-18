package nl.saxion.conferenceroom.client;

import com.rabbitmq.client.Delivery;
import nl.saxion.conferenceroom.utils.JSONUtils;
import nl.saxion.conferenceroom.utils.models.Message;

public class ClientResponseHandler {


    public static void handleResponse(String s, Delivery delivery){
        String messageBody = new String(delivery.getBody());
        System.out.println("Received message: " + messageBody);

        Message response = JSONUtils.toJSONObject(Message.class, messageBody);
        switch (response.head()) {
            case "LIST_BUILDINGS_RESPONSE":
                System.out.println("List of Buildings: " + response.body());
                break;
            case "REQUEST_RESERVATION_RESPONSE":
                System.out.println("Room reservation result: " + response.body());
                String[] parts = response.body().split(" ");
                ReservationManager.getInstance().addReservation(parts[5], parts[3]);
                break;
            case "CANCEL_RESERVATION_RESPONSE":
                System.out.println("Room cancellation result: " + response.body());
                String[] part = response.body().split(" ");
                System.out.println(part[2]);
                ReservationManager.getInstance().removeReservation(part[2]);
                break;
            case "CONFIRM_RESERVATION_RESPONSE":
                System.out.println("Room confirmation result: " + response.body());
                break;
            default:
                System.out.println("Unknown response type: " + response.head());
                break;
        }
    }
}
