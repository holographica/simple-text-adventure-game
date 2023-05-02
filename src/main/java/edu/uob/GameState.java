package edu.uob;

import java.util.HashMap;
import java.util.HashSet;

public class GameState {
    private HashMap<String, Location> locations;
    private HashSet<GameAction> actions;
    private static Location startLocation;
    private HashMap<String, Player> playerList;
    private Player currentPlayer;
    private HashMap<String, GameEntity> entityList;
    private HashMap<String, GameAction> actionList;

    public GameState(){
        this.locations  = new HashMap<>();
        this.actions = new HashSet<>();
        this.playerList = new HashMap<>();
        this.entityList = new HashMap<>();
        this.actionList = new HashMap<>();
    }

    public HashMap<String, Location> getLocations(){
        return this.locations;
    }

    public Location getLocationByName(String name){
        return locations.values().stream()
                .filter(loco -> loco.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public HashSet<GameAction> getActions(){
        return this.actions;
    }

    public HashMap<String, Player> getPlayerList(){
        return this.playerList;
    }

    public Player getPlayerByName(String playerName){
        playerName = playerName.toLowerCase();
        return this.playerList.get(playerName);
    }
    public Player getCurrentPlayer(){
        return this.currentPlayer;
    }

    public static Location getStartLocation(){
        return startLocation;
    }
    public <T extends GameEntity> HashMap<String, T> getEntitiesByType(Class<T> type) {
        HashMap<String, T> entityList = new HashMap<>();
        this.entityList.values().forEach(entity -> {
            if (type.isInstance(entity)) {
                entityList.put(entity.getName(), type.cast(entity));
            }
        });
        return entityList;
    }

    public HashMap<String, Artefact> getAllArtefacts() {
        return getEntitiesByType(Artefact.class);
    }

    public HashMap<String, GameCharacter> getAllCharacters() {
        return getEntitiesByType(GameCharacter.class);
    }

    public HashMap<String, Furniture> getAllFurniture() {
        return getEntitiesByType(Furniture.class);
    }

    public void addLocation(Location newLocation){
        this.locations.put(newLocation.getName(),newLocation);
    }

    public void addAction(GameAction newAction){
        this.actions.add(newAction);
    }

    public void addPlayer(Player newPlayer){
        this.playerList.put(newPlayer.getName(), newPlayer);
    }
    public void setCurrentPlayer(Player currPlayer){
        this.currentPlayer = currPlayer;
    }

    public void setStartLocation(Location location){
        startLocation = location;
    }

    public void setEntityList(HashMap<String, GameEntity> entityList){
        this.entityList = entityList;
        getAllArtefacts();
    }

    public void setActionList(HashMap<String, GameAction> actionList){
        this.actionList = actionList;
    }


}
