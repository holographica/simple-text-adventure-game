package edu.uob;

import java.util.*;

public class BasicCommandHandler extends CommandHandler {
    private final Map<String, GameEntity> targetEntities;

    public BasicCommandHandler(String command, GameState gameState, Set<String> targetCommands, HashMap<String, GameEntity> targetEntities) {
        super(command,gameState);
        this.targetCommands = new HashSet<>(targetCommands);
        this.targetEntities = new HashMap<>(targetEntities);
        createTargetSets();
    }

    /**
     * A method that handles basic commands based on
     * the contents of the user input command.
     */
    public String basicCommandHandler() throws GameException {
        if (noTargetEntities()) {
            handleNoEntityCommand();
            return getResponseString();
        }
        if (singleTargetLocation()) {
            handleGoto();
            return getResponseString();
        }
        if (singleTargetArtefact()) {
            handleSingleEntityCommand();
            return getResponseString();
        }
        throw new GameException.NoValidContentException();
    }

    /**
     * A helper method to check for the presence of target entities
     * within the user input.
     */
    public boolean noTargetEntities() {
        return this.targetArtefacts.isEmpty()
                && this.targetCharacters.isEmpty()
                && this.targetFurniture.isEmpty()
                && this.targetLocations.isEmpty();
    }

    /**
     * A helper method to check whether user input
     * contains exactly one target location.
     */
    public boolean singleTargetLocation() {
        if (this.targetLocations.size() == 1) {
            return (this.targetCharacters.isEmpty()
                    && this.targetArtefacts.isEmpty()
                    && this.targetFurniture.isEmpty());
        } else {
            return false;
        }
    }

    /**
     * A helper method to check whether user input
     * contains exactly one target artefact.
     */
    public boolean singleTargetArtefact() {
        if (this.targetArtefacts.size() == 1) {
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
    public void handleNoEntityCommand() throws GameException.NoValidContentException {
        if (targetCommands.contains("look")) {
            handleLook();
        } else if (targetCommands.contains("inv") || targetCommands.contains("inventory")) {
            handleInv();
        } else if (targetCommands.contains("health")) {
            handleHealth();
        } else {
            throw new GameException.NoValidContentException();
        }
    }

    /**
     * A helper method to handle basic commands
     * that have a single subject entity.
     */
    public void handleSingleEntityCommand() throws GameException {
        if (targetCommands.contains("get")) {
            handleGetDrop(true);
        } else if (targetCommands.contains("drop")) {
            handleGetDrop(false);
        } else {
            throw new GameException.NoValidContentException();
        }
    }

    /**
     * A method to handle the built-in 'look' command,
     * which gives details of the player's current location.
     */
    public void handleLook() {
        final Location currLocation = GameState.getLocationByName(gameState.getCurrentPlayer().getCurrentLocation());
        addToResponseString("You are currently in: ");
        addToResponseString(currLocation.getName() + " - ");
        addToResponseString(currLocation.getDescription());
        addToResponseString("\nYou can see: ");
        currLocation.getAccessibleEntities().values().forEach(
                tempEntity -> {
                    if (!tempEntity.getName().equals(this.getCurrentPlayer().getName())) {
                        addToResponseString(tempEntity.getName() + " - ");
                        addToResponseString(tempEntity.getDescription() + ", ");
                    }
                }
        );
        gameState.getOtherPlayers().values().forEach(
                player -> {
                    if (player.getCurrentLocation().equals(currLocation.getName())) {
                        addToResponseString(player.getName() + "- ");
                        addToResponseString(player.getDescription() + ", ");
                    }
                }
        );
        addToResponseString("\nYou can see paths to: ");
        currLocation.getPaths().values().forEach(
                path -> addToResponseString(path.getName() + ", ")
        );
    }

    /**
     * A method to handle the built-in 'inventory' command,
     * which gives details of the player's inventory.
     */
    public void handleInv() {
        addToResponseString("Your inventory contains: ");
        for (final Artefact aft : this.getCurrentPlayer().getInventory().values()) {
            addToResponseString(aft.getName() + ", ");
        }
    }

    /**
     * A method to handle the built-in 'health' command,
     * which shows the player's current health.
     */
    public void handleHealth() {
        addToResponseString("Your current health is ");
        addToResponseString(gameState.getCurrentPlayer().getPlayerHealth());
    }

    /**
     * A method to handle the built-in 'goto' command,
     * which moves the player to a designated location.
     */
    public void handleGoto() throws GameException {
        final Location targetLocation = getLocationHelper(this.targetLocations);
        if (tokens.indexOf("goto") > tokens.indexOf(targetLocation.getName())) {
            throw new GameException.InvalidCommandStructureException();
        }
        if (!this.accessibleLocations.containsValue(targetLocation)) {
            throw new GameException.RequiredEntityException();
        }
        this.gameState.getCurrentPlayer().setCurrentLocation(targetLocation.getName());
        addToResponseString(this.gameState.getCurrentPlayer().getName() + " ");
        addToResponseString("Moved to a new location: " + targetLocation.getName());
    }

    /**
     * A method to handle the built-in 'get' and 'drop' commands,
     * which respectively pick up or drop a designated artefact.
     */
    public void handleGetDrop(final boolean get) throws GameException {
        final String cmd = get ? "get" : "drop";
        final Artefact targetArtefact = getArtefactHelper(this.targetArtefacts);
        if (tokens.indexOf(cmd) > tokens.indexOf(targetArtefact.getName())) {
            throw new GameException.InvalidCommandStructureException();
        }
        final Location currLocation = GameState.getLocationByName(this.gameState.getCurrentPlayer().getCurrentLocation());
        if (get) {
            if (!this.accessibleEntities.containsKey(targetArtefact.getName())) {
                throw new GameException.RequiredEntityException();
            }
            this.gameState.getCurrentPlayer().addToInventory(targetArtefact);
            currLocation.removeArtefact(targetArtefact.getName());
            setResponseString("You picked up a " + targetArtefact.getName());
        } else {
            if (!this.getCurrentPlayer().getInventory().containsKey(targetArtefact.getName())) {
                throw new GameException.RequiredEntityException();
            }
            this.gameState.getCurrentPlayer().removeFromInventory(targetArtefact);
            currLocation.addArtefact(targetArtefact);
            setResponseString("You dropped a " + targetArtefact.getName());
        }
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
