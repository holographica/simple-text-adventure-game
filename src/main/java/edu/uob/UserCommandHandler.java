package edu.uob;

import java.util.*;

public class UserCommandHandler {
    private String command;
    private ArrayList<String> tokens;
    private Player currentPlayer;
    public static final String[] basicCommandList = {"inventory", "inv","get","drop","goto","look","health"};
    private GameState gameState;
    private HashSet<String> targetCommands;
    private HashSet<Artefact> targetArtefacts;
    private HashSet<GameCharacter> targetCharacters;
    private HashSet<Furniture> targetFurniture;
    private HashSet<Location> targetLocations;
    private HashMap<String, GameEntity> accessibleEntities;
    private HashMap<String, Location> accessibleLocations;
    private HashMap<String, HashSet<GameAction>> targetActions;
    private String responseString;

    public UserCommandHandler(String command, GameState gameState) {
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
        setAccessibleLocations();
    }

    public void setTokens(String command){
        String[] splitCmd = command.toLowerCase().split(":",2);
        this.currentPlayer = this.gameState.getPlayerByName(splitCmd[0]);
        this.tokens = new ArrayList<>(Arrays.stream(splitCmd[1]
                        .split(" "))
                .toList());
        StringBuilder commandBuilder = new StringBuilder();
        for (String token: this.tokens){
            commandBuilder.append(token).append(" ");
        }
        this.command = commandBuilder.toString();
    }

    public void checkRequiredEntities(HashMap<String,GameEntity> requiredEntities){
        if (requiredEntities.containsKey("health") && this.getCurrentPlayer().getHealthAsInt()>0){
            requiredEntities.remove("health");
        }
        if (!accessibleEntities.keySet().containsAll(requiredEntities.keySet())){
            setResponseString("Error: don't have access to required entities");
        }
    }

    public void setAccessibleEntities(){
        this.accessibleEntities = new HashMap<>();
        String locationName = this.currentPlayer.getCurrentLocation();
        // add entities at player's current location to list of accessible subjects
        Location currLocation = GameState.getLocationByName(locationName);
        this.addAccessibleEntities(currLocation.getAccessibleEntities());
        // add player's current inventory to list of accessible subjects
        for (Artefact e: this.currentPlayer.getInventory().values()){
            this.addAccessibleEntity(e);
        }
    }

    public void addAccessibleEntity(GameEntity newEntity){
        this.accessibleEntities.put(newEntity.getName(),newEntity);
    }

    public void addAccessibleEntities(HashMap<String, GameEntity> entityList){
        entityList.values().forEach(
                this::addAccessibleEntity
        );
    }
    public void addAccessibleLocation(Location newLocation){
        this.accessibleLocations.put(newLocation.getName(),newLocation);
    }

    public void setAccessibleLocations(){
        this.accessibleLocations = new HashMap<>();
        String locationName = this.currentPlayer.getCurrentLocation();
        Location currLocation = GameState.getLocationByName(locationName);
        this.addAccessibleLocation(currLocation);
        currLocation.getPaths().keySet().forEach(
                pathTo -> {
                    Location pathLocation = GameState.getLocationByName(pathTo);
                    this.addAccessibleLocation(pathLocation);
                });
    }

    public String getCommand() {
        return this.command;
    }
    public ArrayList<String> getTokens() {
        return this.tokens;
    }
    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }
    public String getResponseString(){
        return this.responseString;
    }
    public String parseCommand(){
        for (String token: getTokens()){
            checkForBasicCommand(token);
            checkIfEntity(token);
            checkIfAction(token);
        }
        checkMultipleWordTriggers();

        if (!targetCommands.isEmpty()) {
            System.out.println("at least 1 basic command");
            if (targetCommands.size() == 1 && targetActions.isEmpty()) {
                basicCommandHandler();
                return this.responseString;
            } else if (targetCommands.size() == 1){
                setResponseString("too many basic commands/actions detected");
                return this.responseString;
            } else {
                setResponseString("too many basic commands detected");
                return this.responseString;
            }
        }

        if (!targetActions.isEmpty()) {
            HashSet<GameAction> uniqueActions = new HashSet<>();
            HashSet<String> triggersToRemove = new HashSet<>();
            this.targetActions.forEach(
                (trigger, actionSet) -> {
                    actionSet.forEach(
                        action -> {
                            checkRequiredEntities(action.getRequiredEntities());
                        }
                    );
                    if (actionSet.isEmpty()) {
                        triggersToRemove.add(trigger);
                    }
                    else if (actionSet.size() > 1) {
                        setResponseString("Error: too many possible actions");
                    }
                    else {
                        uniqueActions.addAll(actionSet);
                    }
                }
            );
            triggersToRemove.forEach(
                    trigger -> targetActions.remove(trigger)
            );
            executeAction(uniqueActions);
        }
        return this.responseString;
    }

    public void executeAction(HashSet<GameAction> uniqueActions) {
        if (uniqueActions.size() != 1) {
            setResponseString("Error: valid commands require exactly one action");
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

    public void consumeHealth(){
        this.getCurrentPlayer().decreasePlayerHealth();
    }

    public void produceHealth(){
        this.getCurrentPlayer().increasePlayerHealth();
    }

    public void consumeEntity(GameEntity entity) {
        if ("health".equalsIgnoreCase(entity.getName())){
            this.getCurrentPlayer().decreasePlayerHealth();
            addToResponseString("decreased health");
            return;
        }
        Location currLocation = GameState.getLocationByName(this.getCurrentPlayer().getCurrentLocation());
        Location storeroom = GameState.getLocationByName("storeroom");
        if (entity instanceof Artefact) {
            this.getCurrentPlayer().removeFromInventory((Artefact) entity);
        }
        currLocation.removeEntity(entity.getName());
        storeroom.addEntity(entity);
    }

    public void produceEntity(GameEntity entity) {
        if ("health".equalsIgnoreCase(entity.getName())) {
            this.getCurrentPlayer().increasePlayerHealth();
            addToResponseString("increased health");
            return;
        }
        Location newLocation = GameState.getLocationByName(this.getCurrentPlayer().getCurrentLocation());
        if (!(entity instanceof Location)){
            Location priorLocation = gameState.getEntityLocation(entity.getName());
            priorLocation.removeEntity(entity.getName());
        }
        newLocation.addEntity(entity);
    }

    public void basicCommandHandler(){
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
        setResponseString("Error: invalid command detected");
    }

    public boolean noTargetEntities(){
        return this.targetArtefacts.size() == 0
                && this.targetCharacters.size() == 0
                && this.targetFurniture.size() == 0
                && this.targetLocations.size() == 0;
    }

    public boolean singleTargetLocation(){
        if (this.targetLocations.size()==1){
            return this.targetCharacters.size() == 0
                    && this.targetArtefacts.size() == 0
                    && this.targetFurniture.size() == 0;
        }
        return false;
    }

    public boolean singleTargetArtefact(){
        if (this.targetArtefacts.size()==1){
            return this.targetCharacters.size() == 0
                    && this.targetLocations.size() == 0
                    && this.targetFurniture.size() == 0;
        }
        return false;
    }

    public void handleNoEntityCommand(){
        if (targetCommands.contains("look")){
            handleLook();
        }
        else if (targetCommands.contains("inv")){
            handleInv();
        }
        else if (targetCommands.contains("health")){
            handleHealth();
        }
        else {
            setResponseString("Error: invalid command detected");
        }
    }

    public void handleSingleEntityCommand(){
        if (targetCommands.contains("get")){
            handleGetDrop(true);
        }
        else if (targetCommands.contains("drop")){
            handleGetDrop(false);
        }
        else {
            setResponseString("Error: invalid command detected");
        }
    }

    public void handleLook(){
        Location currLocation = GameState.getLocationByName(gameState.getCurrentPlayer().getCurrentLocation());
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
        addToResponseString("\nYou can see paths to: ");
        currLocation.getPaths().values().forEach(
                path -> {
                    addToResponseString(path.getName()+ ", ");
                }
        );
    }

    public void handleInv(){
        addToResponseString("Your inventory contains: ");
        for (Artefact aft: this.getCurrentPlayer().getInventory().values() ){
            addToResponseString(aft.getName() + ", ");
        }
    }

    public void handleHealth(){
        addToResponseString("Your current health is ");
        addToResponseString(gameState.getCurrentPlayer().getPlayerHealth());
    }

    public void handleGoto(){
        if (!targetCommands.contains("goto")){
            setResponseString("Error: invalid command detected");
            return;
        }
        Location targetLocation = getLocationHelper(this.targetLocations);
        if (tokens.indexOf("goto")< tokens.indexOf(targetLocation.getName())){
            setResponseString("Error: invalid basic command structure");
            return;
        }
        if (!this.accessibleLocations.containsValue(targetLocation)){
            setResponseString("Error: this location is not currently accessible");
            return;
        }
        this.gameState.getCurrentPlayer().setCurrentLocation(targetLocation.getName());
        addToResponseString(this.gameState.getCurrentPlayer().getName() + " ");
        addToResponseString("moved to a new location: " + targetLocation.getName());
    }

    public void handleGetDrop(boolean get){
        String cmd = get ? "get":"drop";
        if (!targetCommands.contains(cmd)){
            setResponseString("Error: invalid command detected");
            return;
        }
        Artefact targetArtefact = getArtefactHelper(this.targetArtefacts);
        Location currLocation = GameState.getLocationByName(this.gameState.getCurrentPlayer().getCurrentLocation());
        if (tokens.indexOf(cmd) < tokens.indexOf(targetArtefact.getName())){
            setResponseString("Error: invalid command structure");
            return;
        }
        if (get){
            if (!this.accessibleEntities.containsKey(targetArtefact.getName())) {
                setResponseString("Error: you can't access this artefact");
                return;
            }
            this.gameState.getCurrentPlayer().addToInventory(targetArtefact);
            currLocation.removeArtefact(targetArtefact.getName());
            setResponseString("You picked up a "+targetArtefact.getName());
        } else {
            if (!this.getCurrentPlayer().getInventory().containsKey(targetArtefact.getName())) {
                setResponseString("Error: you can't access this artefact");
                return;
            }
            this.gameState.getCurrentPlayer().removeFromInventory(targetArtefact);
            currLocation.addArtefact(targetArtefact);
            setResponseString("You dropped a "+targetArtefact.getName());
        }
    }

    public void addToResponseString(String toAppend){
        setResponseString(this.getResponseString() + toAppend);
    }

    public void setResponseString(String newString){
        this.responseString = newString;
    }

    public void checkIfEntity(String token){
        checkEntity(token, this.targetArtefacts, name -> this.gameState.getAllArtefacts().get(name));
        checkEntity(token, this.targetCharacters, name -> this.gameState.getAllCharacters().get(name));
        checkEntity(token, this.targetFurniture, name -> this.gameState.getAllFurniture().get(name));
        checkEntity(token, this.targetLocations, name -> this.gameState.getLocations().get(name));
    }

    public void checkIfAction(String triggerPhrase){
        if (this.gameState.getActions().containsKey(triggerPhrase)){
            if (targetActions.containsKey(triggerPhrase)){
                setResponseString("Error: duplicate actions found in command");
            }
            else {
                targetActions.put(triggerPhrase, gameState.getActions().get(triggerPhrase));
            }
        }
    }

    public void checkMultipleWordTriggers(){
        gameState.getActions().forEach(
            (trigger, actionSet) -> {
                if (command.contains(trigger) && trigger.contains(" ")){
                    if (this.targetActions.containsKey(trigger)){
                        setResponseString("Error: multiple actions found in command");
                    }
                    else {
                        this.targetActions.put(trigger, gameState.getActionsByTrigger(trigger));
                    }
                }
            }
        );
    }

    public <T extends GameEntity> void checkEntity(String token, HashSet<T> entitySet, EntityChecker<T> entityChecker) {
        T entity = entityChecker.getEntityByName(token);
        if (entity != null) {
            if (entitySet.contains(entity)) {
                setResponseString("Error: duplicate subjects found in command");
            }
            else {
                entitySet.add(entity);
            }
        }
    }

    public void checkForBasicCommand(String token){
        if (Arrays.asList(basicCommandList).contains(token)){
            targetCommands.add(token);
        }
    }

    public Location getLocationHelper(HashSet<Location> targetSet) {
        ArrayList<Location> list = new ArrayList<>(targetSet);
        return list.get(0);
    }

    public Artefact getArtefactHelper(HashSet<Artefact> targetSet){
        ArrayList<Artefact> list = new ArrayList<>(targetSet);
        return list.get(0);
    }
}
