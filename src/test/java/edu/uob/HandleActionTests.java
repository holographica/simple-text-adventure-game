package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
final class HandleActionTests {
    private GameServer server;

    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "test-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
        GameParser parser = new GameParser(entitiesFile, actionsFile);
        parser.getGameState();
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> server.handleCommand(command.toLowerCase()),
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testActionsSimple(){
        sendCommandToServer("chris: get axe");
        sendCommandToServer("chris: goto forest");
        String response = sendCommandToServer("chris: chop tree");
        assertTrue(response.contains("You cut down the tree with the axe"), "Response should give correct details of the most recently performed action");
    }

    @Test
    void testVariousActions(){
        String response = sendCommandToServer("chris: drink");
        assertTrue(response.contains("You drink the potion and your health improves"),"Response should contain correct action narration.");
        response = sendCommandToServer("chris: health");
        assertTrue(response.contains("Your current health is 3"),"Current player health should be 3.");
        sendCommandToServer("chris: get axe");
        sendCommandToServer("chris: get coin");
        sendCommandToServer("chris: goto forest");
        sendCommandToServer("chris: get key");
        sendCommandToServer("chris: goto cabin");

        Location cabin = server.getGameState().getLocations().get("cabin");
        Player player = server.getGameState().getCurrentPlayer();
        assertFalse(cabin.getPaths().containsKey("cellar"),"There should not be a path that leads from the cabin to the cellar.");
        response = sendCommandToServer("chris: please kindly open the trapdoor using the key");
        assertTrue(response.contains("You unlock the door and see steps leading down into a cellar"),"Response should contain correct action narration.");
        assertTrue(cabin.getPaths().containsKey("cellar"),"There should now exist a path from the cabin to the cellar.");

        Location cellar = server.getGameState().getLocations().get("cellar");
        sendCommandToServer("chris: goto cellar");
        assertEquals("cellar", player.getCurrentLocation(),"Current player location should be the cellar.");
        sendCommandToServer("chris: health");
        assertEquals(3, player.getHealthAsInt(),"Current player health should be 3.");
        response = sendCommandToServer("chris: fight elf with all your might");
        assertTrue(response.contains("you lose some health"),"Response should contain correct action narration.");
        response = sendCommandToServer("chris: health");
        assertTrue(response.contains("Your current health is 2"),"Response should contain correct basic command response.");
        assertEquals(2, player.getHealthAsInt(),"Current player health should be the 2.");

        sendCommandToServer("chris: fight elf");
        assertTrue(player.getInventory().containsKey("axe"),"Current player inventory should contain an axe.");
        assertTrue(player.getInventory().containsKey("coin"),"Current player inventory should contain a coin.");
        assertTrue(cellar.getCharacters().containsKey("elf"),"Current player inventory should contain an elf.");
        assertFalse(cellar.getArtefacts().containsKey("coin"),"Cellar should no longer contain a coin.");
        assertFalse(cellar.getArtefacts().containsKey("axe"),"Cellar should no longer contain an axe.");
        sendCommandToServer("chris: fight elf");
        assertEquals("cabin", player.getCurrentLocation(), "Current player location should be the cellar.");
        assertFalse(player.getInventory().containsKey("axe"),"Current player inventory should not contain an axe.");
        assertTrue(cellar.getArtefacts().containsKey("axe"),"Cellar should now contain an axe.");
        assertTrue(cellar.getArtefacts().containsKey("coin"),"Cellar should now contain a coin.");
    }

    @Test
    void testMoreActions(){
        String response = sendCommandToServer("chris: get axe");
        assertFalse(response.contains("You drink the potion and your health improves"),"Response should contain correct action narration.");
        response = sendCommandToServer("chris: inv");
        assertTrue(response.contains("axe"),"Player inventory should now contain an axe");
        sendCommandToServer("chris: get coin");
        sendCommandToServer("chris: goto forest");
        sendCommandToServer("chris: chop tree");
        sendCommandToServer("chris: get log");
        response = sendCommandToServer("chris: inv");
        assertTrue(response.contains("log"),"Player inventory should now contain a log");
        response = sendCommandToServer("chris: look");
        assertFalse(response.contains("tree"),"Current location should no longer contain a tree.");

        Location riverbank = server.getGameState().getLocations().get("riverbank");
        Player player = server.getGameState().getCurrentPlayer();
        assertTrue(riverbank.getPaths().containsKey("forest"),"There should be a path that leads from the riverbank to the forest.");
        sendCommandToServer("chris: goto riverbank");
        sendCommandToServer("chris: get horn");
        response = sendCommandToServer("chris: blow horn");
        assertTrue(response.contains("You blow the horn and as if by magic, a lumberjack appears !"),response);
        assertFalse(riverbank.getPaths().containsKey("cellar"),"There should not be a path leading from the riverbank to the cellar.");
        sendCommandToServer("chris: goto clearing");
        assertEquals("riverbank", player.getCurrentLocation(),"Current player location should not have changed.");
        sendCommandToServer("chris: drop log");
        response = sendCommandToServer("chris: bridge");
        response = sendCommandToServer("chris: goto clearing");
        assertTrue(response.contains("Moved to a new location: clearing"),"Response should contain correct details of executed action.");
        response = sendCommandToServer("chris: health");
        assertFalse(response.contains("Your current health is 2"),"Response should contain correct player information.");
        assertEquals(3, player.getHealthAsInt(),"Current player health should be 3.");
    }

    @Test
    void testMoreActions2(){
        Location forest = server.getGameState().getLocations().get("forest");
        sendCommandToServer("chris: get coin");
        sendCommandToServer("chris: get axe");
        sendCommandToServer("chris: goto forest");
        assertTrue(forest.getFurniture().containsKey("tree"),"Forest should contain a tree.");
        sendCommandToServer("chris: chop cut down tree");
        assertFalse(forest.getArtefacts().containsKey("tree"),"Forest should no logner contain a tree.");
        assertTrue(forest.getArtefacts().containsKey("log"),"Forest should now contain a log.");
        sendCommandToServer("chris: get log");
        Player player = server.getGameState().getCurrentPlayer();
        assertTrue(player.getInventory().containsKey("log"),"Player inventory should now contain a log.");
        sendCommandToServer("chris: goto riverbank");
        Location riverbank = server.getGameState().getLocations().get("riverbank");
        sendCommandToServer("chris: bridge");
        assertTrue(riverbank.getPaths().containsKey("clearing"),"Location should now contain a path to the clearing.");
        riverbank = server.getGameState().getLocations().get("riverbank");
        String response = sendCommandToServer("chris: burn");
        assertTrue(response.contains("You burn the bridge"),"Response should contain correct action narration"+response);
        assertFalse(riverbank.getPaths().containsKey("clearing"),"Location should no longer contain a path to the clearing.");
        sendCommandToServer("chris: goto clearing");
        assertNotEquals("clearing", player.getCurrentLocation(), "Location should no longer be accessible.");
        sendCommandToServer("chris: get horn");
        sendCommandToServer("chris: blow horn");
        assertTrue(riverbank.getCharacters().containsKey("lumberjack"),"Location should now contain the lumberjack.");
    }

    @Test
    void testMultiplePlayers1() {
        sendCommandToServer("chris: get coin");
        sendCommandToServer("simon: get axe");
        String response = sendCommandToServer("simon: look");
        assertTrue(response.contains("chris"), "Players should be able to see each other if at same location.");
        assertFalse(response.contains("simon"), "Players should be not be able to see themselves.");
        sendCommandToServer("chris: goto forest");
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("chris"), "Players should be not be able to see other players in other locations.");
        response = sendCommandToServer("chris: inv");
        Player player = server.getGameState().getCurrentPlayer();
        assertFalse(player.getInventory().containsKey("axe"),response);
        assertEquals("chris", player.getName(), "Current player should be chris");
        response = sendCommandToServer("chris: chop tree");
        assertFalse(response.contains("You cut down the tree with the axe"),"Chop action should not have been executed without required entities");
        Location forest = server.getGameState().getLocations().get("forest");
        assertFalse(forest.getArtefacts().containsKey("log"), "Chop action should not have been executed");
        assertTrue(forest.getFurniture().containsKey("tree"), "Chop action should not have been executed");
    }
}
