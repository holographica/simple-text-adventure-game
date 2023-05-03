package edu.uob;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import java.util.HashMap;
import java.util.Map;

public class Location extends GameEntity {
    /**
     * Stores the available paths from the current location,
     * mapping location names to Location objects.
     */
    private final Map<String, Location> pathsFromLocation;
    /**
     * Stores the artefacts present at the current location,
     * mapping artefact names to Artefact objects.
     */
    private final Map<String, Artefact> artefacts;
    /**
     * Stores the characters present at the current location,
     * mapping character names to GameCharacter objects.
     */
    private final Map<String, GameCharacter> characters;
    /**
     * Stores the furniture present at the current location,
     * mapping furniture names to Furniture objects.
     */
    private final Map<String, Furniture> furniture;

    public Location(final Node newEntity) {
        super(newEntity);
        this.pathsFromLocation = new HashMap<>();
        this.artefacts = new HashMap<>();
        this.characters = new HashMap<>();
        this.furniture = new HashMap<>();
    }

    /**
     * Returns a HashMap of all paths to other
     * locations from this location.
     */
    public Map<String, Location> getPaths() {
        return new HashMap<>(this.pathsFromLocation);
//        return this.pathsFromLocation;
    }

    public Map<String, Artefact> getArtefacts() {
        return new HashMap<>(this.artefacts);
//        return this.artefacts;
    }

    public Map<String, GameCharacter> getCharacters() {
        return new HashMap<>(this.characters);
//        return this.characters;
    }

    public Map<String, Furniture> getFurniture() {
        return new HashMap<>(this.furniture);
//        return this.furniture;
    }

    /**
     * Returns a HashMap containing all entities accessible in the current location,
     * including artefacts, characters, furniture, and paths
     */
    public Map<String, GameEntity> getAccessibleEntities(){
        final HashMap<String, GameEntity> accessibleEntities = new HashMap<>();
        this.getArtefacts().values().forEach(
                artefact -> accessibleEntities.put(artefact.getName(),artefact)
        );
        this.getCharacters().values().forEach(
                gameChar -> accessibleEntities.put(gameChar.getName(),gameChar)
        );
        this.getFurniture().values().forEach(
                furniture -> accessibleEntities.put(furniture.getName(),furniture)
        );
        this.pathsFromLocation.values().forEach(
                path -> accessibleEntities.put(path.getName(),path)
        );
        return accessibleEntities;
    }

    /**
     * Adds a path to the specified location
     */
    public void addPath(final Location pathTo){
        this.pathsFromLocation.put(pathTo.getName(), pathTo);
    }

    /**
     * Adds an artefact to the current location
     */
    public void addArtefact(final Artefact newArtefact){
        this.artefacts.put(newArtefact.getName(), newArtefact);
    }

    public void addArtefact(final Node artefactNode){
        final Artefact temp = new Artefact(artefactNode);
        addArtefact(temp);
    }

    /**
     * Adds all artefacts present in the given subgraph to the current location.
     */
    public void addAllArtefacts(final Graph subgraph){
        subgraph.getNodes(false)
                .forEach(this::addArtefact);
    }

    public void addFurniture(final Furniture newFurniture){
        this.furniture.put(newFurniture.getName(),newFurniture);
    }

    public void addFurniture(final Node furnitureNode){
        final Furniture temp = new Furniture(furnitureNode);
        addFurniture(temp);
    }

    /**
     * Adds all furniture present in the given subgraph to the current location.
     */
    public void addAllFurniture(final Graph subgraph){
        subgraph.getNodes(false)
                .forEach(this::addFurniture);
    }

    public void addCharacter(final GameCharacter newCharacter){
        this.characters.put(newCharacter.getName(),newCharacter);
    }

    public void addCharacter(final Node characterNode){
        final GameCharacter temp = new GameCharacter(characterNode);
        addCharacter(temp);
    }

    /**
     * Adds all characters present in the given subgraph to the current location.
     */
    public void addAllCharacters(final Graph subgraph){
        subgraph.getNodes(false)
                .forEach(this::addCharacter);
    }

    public void removeArtefact(final String artefactName){
        this.artefacts.remove(artefactName);
    }

    /**
     * Adds a game entity to the appropriate mapping,
     * based on its type.
     */
    public void addEntity(final GameEntity entity){
        if (entity instanceof Artefact){
            addArtefact((Artefact) entity);
        }
        else if (entity instanceof GameCharacter){
            addCharacter((GameCharacter) entity);
        }
        else if (entity instanceof Furniture){
            addFurniture((Furniture) entity);
        }
        else if (entity instanceof Location){
            addPath((Location) entity);
        }
    }

    /**
     * Removes a path to a specified location, given its name.
     */
    public void removePath(final String pathTo){
        this.pathsFromLocation.remove(pathTo);
    }

    /**
     * Removes the specified entity from the location, given its name.
     */
    public void removeEntity(final String entityName){
        this.artefacts.remove(entityName);
        this.characters.remove(entityName);
        this.furniture.remove(entityName);
        this.pathsFromLocation.remove(entityName);
    }


}
