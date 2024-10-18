package nl.saxion.conferenceroom.building;

import java.util.UUID;

public class Reservation {
    private String id;
    private String client;
    private int numOfRooms;
    private boolean checkIn;

    // Constructor to initialize the Reservation
    public Reservation(String client, int amountOfRooms) {
        this.id = generateRandomId();
        this.client = client;
        this.numOfRooms = amountOfRooms;
        this.checkIn = false; // Default value for check-in
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getClient() {
        return client;
    }

    public int getNumOfRooms() {
        return numOfRooms;
    }

    public boolean isCheckIn() {
        return checkIn;
    }

    // Setter for check-in
    public void setCheckIn(boolean checkIn) {
        this.checkIn = checkIn;
    }

    // Method to generate a random ID
    private String generateRandomId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(0, 6); // Get the first 6 characters for a shorter ID
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Client: " + client + ", Rooms: " + numOfRooms;
    }
}
