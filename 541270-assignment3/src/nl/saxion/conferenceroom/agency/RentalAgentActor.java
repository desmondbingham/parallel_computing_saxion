package nl.saxion.conferenceroom.agency;

import java.util.Scanner;
import java.util.UUID;

public class RentalAgentActor {

    private Agent agent;
    private AgentConnection agentConnection;
    private Scanner s;
    BuildingManager buildingManager;


    public static void main(String[] args ) {
        Agent agent = new Agent(String.valueOf(UUID.randomUUID()));

        AgentConnection agentConnection = new AgentConnection(agent);
        RentalAgentResponse rentalAgentResponse = new RentalAgentResponse();
        rentalAgentResponse.setAgentName(agent.name());

        BuildingManager buildingManager = BuildingManager.getInstance();

        RentalAgentActor rentalAgentActor = new RentalAgentActor(agent, agentConnection, buildingManager);
    }

    public RentalAgentActor(Agent agent, AgentConnection agentConnection, BuildingManager buildingManager) {
        this.agent = agent;
        this.agentConnection = agentConnection;
        this.buildingManager = buildingManager;
    }

}
