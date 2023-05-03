package edu.uob;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class to parse and execute all user commands.
 */
public class UserCommandHandler {
    /**
     * A string holding the command taken from user input.
     */
    private String command;
    /**
     * A list of each word in the user input command.
     */
    private List<String> tokens;
    /**
     * A player object to hold details of the player executing the command.
     */
    private Player currentPlayer;
    /**
     * A list of all built-in commands.
     */
    public static final String[] BASIC_COMMAND_LIST = {"inventory","inv","get","drop","goto","look","health"};
    /**
     * An object holding the current game state.
     */
    private final GameState gameState;
    /**
     * A set to contain all built-in commands detected in the user command.
     */
    private final Set<String> targetCommands;
    /**
     * A set to contain all artefact names detected in the user command.
     */
    private final Set<Artefact> targetArtefacts;
    /**
     * A set to contain all game character names detected in the user command.
     */
    private final Set<GameCharacter> targetCharacters;
    /**
     * A set to contain all furniture item names detected in the user command.
     */
    private final Set<Furniture> targetFurniture;
    /**
     * A set to contain all game location names detected in the user command.
     */
    private final Set<Location> targetLocations;
    /**
     * A mapping of all entities accessible to the player
     * to the entity names.
     */
    private Map<String, GameEntity> accessibleEntities;
    /**
     * A mapping of all locations accessible to the player
     * to the location names.
     */
    private Map<String, Location> accessibleLocations;
    /**
     * A mapping of each trigger phrase detected in the user command
     * to a set of the game actions to which they correspond.
     */
    private final Map<String, Set<GameAction>> targetActions;
    /**
     * A string to contain the response that will be shown to the player.
     */
    private String responseString;
    /**
     * A constructor for the class which initialises class variables.
     */
    public UserCommandHandler(final String command, final GameState gameState) {
        this.gameState = gameState;
        setTokens(command);
        this.targetCommands = new HashSet<>();
        this.targetArtefacts = new HashSet<>();
        this.targetCharacters = new HashSet<>();
        this.targetFurniture = new HashSet<>();
        this.targetLocations = new HashSet<>();
        this.accessibleEntities = new HashMap<>();
        this.targetActions = new HashMap<>();
        this.responseString="";
        setAccessibleEntities();
//        setAccessibleLocations();
    }

    private void setTokens(final String command){
        final String[] splitCmd = command.toLowerCase(Locale.ROOT).split(":",2);
        this.currentPlayer = this.gameState.getPlayerByName(splitCmd[0]);
        gameState.setCurrentPlayer(currentPlayer);
        this.tokens = new ArrayList<>(Arrays.stream(splitCmd[1]
                        .split(" "))
                .toList());
        final StringBuilder commandBuilder = new StringBuilder();
        for (final String token: this.tokens){
            commandBuilder.append(token).append(' ');
        }
        this.command = commandBuilder.toString();
    }
    /**
     * A method to check whether the player has access to all entities
     * required to execute their chosen command.
     */
    public void checkRequiredEntities(final Map<String,GameEntity> requiredEntities) throws GameException {
        if (requiredEntities.isEmpty()){
            return;
        }
        if (requiredEntities.containsKey("health") && this.getCurrentPlayer().getHealthAsInt()>0){
            requiredEntities.remove("health");
        }
        if (!accessibleEntities.keySet().containsAll(requiredEntities.keySet())){
            throw new GameException.RequiredEntityException();
        }
    }

    private void setAccessibleEntities(){
        final String locationName = this.currentPlayer.getCurrentLocation();
        final Location currLocation = GameState.getLocationByName(locationName);
        this.addAccessibleEntities(currLocation.getAccessibleEntities());
        for (final Artefact e: this.currentPlayer.getInventory().values()){
            this.addAccessibleEntity(e);
        }
    }

    private void addAccessibleEntity(final GameEntity newEntity){
        this.accessibleEntities.put(newEntity.getName(),newEntity);
    }

    private void addAccessibleEntities(final Map<String, GameEntity> entityList){
        entityList.values().forEach(
                this::addAccessibleEntity
        );
    }
    private void addAccessibleLocation(final Location newLocation){
        this.accessibleLocations.put(newLocation.getName(),newLocation);
    }

//    private void setAccessibleLocations(){
//        this.accessibleLocations = new HashMap<>();
//        final String locationName = this.currentPlayer.getCurrentLocation();
//        final Location currLocation = GameState.getLocationByName(locationName);
//        this.addAccessibleLocation(currLocation);
//        currLocation.getPaths().keySet().forEach(
//                pathTo -> {
//                    final Location pathLocation = GameState.getLocationByName(pathTo);
//                    this.addAccessibleLocation(pathLocation);
//                });
//    }

    public String getCommand() {
        return this.command;
    }
    public List<String> getTokens() {
        return new ArrayList<>(this.tokens);
    }
    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }
    public String getResponseString(){
        return this.responseString;
    }
    /**
     * A method to check the user input command. Each token in the command
     * is checked to see if it is a basic command, entity or action.
     * The command is then handled based on its content.
     */
    public String parseCommand() throws GameException{
        for (final String token: getTokens()){
            checkForBasicCommand(token);
            checkIfEntity(token);
            checkIfAction(token);
        }

        // need:
        // list of target entities
        // target commands

        // target actions + entities
        checkMultipleWordTriggers();
        if (!targetCommands.isEmpty()) {
            if (targetCommands.size() == 1 && targetActions.isEmpty()) {
                BasicCommandParser bp = new BasicCommandParser(command,gameState,targetCommands, getTargetEntities());
                setResponseString(bp.basicCommandHandler());
//                basicCommandHandler();
            } else {
                throw new GameException.ExactlyOneCommandException();
            }
            return this.responseString;
        }

        handleActions();
//        if (!targetActions.isEmpty()) {
//            final HashSet<GameAction> uniqueActions = new HashSet<>();
//            final HashSet<String> triggersToRemove = new HashSet<>();
//            final AtomicBoolean exceptionThrown = new AtomicBoolean(false);
//            this.targetActions.forEach(
//                (trigger, actionSet) -> {
//                    if (exceptionThrown.get()){
//                        return;
//                    }
//                    actionSet.forEach(
//                        action -> {
//                            if (exceptionThrown.get()) {
//                                return;
//                            }
//                            try {
//                                checkRequiredEntities(action.getRequiredEntities());
//                            } catch (GameException e) {
//                                System.out.println("HERE ");
//                                exceptionThrown.set(true);
//                            }
//                        });
//                        if (actionSet.isEmpty()) {
//                            triggersToRemove.add(trigger);
//                        }
//                        else if (actionSet.size() > 1) {
//                            try {
//                                throw new GameException.ExactlyOneCommandException();
//                            } catch (GameException.ExactlyOneCommandException e) {
//                                exceptionThrown.set(true);
//                            }
//                        }
//                        else {
//                            uniqueActions.addAll(actionSet);
//                        }
//                    if (exceptionThrown.get()){
//                        return;
//                    }
//                }
//            );
//            if (!exceptionThrown.get()) {
//                triggersToRemove.forEach(targetActions::remove);
//                executeAction(uniqueActions);
//            }
//        }
        return this.responseString;
    }

    public void handleActions() throws GameException {
        final HashSet<GameAction> uniqueActions = new HashSet<>();
        final HashSet<String> triggersToRemove = new HashSet<>();
        final AtomicBoolean exceptionThrown = new AtomicBoolean(false);
        this.targetActions.forEach(
            (trigger, actionSet) -> {
                if (exceptionThrown.get()){
                    return;
                }
                actionSet.forEach(
                    action -> {
                        if (exceptionThrown.get()) {
                            return;
                        }
                        try {
                            checkRequiredEntities(action.getRequiredEntities());
                        } catch (GameException e) {
                            exceptionThrown.set(true);
                        }
                    });
                    if (actionSet.isEmpty()) {
                        triggersToRemove.add(trigger);
                    }
                    else if (actionSet.size() > 1) {
                        exceptionThrown.set(true);
//                        try {
//                            throw new GameException.ExactlyOneCommandException();
//                        } catch (GameException.ExactlyOneCommandException e) {
//                            exceptionThrown.set(true);
//                        }
                    }
                    else {
                        uniqueActions.addAll(actionSet);
                    }
                if (exceptionThrown.get()){
                    return;
                }
            }
        );
        if (!exceptionThrown.get()) {
            triggersToRemove.forEach(targetActions::remove);
            executeAction(uniqueActions);
        }
    }


    /**
     * A method that attempts to execute the action found in the user command.
     */
    public void executeAction(final Set<GameAction> uniqueActions) throws GameException {
        if (uniqueActions.size() != 1) {
            throw new GameException.ExactlyOneCommandException();
        } else {
            uniqueActions.forEach(
                action -> {
                    action.getConsumedEntities().forEach(
                            this::consumeEntity
                    );
                    if (action.doesConsumeHealth()){
                        consumeHealth();
                    }
                    action.getProducedEntities().forEach(
                            this::produceEntity
                    );
                    if (action.doesProduceHealth()){
                        produceHealth();
                    }
                    setResponseString(action.getNarration());
                }
            );
        }
    }

    /**
     * A helper method to decrease the current player's
     * health when required by an action.
     */
    public void consumeHealth(){
        this.getCurrentPlayer().decreasePlayerHealth();
    }

    /**
     * A helper method to increase the current player's
     * health when required by an action.
     */
    public void produceHealth(){
        this.getCurrentPlayer().increasePlayerHealth();
    }

    /**
     * A helper method to consume a game entity when required by an action.
     * The method removes the entity from its current location and moves it
     * to the storeroom.
     */
    public void consumeEntity(final GameEntity entity) {
//        addToResponseString("CONSUMING: "+entity.getName());
//        System.out.println("CONSUMOING: "+entity.getName());
        if ("health".equalsIgnoreCase(entity.getName())){
            this.getCurrentPlayer().decreasePlayerHealth();
            addToResponseString("Your health was decreased.");
            return;
        }
        final Location currLocation = GameState.getLocationByName(this.getCurrentPlayer().getCurrentLocation());
        final Location storeroom = GameState.getLocationByName("storeroom");
        if (entity instanceof Artefact) {
            this.getCurrentPlayer().removeFromInventory((Artefact) entity);
        }
        if (entity instanceof Location){
//            System.out.println("REMOVING PATH ");
//            addToResponseString("REMOVED PATH TO "+entity.getName());
            currLocation.removePath(entity.getName());
        }
        currLocation.removeEntity(entity.getName());
        storeroom.addEntity(entity);
    }

    /**
     * A helper method to produce a game entity when required by an action.
     * The method removes the entity from its current location and moves it
     * to its new location.
     */
    public void produceEntity(final GameEntity entity) {
        if ("health".equalsIgnoreCase(entity.getName())) {
            this.getCurrentPlayer().increasePlayerHealth();
            addToResponseString("Your health was increased.");
            return;
        }
        final Location newLocation = GameState.getLocationByName(this.getCurrentPlayer().getCurrentLocation());
        if (!(entity instanceof Location)){
            final Location priorLocation = gameState.getEntityLocation(entity.getName());
            priorLocation.removeEntity(entity.getName());
        }
        newLocation.addEntity(entity);
    }

    /**
     * A method that handles basic commands based on
     * the contents of the user input command.
     */
    public void basicCommandHandler() throws GameException {
        if (noTargetEntities()){
            handleNoEntityCommand();
            return;
        }
        if (singleTargetLocation()){
            handleGoto();
            return;
        }
        if (singleTargetArtefact()){
            handleSingleEntityCommand();
            return;
        }
        throw new GameException.InvalidBasicCommandException();
    }

    /**
     * A helper method to check for the presence of target entities
     * within the user input.
     */
    public boolean noTargetEntities(){
        return this.targetArtefacts.isEmpty()
                && this.targetCharacters.isEmpty()
                && this.targetFurniture.isEmpty()
                && this.targetLocations.isEmpty();
    }

    /**
     * A helper method to check whether user input
     * contains exactly one target location.
     */
    public boolean singleTargetLocation(){
        if (this.targetLocations.size()==1){
            return (this.targetCharacters.isEmpty()
                    && this.targetArtefacts.isEmpty()
                    && this.targetFurniture.isEmpty());
        }
        else {
            return false;
        }
    }

    /**
     * A helper method to check whether user input
     * contains exactly one target artefact.
     */
    public boolean singleTargetArtefact(){
        if (this.targetArtefacts.size()==1){
            return (this.targetCharacters.isEmpty()
                    && this.targetLocations.isEmpty()
                    && this.targetFurniture.isEmpty());
        }
        return false;
    }

    /**
     * A helper method to handle basic commands
     * that have no subject entities.
     */
    public void handleNoEntityCommand() throws GameException.InvalidBasicCommandException {
        if (targetCommands.contains("look")){
            handleLook();
        }
        else if (targetCommands.contains("inv")||targetCommands.contains("inventory")){
            handleInv();
        }
        else if (targetCommands.contains("health")){
            handleHealth();
        }
        else {
            throw new GameException.InvalidBasicCommandException();
        }
    }

    /**
     * A helper method to handle basic commands
     * that have a single subject entity.
     */
    public void handleSingleEntityCommand() throws GameException {
        if (targetCommands.contains("get")){
            handleGetDrop(true);
        }
        else if (targetCommands.contains("drop")){
            handleGetDrop(false);
        }
        else {
            throw new GameException.InvalidBasicCommandException();
        }
    }

    /**
     * A method to handle the built-in 'look' command,
     * which gives details of the player's current location.
     */
    public void handleLook(){
        final Location currLocation = GameState.getLocationByName(gameState.getCurrentPlayer().getCurrentLocation());
        addToResponseString("You are currently in: ");
        addToResponseString(currLocation.getName() + " - ");
        addToResponseString(currLocation.getDescription());
        addToResponseString("\nYou can see: ");
        currLocation.getAccessibleEntities().values().forEach(
                tempEntity -> {
                    if (!tempEntity.getName().equals(this.getCurrentPlayer().getName())){
                        addToResponseString(tempEntity.getName() + " - ");
                        addToResponseString(tempEntity.getDescription() + ", ");
                    }
                }
        );
        gameState.getOtherPlayers().values().forEach(
                player -> {
                    if (player.getCurrentLocation().equals(currLocation.getName())){
                        addToResponseString(player.getName() + "- ");
                        addToResponseString(player.getDescription() + ", ");
                    }
                }
        );
        addToResponseString("\nYou can see paths to: ");
        currLocation.getPaths().values().forEach(
                path -> addToResponseString(path.getName()+ ", ")
        );
    }

    /**
     * A method to handle the built-in 'inventory' command,
     * which gives details of the player's inventory.
     */
    public void handleInv(){
        addToResponseString("Your inventory contains: ");
        for (final Artefact aft: this.getCurrentPlayer().getInventory().values() ){
            addToResponseString(aft.getName() + ", ");
        }
    }

    /**
     * A method to handle the built-in 'health' command,
     * which shows the player's current health.
     */
    public void handleHealth(){
        addToResponseString("Your current health is ");
        addToResponseString(gameState.getCurrentPlayer().getPlayerHealth());
    }

    /**
     * A method to handle the built-in 'goto' command,
     * which moves the player to a designated location.
     */
    public void handleGoto() throws GameException {
        if (!targetCommands.contains("goto")){
            throw new GameException.InvalidBasicCommandException();
        }
        final Location targetLocation = getLocationHelper(this.targetLocations);
        if (tokens.indexOf("goto")> tokens.indexOf(targetLocation.getName())){
            throw new GameException.InvalidCommandStructureException();
        }
        if (!this.accessibleLocations.containsValue(targetLocation)){
            throw new GameException.RequiredEntityException();
        }
        this.gameState.getCurrentPlayer().setCurrentLocation(targetLocation.getName());
        addToResponseString(this.gameState.getCurrentPlayer().getName() + " ");
        addToResponseString("Moved to a new location: " + targetLocation.getName());
    }

    /**
     * A method to handle the built-in 'get' and 'drop' command,s
     * which respectively pick up or drop a designated artefact.
     */
    public void handleGetDrop(final boolean get) throws GameException {
        final String cmd = get ? "get":"drop";
        if (!targetCommands.contains(cmd)){
            throw new GameException.InvalidBasicCommandException();
        }
        final Artefact targetArtefact = getArtefactHelper(this.targetArtefacts);
        if (tokens.indexOf(cmd) > tokens.indexOf(targetArtefact.getName())){
            throw new GameException.InvalidCommandStructureException();
        }
        final Location currLocation = GameState.getLocationByName(this.gameState.getCurrentPlayer().getCurrentLocation());
        if (get){
            if (!this.accessibleEntities.containsKey(targetArtefact.getName())) {
                throw new GameException.RequiredEntityException();
            }
            this.gameState.getCurrentPlayer().addToInventory(targetArtefact);
            currLocation.removeArtefact(targetArtefact.getName());
            setResponseString("You picked up a "+targetArtefact.getName());
        } else {
            if (!this.getCurrentPlayer().getInventory().containsKey(targetArtefact.getName())) {
                throw new GameException.RequiredEntityException();
            }
            this.gameState.getCurrentPlayer().removeFromInventory(targetArtefact);
            currLocation.addArtefact(targetArtefact);
            setResponseString("You dropped a "+targetArtefact.getName());
        }
    }

    /**
     * A method to add a message to the response string
     * which is shown to the user after a command has been handled.
     */
    public void addToResponseString(final String toAppend){
        setResponseString(this.getResponseString() + toAppend);
    }

    /**
     * A method to set the response string
     * which is shown to the user after a command has been handled.
     */
    public void setResponseString(final String newString){
        this.responseString = newString;
    }

    /**
     * A helper method that takes a possible entity name,
     * and checks whether the entity exists in the game.
     */
    public void checkIfEntity(final String token) throws GameException.DuplicateSubjectException {
        checkEntity(token, this.targetArtefacts, name -> this.gameState.getAllArtefacts().get(name));
        checkEntity(token, this.targetCharacters, name -> this.gameState.getAllCharacters().get(name));
        checkEntity(token, this.targetFurniture, name -> this.gameState.getAllFurniture().get(name));
        checkEntity(token, this.targetLocations, name -> this.gameState.getAllLocations().get(name));
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
                        try {
                            throw new GameException.MultipleActionException();
                        } catch (GameException.MultipleActionException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    else {
                        this.targetActions.put(trigger, gameState.getActionsByTrigger(trigger));
                    }
                }
            }
        );
    }

    /**
     * A helper method to check whether an entity exists in the game.
     * The method marks user inputs that contain duplicate entities
     * as invalid.
     */
    public <T extends GameEntity> void checkEntity(final String token, final Set<T> entitySet, final EntityChecker<T> entityChecker) throws GameException.DuplicateSubjectException {
        final T entity = entityChecker.getEntityByName(token);
        if (entity != null) {
            if (entitySet.contains(entity)) {
                throw new GameException.DuplicateSubjectException();
            }
            else {
                entitySet.add(entity);
            }
        }
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


}

