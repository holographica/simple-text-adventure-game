package edu.uob;

import java.lang.reflect.Array;
import java.util.*;

public class CommandHandler {
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
    private HashMap<String, GameAction> targetActions;

    private String responseString;

    // command handler constructor
    public CommandHandler(String command, GameState gameState) {
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
        // DO I SET CURRENT PLAYER HERE??

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
            // if so, add to current list of actions

        }

        // TODO
        // finally check whether full command string
        // .contains any of the strings in actions hashmap




        if (!targetCommands.isEmpty()) {
            if (targetCommands.size() == 1 && targetActions.isEmpty()) {
                    // call basic command handler
                basicCommandHandler();

                // return?
                // do i return a string ie 'command executed successfully'?
                // or results of executed command

            } else if (targetCommands.size() == 1){
                    // abort
                    // return error msg - too many commands/actions detected
                    addToResponseString("too many basic commands/actions detected");
            } else {
                // abort
                // return error msg - too many commands detected
                addToResponseString("too many basic commands detected");
            }
        }

        if (!targetActions.isEmpty()){
            // first make list of actions that target action triggers correspond to
            // if size of list >1, abort and print error msg - too many actions
            // otherwise if size of list ==1, this is fine
                // execute action
                // ensure game state, player inv, subjects consumed/produced
                // are updated correctly
        }

        // if got to here: both basic cmd list and action lists are empty
        // ==> abort, print error message

//        System.out.println("\nGOT HERE : no command or action detected");


        // now need to handle command based on rules:
            // IF BASIC CMD LIST.SIZE == 1 && ACTION TRIGGER LIST.ISEMPTY:
                // CALL BASIC CMD HANDLER
            // IF BASIC CMD LIST.SIZE==1 && ACTION TRIGGER LIST !EMPTY:
                // RETURN ERROR MSG - TOO MANY ACTIONS/COMMANDS
            // IF BASIC CMD LIST.SIZE >1 :
                // RETURN ERROR MSG - TOO MANY COMMANDS
            // IF BASIC CMD LIST.ISEMPTY && ACTION LIST.SIZE ! EMPTY
                // CALL ACTION HANDLER

        return this.responseString;
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
            handleNoEntityCommand();
            return;
        }

        if (singleTargetLocation()){
            handleGoto();
            return;
        }

        if (singleTargetArtefact()){
            handleSingleEntityCommand();
        }


        // NB: THEY DON'T WANT LOOK LOOK/GOTO GOTO
        // I'M USING HASHSET FOR COMMANDS - IS THIS A PROBLEM


        if (targetCommands.contains("get")){
            System.out.println("you just got this!");
        }
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

    // CONTAINS NO SUBJECTS
    public void handleInv(){
        this.responseString += "Your inventory contains: ";
        for (Artefact aft: this.getCurrentPlayer().getInventory().values() ){
            addToResponseString(aft.getName() + ", ");
        }
    }


    public void handleNoEntityCommand(){
        if (targetCommands.contains("look")){
            handleLook();
        }
        else if (targetCommands.contains("inv")){
            handleInv();
        }
        else {
            addToResponseString("Error: invalid command detected");
        }
    }

    public void handleSingleEntityCommand(){
        if (targetCommands.contains("get")){
            handleGet();
        }
        else if (targetCommands.contains("drop")){
            handleDrop();
        }
        else {
            addToResponseString("Error: invalid command detected");
        }
    }

    public void handleGoto(){
        if (!targetCommands.contains("goto")){
            addToResponseString("Error: invalid command detected");
            return;
        }
        Location targetLocation = getLocationHelper(this.targetLocations);
        if (!this.accessibleLocations.containsValue(targetLocation)){
            addToResponseString("Error: this location is not currently accessible");
            return;
        }
        this.gameState.getCurrentPlayer().setCurrentLocation(targetLocation.getName());
        addToResponseString(this.gameState.getCurrentPlayer().getName() + " ");
        addToResponseString("moved to a new location: " + targetLocation.getName());
    }

    public void handleGet(){
        if (!targetCommands.contains("get")){
            addToResponseString("Error: invalid command detected");
            return;
        }
        Artefact targetArtefact = getArtefactHelper(this.targetArtefacts);
        if (!this.accessibleEntities.containsKey(targetArtefact.getName())){
            addToResponseString("Error: you can't access this artefact");
            return;
        }
        Location currLocation = this.gameState.getLocationByName(this.gameState.getCurrentPlayer().getCurrentLocation());
        this.gameState.getCurrentPlayer().addToInventory(targetArtefact);
        currLocation.removeArtefact(targetArtefact.getName());
        System.out.println(this.getCurrentPlayer().getInventory().values());
    }

    public void handleDrop(){
        if (!targetCommands.contains("drop")){
            addToResponseString("Error: invalid command detected");
            return;
        }
        Artefact targetArtefact = getArtefactHelper(this.targetArtefacts);
        if (!this.getCurrentPlayer().getInventory().containsKey(targetArtefact.getName())){
            addToResponseString("Error: you don't have this artefact");
            return;
        }
        Location currLocation = this.gameState.getLocationByName(this.gameState.getCurrentPlayer().getCurrentLocation());
        this.gameState.getCurrentPlayer().removeFromInventory(targetArtefact);
        currLocation.addArtefact(targetArtefact);
    }


    public void handleLook(){
        Location currLocation = gameState.getLocationByName(gameState.getCurrentPlayer().getCurrentLocation());
        addToResponseString("You are currently in: ");
        addToResponseString(currLocation.getName() + " - ");
        addToResponseString(currLocation.getDescription()+"\n");
        addToResponseString("You can see: ");
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


    public void addToResponseString(String toAppend){
        setResponseString(this.getResponseString() + toAppend);
    }

    public void setResponseString(String newString){
        this.responseString = newString;
    }

    public void checkIfEntity(String token){
        checkIfArtefact(token);
        checkIfCharacter(token);
        checkIfFurniture(token);
        checkIfLocation(token);
    }

    public void checkIfArtefact(String token){
        gameState.getAllArtefacts().values().forEach(
                artefact -> {
                    if (artefact.getName().equals(token)){
                        if (this.targetArtefacts.contains(artefact)){
                            addToResponseString("Error: duplicate subjects found in command");
                        }
                        else this.targetArtefacts.add(artefact);
                    }
                }
        );
    }

    public void checkIfCharacter(String token){
        gameState.getAllCharacters().values().forEach(
                gameChar -> {
                    if (gameChar.getName().equals(token)){
                        if (this.targetCharacters.contains(gameChar)){
                            addToResponseString("Error: duplicate subjects found in command");
                        }
                        this.targetCharacters.add(gameChar);
                    }
                }
        );
    }

    public void checkIfFurniture(String token){
        gameState.getAllFurniture().values().forEach(
                furniture -> {
                    if (furniture.getName().equals(token)){
                        if (this.targetFurniture.contains(furniture)){
                            addToResponseString("Error: duplicate subjects found in command");
                        }
                        this.targetFurniture.add(furniture);
                }
            }
        );
    }

    public void checkIfLocation(String token){
        gameState.getLocations().values().forEach(
                location -> {
                    if (location.getName().equalsIgnoreCase(token)){
                        this.targetLocations.add(location);
                    }
                }
        );
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







