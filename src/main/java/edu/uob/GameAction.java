package edu.uob;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class GameAction {
    private String narration;
    private HashSet<String> triggers;
    private HashSet<GameEntity> subjectEntities;

    // for basic cmds these are target artefacts/locations/chars etc
    private HashSet<GameEntity> consumedEntities;
    private HashSet<GameEntity> producedEntities;
    private boolean consumesHealth;
    private boolean producesHealth;

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
    public boolean equals(Object actionToCompare) {
        if (this == actionToCompare) return true;
        if (actionToCompare == null || this.getClass() != actionToCompare.getClass()) {
            return false;
        }
        GameAction newAction = (GameAction) actionToCompare;
        if (!Objects.equals(this.triggers, newAction.triggers)) {
            return false;
        }
        else if (!Objects.equals(this.subjectEntities, newAction.subjectEntities)){
            return false;
        }
        else if (!Objects.equals(this.consumedEntities, newAction.consumedEntities)){
            return false;
        }
        else if (!Objects.equals(this.producedEntities, newAction.producedEntities)){
            return false;
        }
        else return Objects.equals(this.narration, newAction.narration);
    }

    public String getNarration(){
        return this.narration;
    }

    public void setNarration(String newNarration){
        this.narration = newNarration;
    }

    public HashSet<String> getTriggers(){
        return this.triggers;
    }

    public void addTrigger(String triggerPhrase){
        this.triggers.add(triggerPhrase);
    }

    public HashSet<GameEntity> getSubjectEntities() {
        return subjectEntities;
    }

    public void setSubjectEntities(HashSet<GameEntity> subjectEntities) {
        this.subjectEntities = subjectEntities;
    }

    public void addSubjectEntity(String entityName){
        GameEntity foundEntity = GameState.getEntitiesByType(GameEntity.class).get(entityName);
        if (foundEntity != (null)){
            this.subjectEntities.add(foundEntity);
        }
    }

    public HashSet<GameEntity> getConsumedEntities() {
        return consumedEntities;
    }

    public void addConsumedEntity(String entityName){
        GameEntity foundEntity = GameState.getEntitiesByType(GameEntity.class).get(entityName);
        if (foundEntity != (null)){
            this.consumedEntities.add(foundEntity);
        }
        else if (entityName.equalsIgnoreCase("health")){
            this.setConsumesHealth();
        }
    }

    public HashSet<GameEntity> getProducedEntities() {
        return this.producedEntities;
    }


    public void addProducedEntity(String entityName){
        GameEntity foundEntity = GameState.getEntitiesByType(GameEntity.class).get(entityName);
        if (foundEntity != (null)){
            this.producedEntities.add(foundEntity);
        }
        else if (entityName.equalsIgnoreCase("health")) {
            this.setProducesHealth();
        }
    }

    public void setConsumesHealth(){
        this.consumesHealth=true;
    }

    public boolean doesConsumeHealth(){
        return this.consumesHealth;
    }

    public void setProducesHealth(){
        this.producesHealth=true;
    }

    public boolean doesProduceHealth(){
        return this.producesHealth;
    }

    public HashMap<String, GameEntity> getRequiredEntities(){
        HashMap<String, GameEntity> requiredEntities = new HashMap<>();
        this.subjectEntities.forEach(
                entity -> {
                    requiredEntities.put(entity.getName(), entity);
                }
        );
        this.consumedEntities.forEach(
                entity -> {
                    requiredEntities.put(entity.getName(), entity);
                }
        );
        return requiredEntities;
    }


}
