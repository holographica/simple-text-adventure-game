package edu.uob;

import java.util.*;

/**
 * A class to hold instances of game actions.
 * The class holds various action parameters,
 * including subject entities and trigger phrases.
 */
public class GameAction {
    /**
     * Holds the message returned when the action is successfully executed.
     */
    private String narration;
    /**
     * A set containing the action's trigger phrases.
     */
    private final Set<String> triggers;
    /**
     * A set containing entities that are designated
     * as subjects of the action.
     */
    private final Set<GameEntity> subjectEntities;
    /**
     * A set containing entities that are designated
     * to be consumed by the action.
     */
    private final Set<GameEntity> consumedEntities;
    /**
     * A set containing entities that are designated
     * to be produced by the action.
     */
    private final Set<GameEntity> producedEntities;
    /**
     * A flag to denote whether the action consumes health.
     */
    private boolean consumesHealth;
    /**
     * A flag to denote whether the action produces health.
     */
    private boolean producesHealth;

    /**
     * A class constructor for a game action, where
     * class variables are initialised.
     */
    public GameAction(){
        this.narration = "";
        this.triggers = new HashSet<>();
        this.subjectEntities = new HashSet<>();
        this.consumedEntities = new HashSet<>();
        this.producedEntities = new HashSet<>();
        this.consumesHealth = false;
        this.producesHealth = false;
    }

    @Override
    public boolean equals(final Object actionToCompare) {
        boolean doesEqual=true;
        if (this == actionToCompare) {
            return doesEqual;
        }
        if (actionToCompare == null || this.getClass() != actionToCompare.getClass()) {
            doesEqual = false;
        }
        final GameAction newAction = (GameAction) actionToCompare;
        if (!Objects.equals(this.triggers, newAction.triggers)) {
            doesEqual = false;
        }
        else if (!Objects.equals(this.subjectEntities, newAction.subjectEntities)){
            doesEqual = false;
        }
        else if (!Objects.equals(this.consumedEntities, newAction.consumedEntities)){
            doesEqual = false;
        }
        else if (!Objects.equals(this.producedEntities, newAction.producedEntities)){
            doesEqual = false;
        }
        else if (!Objects.equals(this.narration, newAction.narration)){
            doesEqual = false;
        }
        return doesEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(triggers, subjectEntities, consumedEntities, producedEntities, narration);
    }


    public String getNarration(){
        return this.narration;
    }

    public void setNarration(final String newNarration){
        this.narration = newNarration;
    }

    /**
     * A method to get all trigger phrases of an action.
     */
    public Set<String> getTriggers(){
        return new HashSet<>(this.triggers);
    }

    /**
     * A method to add a trigger phrase to the list of trigger
     * phrases for a given game action instance.
     */
    public void addTrigger(final String triggerPhrase){
        this.triggers.add(triggerPhrase);
    }

    /**
     * A method to add an entity to the list of subject entities
     * for a given game action instance.
     */
    public void addSubjectEntity(final String entityName){
        final GameEntity foundEntity = GameState.getEntitiesByType(GameEntity.class).get(entityName);
        if (foundEntity != null){
            this.subjectEntities.add(foundEntity);
        }
    }

    /**
     * A method to get the list of consumed entities
     * for a given game action instance.
     */
    public Set<GameEntity> getConsumedEntities() {
        return new HashSet<>(this.consumedEntities);
    }

    /**
     * A method to add an entity to the list of consumed entities
     * for a given game action instance.
     */
    public void addConsumedEntity(final String entityName){
        final GameEntity foundEntity = GameState.getEntitiesByType(GameEntity.class).get(entityName);
        if (foundEntity != null){
            this.consumedEntities.add(foundEntity);
        }
        final Location location = GameState.getLocationByName(entityName);
        if (location != null){
            System.out.println("ADDED: "+entityName);
            this.consumedEntities.add(location);
        }
        if ("health".equalsIgnoreCase(entityName)){
            this.setConsumesHealth();
        }
    }

    /**
     * A method to get the list of produced entities
     * for a given game action instance.
     */
    public Set<GameEntity> getProducedEntities() {
        return new HashSet<>(this.producedEntities);
    }


    /**
     * A method to add an entity to the list of produced entities
     * for a given game action instance.
     */
    public void addProducedEntity(final String entityName){
        final GameEntity foundEntity = GameState.getEntitiesByType(GameEntity.class).get(entityName);
        if (foundEntity != null){
            this.producedEntities.add(foundEntity);
        }
        else if ("health".equalsIgnoreCase(entityName)) {
            this.setProducesHealth();
        }
    }

    /**
     * A method to set the flag that denotes
     * whether the action consumes health.
     */
    public void setConsumesHealth(){
        this.consumesHealth=true;
    }

    /**
     * A method to check whether the action consumes health.
     */
    public boolean doesConsumeHealth(){
        return this.consumesHealth;
    }

    /**
     * A method to set the flag that denotes
     * whether the action produces health.
     */
    public void setProducesHealth(){
        this.producesHealth=true;
    }

    /**
     * A method to check whether the action produces health.
     */
    public boolean doesProduceHealth(){
        return this.producesHealth;
    }

    /**
     * A method to get a mapping of all entities required
     * for the successful execution of a given action.
     */
    public Map<String, GameEntity> getRequiredEntities(){
        final HashMap<String, GameEntity> requiredEntities = new HashMap<>();
        this.subjectEntities.forEach(
                entity -> requiredEntities.put(entity.getName(), entity)
        );
        this.consumedEntities.forEach(
                entity -> requiredEntities.put(entity.getName(), entity)
        );
        return new HashMap<>(requiredEntities);
    }


}
