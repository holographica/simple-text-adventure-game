package edu.uob;

import java.util.*;

public class UserCommandHandler {
    private String command;
    private ArrayList<String> tokens;
    private Player currentPlayer;
    public static final String[] basicCommandList = {"inventory", "inv","get","drop","goto","look"};
    private GameState gameState;

    // list of basic commands that were found in user input
    private HashSet<String> targetCommands;

    // list of entities that were found in user input
    private HashSet<Artefact> targetArtefacts;

    // list of game characters that were found in user input
    private HashSet<GameCharacter> targetCharacters;

    // list of furniture that was found in user input
    private HashSet<Furniture> targetFurniture;

//    private HashMap<String, Location> targetLocations;

    private HashSet<Location> targetLocations;
    // TODO
    // do i need list of all target entities?

    // list of entities that are accessible to the current player
    private HashMap<String, GameEntity> accessibleEntities;

    // list of locations that are accessible to the current player
    private HashMap<String, Location> accessibleLocations;

    // list of actions that were found in user input
    private HashMap<String, HashSet<GameAction>> targetActions;

    private String responseString;

    // command handler constructor
    public UserCommandHandler(String command, GameState gameState) {
        this.gameState = gameState;

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

    public HashMap<String, GameEntity> getAccessibleEntities(){
        return this.accessibleEntities;
    }
    public HashMap<String, Location> getAccessibleLocations(){
        return this.accessibleLocations;
    }

    public void checkRequiredEntities(HashMap<String,GameEntity> requiredEntities){
        if (requiredEntities.containsKey("health") && this.getCurrentPlayer().getPlayerHealth()>0){
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
        Location currLocation = this.gameState.getLocationByName(locationName);
        this.addAccessibleEntities(currLocation.getAccessibleEntities());
        // add player's current inventory to list of accessible subjects
        for (Artefact e: this.currentPlayer.getInventory().values()){
            this.addAccessibleEntity(e);
        }
        // get health of player?

//        GameEntity health = this.currentPlayer.;
//        this.accessibleEntities.
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
        Location currLocation = this.gameState.getLocationByName(locationName);
        this.addAccessibleLocation(currLocation);
        currLocation.getPaths().keySet().forEach(
                pathTo -> {
                    Location pathLocation = this.gameState.getLocationByName(pathTo);
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
        // CHECK FOR BASIC COMMANDS AND ADD THEM TO BASIC COMMAND LIST
        // IF LIST.SIZE == 1, CALL BASIC COMMAND HANDLER

        // CHECK FOR ENTITIES, ADD THEM TO SUBJECT LIST

        // CHECK FOR ACTION TRIGGERS, ADD THEM TO ACTION LIST
        // IF NOT EMPTY - CALL ACTION HANDLER

        // IF BASIC CMD LIST.SIZE == 1 && ACTION TRIGGER LIST.ISEMPTY:
            // CALL BASIC CMD HANDLER
        // IF BASIC CMD LIST.SIZE==1 && ACTION TRIGGER LIST !EMPTY:
            // RETURN ERROR MSG - TOO MANY ACTIONS/COMMANDS
        // IF BASIC CMD LIST.SIZE >1 :
            // RETURN ERROR MSG - TOO MANY COMMANDS
        // IF BASIC CMD LIST.ISEMPTY && ACTION LIST.SIZE ! EMPTY
            // CALL ACTION HANDLER

        for (String token: getTokens()){

            // first check whether token is a basic command
            // if so, add to current list of basic commands
            checkForBasicCommand(token);

            // next check if token is a valid entity
            // if so, add to correct list of current subject entities
            checkIfEntity(token);

            // TODO
            // next check if token is a valid action trigger phrase
            // ie matches any of the keys in actions hashmap
            // if so, add all actions matching that trigger to set of target actions
            checkIfAction(token);
        }
        checkMultipleWordTriggers();

//        System.out.println("actions: " + gameState.getActions());


        if (!targetCommands.isEmpty()) {
            System.out.println("at least 1 basic command");
            if (targetCommands.size() == 1 && targetActions.isEmpty()) {
                    // call basic command handler
                basicCommandHandler();

                // return?
                // do i return a string ie 'command executed successfully'?
                // or results of executed command

            } else if (targetCommands.size() == 1){
                    // abort
                    // return error msg - too many commands/actions detected
                    setResponseString("too many basic commands/actions detected");
                    return responseString;
            } else {
                // abort
                // return error msg - too many commands detected
                setResponseString("too many basic commands detected");
                return responseString;
            }
        }

        if (!targetActions.isEmpty()) {
//            System.out.println("TARGET ACTIONS NOT EMPTY");
            HashSet<GameAction> uniqueActions = new HashSet<>();
            HashSet<String> triggersToRemove = new HashSet<>();
            // go through each key in the hashmap
            this.targetActions.forEach(
                (trigger, actionSet) -> {
                    System.out.println("Started here");
//                    System.out.println("trigger ie keyphrase: " + trigger + "  | actionset: "+ actionSet + "\n");
                    actionSet.forEach(
                        action -> {
                            checkRequiredEntities(action.getRequiredEntities());
                        }
                    );
                    if (actionSet.isEmpty()) {
//                        System.out.println("empty set - removed " + trigger);
                        triggersToRemove.add(trigger);
                    }

                    else if (actionSet.size() > 1) {
                        setResponseString("Error: too many possible actions");
                    }
                    else {
                        System.out.println("action set : " + actionSet);
                        uniqueActions.addAll(actionSet);
                    }
//
//
//                    System.out.println("target actiohs size: " + targetActions.size());
//                    for (String str: targetActions.keySet()){
//                        System.out.println("key: "+str);
//                    }
//                    System.out.println("\n");

                    // if action set size >1 , error, return
                    // if set size ==0, remove from hashmap
                    // if ==1, do nothing
                }
            );

            triggersToRemove.forEach(
                    trigger -> targetActions.remove(trigger)
            );
            executeAction(uniqueActions);
//            if (uniqueActions.size()==1){
//                System.out.println("target actions size 1!!");
//                executeAction(uniqueActions);
//                // execute action!
//                // TODO
//                //  HANDLE ACTUALLY DOING THE ACTION
//                //  IE: CALL FUNCTIONS!
//
//
//                // execute action here
//                // update gamestate
//                // consume/produce objects as necessary
//            }
//            else {
//                setResponseString("Error: valid commands require exactly one action");
//            }
        }

        return this.responseString;
    }

    public void executeAction(HashSet<GameAction> uniqueActions) {
        if (uniqueActions.size() != 1) {
            setResponseString("Error: valid commands require exactly one action");
        } else {
            uniqueActions.forEach(
                action -> {

                    // get what it consumes
                    // remove this from the location or player
                    // send to storeroom
                    action.getConsumedEntities().forEach(
                        entity -> consumeEntity(entity)
                    );
                    action.getProducedEntities().forEach(
                            entity -> {produceEntity(entity);}
                    );
                    setResponseString(action.getNarration());
                }
            );
        }
    }

    public void consumeEntity(GameEntity entity) {
        Player currPlayer = this.getCurrentPlayer();
        if (entity.getName().equalsIgnoreCase("health")){
            currPlayer.decreasePlayerHealth();
            return;
        }
        Location currLocation = gameState.getLocationByName(currPlayer.getCurrentLocation());
        Location storeroom = gameState.getLocationByName("storeroom");
        if (entity instanceof Artefact) {
            currPlayer.removeFromInventory((Artefact) entity);
        }
        currLocation.removeEntity(entity.getName());
        storeroom.addEntity(entity);
    }

    public void produceEntity(GameEntity entity) {
        if (entity.getName().equalsIgnoreCase("health")) {
            this.getCurrentPlayer().increasePlayerHealth();
            return;
        }
        Location newLocation = gameState.getLocationByName(this.getCurrentPlayer().getCurrentLocation());
        if (!(entity instanceof Location)){
            Location priorLocation = gameState.getEntityLocation(entity.getName());
            priorLocation.removeEntity(entity.getName());
        }
        newLocation.addEntity(entity);
    }



    public void basicCommandHandler(){
        // should have already checked that size of basic cmd list ==1
        // also should have already checked if action list is empty
        // things MUST be in right order


        // TODO: should now check whether subject list ==1 or 0

        // TODO: NEED TO CHECK THAT ENTITIES COME AFTER COMMAND
        //  IF NOT CORRECT ORDER - COMMAND IS INVALID

        // so: for look, inv: all maps must be empty

        // below:
        // checks if 0 target entities
        // then looks for basic command
        // if found in target cmds list - execute cmd
        // otherwise add error msg and return
        if (noTargetEntities()){
            System.out.println("NO TARGETS");
            handleNoEntityCommand();
            return;
        }

        if (singleTargetLocation()){
            System.out.println("ONE LOCO");
            handleGoto();
            return;
        }

        if (singleTargetArtefact()){
            System.out.println("ONE ARTEFACT");
            handleSingleEntityCommand();
            return;
        }

        setResponseString("Error: invalid command detected");


        // call specific command (look, goto etc) handler based on
        // command found in basic cmd list

        // in each basic cmd handler:
            // check if any extraneous entities in entity list
                // if so: abort, print error msg
            // check if all required subjects are present
                // if not: abort, print error msg
            // check if consumed entities are present
                // if not: abort, print error msg

            // then: execute command
            // ensure produced/consumed subjects are handled correctly
            // and added/removed from correct lists / player inventory
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
        Location currLocation = gameState.getLocationByName(gameState.getCurrentPlayer().getCurrentLocation());
        addToResponseString("You are currently in: ");
        addToResponseString(currLocation.getName() + " - ");
        addToResponseString(currLocation.getDescription());
        addToResponseString("\nYou can see: ");
        currLocation.getAccessibleEntities().values().forEach(
                tempEntity -> {
                    addToResponseString(tempEntity.getName() + " - ");
                    addToResponseString(tempEntity.getDescription() + ", ");
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
        this.responseString += "Your inventory contains: ";
        for (Artefact aft: this.getCurrentPlayer().getInventory().values() ){
            addToResponseString(aft.getName() + ", ");
        }
    }

    public void handleGoto(){
        if (!targetCommands.contains("goto")){
            setResponseString("Error: invalid command detected");
            return;
        }
        Location targetLocation = getLocationHelper(this.targetLocations);
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
        Location currLocation = this.gameState.getLocationByName(this.gameState.getCurrentPlayer().getCurrentLocation());
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

                // TODO
                //  this currently checks for spaces in the triggers it's checking
                //  so to avoid probs with 'cut' vs 'cut down'
                //   might have to remove if it causes issues
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

//    public String handleBasicCommand(){}

//    public String handleLookCommand(){
        // what is the command subject?
        // check player current location, paths from location
            // add these to hashmap/set
        // if no subjects in command subject list:
            // valid command - print description of current location
        // if one valid, accessible location in cmd subject list:
            // there MUST be a path to that location from curr location
            // valid command - print desc of that location
        // return error message if:
            // location is not accessible
            // command subjects contains any artefacts, chars, furniture


//    }

//    public String handleGotoCommand(){
//        // CHECK FOR ACTION TRIGGER WORDS?
//        // NOT HERE - PROB IN BASIC COMMAND CHECKER
//
//        // CHECK FOR EXTRANEOUS SUBJECTS?
//
//    }







    // need to check string is valid?



//    public void parseCommand(){
//        if (!singlePlayer){
//            // get token directly after the colon
//            String
//        }
    }







