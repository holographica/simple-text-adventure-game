package edu.uob;

import java.util.HashMap;

public class Player extends GameCharacter{
    private HashMap<String, Artefact> inventory;
    private String currentLocation;
    private int playerHealth;
    public Player(String name, String description) {
        super(name, description);
        this.currentLocation = GameState.getStartLocation().getName();
        this.playerHealth=3;
        this.inventory = new HashMap<>();
    }
    public HashMap<String, Artefact> getInventory(){
        return this.inventory;
    }
    public String getCurrentLocation(){
        return this.currentLocation;
    }
    public String getPlayerHealth(){
        return Integer.toString(this.playerHealth);
    }
    public int getHealthAsInt(){
        return this.playerHealth;
    }
    public void addToInventory(Artefact newArtefact){
        this.inventory.put(newArtefact.getName(), newArtefact);
    }
    public void removeFromInventory(Artefact artefact){
        this.inventory.remove(artefact.getName());
    }
    public void setCurrentLocation(String newLocation){
        this.currentLocation = newLocation;
    }
    public void increasePlayerHealth(){
        if (playerHealth<3){
            this.playerHealth++;
        }
    }
    public void decreasePlayerHealth(){
        if (playerHealth>1){
            this.playerHealth--;
        }
        else {
            this.playerHealth=3;
            final Location currLocation = GameState.getLocationByName(getCurrentLocation());
            if (!this.inventory.isEmpty()) {
                this.inventory.values().forEach(
                        currLocation::addArtefact
                );
                this.inventory = new HashMap<>();
            }
            currLocation.removeEntity(this.getName());
            final Location startLocation = GameState.getStartLocation();
            startLocation.addEntity(this);
            this.setCurrentLocation(startLocation.getName());
        }
    }
}
