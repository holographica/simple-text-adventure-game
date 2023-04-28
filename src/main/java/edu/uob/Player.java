package edu.uob;

import java.util.HashSet;

public class Player extends GameCharacter{

    private HashSet<Artefact> inventory;
    private Location currentLocation;

    public Player(String name, String description) {
        super(name, description);
    }
}
