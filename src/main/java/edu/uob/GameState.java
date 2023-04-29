package edu.uob;

import java.util.HashMap;
import java.util.HashSet;

public class GameState {

    // (is it fine to use hashsets? better/worse than arraylist?)
    // maybe use hashmap instead??
    private HashSet<Location> locations;
    private HashSet<GameAction> actions;
    private Location startLocation;
    private HashMap<String, Player> currentPlayers;
    private HashMap<String, GameEntity> entityList;
    private HashMap<String, GameAction> actionList;

    // DO I NEED INDIVIDUAL LISTS OF ARTEFACTS/CHARS/FURNITURE/LOCATIONS?
    // or can i just get them when needed, using methods?

    public GameState(){
        this.locations  = new HashSet<>();
        this.actions = new HashSet<>();
        this.currentPlayers = new HashMap<>();
        this.entityList = new HashMap<>();
        this.actionList = new HashMap<>();
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

    public HashMap<String, Player> getCurrentPlayers(){
        return this.currentPlayers;
    }
    public Player getPlayerByName(String playerName){
        return this.currentPlayers.get(playerName);
    }

    public Location getStartLocation(){
        return this.startLocation;
    }

    public HashMap<String, GameEntity> getAllArtefacts(){
        HashMap<String,GameEntity> artefactList = new HashMap<>();
        this.entityList.values().forEach(
                entity -> {
                    if (entity instanceof Artefact){
                        artefactList.put(entity.getName(),entity);
                    }
                }
        );
        return artefactList;
    }

    public HashMap<String, GameEntity> getAllCharacters(){
        HashMap<String,GameEntity> charList = new HashMap<>();
        this.entityList.values().forEach(
                entity -> {
                    if (entity instanceof GameCharacter){
                        charList.put(entity.getName(),entity);
                    }
                }
        );
        return charList;
    }

    public HashMap<String, GameEntity> getAllFurniture(){
        HashMap<String,GameEntity> furnitureList = new HashMap<>();
        this.entityList.values().forEach(
                entity -> {
                    if (entity instanceof Furniture){
                        furnitureList.put(entity.getName(),entity);
                    }
                }
        );
        return furnitureList;
    }

    public void addLocation(Location newLocation){
        this.locations.add(newLocation);
    }

    public void addAction(GameAction newAction){
        this.actions.add(newAction);
    }

    public void addPlayer(Player newPlayer){
        this.currentPlayers.put(newPlayer.getName(), newPlayer);
    }

    public void setStartLocation(Location location){
        this.startLocation = location;
    }

    public void setEntityList(HashMap<String, GameEntity> entityList){
        this.entityList = entityList;
        getAllArtefacts();
    }

    public void setActionList(HashMap<String, GameAction> actionList){
        this.actionList = actionList;
    }


}
