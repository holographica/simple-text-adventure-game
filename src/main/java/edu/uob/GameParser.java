package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.*;

import java.io.*;
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
    HashMap<String, GameEntity> entityList;
    HashMap<String, GameAction> actionList;

    public GameParser(File entitiesFile, File actionsFile){
        this.parser = new Parser();
        this.entitiesFile = entitiesFile;
        this.actionsFile =  actionsFile;
        this.gameState = new GameState();
        this.entityList = new HashMap<>();
        this.actionList = new HashMap<>();
        loadBasicCommands();
    }

    public void loadBasicCommands(){
        this.basicCommands.addAll(List.of(HandleCommand.basicCommands));
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

        // don't actually need to do this - assume they are valid
        try {
            checkForReservedWords(locationGraph);
        } catch (IOException e) {
            throw new RuntimeException("Reserved keyword found in config files.");
        }

        parseLocations(locationGraph);
        addPathsToLocations(pathGraph);

        printLocationDetails();
    }

    public void printLocationDetails(){
        this.gameState.getLocations().forEach(
                location -> {
                    System.out.println("name: " + location.getName());
                    System.out.println("desc: " + location.getDescription());
                    System.out.println("paths: "+ location.getPaths());
                    for (GameEntity e: location.getArtefacts()){
                        System.out.println("aft: "+e.getName());
                    }
                    for (GameEntity e: location.getCharacters()){
                        System.out.println("char: "+e.getName());
                    }
                    for (GameEntity e: location.getFurniture()){
                        System.out.println("furniture: "+e.getName());
                    }
                }
        );
    }

    public void checkForReservedWords(Graph subgraph) throws IOException {
        for (String word: basicCommands){
            if (subgraph.toString().contains(word)){
                throw new IOException("Config file contains reserved keyword.");
            }
        }
        // check for entities and actions
    }

    public void parseLocations(Graph locationGraph){
        boolean isFirst = false;
        for (Graph location: locationGraph.getSubgraphs()){
            Location newLocation = new Location(location.getNodes(false).get(0));
            if (!isFirst){
                isFirst=true;
                this.gameState.setStartLocation(newLocation);
            }
            parseLocationAttributes(newLocation, location.getSubgraphs());
            this.gameState.addLocation(newLocation);
        }
    }

    public void addPathsToLocations(Graph pathGraph){
        // map each location by its name
        HashMap<String, Location> locationMap = new HashMap<>();
        this.gameState.getLocations().forEach(
                location -> locationMap.put(location.getName(), location));
        // get path source/target location names, then add paths
        pathGraph.getEdges().forEach(
                edge -> {
                    String source = edge.getSource().getNode().getId().getId();
                    String target = edge.getTarget().getNode().getId().getId();
                    locationMap.get(source).addPath(target);
                }
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
        return this.entityList;
    }

    public void setEntityList(){

    }



    public void parseActions(){

    }



}
