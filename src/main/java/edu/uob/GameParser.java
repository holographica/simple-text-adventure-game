package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

public class GameParser {
    File entitiesFile;
    File actionsFile;
    Parser parser;
    GameState gameState;
    HashSet<String> basicCommands;
    HashMap<String, HashSet<GameAction>> actionList;

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
        parseActions();
        System.out.println("actionlist: " + gameState.getActions());
        return this.gameState;
    }

    public void parseEntities() {
        try (BufferedReader reader = new BufferedReader(new FileReader(entitiesFile))) {
            parser.parse(reader);
        } catch (IOException e ) {
            System.out.println("io exception");
        } catch (ParseException e) {
            System.out.println("parse exception");
        }
        Graph entitiesGraph = parser.getGraphs().get(0);
        Graph locationGraph = entitiesGraph.getSubgraphs().get(0);
        Graph pathGraph = entitiesGraph.getSubgraphs().get(1);
        parseLocations(locationGraph);
        addPathsToLocations(pathGraph);
        gameState.setEntityList(this.getEntityList());
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

    public HashMap<String, GameEntity> getEntityList(){
        return this. entityList;
    }

    public void addEntityToList(GameEntity entity){
        this.entityList.put(entity.getName(), entity);
    }


    // TODO
    //  PUT THIS IN SEPARATE CLASS - TOO MUCH GOING ON
    //   SAME WITH ENTITIES

    public void parseActions(){
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse("config" + File.separator + "test-actions.xml");
            Element root = document.getDocumentElement();
            NodeList actions = root.getChildNodes();
            makeNodeList(actions)
                .forEach(
                    element -> {
                        if (element.getNodeType() == Node.ELEMENT_NODE){
                            GameAction newAction = new GameAction();
                            parseActionElements((Element) element, newAction);
                            addActionToSet(this.actionList, newAction);
                        }
                    }
                );
            gameState.setActionList(this.actionList);
        } catch(ParserConfigurationException pce) {
            System.out.println("ParserConfigurationException was thrown when attempting to read basic actions file");
        } catch(SAXException saxe) {
            System.out.println("SAXException was thrown when attempting to read basic actions file");

        } catch(IOException ioe) {
        System.out.println("IOException was thrown when attempting to read basic actions file");
        }
    }

    public void addActionToSet(HashMap<String, HashSet<GameAction>> allActions, GameAction action){
        action.getTriggers().forEach(
                trigger -> {
                    if (allActions.containsKey(trigger)){
                        this.actionList.get(trigger).add(action);
                    }
                    else {
                        HashSet<GameAction> actionSet = new HashSet<>();
                        actionSet.add(action);
                        this.actionList.put(trigger, actionSet);
                    }
                }
        );
    }

    public void parseActionElements(Element actionElement, GameAction newAction){
        parseNarration(actionElement, newAction);
//        System.out.println("parsed narration");
        parseTriggers(actionElement, newAction);
        parseSubjects(actionElement, newAction);
//        System.out.println("parsed subjects");
        parseConsumed(actionElement, newAction);
//        System.out.println("parsed consujed");
        parseProduced(actionElement, newAction);
    }

    public void parseTriggers (Element action, GameAction newAction){
        Element triggers = (Element) action.getElementsByTagName("triggers").item(0);
        NodeList keyphrases = triggers.getElementsByTagName("keyphrase");
        makeNodeList(keyphrases)
            .forEach(
                node -> {
                    String triggerPhrase = node.getTextContent();
                    newAction.addTrigger(triggerPhrase);
                }
            );
    }

    public void parseSubjects (Element action, GameAction newAction){
        Element subjects = (Element) action.getElementsByTagName("subjects").item(0);
        NodeList entities = subjects.getElementsByTagName("entity");
        makeNodeList(entities)
            .forEach(
                node -> {
                    String entityName = node.getTextContent();
                    newAction.addSubjectEntity(entityName);
                }
            );
    }

    public void parseConsumed (Element action, GameAction newAction){
        Element consumed = (Element) action.getElementsByTagName("consumed").item(0);
        NodeList entities = consumed.getElementsByTagName("entity");
        makeNodeList(entities)
            .forEach(
                node -> {
                    String entityName = node.getTextContent();
                    newAction.addConsumedEntity(entityName);
                }
            );
    }

    public void parseProduced (Element action, GameAction newAction){
        Element produced = (Element) action.getElementsByTagName("produced").item(0);
        NodeList entities = produced.getElementsByTagName("entity");
        makeNodeList(entities)
            .forEach(
                node -> {
                    String entityName = node.getTextContent();
                    newAction.addProducedEntity(entityName);
                }
            );
    }

    public void parseNarration(Element action, GameAction newAction){
        Element narrationElement = (Element) action.getElementsByTagName("narration").item(0);
        String narration = narrationElement.getTextContent();
        newAction.setNarration(narration);
    }

    public ArrayList<Node> makeNodeList(NodeList list){
        ArrayList<Node> nodesToIterateOver = new ArrayList<>();
        for (int i=0; i < list.getLength(); i++){
            nodesToIterateOver.add(i, list.item(i));
        }
        return nodesToIterateOver;
    }

}
