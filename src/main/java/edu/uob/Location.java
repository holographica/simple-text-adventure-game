package edu.uob;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.jar.JarFile;

public class Location extends GameEntity {

//  private HashMap<String, Location> paths;
  private HashSet<String> paths;
  private HashSet<Artefact> artefacts;
  private HashSet<GameCharacter> characters;
  private HashSet<Furniture> furniture;

    public Location(Node newEntity) {
        super(newEntity);
        this.paths = new HashSet<>();
        this.artefacts = new HashSet<>();
        this.characters = new HashSet<>();
        this.furniture = new HashSet<>();
    }

    public HashSet<String> getPaths() {
        return this.paths;
    }

    public HashSet<Artefact> getArtefacts() {
        return this.artefacts;
    }

    public HashSet<GameCharacter> getCharacters() {
        return this.characters;
    }

    public HashSet<Furniture> getFurniture() {
        return this.furniture;
    }

    public void addPath(String path){
        this.paths.add(path);
    }

    public void addArtefact(Artefact newArtefact){
        this.artefacts.add(newArtefact);
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
        this.furniture.add(newFurniture);
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
        this.characters.add(newCharacter);
    }

    public void addCharacter(Node characterNode){
        GameCharacter temp = new GameCharacter(characterNode);
        addCharacter(temp);
    }

    public void addAllCharacters(Graph subgraph){
        subgraph.getNodes(false)
                .forEach(this::addCharacter);
    }



    // paths to other locations (bidirectional or single direction)
    // characters at the location
    // artefacts at the location
    // furniture belonging




}
