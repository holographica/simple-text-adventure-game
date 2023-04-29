package edu.uob;

import java.io.IOException;
import java.util.*;

public class HandleCommand {
    private String command;
    private ArrayList<String> tokens;
    private int commandStartIndex;
    private Player currentPlayer;
    public static final String[] basicCommandList = {"inventory", "inv","get","drop","goto","look"};
    private HashSet<String> currentCommands;
    private HashMap<String, GameEntity> currentSubjects;
    private HashMap<String, GameEntity> accessibleSubjects;
    private HashMap<String, Location> accessibleLocations;
    private HashMap<String, GameAction> currentActions;
    private GameState gameState;

    // raw command
    public HandleCommand(String command, GameState gameState) throws IOException{
        this.command = command;
        List<String> tokens = Arrays.stream(command
                            .toLowerCase()
                            .split(" "))
                            .toList();
        this.gameState = gameState;
        this.tokens = new ArrayList<>(tokens);
        this.currentPlayer = gameState.getPlayerByName(this.tokens.get(0));
        this.currentCommands = new HashSet<>();
        this.currentSubjects = new HashMap<>();
        this.accessibleSubjects = new HashMap<>();
        this.currentActions = new HashMap<>();
        this.commandStartIndex = this.tokens.indexOf(":");
        setAccessibleSubjects();
    }

    public HashMap<String, GameEntity> getAccessibleSubjects(){
        return this.accessibleSubjects;
    }

    public HashMap<String, Location> getAccessibleLocations(){
        return this.accessibleLocations;
    }

    public void setAccessibleSubjects(){
                // add player's current inventory to list of accessible subjects
        this.currentPlayer.getInventory().values().forEach(
                this::addAccessibleSubject
        );
        // add player's current location to list of accessible subjects
        Location currLocation = this.currentPlayer.getCurrentLocation();
        addAccessibleSubjects(currLocation.getAccessibleSubjects());

        // add current location,
        // and paths from location,
        // to accessible locations?
    }

    public void addAccessibleSubject(GameEntity newSubject){
        this.accessibleSubjects.put(newSubject.getName(),newSubject);
    }

    public void addAccessibleSubjects(HashMap<String, GameEntity> subjectList){
        subjectList.values().forEach(
                this::addAccessibleSubject
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
            checkForBasicCommand(token);

        }
    }




    public void checkForBasicCommand(String token){
        if (Arrays.asList(basicCommandList).contains(token)){
            currentCommands.add(token);
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

    public String handleGotoCommand(){
        // CHECK FOR ACTION TRIGGER WORDS?
        // NOT HERE - PROB IN BASIC COMMAND CHECKER

        // CHECK FOR EXTRANEOUS SUBJECTS?

    }







    // need to check string is valid?



//    public void parseCommand(){
//        if (!singlePlayer){
//            // get token directly after the colon
//            String
//        }
    }







