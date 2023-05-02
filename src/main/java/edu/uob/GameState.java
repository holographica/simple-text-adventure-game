package edu.uob;

import java.util.HashMap;
import java.util.HashSet;

public class GameState {

    // (is it fine to use hashsets? better/worse than arraylist?)
    // maybe use hashmap instead??
    private HashMap<String, Location> locations;
    private HashSet<GameAction> actions;
    private static Location startLocation;
    private HashMap<String, Player> playerList;
    private Player currentPlayer;
    private HashMap<String, GameEntity> entityList;
    private HashMap<String, GameAction> actionList;

    // DO I NEED INDIVIDUAL LISTS OF ARTEFACTS/CHARS/FURNITURE/LOCATIONS?
    // or can i just get them when needed, using methods?

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

    // do i need this? helps with paths?
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
    public Player getCurrentPlayer() { return this.currentPlayer; }

    public static Location getStartLocation(){
        return startLocation;
    }



    // TODO
    // TODO
    // NB: changed 3 funcs below to return <String, Artefact> not <String, GameEntity>
    // so have to do explicit cast
    // is this bad? change back if necessary

    public HashMap<String, Artefact> getAllArtefacts(){
        HashMap<String,Artefact> artefactList = new HashMap<>();
        this.entityList.values().forEach(
                entity -> {
                    if (entity instanceof Artefact){
                        artefactList.put(entity.getName(), (Artefact) entity);
                    }
                }
        );
        return artefactList;
    }

    public HashMap<String, GameCharacter> getAllCharacters(){
        HashMap<String,GameCharacter> charList = new HashMap<>();
        this.entityList.values().forEach(
                entity -> {
                    if (entity instanceof GameCharacter){
                        charList.put(entity.getName(), (GameCharacter) entity);
                    }
                }
        );
        return charList;
    }

    public HashMap<String, Furniture> getAllFurniture(){
        HashMap<String,Furniture> furnitureList = new HashMap<>();
        this.entityList.values().forEach(
                entity -> {
                    if (entity instanceof Furniture){
                        furnitureList.put(entity.getName(), (Furniture) entity);
                    }
                }
        );
        return furnitureList;
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
    public void setCurrentPlayer(Player currPlayer) { this.currentPlayer = currPlayer; }

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
