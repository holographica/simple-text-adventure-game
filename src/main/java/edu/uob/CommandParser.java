package edu.uob;

import java.util.*;

/**
 * An abstract class that forms a base for the BasicCommandParser
 * and ActionParser classes. It contains necessary
 * variables and methods that both classes need
 */
public abstract class CommandParser {

    /**
     * A list of each word in the user input command.
     */
    protected List<String> tokens;

    /**
     * A player object to hold details of the player executing the command.
     */
    protected Player currentPlayer;

    /**
     * A list of all built-in commands.
     */
    public static final String[] BASIC_COMMAND_LIST = {"inventory","inv","get","drop","goto","look","health"};

    /**
     * An object holding the current game state.
     */
    protected final GameState gameState;

    /**
     * A set to contain all built-in commands detected in the user command.
     */
    protected Set<String> targetCommands;

    /**
     * A set to contain all artefact names detected in the user command.
     */
    protected final Set<Artefact> targetArtefacts;

    /**
     * A set to contain all game character names detected in the user command.
     */
    protected final Set<GameCharacter> targetCharacters;

    /**
     * A set to contain all furniture item names detected in the user command.
     */
    protected final Set<Furniture> targetFurniture;

    /**
     * A set to contain all game location names detected in the user command.
     */
    protected final Set<Location> targetLocations;

    /**
     * A mapping of all target entities found in the user command.
     */
    protected Map<String, GameEntity> targetEntities;

    /**
     * A mapping of all entities accessible to the player
     * to the entity names.
     */
    protected Map<String, GameEntity> accessibleEntities;

    /**
     * A mapping of all locations accessible to the player
     * to the location names.
     */
    protected Map<String, Location> accessibleLocations;

    /**
     * A mapping of each trigger phrase detected in the user command
     * to a set of the game actions to which they correspond.
     */
    protected Map<String, Set<GameAction>> targetActions;

    /**
     * A string to contain the response that will be shown to the player.
     */
    protected String responseString;

    /**
     * A constructor for the class which initialises class variables.
     */
    public CommandParser(final String command, final GameState gameState) {
        this.gameState = gameState;
        this.tokens = Arrays.stream(command.split(" ")).toList();
        this.currentPlayer = gameState.getCurrentPlayer();
        this.targetCommands = new HashSet<>();
        this.targetArtefacts = new HashSet<>();
        this.targetCharacters = new HashSet<>();
        this.targetFurniture = new HashSet<>();
        this.targetLocations = new HashSet<>();
        this.targetEntities = new HashMap<>();
        this.accessibleEntities = new HashMap<>();
        this.accessibleLocations = new HashMap<>();
        this.targetActions = new HashMap<>();
        this.responseString="";
        setAccessibleEntities();
        setAccessibleLocations();
    }

    private void addAccessibleLocation(final Location newLocation){
        this.accessibleLocations.put(newLocation.getName(),newLocation);
    }

    private void setAccessibleLocations(){
        this.accessibleLocations = new HashMap<>();
        final String locationName = this.currentPlayer.getCurrentLocation();
        final Location currLocation = GameState.getLocationByName(locationName);
        this.addAccessibleLocation(currLocation);
        currLocation.getPaths().keySet().forEach(
                pathTo -> {
                    final Location pathLocation = GameState.getLocationByName(pathTo);
                    this.addAccessibleLocation(pathLocation);
                });
    }

    protected void setAccessibleEntities(){
        final String locationName = this.currentPlayer.getCurrentLocation();
        final Location currLocation = GameState.getLocationByName(locationName);
        this.addAccessibleEntities(currLocation.getAccessibleEntities());
        for (final Artefact e: this.currentPlayer.getInventory().values()){
            this.addAccessibleEntity(e);
        }
    }

    protected void addAccessibleEntity(final GameEntity newEntity){
        this.accessibleEntities.put(newEntity.getName(),newEntity);
    }

    protected void addAccessibleEntities(final Map<String, GameEntity> entityList){
        entityList.values().forEach(
                this::addAccessibleEntity
        );
    }

    /**
     * A method that returns the current player.
     */
    protected Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    /**
     * A method that returns the response string
     * which is shown to the user after a command has been handled.
     */
    protected String getResponseString(){
        return this.responseString;
    }

    /**
     * A method to append to the response string
     * which is shown to the user after a command has been handled.
     */
    protected void addToResponseString(final String toAppend){
        setResponseString(this.getResponseString() + toAppend);
    }

    /**
     * A method to set the response string
     * which is shown to the user after a command has been handled.
     */
    protected void setResponseString(final String newString){
        this.responseString = newString;
    }

    /**
     * A helper method that gets the first location in a set.
     */
    public Location getLocationHelper(final Set<Location> targetSet) {
        final ArrayList<Location> list = new ArrayList<>(targetSet);
        return list.get(0);
    }

    /**
     * A helper method that gets the first artefact in a set.
     */
    public Artefact getArtefactHelper(final Set<Artefact> targetSet){
        final ArrayList<Artefact> list = new ArrayList<>(targetSet);
        return list.get(0);
    }

    public void createTargetSets(){
        for (GameEntity e: targetEntities.values()){
            if (e instanceof Artefact){
                this.targetArtefacts.add((Artefact) e);
            }
            else if (e instanceof GameCharacter){
                this.targetCharacters.add((GameCharacter) e);
            }
            else if (e instanceof Location){
                this.targetLocations.add((Location) e);
            }
            else if (e instanceof Furniture){
                this.targetFurniture.add((Furniture) e);
            }
        }
    }
}
