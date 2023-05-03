package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntityParser {

    Map<String, GameEntity> entityList;
    File entitiesFile;
    Parser parser;

    public EntityParser(File entitiesFile){
        this.entityList = new HashMap<>();
        this.parser = new Parser();
        this.entitiesFile = entitiesFile;
    }

    public void parseEntities() {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.entitiesFile))) {
            parser.parse(reader);
        } catch (IOException | ParseException e ) {
            System.out.println(e.getMessage());
        }
        final Graph entitiesGraph = parser.getGraphs().get(0);
        final Graph locationGraph = entitiesGraph.getSubgraphs().get(0);
        final Graph pathGraph = entitiesGraph.getSubgraphs().get(1);
        parseLocations(locationGraph);
        addPathsToLocations(pathGraph);
        GameParser.gameState.setEntityList(this.getEntityList());
    }

    public void parseLocations(Graph locationGraph){
        boolean isFirst = false;
        for (final Graph location: locationGraph.getSubgraphs()){
            final Location newLocation = new Location(location.getNodes(false).get(0));
            if (!isFirst){
                isFirst=true;
                GameParser.gameState.setStartLocation(newLocation);
                getEntitiesFromLocation(newLocation);
            }
            parseLocationAttributes(newLocation, location.getSubgraphs());
            GameParser.gameState.addLocation(newLocation);
            getEntitiesFromLocation(newLocation);
        }
    }

    public void addPathsToLocations(Graph pathGraph){
        final HashMap<String, Location> locationMap = new HashMap<>();
        GameParser.gameState.getLocations().values().forEach(
                location -> locationMap.put(location.getName(), location));
        pathGraph.getEdges().forEach(
                edge -> {
                    final String source = edge.getSource().getNode().getId().getId();
                    final Location target = new Location(edge.getTarget().getNode());
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
                    parseAllAttributes("artefacts",location,attribute);
                    parseAllAttributes("furniture",location,attribute);
                    parseAllAttributes("characters",location,attribute);
                }
        );
    }

    public void parseAllAttributes(String entityType, Location location, Graph subgraph){
        if (subgraph.getId().getId().equalsIgnoreCase(entityType)) {
            switch (entityType.toLowerCase()) {
                case "artefacts" -> location.addAllArtefacts(subgraph);
                case "furniture" -> location.addAllFurniture(subgraph);
                case "characters" -> location.addAllCharacters(subgraph);
            }
        }
    }

    public Map<String, GameEntity> getEntityList(){
        return new HashMap<>(this.entityList);
    }

    public void addEntityToList(GameEntity entity){
        this.entityList.put(entity.getName(), entity);
    }

}
