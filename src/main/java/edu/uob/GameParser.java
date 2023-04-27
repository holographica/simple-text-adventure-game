package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.*;

import java.awt.font.ShapeGraphicAttribute;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GameParser {
    File entitiesFile;
    File actionsFile;
    Parser parser;
    GameState gameState;

    public GameParser(File entitiesFile, File actionsFile){
        this.parser = new Parser();
        this.entitiesFile = entitiesFile;
        this.actionsFile =  actionsFile;
        this.gameState = new GameState();
    }

    public void parseEntities() {

        try (BufferedReader reader = new BufferedReader(new FileReader(entitiesFile))) {
            parser.parse(reader);
        } catch (IOException e) {
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

//        for (Graph loco : locationGraph.getSubgraphs()) {
//            // make new location - unique id/description is always first node in subgraph
//            Location newLocation = new Location(loco.getNodes(false).get(0));
//            // extract attributes from that location
//            parseLocationAttributes(newLocation, loco.getSubgraphs());
//            // add completed location to list
//            this.gameState.addLocation(newLocation);
//        }

    }

    public void parseLocations(Graph locationGraph){
        locationGraph.getSubgraphs().forEach(
                location -> {
                    Location newLocation = new Location(location.getNodes(false).get(0));
                    parseLocationAttributes(newLocation, location.getSubgraphs());
                    this.gameState.addLocation(newLocation);
                }
        );
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
        });
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


}
