package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.*;

import java.io.*;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GameParser {
    File entitiesFile;
    File actionsFile;
    Parser parser;
    GameState gameState;
    HashSet<String> basicCommands;
    HashMap<String, GameAction> actionList;

    // NEED TO CHANGE THIS - TAKE OUT OF HERE, KEEP IN GAME STATE
    // AS ENTITIES CAN CHANGE
    HashMap<String, GameEntity> entityList;

    public GameParser(File entitiesFile, File actionsFile){
        this.parser = new Parser();
        this.entitiesFile = entitiesFile;
        this.actionsFile =  actionsFile;
        this.gameState = new GameState();
        this.entityList = new HashMap<>();
        this.actionList = new HashMap<>();
    }


    public GameState getGameState(){
        parseEntities();
        return this.gameState;
    }

    public void parseEntities() {
        try (BufferedReader reader = new BufferedReader(new FileReader(entitiesFile))) {
            parser.parse(reader);
        } catch (IOException e ) {
            System.out.println("io exception");
            throw new RuntimeException(e);
        } catch (ParseException e) {
            System.out.println("parse exception");
            throw new RuntimeException(e);
        }

        Graph entitiesGraph = parser.getGraphs().get(0);
        Graph locationGraph = entitiesGraph.getSubgraphs().get(0);
        Graph pathGraph = entitiesGraph.getSubgraphs().get(1);

        parseLocations(locationGraph);
        addPathsToLocations(pathGraph);

        this.gameState.setEntityList(this.getEntityList());
    }

    public void printLocationDetails(){
        this.gameState.getLocations().values().forEach(
                location -> {
                    System.out.println("name: " + location.getName());
                    System.out.println("desc: " + location.getDescription());
                    System.out.println("paths: "+ location.getPaths());
                    for (GameEntity e: location.getArtefacts().values()){
                        System.out.println("aft: "+e.getName());
                    }
                    for (GameEntity e: location.getCharacters().values()){
                        System.out.println("char: "+e.getName());
                    }
                    for (GameEntity e: location.getFurniture().values()){
                        System.out.println("furniture: "+e.getName());
                    }
                }
        );
    }

    public void parseLocations(Graph locationGraph){
        boolean isFirst = false;
        for (Graph location: locationGraph.getSubgraphs()){
            Location newLocation = new Location(location.getNodes(false).get(0));
            if (!isFirst){
                isFirst=true;
                this.gameState.setStartLocation(newLocation);
                getEntitiesFromLocation(newLocation);
            }
            parseLocationAttributes(newLocation, location.getSubgraphs());
            this.gameState.addLocation(newLocation);
            getEntitiesFromLocation(newLocation);
        }
    }

    public void addPathsToLocations(Graph pathGraph){
        // map each location by its name
        HashMap<String, Location> locationMap = new HashMap<>();
        this.gameState.getLocations().values().forEach(
                location -> locationMap.put(location.getName(), location));
        // get path source/target locations, then add paths
        pathGraph.getEdges().forEach(
                edge -> {
                    String source = edge.getSource().getNode().getId().getId();
                    Location target = new Location(edge.getTarget().getNode());
                    locationMap.get(source).addPath(target);
                }
        );
    }

    public void getEntitiesFromLocation(Location target){
        addEntityToList(target);
        target.getCharacters().values().forEach(
                this::addEntityToList
        );
        target.getArtefacts().values().forEach(
                this::addEntityToList
        );
        target.getFurniture().values().forEach(
                this::addEntityToList
        );
    }

    public void parseLocationAttributes(Location location, ArrayList<Graph> locationAttributes) {
        locationAttributes.forEach(
            attribute -> {
                parseArtefacts(location, attribute);
                parseFurniture(location, attribute);
                parseCharacters(location, attribute);
            }
        );
    }

    public void parseArtefacts(Location location, Graph subgraph){
        if (subgraph.getId().getId().equalsIgnoreCase("artefacts")){
            location.addAllArtefacts(subgraph);
        }
    }

    public void parseFurniture(Location location, Graph subgraph) {
        if (subgraph.getId().getId().equalsIgnoreCase("furniture")){
            location.addAllFurniture(subgraph);
        }
    }

    public void parseCharacters(Location location, Graph subgraph){
        if (subgraph.getId().getId().equalsIgnoreCase("characters")){
            location.addAllCharacters(subgraph);
        }
    }

    public HashMap<String, GameEntity> getEntityList(){
        return this. entityList;
    }



    public void addEntityToList(GameEntity entity){
        this.entityList.put(entity.getName(), entity);
    }



    public void parseActions(){

    }



}
