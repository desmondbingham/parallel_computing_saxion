package nl.saxion.conferenceroom.building;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class BuildingActor {

    private Building building;
    private BuildingConnection buildingConnection;
    private Scanner s;

    public BuildingActor() throws IOException, TimeoutException {
        s = new Scanner(System.in);
        building = initBuilding();
        buildingConnection = new BuildingConnection(building);
    }

    public static void main(String[] args ) throws IOException, TimeoutException {
        new BuildingActor();
    }

    private Building initBuilding() {
        System.out.print("Enter the name of the building: ");
        String name = s.nextLine();

        System.out.print("Enter the amount of rooms in the building: ");
        int numOfRooms = s.nextInt();

        return new Building(name, numOfRooms);
    }


}
