package edu.uob;

import com.alexmerz.graphviz.Parser;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

public class GameParser {
    File entitiesFile;
    File actionsFile;
    Parser parser;
    ActionParser actionParser;
    EntityParser entityParser;
    static GameState gameState;
    HashMap<String, HashSet<GameAction>> actionList;
    HashMap<String, GameEntity> entityList;

    public GameParser(File entitiesFile, File actionsFile){
        this.parser = new Parser();
        this.actionParser = new ActionParser(actionsFile);
        this.entityParser = new EntityParser(entitiesFile);
        this.entitiesFile = entitiesFile;
        this.actionsFile =  actionsFile;
        gameState = new GameState();
        this.entityList = new HashMap<>();
        this.actionList = new HashMap<>();
    }

    public GameState getGameState(){
        entityParser.parseEntities();
        actionParser.parseActions();
        return gameState;
    }
}
