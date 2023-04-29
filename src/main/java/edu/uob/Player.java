package edu.uob;

import java.util.HashMap;
import java.util.HashSet;

public class Player extends GameCharacter{

    private HashMap<String, Artefact> inventory;
    private Location currentLocation;

    public Player(String name, String description) {
        super(name, description);
    }

    public HashMap<String, Artefact> getInventory(){
        return this.inventory;
    }

    public Location getCurrentLocation(){
        return this.currentLocation;
    }

    public void addToInventory(Artefact newArtefact){
        this.inventory.put(newArtefact.getName(), newArtefact);
    }

    public void setCurrentLocation(Location newLocation){
        this.currentLocation = newLocation;
    }
}
