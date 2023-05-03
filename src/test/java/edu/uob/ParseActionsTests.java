package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class ParseActionsTests {

    private GameServer server;
    private GameParser parser;

    private boolean compareActionSetHelper (HashSet<GameAction> actionSet, ArrayList<String> triggers, String narration){
        boolean completeMatch = true;
        if (actionSet.isEmpty()){
            return false;
        }
        for (GameAction action : actionSet) {
            for (String trigger : triggers) {
                if (!action.getTriggers().contains(trigger) || !action.getNarration().equalsIgnoreCase(narration)) {
                    completeMatch = false;
                    break;
                }
            }
        }
        return completeMatch;
    }


    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "test-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
        parser = new GameParser(entitiesFile, actionsFile);
        parser.getGameState();
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> server.handleCommand(command),
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void testActionsExist() {
        assertTrue(GameState.getActionList().containsKey("chop"));
        assertTrue(GameState.getActionList().containsKey("cut down"));
        assertTrue(GameState.getActionList().containsKey("drink"));
        assertTrue(GameState.getActionList().containsKey("unlock"));
        assertTrue(GameState.getActionList().containsKey("pay"));
        assertTrue(GameState.getActionList().containsKey("fight"));
        assertTrue(GameState.getActionList().containsKey("attack"));
        assertTrue(GameState.getActionList().containsKey("sing"));
        assertTrue(GameState.getActionList().containsKey("open"));
        assertFalse(GameState.getActionList().containsKey("stab"));
        assertFalse(GameState.getActionList().containsKey("key"));
        assertFalse(GameState.getActionList().containsKey("cellar"));
        assertFalse(GameState.getActionList().containsKey("log"));
        assertFalse(GameState.getActionList().containsKey("health"));
        assertFalse(GameState.getActionList().containsKey("potion"));
        assertFalse(GameState.getActionList().containsKey("elf"));
        assertFalse(GameState.getActionList().containsKey("coin"));
    }

    @Test
    void testMultipleTriggers() {
        HashSet<GameAction> chaseActions = GameState.getActionList().get("chase");
        assertNull(chaseActions);
        HashSet<GameAction> testActionSet = GameState.getActionList().get("chop");

        ArrayList<String> actionTriggers = new ArrayList<>();
        String narration = "You cut down the tree with the axe";
        actionTriggers.add("chop");
        actionTriggers.add("cut");
        actionTriggers.add("cut down");
        assertTrue(compareActionSetHelper(testActionSet, actionTriggers, narration),"Action set should contain correct triggers and narration.");

        testActionSet = GameState.getActionList().get("drink");
        actionTriggers = new ArrayList<>();
        actionTriggers.add("drink");
        narration = "You drink the potion and your health improves";
        assertTrue(compareActionSetHelper(testActionSet, actionTriggers, narration), "Action set should contain correct triggers and narration.");
    }
}
