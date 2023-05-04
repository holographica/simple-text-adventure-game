package edu.uob;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActionHandler extends CommandHandler {

    public ActionHandler(String command, GameState gameState, Map<String, Set<GameAction>> targetActions, HashMap<String, GameEntity> targetEntities) {
        super(command, gameState);
        this.targetActions = targetActions;
        this.targetEntities = targetEntities;

    }

    public String handleActions() throws GameException {
        ActionHandlerState state = new ActionHandlerState();
        processActions(state);
        if (state.wasExceptionThrown()){
            throw new GameException.RequiredEntityException();
        }
        if (!state.wasExceptionThrown()) {
            state.getTriggers().forEach(targetActions::remove);
            executeAction(state.getUniqueActions());
        }
        return getResponseString();
    }

    private void processActions(ActionHandlerState state) {
        targetActions.forEach((trigger, actionSet) -> {
            if (state.wasExceptionThrown()) {
                return;
            }

            processActionSet(actionSet, state, trigger);
        });
    }

    private void processActionSet(Set<GameAction> actionSet, ActionHandlerState state,String trigger) {
        actionSet.forEach(
            action -> {
                if (state.wasExceptionThrown()) {
                    return;
                }
                try {
                    checkRequiredEntities(action.getRequiredEntities());
                } catch (GameException e) {
                    state.exceptionThrown.set(true);
                }
            }
        );
        processUniqueActionsAndTriggers(state, actionSet, trigger);
    }

    private void processUniqueActionsAndTriggers(ActionHandlerState state, Set<GameAction> actionSet, String trigger) {
        if (actionSet.isEmpty()) {
            state.addTrigger(trigger);
        } else if (actionSet.size() > 1) {
            state.setExceptionThrown(true);
        } else {
            state.setUniqueActions(actionSet);
        }
    }

    public void executeAction(final Set<GameAction> uniqueActions) throws GameException {
        if (uniqueActions.size() != 1) {
            throw new GameException.ExactlyOneCommandException();
        }
        GameAction action = uniqueActions.iterator().next();
        action.getConsumedEntities().forEach(this::consumeEntity);
        if (action.doesConsumeHealth()) {
            consumeHealth();
        }
        action.getProducedEntities().forEach(this::produceEntity);
        if (action.doesProduceHealth()) {
            produceHealth();
        }
        setResponseString(action.getNarration());
    }

    /**
     * A method to check whether the player has access to all entities
     * required to execute their chosen command.
     */
    public void checkRequiredEntities(final Map<String,GameEntity> requiredEntities) throws GameException.RequiredEntityException {
        if (requiredEntities.isEmpty()){
            return;
        }
        if (requiredEntities.containsKey("health") && this.getCurrentPlayer().getHealthAsInt()>0){
            requiredEntities.remove("health");
        }
        // Add all accessible entities and locations to a set, so they can be compared
        HashSet<String> entitySet  = new HashSet<>(accessibleEntities.keySet());
        entitySet.addAll(accessibleLocations.keySet());

        if (!entitySet.containsAll(requiredEntities.keySet())){
            System.out.println("NOT ALL STUFFS");
            throw new GameException.RequiredEntityException();
        }
    }

    public void consumeHealth(){
        this.getCurrentPlayer().decreasePlayerHealth();
    }

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
