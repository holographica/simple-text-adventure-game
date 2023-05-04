package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class GameExceptionTests {

    GameParser parser;
    private GameServer server;
    GameState gameState;

    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "test-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
        parser = new GameParser(entitiesFile, actionsFile);
        gameState = parser.getGameState();
        Player player = new Player("chris","a fine player");
        gameState.setCurrentPlayer(player);
    }

    @Test
    public void testExactlyOneCommandException() {
        String response = server.handleCommand("chris: get drink");
        assertEquals("Command does not contain exactly one basic command or action.",response,
                "ExactlyOneCommandException should be thrown when the command does not contain exactly one basic command or action.");
    }

    @Test
    public void testInvalidCommandStructureException() {
        String response = server.handleCommand("chris: potion get");
        assertEquals("Input command structure is invalid.",response,
                "InvalidCommandStructureException should be thrown when the command structure is invalid.");
    }

    @Test
    public void testNoValidContentException() {
        String response = server.handleCommand("chris: look trapdoor");
        assertEquals("Input command didn't contain any valid entities, commands or actions.",response,
                "NoValidContentException should be thrown when the command structure is invalid.");
    }

    @Test
    public void testRequiredEntityException() {
        String response = server.handleCommand("chris: burn");
        assertEquals("User does not have access to all entities required to execute the chosen command",response,
                "RequiredEntityException should be thrown when the user doesn't have access to all " +
                        "entities required to execute a chosen command.");
    }

    @Test
    public void testMultipleActionException() {
        String response = server.handleCommand("chris: drink drink");
        assertEquals("Command contains multiple actions or duplicate trigger phrases.",response,
                "MultipleActionException should be thrown when the input command contains duplicate trigger phrases.");
    }

    @Test
    public void testDuplicateSubjectException() {
        String response = server.handleCommand("chris: get potion potion");
        assertEquals("Command contains duplicate subjects.",response,
                "DuplicateSubjectException should be thrown when the input command contains duplicate subjects.");
    }


}
