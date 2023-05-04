package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

final class ParseEntitiesTests {

    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "test-actions.xml").toAbsolutePath().toFile();
        GameParser parser = new GameParser(entitiesFile, actionsFile);
        parser.getGameState();
    }


    @Test
    void testLocationsExist() {
        assertTrue(GameState.getEntityList().containsKey("cabin"),"Entity list should contain cabin");
        assertTrue(GameState.getEntityList().containsKey("storeroom"),"Entity list should contain storeroom");
        assertTrue(GameState.getEntityList().containsKey("forest"),"Entity list should contain forest");
        assertTrue(GameState.getEntityList().containsKey("cellar"),"Entity list should contain cellar");
        assertTrue(GameState.getEntityList().containsKey("riverbank"),"Entity list should contain riverbank");
        assertTrue(GameState.getEntityList().containsKey("clearing"),"Entity list should contain clearing");
    }

    @Test
    void testLocationDescriptions(){
        Location tempLocation = GameState.getLocationByName("cabin");
        assertEquals("A log cabin in the woods", tempLocation.getDescription(),"Entity list should contain cabin");
        tempLocation = GameState.getLocationByName("forest");
        assertEquals("A deep dark forest", tempLocation.getDescription());
        tempLocation = GameState.getLocationByName("cellar");
        assertEquals("A dusty cellar", tempLocation.getDescription());
        tempLocation = GameState.getLocationByName("riverbank");
        assertEquals("A grassy riverbank", tempLocation.getDescription());
        tempLocation = GameState.getLocationByName("clearing");
        assertEquals("A clearing in the woods", tempLocation.getDescription());
        tempLocation = GameState.getLocationByName("storeroom");
        assertEquals("Storage for any entities not placed in the game", tempLocation.getDescription());
    }

    @Test
    void testCharsAtLocation(){
        Location tempLocation = GameState.getLocationByName("cellar");
        GameCharacter tempCharacter = tempLocation.getCharacters().get("elf");
        assertEquals(tempCharacter.getDescription(),"An angry looking Elf");
        tempLocation = GameState.getLocationByName("storeroom");
        tempCharacter =  tempLocation.getCharacters().get("lumberjack");
        assertEquals(tempCharacter.getDescription(), "A burly wood cutter");
    }

    @Test
    void testArtefactsAtLocation(){
        Location tempLocation = GameState.getLocationByName("cabin");
        Artefact tempArtefact = tempLocation.getArtefacts().get("potion");
        assertEquals(tempArtefact.getDescription(),"A bottle of magic potion");
        tempArtefact = tempLocation.getArtefacts().get("axe");
        assertEquals(tempArtefact.getDescription(),"A razor sharp axe");
        tempArtefact = tempLocation.getArtefacts().get("coin");
        assertEquals(tempArtefact.getDescription(),"A silver coin");

        tempLocation = GameState.getLocationByName("forest");
        tempArtefact =  tempLocation.getArtefacts().get("key");
        assertEquals(tempArtefact.getDescription(), "A rusty old key");

        tempLocation = GameState.getLocationByName("riverbank");
        tempArtefact =  tempLocation.getArtefacts().get("horn");
        assertEquals(tempArtefact.getDescription(), "An old brass horn");

        tempLocation = GameState.getLocationByName("storeroom");
        tempArtefact =  tempLocation.getArtefacts().get("log");
        assertEquals(tempArtefact.getDescription(), "A heavy wooden log");
        tempArtefact =  tempLocation.getArtefacts().get("shovel");
        assertEquals(tempArtefact.getDescription(), "A sturdy shovel");
        tempArtefact =  tempLocation.getArtefacts().get("gold");
        assertEquals(tempArtefact.getDescription(), "A big pot of gold");
    }

    @Test
    void testFurnitureAtLocation() {
        Location tempLocation = GameState.getLocationByName("cabin");
        Furniture tempFurniture = tempLocation.getFurniture().get("trapdoor");
        assertEquals(tempFurniture.getDescription(), "A locked wooden trapdoor in the floor");

        tempLocation = GameState.getLocationByName("forest");
        tempFurniture = tempLocation.getFurniture().get("tree");
        assertEquals(tempFurniture.getDescription(), "A tall pine tree");

        tempLocation = GameState.getLocationByName("riverbank");
        tempFurniture = tempLocation.getFurniture().get("river");
        assertEquals(tempFurniture.getDescription(), "A fast flowing river");

        tempLocation = GameState.getLocationByName("clearing");
        tempFurniture = tempLocation.getFurniture().get("ground");
        assertEquals(tempFurniture.getDescription(), "It looks like the soil has been recently disturbed");

        tempLocation = GameState.getLocationByName("storeroom");
        tempFurniture = tempLocation.getFurniture().get("hole");
        assertEquals(tempFurniture.getDescription(), "A deep hole in the ground");
    }

    @Test
    void testPathsFromLocation() {
        Location tempLocation = GameState.getLocationByName("cabin");
        Location pathTo = tempLocation.getPaths().get("forest");
        assertEquals(pathTo.getDescription(), "A deep dark forest");

        tempLocation = GameState.getLocationByName("forest");
        pathTo = tempLocation.getPaths().get("cabin");
        assertEquals(pathTo.getDescription(), "A log cabin in the woods");

        pathTo = tempLocation.getPaths().get("cabin");
        assertEquals(pathTo.getDescription(), "A log cabin in the woods");

        tempLocation = GameState.getLocationByName("forest");
        pathTo = tempLocation.getPaths().get("riverbank");
        assertEquals(pathTo.getDescription(), "A grassy riverbank");

        tempLocation = GameState.getLocationByName("riverbank");
        pathTo = tempLocation.getPaths().get("forest");
        assertEquals(pathTo.getDescription(), "A deep dark forest");

        tempLocation = GameState.getLocationByName("clearing");
        pathTo = tempLocation.getPaths().get("riverbank");
        assertEquals(pathTo.getDescription(), "A grassy riverbank");
    }

    @Test
    void testEntityList(){
        HashMap<String, GameEntity> tempList = new HashMap<>(GameState.getEntityList());
        GameEntity tempEntity = tempList.get("cabin");
        assertEquals(tempEntity.getDescription(), "A log cabin in the woods");

        tempEntity = tempList.get("potion");
        assertEquals(tempEntity.getDescription(), "A bottle of magic potion");

        tempEntity = tempList.get("axe");
        assertEquals(tempEntity.getDescription(), "A razor sharp axe");

        tempEntity = tempList.get("coin");
        assertEquals(tempEntity.getDescription(), "A silver coin");

        tempEntity = tempList.get("trapdoor");
        assertEquals(tempEntity.getDescription(), "A locked wooden trapdoor in the floor");

        tempEntity = tempList.get("forest");
        assertEquals(tempEntity.getDescription(), "A deep dark forest");

        tempEntity = tempList.get("key");
        assertEquals(tempEntity.getDescription(), "A rusty old key");

        tempEntity = tempList.get("tree");
        assertEquals(tempEntity.getDescription(), "A tall pine tree");

        tempEntity = tempList.get("cellar");
        assertEquals(tempEntity.getDescription(), "A dusty cellar");

        tempEntity = tempList.get("elf");
        assertEquals(tempEntity.getDescription(), "An angry looking Elf");

        tempEntity = tempList.get("riverbank");
        assertEquals(tempEntity.getDescription(), "A grassy riverbank");

        tempEntity = tempList.get("horn");
        assertEquals(tempEntity.getDescription(), "An old brass horn");

        tempEntity = tempList.get("river");
        assertEquals(tempEntity.getDescription(), "A fast flowing river");

        tempEntity = tempList.get("clearing");
        assertEquals(tempEntity.getDescription(), "A clearing in the woods");

        tempEntity = tempList.get("ground");
        assertEquals(tempEntity.getDescription(), "It looks like the soil has been recently disturbed");

        tempEntity = tempList.get("storeroom");
        assertEquals(tempEntity.getDescription(), "Storage for any entities not placed in the game");

        tempEntity = tempList.get("lumberjack");
        assertEquals(tempEntity.getDescription(), "A burly wood cutter");

        tempEntity = tempList.get("log");
        assertEquals(tempEntity.getDescription(), "A heavy wooden log");

        tempEntity = tempList.get("shovel");
        assertEquals(tempEntity.getDescription(), "A sturdy shovel");

        tempEntity = tempList.get("gold");
        assertEquals(tempEntity.getDescription(), "A big pot of gold");

        tempEntity = tempList.get("hole");
        assertEquals(tempEntity.getDescription(), "A deep hole in the ground");

    }
}
