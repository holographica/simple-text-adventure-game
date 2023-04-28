package edu.uob;

import java.util.HashSet;

public class GameState {

    // (is it fine to use hashsets? better/worse than arraylist?)
    private HashSet<Location> locations;
    private HashSet<GameAction> actions;
    private Location startLocation;
    private HashSet<Player> currentPlayers;

    public GameState(){
        this.locations  = new HashSet<>();
        this.actions = new HashSet<>();
        this.currentPlayers = new HashSet<>();
    }

    public HashSet<Location> getLocations(){
        return this.locations;
    }

    // do i need this? helps with paths?
    public Location getLocationByName(String name){
        return locations.stream()
                .filter(loco -> loco.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public HashSet<GameAction> getActions(){
        return this.actions;
    }

    public HashSet<Player> getCurrentPlayers(){
        return this.currentPlayers;
    }

    public Location getStartLocation(){
        return this.startLocation;
    }

    public void addLocation(Location newLocation){
        this.locations.add(newLocation);
    }

    public void addAction(GameAction newAction){
        this.actions.add(newAction);
    }

    public void addPlayer(Player newPlayer){
        this.currentPlayers.add(newPlayer);
    }

    public void setStartLocation(Location location){
        this.startLocation = location;
    }


}
