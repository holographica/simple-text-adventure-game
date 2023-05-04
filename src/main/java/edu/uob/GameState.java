package edu.uob;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class GameState {
    private static HashMap<String, Location> locations;
    private static Location startLocation;
    private final HashMap<String, Player> playerList;
    private Player currentPlayer;
    private static Map<String, GameEntity> entityList;
    private static Map<String, HashSet<GameAction>> actionList;

    public GameState(){
        locations  = new HashMap<>();
        this.playerList = new HashMap<>();
        currentPlayer = getCurrentPlayer();
        entityList = new HashMap<>();
        actionList = new HashMap<>();
    }

    public HashMap<String, Location> getLocations(){
        return locations;
    }

    public static Location getLocationByName(String name){
        return locations.values().stream()
                .filter(loco -> loco.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public HashMap<String, HashSet<GameAction>> getActions(){
        return new HashMap<>(actionList);
    }

    public HashSet<GameAction> getActionsByTrigger(String trigger){
        return actionList.getOrDefault(trigger, null);
    }

    public HashMap<String, Player> getOtherPlayers(){
        final HashMap<String, Player> otherPlayers = new HashMap<>(this.playerList);
        otherPlayers.remove(currentPlayer.getName());
        return otherPlayers;
    }

    public Player getPlayerByName(String playerName){
        return this.playerList.get(playerName.toLowerCase());
    }

    public Player getCurrentPlayer(){
        return this.currentPlayer;
    }

    public static Location getStartLocation(){
        return startLocation;
    }

    public static <T extends GameEntity> HashMap<String, T> getEntitiesByType(Class<T> type) {
        final HashMap<String, T> entitiesByType = new HashMap<>();
        entityList.values().forEach(entity -> {
            if (type.isInstance(entity)) {
                entitiesByType.put(entity.getName(), type.cast(entity));
            }
        });
        return entitiesByType;
    }

    public HashMap<String, Artefact> getAllArtefacts() {
        return getEntitiesByType(Artefact.class);
    }

    public HashMap<String, GameCharacter> getAllCharacters() {
        return getEntitiesByType(GameCharacter.class);
    }

    public HashMap<String, Location> getAllLocations() {
        return getEntitiesByType(Location.class);
    }

    public HashMap<String, Furniture> getAllFurniture() {
        return getEntitiesByType(Furniture.class);
    }

    public static Map<String, GameEntity> getEntityList() {
        return new HashMap<>(entityList);
    }

    public static Map<String, HashSet<GameAction>> getActionList(){
        return new HashMap<>(actionList);
    }

    public void addLocation(Location newLocation){
        locations.put(newLocation.getName(),newLocation);
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

    public void setEntityList(Map<String, GameEntity> newList){
        entityList = new HashMap<>(newList);
        getAllArtefacts();
        getAllFurniture();
        getAllCharacters();
        getAllLocations();
    }

    public void setActionList(Map<String, HashSet<GameAction>> newList){
        actionList = new HashMap<>(newList);
    }

    public Location getEntityLocation(String entityName){
        for (Location location : this.getLocations().values()) {
            if (location.getAccessibleEntities().containsKey(entityName)) {
                return location;
            }
        }
        return null;
    }
}
