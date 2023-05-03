package edu.uob;

import java.util.*;

/**
 * An abstract class that forms a base for the BasicCommandParser
 * and ActionParser classes. It contains necessary
 * variables and methods that both classes need
 */
public abstract class CommandParser {

    /**
     * A string holding the command taken from user input.
     */
    private String command;

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
    protected final Map<String, Set<GameAction>> targetActions;

    /**
     * A string to contain the response that will be shown to the player.
     */
    protected String responseString;

    /**
     * A constructor for the class which initialises class variables.
     */
    public CommandParser(final String command, final GameState gameState) {
        this.gameState = gameState;
        this.command = command;
        this.tokens = Arrays.stream(command.split(" ")).toList();
        this.currentPlayer = gameState.getCurrentPlayer();
        this.targetCommands = new HashSet<>();
        this.targetArtefacts = new HashSet<>();
        this.targetCharacters = new HashSet<>();
        this.targetFurniture = new HashSet<>();
        this.targetLocations = new HashSet<>();
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
     * A method that returns a list of tokens from the user command..
     */
    protected List<String> getTokens() {
        return new ArrayList<>(this.tokens);
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
     * A helper method that takes a token from user input
     * and checks whether it is found in the list of built-in commands.
     */
    public void checkForBasicCommand(final String token){
        if (Arrays.asList(BASIC_COMMAND_LIST).contains(token)){
            targetCommands.add(token);
        }
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

    public HashMap<String,GameEntity> getTargetEntities(){
        HashMap<String, GameEntity> temp = new HashMap<>();
        this.targetArtefacts.forEach(
                item -> temp.put(item.getName(),item)
        );
        this.targetLocations.forEach(
                item -> temp.put(item.getName(),item)
        );
        this.targetFurniture.forEach(
                item -> temp.put(item.getName(),item)
        );
        this.targetCharacters.forEach(
                item -> temp.put(item.getName(),item)
        );
        return temp;
    }

    /**
     * A helper method that takes a possible trigger phrase,
     * and checks whether it corresponds to any game actions.
     */
    public void checkIfAction(final String triggerPhrase) throws GameException.MultipleActionException {
        if (this.gameState.getActions().containsKey(triggerPhrase)){
            if (targetActions.containsKey(triggerPhrase)){
                throw new GameException.MultipleActionException();
            }
            else {
                targetActions.put(triggerPhrase, gameState.getActions().get(triggerPhrase));
            }
        }
    }

    /**
     * A helper method that checks the user input
     * for the possible presence of multi-word action trigger phrases.
     */
    public void checkMultipleWordTriggers(){
        gameState.getActions().forEach(
            (trigger, actionSet) -> {
                if (command.contains(trigger) && trigger.contains(" ")){
                    if (this.targetActions.containsKey(trigger)){
                        handleMultipleActionException();
                    }
                    else {
                        this.targetActions.put(trigger, gameState.getActionsByTrigger(trigger));
                    }
                }
            }
        );
    }

    private void handleMultipleActionException() {
        try {
            throw new GameException.MultipleActionException();
        } catch (GameException.MultipleActionException e) {
            setResponseString(e.getMessage());
        }
    }

}
