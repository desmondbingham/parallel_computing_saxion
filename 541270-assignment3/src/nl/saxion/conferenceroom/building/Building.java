package nl.saxion.conferenceroom.building;

import java.util.ArrayList;
import java.util.List;

public class Building {
    private String name;
    private int totalRooms;
    private int availableRooms;
    private static List<Reservation> reservations;

    public Building(String name, int numOfRooms) {
        this.name = name;
        this.totalRooms = numOfRooms;
        this.availableRooms = numOfRooms;
        this.reservations = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getTotalRooms() {
        return totalRooms;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTotalRooms(int totalRooms) {
        this.totalRooms = totalRooms;
        this.availableRooms = totalRooms;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public void addReservation(Reservation reservation) {
        if (reservation.getNumOfRooms() <= availableRooms) {
            reservations.add(reservation);
            availableRooms -= reservation.getNumOfRooms();
        } else {
            System.out.println("Not enough rooms available for this reservation.");
        }
    }

    public void removeReservation(Reservation reservation) {
        if (reservations.remove(reservation)) {
            availableRooms += reservation.getNumOfRooms();
        }
    }

    public void updateRoomAvailability(int amount, boolean isIncrease) {
        if (isIncrease) {
            availableRooms += amount;
        } else {
            availableRooms -= amount;
        }

        if (availableRooms > totalRooms) {
            availableRooms = totalRooms;
        }
        System.out.println("Available rooms updated. Current available: " + availableRooms);
    }

    public static Reservation findReservationById(String reservationId) {
        for (Reservation reservation : reservations) {
            if (reservation.getId().equals(reservationId)) {
                return reservation;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Building: " + name + ", Total rooms: " + totalRooms + ", Available rooms: " + availableRooms + ", Reservations: " + reservations.size();
    }
}
