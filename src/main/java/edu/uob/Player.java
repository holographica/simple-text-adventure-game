package edu.uob;

import java.util.HashMap;
import java.util.HashSet;

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
    public int getPlayerHealth(){
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
        this.playerHealth++;
    }
    public void decreasePlayerHealth(){
        this.playerHealth--;
    }
}
