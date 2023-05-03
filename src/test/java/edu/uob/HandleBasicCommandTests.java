package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
final class HandleBasicCommandTests {
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
    public void testParseInv(){
        String response = sendCommandToServer("chris: inv");
        assertTrue(response.contains("Your inventory contains:"), "Response should give inventory details");
        assertFalse(response.contains("log"), "Inventory details should not contain log");
        assertFalse(response.contains("gold"), "Inventory details should not contain gold");
        assertFalse(response.contains("shovel"), "Inventory details should not contain shovel");

        Player currPlayer = server.getGameState().getCurrentPlayer();
        assertFalse(currPlayer.getInventory().containsKey("log"), "Player inventory should not contain log");
        assertFalse(currPlayer.getInventory().containsKey("gold"), "Player inventory should not contain gold");
        assertFalse(currPlayer.getInventory().containsKey("shovel"), "Player inventory should not contain shovel");

    }

    @Test public void testParseLook(){
        String response = sendCommandToServer("chris: look");

        assertTrue(response.contains("You are currently in:"), "Response should give current location details");
        assertTrue(response.contains("You can see:"), "Response should tell player what they can see");
        assertTrue(response.contains("You can see paths to:"), "Response should detail paths from current location");
        assertTrue(response.contains("cabin - A log cabin in the woods"), "Response should contain cabin name and description");
        assertTrue(response.contains("potion - A bottle of magic potion"), "Response should contain potion name and description");
        assertTrue(response.contains("axe - A razor sharp axe"), "Response should contain axe name and description");
        assertTrue(response.contains("forest"), "Response should contain forest");
        assertTrue(response.contains("coin - A silver coin"), "Response should contain coin name and description");
        assertTrue(response.contains("trapdoor - A locked wooden trapdoor in the floor"), "Response should contain trapdoor name and description");
        assertFalse(response.contains("gold"), "Response should not contain gold");
        assertFalse(response.contains("shovel"), "Response should not contain shovel");
        assertFalse(response.contains("riverbank"), "Response should not contain riverbank");

        sendCommandToServer("chris: get coin");
        response = sendCommandToServer("chris: look");
        assertFalse(response.contains("coin - A silver coin"));
        assertTrue(response.contains("axe - A razor sharp axe"));

        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("coin - A silver coin"),"Response should not contain gold name and description");
        assertTrue(response.contains("axe - A razor sharp axe"),"Response should  contain axe name and description");
        assertTrue(response.contains("trapdoor - A locked wooden trapdoor in the floor"),"Response should contain trapdoor name and description");
        assertFalse(response.contains("elf"),"Response should not contain elf");
        assertFalse(response.contains("riverbank"),"Response should not contain riverbank");
    }

    @Test public void testParseGoto() {
        String response = sendCommandToServer("chris: look");
        assertTrue(response.contains("You are currently in: cabin"));
        Player currPlayer = server.getGameState().getCurrentPlayer();
        assertEquals("cabin", currPlayer.getCurrentLocation(), "Current player location should be cabin");

        response = sendCommandToServer("chris: goto forest");
        currPlayer = server.getGameState().getCurrentPlayer();
        assertTrue(response.contains("new location: forest"), "Response should contain correct message when player moves location");
        assertFalse(response.contains("You are currently in:"), "Response should not tell player their current location");
        assertEquals("forest", currPlayer.getCurrentLocation(), "Current player location should be forest");
        assertNotEquals("cabin", currPlayer.getCurrentLocation(), "Current player location should not be cabin");
        assertNotEquals("riverbank", currPlayer.getCurrentLocation(), "Current player location should not be riverbank");

        response = sendCommandToServer("chris: look");
        assertTrue(response.contains("You are currently in: forest"), "Response should contain correct message when player looks around");
        currPlayer = server.getGameState().getCurrentPlayer();
        assertEquals("forest", currPlayer.getCurrentLocation(), "Current player location should be forest");
        assertNotEquals("cabin", currPlayer.getCurrentLocation(), "Current player location should not be cabin");

        response = sendCommandToServer("chris: goto riverbank");
        assertFalse(response.contains("chris moved to a new location: riverbank"), "Player should not be able to move to an accessible location.");
    }

    @Test
    public void testInvalidCommand(){
        String response = sendCommandToServer("chris: look goto");
        assertTrue(response.contains("Command does not contain exactly one basic command or action."),"Invalid command should return error message");
        assertFalse(response.contains("look"), "Response should not contain a basic command here");
        assertFalse(response.contains("goto"), "Response should not contain a basic command here");

        response = sendCommandToServer("chris: get look");
        assertTrue(response.contains("Command does not contain exactly one basic command or action."),"Invalid command should return error message");
        assertFalse(response.contains("look"));
        assertFalse(response.contains("get"));

        response = sendCommandToServer("chris: inv look");
        assertTrue(response.contains("Command does not contain exactly one basic command or action."),"Invalid command should return error message");
        assertFalse(response.contains("inv"),"Response should not contain a basic command here");
        assertFalse(response.contains("look"),"Response should not contain a basic command here");
    }
}
