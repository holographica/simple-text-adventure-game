package edu.uob;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ParseEntitiesTests {

    private GameServer server;
    private GameParser parser;

    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
        parser = new GameParser(entitiesFile, actionsFile);
        parser.parseEntities();
    }


    @Test
    void testLocationsExist() {
        assertTrue(parser.entityList.containsKey("cabin"));
        assertTrue(parser.entityList.containsKey("storeroom"));
        assertTrue(parser.entityList.containsKey("forest"));
        assertTrue(parser.entityList.containsKey("cellar"));
        assertTrue(parser.entityList.containsKey("riverbank"));
        assertTrue(parser.entityList.containsKey("clearing"));
        assertTrue(parser.entityList.containsKey("riverbank"));
    }

    @Test
    void testLocationDescriptions(){
        GameEntity tempLocation = parser.entityList.get("cabin");
        assertEquals("A log cabin in the woods", tempLocation.getDescription());
        tempLocation = parser.entityList.get("forest");
        assertEquals("A deep dark forest", tempLocation.getDescription());
        tempLocation = parser.entityList.get("cellar");
        assertEquals("A dusty cellar", tempLocation.getDescription());
        tempLocation = parser.entityList.get("riverbank");
        assertEquals("A grassy riverbank", tempLocation.getDescription());
        tempLocation = parser.entityList.get("clearing");
        assertEquals("A clearing in the woods", tempLocation.getDescription());
        tempLocation = parser.entityList.get("storeroom");
        assertEquals("Storage for any entities not placed in the game", tempLocation.getDescription());
    }

    @Test
    void testCharsAtLocation(){
        Location tempLocation = (Location) parser.entityList.get("cellar");
        GameCharacter tempCharacter = tempLocation.getCharacters().get("elf");
        assertEquals(tempCharacter.getDescription(),"An angry looking Elf");
        tempLocation = (Location) parser.entityList.get("storeroom");
        tempCharacter =  tempLocation.getCharacters().get("lumberjack");
        assertEquals(tempCharacter.getDescription(), "A burly wood cutter");
    }

    @Test
    void testArtefactsAtLocation(){
        Location tempLocation = (Location) parser.getEntityList().get("cabin");
        Artefact tempArtefact = tempLocation.getArtefacts().get("potion");
        assertEquals(tempArtefact.getDescription(),"A bottle of magic potion");
        tempArtefact = tempLocation.getArtefacts().get("axe");
        assertEquals(tempArtefact.getDescription(),"A razor sharp axe");
        tempArtefact = tempLocation.getArtefacts().get("coin");
        assertEquals(tempArtefact.getDescription(),"A silver coin");

        tempLocation = (Location) parser.getEntityList().get("forest");
        tempArtefact =  tempLocation.getArtefacts().get("key");
        assertEquals(tempArtefact.getDescription(), "A rusty old key");

        tempLocation = (Location) parser.getEntityList().get("riverbank");
        tempArtefact =  tempLocation.getArtefacts().get("horn");
        assertEquals(tempArtefact.getDescription(), "An old brass horn");

        tempLocation = (Location) parser.getEntityList().get("storeroom");
        tempArtefact =  tempLocation.getArtefacts().get("log");
        assertEquals(tempArtefact.getDescription(), "A heavy wooden log");
        tempArtefact =  tempLocation.getArtefacts().get("shovel");
        assertEquals(tempArtefact.getDescription(), "A sturdy shovel");
        tempArtefact =  tempLocation.getArtefacts().get("gold");
        assertEquals(tempArtefact.getDescription(), "A big pot of gold");
    }

    @Test
    void testFurnitureAtLocation() {
        Location tempLocation = (Location) parser.getEntityList().get("cabin");
        Furniture tempFurniture = tempLocation.getFurniture().get("trapdoor");
        assertEquals(tempFurniture.getDescription(), "A locked wooden trapdoor in the floor");

        tempLocation = (Location) parser.getEntityList().get("forest");
        tempFurniture = tempLocation.getFurniture().get("tree");
        assertEquals(tempFurniture.getDescription(), "A tall pine tree");

        tempLocation = (Location) parser.getEntityList().get("riverbank");
        tempFurniture = tempLocation.getFurniture().get("river");
        assertEquals(tempFurniture.getDescription(), "A fast flowing river");

        tempLocation = (Location) parser.getEntityList().get("clearing");
        tempFurniture = tempLocation.getFurniture().get("ground");
        assertEquals(tempFurniture.getDescription(), "It looks like the soil has been recently disturbed");

        tempLocation = (Location) parser.getEntityList().get("storeroom");
        tempFurniture = tempLocation.getFurniture().get("hole");
        assertEquals(tempFurniture.getDescription(), "A deep hole in the ground");
    }

    @Test
    void testPathsFromLocation() {
        Location tempLocation = (Location) parser.getEntityList().get("cabin");
        Location pathTo = tempLocation.getPaths().get("forest");
        assertEquals(pathTo.getDescription(), "A deep dark forest");

        tempLocation = (Location) parser.getEntityList().get("forest");
        pathTo = tempLocation.getPaths().get("cabin");
        assertEquals(pathTo.getDescription(), "A log cabin in the woods");

        tempLocation = (Location) parser.getEntityList().get("cellar");
        pathTo = tempLocation.getPaths().get("cabin");
        assertEquals(pathTo.getDescription(), "A log cabin in the woods");

        tempLocation = (Location) parser.getEntityList().get("forest");
        pathTo = tempLocation.getPaths().get("riverbank");
        assertEquals(pathTo.getDescription(), "A grassy riverbank");

        tempLocation = (Location) parser.getEntityList().get("riverbank");
        pathTo = tempLocation.getPaths().get("forest");
        assertEquals(pathTo.getDescription(), "A deep dark forest");

        tempLocation = (Location) parser.getEntityList().get("clearing");
        pathTo = tempLocation.getPaths().get("riverbank");
        assertEquals(pathTo.getDescription(), "A grassy riverbank");
    }

    @Test
    void testEntityList(){
        HashMap<String, GameEntity> tempList = parser.getEntityList();
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



        /*
        Test that a character can move from one location to another by taking a path between them:

        Start the game at the "cabin" location.
        Move the character to the "forest" location.
        Verify that the character is now at the "forest" location.
        Test that a character can interact with an artifact:

        Start the game at the "cabin" location.
        Pick up the "axe" artifact.
        Verify that the character has the "axe" in their inventory.
        Test that a character can interact with furniture:

        Start the game at the "cabin" location.
        Open the "trapdoor" furniture.
        Verify that the character can access the hidden room below.
        Test that a character can interact with another character:

        Start the game at the "cellar" location.
        Talk to the "elf" character.
        Verify that the character has completed the dialogue with the "elf".
        Test that a character can complete a quest by interacting with multiple locations, artifacts, and characters:

        Start the game at the "cabin" location.
        Pick up the "key" artifact.
        Move the character to the "forest" location.
        Use the "key" to unlock the "tree" furniture.
        Talk to the "lumberjack" character.
        Pick up the "log" artifact.
        Move the character to the "clearing" location.
        Dig up the "ground" furniture using the "shovel" artifact.
        Place the "log" in the "hole" furniture.
        Use the "horn" artifact to call the hidden creature.
        Verify that the quest is complete and the game has ended.
         */




}
