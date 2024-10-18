package nl.saxion.conferenceroom.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReservationManager {
    private static ReservationManager instance;
    private List<Reservation> reservations;

    private ReservationManager() {
        reservations = new ArrayList<>();
    }

    public static ReservationManager getInstance() {
        if (instance == null) {
            instance = new ReservationManager();
        }
        return instance;
    }

    public void addReservation(String name, String reference) {
        reservations.add(new Reservation(name, reference));
    }

    public boolean removeReservation(String reference) {
        Iterator<Reservation> iterator = reservations.iterator();
        while (iterator.hasNext()) {
            Reservation reservation = iterator.next();
            if (reservation.getReference().equals(reference)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public static class Reservation {
        private String name;
        private String reference;

        public Reservation(String name, String reference) {
            this.name = name;
            this.reference = reference;
        }

        public String getName() {
            return name;
        }

        public String getReference() {
            return reference;
        }

        @Override
        public String toString() {
            return "Building: " + name + " Booking Reference: " + reference + "\n";
        }
    }
}
