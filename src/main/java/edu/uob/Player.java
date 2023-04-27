package edu.uob;

public class Player extends GameCharacter{

    private Inventory inventory;
    private Location currentLocation;


    public Player(String name, String description) {
        super(name, description);
    }
}
