package edu.uob;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

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
    void testLocationsExist(){
        assertTrue(parser.entityList.containsKey("cabin"));
        assertTrue(parser.entityList.containsKey("storeroom"));
        assertTrue(parser.entityList.containsKey("forest"));
        assertTrue(parser.entityList.containsKey("cellar"));
        assertTrue(parser.entityList.containsKey("riverbank"));
        assertTrue(parser.entityList.containsKey("clearing"));
        assertTrue(parser.entityList.containsKey("riverbank"));
    }

//    @Test
//    void test


    //



}
