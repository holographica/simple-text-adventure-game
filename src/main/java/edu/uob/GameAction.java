package edu.uob;

import java.util.HashSet;

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







}
