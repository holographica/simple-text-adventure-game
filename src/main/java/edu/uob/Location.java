package edu.uob;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import java.util.HashMap;

public class Location extends GameEntity {

    private HashMap<String, Location> pathsFromLocation;
    private HashMap<String, Artefact> artefacts;
    private HashMap<String, GameCharacter> characters;
    private HashMap<String, Furniture> furniture;

    public Location(Node newEntity) {
        super(newEntity);
        this.pathsFromLocation = new HashMap<>();
        this.artefacts = new HashMap<>();
        this.characters = new HashMap<>();
        this.furniture = new HashMap<>();
    }

    public HashMap<String, Location> getPaths() {
        return this.pathsFromLocation;
    }

    public HashMap<String, Artefact> getArtefacts() {
        return this.artefacts;
    }

    public HashMap<String, GameCharacter> getCharacters() {
        return this.characters;
    }

    public HashMap<String, Furniture> getFurniture() {
        return this.furniture;
    }

    public HashMap<String, GameEntity> getAccessibleEntities(){
        HashMap<String, GameEntity> accessibleEntities = new HashMap<>();
        this.getArtefacts().values().forEach(
                artefact -> accessibleEntities.put(artefact.getName(),artefact)
        );
        this.getCharacters().values().forEach(
                gameChar -> accessibleEntities.put(gameChar.getName(),gameChar)
        );
        this.getFurniture().values().forEach(
                furniture -> accessibleEntities.put(furniture.getName(),furniture)
        );
        return accessibleEntities;
    }

    public void addPath(Location pathTo){
        this.pathsFromLocation.put(pathTo.getName(), pathTo);
    }

    public void addArtefact(Artefact newArtefact){
        this.artefacts.put(newArtefact.getName(), newArtefact);
    }

    public void addArtefact(Node artefactNode){
        Artefact temp = new Artefact(artefactNode);
        addArtefact(temp);
    }

    public void addAllArtefacts(Graph subgraph){
        subgraph.getNodes(false)
                .forEach(this::addArtefact);
    }

    public void addFurniture(Furniture newFurniture){
        this.furniture.put(newFurniture.getName(),newFurniture);
    }

    public void addFurniture(Node furnitureNode){
        Furniture temp = new Furniture(furnitureNode);
        addFurniture(temp);
    }

    public void addAllFurniture(Graph subgraph){
        subgraph.getNodes(false)
                .forEach(this::addFurniture);
    }

    public void addCharacter(GameCharacter newCharacter){
        this.characters.put(newCharacter.getName(),newCharacter);
    }

    public void addCharacter(Node characterNode){
        GameCharacter temp = new GameCharacter(characterNode);
        addCharacter(temp);
    }

    public void addAllCharacters(Graph subgraph){
        subgraph.getNodes(false)
                .forEach(this::addCharacter);
    }

    public void removeArtefact(String artefactName){
        this.artefacts.remove(artefactName);
    }

    public void removeCharacter(String charName){
        this.characters.remove(charName);
    }
}
