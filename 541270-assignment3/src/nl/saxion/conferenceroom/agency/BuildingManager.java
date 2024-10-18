package nl.saxion.conferenceroom.agency;

import java.util.ArrayList;
import java.util.List;

public class BuildingManager {

    private static BuildingManager instance;
    private List<String> buildings;

    private BuildingManager() {
        this.buildings = new ArrayList<>();
    }

    public static synchronized BuildingManager getInstance() {
        if (instance == null) {
            instance = new BuildingManager();
        }
        return instance;
    }

    public void addBuilding(String building) {
        buildings.add(building);
        System.out.println("Building added: " + building);
    }

    public List<String> getBuildings() {
        return new ArrayList<>(buildings);
    }

    public void removeBuilding(String building) {
        buildings.remove(building);
        System.out.println("Building removed: " + building);
    }

    public boolean buildingExists(String buildingName) {
        return buildings.contains(buildingName);
    }
}
