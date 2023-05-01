package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
public class ParseCommandTests {
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

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> server.handleCommand(command.toLowerCase()),
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testParseInv(){
        // TODO
        // EXTEND TESTS GREATLY
        // NEED TO DO INTEGRATION TESTING
        // IE: CHECK THAT CURR PLAYER INVENTORY CONTAINS CORRECT STUFF


        String response = sendCommandToServer("chris: inv");
        assertTrue(response.contains("Your inventory contains:"));
        assertTrue(response.contains("log"));
        assertFalse(response.contains("gold"));
        assertFalse(response.contains("shovel"));

        Player currPlayer = server.getGameState().getCurrentPlayer();
        assertTrue(currPlayer.getInventory().containsKey("log"));
        assertFalse(currPlayer.getInventory().containsKey("gold"));
        assertFalse(currPlayer.getInventory().containsKey("shovel"));

    }

    @Test public void testParseLook(){
        // TODO
        // EXTEND TESTS GREATLY
        // NEED TO DO INTEGRATION TESTING
        String response = sendCommandToServer("chris: look");

        assertTrue(response.contains("You are currently in:"));
        assertTrue(response.contains("You can see:"));
        assertTrue(response.contains("You can see paths to:"));
        assertTrue(response.contains("cabin - a log cabin in the woods"));
        assertTrue(response.contains("potion - a bottle of magic potion"));
        assertTrue(response.contains("axe - a razor sharp axe"));
        assertTrue(response.contains("forest"));
        assertTrue(response.contains("coin - a silver coin"));
        assertTrue(response.contains("trapdoor - a locked wooden trapdoor in the floor"));
        assertFalse(response.contains("gold"));
        assertFalse(response.contains("shovel"));
        assertFalse(response.contains("riverbank"));
    }

    @Test public void testParseGoto() {
        String response = sendCommandToServer("chris: look");
        assertTrue(response.contains("You are currently in: cabin"));
        Player currPlayer = server.getGameState().getCurrentPlayer();
        assertEquals("cabin", currPlayer.getCurrentLocation());

        response = sendCommandToServer("chris: goto forest");
        currPlayer = server.getGameState().getCurrentPlayer();
        assertTrue(response.contains("chris moved to a new location: forest"));
        assertFalse(response.contains("You are currently in:"));
        assertFalse(response.contains("too many basic commands detected"));
        assertEquals("forest", currPlayer.getCurrentLocation());
        assertNotEquals("cabin", currPlayer.getCurrentLocation());
        assertNotEquals("riverbank", currPlayer.getCurrentLocation());

        response = sendCommandToServer("chris: look");
        assertTrue(response.contains("You are currently in: forest"));
        currPlayer = server.getGameState().getCurrentPlayer();
        assertEquals("forest", currPlayer.getCurrentLocation());
        assertNotEquals("cabin", currPlayer.getCurrentLocation());
    }

    @Test
    public void testInvalidCommand(){
        String response = sendCommandToServer("chris: look goto");
        assertTrue(response.contains("too many basic commands detected"));
        assertFalse(response.contains("look"));
        assertFalse(response.contains("goto"));

        response = sendCommandToServer("chris: get look");
        assertTrue(response.contains("too many basic commands detected"));
        assertFalse(response.contains("look"));
        assertFalse(response.contains("get"));

        response = sendCommandToServer("chris: inv look");
        assertTrue(response.contains("too many basic commands detected"));
        assertFalse(response.contains("inv"));
        assertFalse(response.contains("look"));
    }



}
