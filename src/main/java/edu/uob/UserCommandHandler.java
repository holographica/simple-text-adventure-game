package edu.uob;

import java.util.*;

/**
 * A class to parse and execute all user commands.
 */
public class UserCommandHandler {
    private String command;

    private List<String> tokens;

    public static final String[] BASIC_COMMAND_LIST = {"inventory","inv","get","drop","goto","look","health"};

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
     * A mapping of each trigger phrase detected in the user command
     * to a set of the game actions to which they correspond.
     */
    private final Map<String, Set<GameAction>> targetActions;
    /**
     * Contains the user output.
     */
    private String responseString;

    public UserCommandHandler(final String command, final GameState gameState) {
        this.gameState = gameState;
        setTokens(command);
        this.targetCommands = new HashSet<>();
        this.targetArtefacts = new HashSet<>();
        this.targetCharacters = new HashSet<>();
        this.targetFurniture = new HashSet<>();
        this.targetLocations = new HashSet<>();
        this.targetActions = new HashMap<>();
        this.responseString="";
    }

    /**
     * Splits command into tokens. Extracts and sets the
     * current player, then makes a list of the remaining tokens.
     */
    private void setTokens(final String command){
        final String[] splitCmd = command.toLowerCase(Locale.ROOT).split(":",2);

        Player currPlayer = gameState.getPlayerByName(splitCmd[0]);
        gameState.setCurrentPlayer(currPlayer);
        this.tokens = new ArrayList<>(Arrays.stream(splitCmd[1]
                        .split(" "))
                .toList());
        final StringBuilder commandBuilder = new StringBuilder();
        for (final String token: this.tokens){
            commandBuilder.append(token).append(' ');
        }
        this.command = commandBuilder.toString();
    }

    public String getCommand() {
        return this.command;
    }

    public List<String> getTokens() {
        return new ArrayList<>(this.tokens);
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
        checkMultipleWordTriggers();
        if (!targetCommands.isEmpty()) {
            if (targetCommands.size() == 1 && targetActions.isEmpty()) {
                BasicCommandHandler basicCmdHandler = new BasicCommandHandler(command,gameState,targetCommands,getTargetEntities());
                setResponseString(basicCmdHandler.basicCommandHandler());
            } else {
                throw new GameException.ExactlyOneCommandException();
            }
            return this.responseString;
        }
        ActionHandler actionCmdParser = new ActionHandler(command,gameState,targetActions,getTargetEntities());
        setResponseString(actionCmdParser.handleActions());
        return this.responseString;
    }

    /**
     * A method to set the response string which is shown
     * to the user after a command has been handled.
     */
    public void setResponseString(final String newString){
        this.responseString = newString;
    }

    /**
     * A helper method to check whether an entity exists in the game.
     * The method marks user inputs that contain duplicate entities
     * as invalid, and adds valid entities to their respective sets.
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
                            setResponseString(e.getMessage());
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
     * A helper method that takes a token from user input
     * and checks whether it is found in the list of built-in commands.
     */
    public void checkForBasicCommand(final String token){
        if (Arrays.asList(BASIC_COMMAND_LIST).contains(token)){
            targetCommands.add(token);
        }
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
