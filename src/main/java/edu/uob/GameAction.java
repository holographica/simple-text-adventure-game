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

    public GameAction(){
        this.narration = "";
        this.triggers = new HashSet<>();
        this.subjectEntities = new HashSet<>();
        this.consumedEntities = new HashSet<>();
        this.producedEntities = new HashSet<>();
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

        // list consists of gameEntities
        // so use instanceof to check specific type when needed

        if (foundEntity != (null)){
            this.subjectEntities.add(foundEntity);
        }
        else {
            // TODO remove this once working?
            System.out.println("Could not find subject entity to add to action");
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
        else {
            // TODO remove this once working?
            System.out.println("Could not find consumed entity to add to action");
            System.out.println("name: "+entityName+ "\n");
        }
    }

    public HashSet<GameEntity> getProducedEntities() {
        return producedEntities;
    }


    public void addProducedEntity(String entityName){
        GameEntity foundEntity = GameState.getEntitiesByType(GameEntity.class).get(entityName);
        if (foundEntity != (null)){
            this.producedEntities.add(foundEntity);
        }
        else {
//            GameEntity
            // TODO remove this once working?
            System.out.println("Could not find produced entity to add to action");
            System.out.println("name: "+entityName+ "\n");
        }
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
