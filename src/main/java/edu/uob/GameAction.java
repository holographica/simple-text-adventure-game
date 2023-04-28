package edu.uob;

import java.util.HashSet;
import java.util.List;

public class GameAction {
    private String narration;
    private HashSet<String> triggers;
    private HashSet<GameEntity> subjectEntities;
    private HashSet<GameEntity> consumedEntities;
    private HashSet<GameEntity> producedEntities;

    public GameAction(){
        this.narration = "";
        this.triggers = new HashSet<>();
        this.subjectEntities = new HashSet<>();
        this.consumedEntities = new HashSet<>();
        this.producedEntities = new HashSet<>();
    }







}
