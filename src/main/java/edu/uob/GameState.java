package edu.uob;

import java.util.HashMap;
import java.util.HashSet;

public class GameState {
    private HashMap<String, Location> locations;
    private static Location startLocation;
    private HashMap<String, Player> playerList;
    private Player currentPlayer;
    private static HashMap<String, GameEntity> entityList;
    private static HashMap<String, HashSet<GameAction>> actionList;

    public GameState(){
        this.locations  = new HashMap<>();
        this.playerList = new HashMap<>();
        currentPlayer = getCurrentPlayer();
        entityList = new HashMap<>();
        actionList = new HashMap<>();
    }

    public HashMap<String, Location> getLocations(){
        return this.locations;
    }

    public Location getLocationByName(String name){
        return locations.values().stream()
                .filter(loco -> loco.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public HashMap<String, HashSet<GameAction>> getActions(){
        return actionList;
    }

    public HashSet<GameAction> getActionsByTrigger(String trigger){
        return actionList.getOrDefault(trigger, null);
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
    public static <T extends GameEntity> HashMap<String, T> getEntitiesByType(Class<T> type) {
        HashMap<String, T> entitiesByType = new HashMap<>();
        entityList.values().forEach(entity -> {
            if (type.isInstance(entity)) {
                entitiesByType.put(entity.getName(), type.cast(entity));
            }
        });
//        HashMap<String, Player> playersHere = getOtherPlayersAtLocation();
//        playersHere.values().forEach (
//                player -> entitiesByType.put(player.getName(), type.cast(player))
//        );
        return entitiesByType;
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

    public void addPlayer(Player newPlayer){
        this.playerList.put(newPlayer.getName(), newPlayer);
    }
    public void setCurrentPlayer(Player currPlayer){
        this.currentPlayer = currPlayer;
    }


    // TODO
    //  when do i use this? during look command?
    //  need to finish and use it somewhere as no usage currently
    public HashMap<String, Player> getOtherPlayersAtLocation(){
        HashMap<String, Player> otherPlayers = new HashMap<>();
        playerList.values().forEach(
                player -> otherPlayers.put(player.getName(), player)
        );
        otherPlayers.remove(this.currentPlayer.getName());
        return otherPlayers;
    }

    public void setStartLocation(Location location){
        startLocation = location;
    }

    public void setEntityList(HashMap<String, GameEntity> newList){
        entityList = newList;
        getAllArtefacts();
        getAllFurniture();
        getAllCharacters();

    }

    // TODO
    //  do i actually need this anywhere? probs not? delete if not
    public void setActionList(HashMap<String, HashSet<GameAction>> newList){
        actionList = newList;
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
