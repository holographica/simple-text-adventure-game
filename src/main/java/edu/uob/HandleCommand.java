package edu.uob;

import java.io.IOException;
import java.util.*;

public class HandleCommand {
    private String command;
    private ArrayList<String> tokens;
    private int commandStartIndex;
    private Player currentPlayer;
    public static final String[] basicCommandList = {"inventory", "inv","get","drop","goto","look"};
    private GameState gameState;

    // list of basic commands that were found in user input
    private HashSet<String> targetCommands;

    // list of artefacts that were found in user input
    private HashMap<String, GameEntity> targetArtefacts;

    // list of game characters that were found in user input
    private HashMap<String, GameEntity> targetCharacters;

    // list of furniture that was found in user input
    private HashMap<String, GameEntity> targetFurniture;

    // list of entities that are accessible to the current player
    private HashMap<String, GameEntity> accessibleEntities;

    // list of locations that are accessible to the current player
    private HashMap<String, Location> accessibleLocations;

    // list of actions that were found in user input
    private HashMap<String, GameAction> targetActions;

    // command handler constructor
    public HandleCommand(String command, GameState gameState) throws IOException{
        this.command = command;
        List<String> tokens = Arrays.stream(command
                            .toLowerCase()
                            .split(" "))
                            .toList();
        this.gameState = gameState;
        this.tokens = new ArrayList<>(tokens);
        this.currentPlayer = gameState.getPlayerByName(this.tokens.get(0));
        this.targetCommands = new HashSet<>();
        this.targetArtefacts = new HashMap<>();
        this.targetCharacters = new HashMap<>();
        this.targetFurniture = new HashMap<>();
        this.accessibleEntities = new HashMap<>();
        this.targetActions = new HashMap<>();
        this.commandStartIndex = this.tokens.indexOf(":");
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
                // add player's current inventory to list of accessible subjects
        this.currentPlayer.getInventory().values().forEach(
                this::addAccessibleEntity
        );
        // add entities at player's current location to list of accessible subjects
        Location currLocation = this.currentPlayer.getCurrentLocation();
        addAccessibleEntities(currLocation.getAccessibleEntities());

        // DO I DO THIS? DO I ADD THEM AS SUBJECTS OR LOCATIONS
        // add current location,
        // and paths from location,
        // to accessible locations?
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
        Location currLocation = this.currentPlayer.getCurrentLocation();
        this.addAccessibleLocation(currLocation);
        currLocation.getPaths().values().forEach(
                this::addAccessibleLocation
        );
    }


    public String getCommand() {
        return this.command;
    }

    public ArrayList<String> getTokens() {return this.tokens;}

    public int getCommandStartIndex() { return this.commandStartIndex; }

    public Player getCurrentPlayer(){
        return this.currentPlayer;
    }

    public void parseCommand(){
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

            // next check if token is a valid action
            // if so, add to current list of actions
        }


        if (!targetCommands.isEmpty()) {
            if (targetCommands.size() == 1 && targetActions.isEmpty()) {
                    // call basic command handler
                System.out.println("basic command");

                // return?
                // do i return a string ie 'command executed successfully'?
                // or results of executed command

            } else if (targetCommands.size() == 1){
                    // abort
                    // return error msg - too many commands/actions detected
                    System.out.println("too many actions detected");
            } else {
                // abort
                // return error msg - too many commands detected
                System.out.println("too many commands detected");
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



        // now need to handle command based on rules:
            // IF BASIC CMD LIST.SIZE == 1 && ACTION TRIGGER LIST.ISEMPTY:
                // CALL BASIC CMD HANDLER
            // IF BASIC CMD LIST.SIZE==1 && ACTION TRIGGER LIST !EMPTY:
                // RETURN ERROR MSG - TOO MANY ACTIONS/COMMANDS
            // IF BASIC CMD LIST.SIZE >1 :
                // RETURN ERROR MSG - TOO MANY COMMANDS
            // IF BASIC CMD LIST.ISEMPTY && ACTION LIST.SIZE ! EMPTY
                // CALL ACTION HANDLER


    }

    public void basicCommandHandler(){
        // should have already checked that size of basic cmd list ==1
        // also should have already checked if action list is empty
        // so:

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



    public void checkIfEntity(String token){
        checkIfArtefact(token);
        checkIfCharacter(token);
        checkIfFurniture(token);
    }

    public void checkIfArtefact(String token){
        gameState.getAllArtefacts().values().forEach(
                artefact -> {
                    if (artefact.getName().equals(token)){
                        this.targetArtefacts.put(artefact.getName(),artefact);
                    }
                }
        );
    }

    public void checkIfCharacter(String token){
        gameState.getAllCharacters().values().forEach(
                gameChar -> {
                    if (gameChar.getName().equals(token)){
                        this.targetArtefacts.put(gameChar.getName(),gameChar);
                    }
                }
        );
    }

    public void checkIfFurniture(String token){
        gameState.getAllFurniture().values().forEach(
                furniture -> {
                    if (furniture.getName().equals(token)){
                        this.targetArtefacts.put(furniture.getName(),furniture);
                    }
                }
        );
    }

    public void checkForBasicCommand(String token){
        if (Arrays.asList(basicCommandList).contains(token)){
            targetCommands.add(token);
        }
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







