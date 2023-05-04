package edu.uob;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActionCommandParser extends CommandParser{

    public ActionCommandParser(String command, GameState gameState,Map<String, Set<GameAction>> targetActions, HashMap<String, GameEntity> targetEntities) {
        super(command, gameState);
        this.targetActions = targetActions;
        this.targetEntities = targetEntities;

    }



    public String handleActions() throws GameException {
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
                    }
                    else {
                        uniqueActions.addAll(actionSet);
                    }
//                    if (exceptionThrown.get()){
//                        return;
//                    }
                }
        );
        if (!exceptionThrown.get()) {
            triggersToRemove.forEach(targetActions::remove);
            executeAction(uniqueActions);
        }
        return getResponseString();
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
}
