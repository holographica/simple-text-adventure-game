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
        parser.parseEntities();
        parser.parseActions();
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> server.handleCommand(command),
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void testActionsExist() {
        assertTrue(parser.actionList.containsKey("chop"));
        assertTrue(parser.actionList.containsKey("cut down"));
        assertTrue(parser.actionList.containsKey("drink"));
        assertTrue(parser.actionList.containsKey("unlock"));
        assertTrue(parser.actionList.containsKey("pay"));
        assertTrue(parser.actionList.containsKey("fight"));
        assertTrue(parser.actionList.containsKey("attack"));
        assertTrue(parser.actionList.containsKey("sing"));
        assertTrue(parser.actionList.containsKey("open"));
        assertFalse(parser.actionList.containsKey("stab"));
        assertFalse(parser.actionList.containsKey("key"));
        assertFalse(parser.actionList.containsKey("cellar"));
        assertFalse(parser.actionList.containsKey("log"));
        assertFalse(parser.actionList.containsKey("health"));
        assertFalse(parser.actionList.containsKey("potion"));
        assertFalse(parser.actionList.containsKey("elf"));
        assertFalse(parser.actionList.containsKey("coin"));
    }

    @Test
    void testMultipleTriggers() {
        HashSet<GameAction> chaseActions = parser.actionList.get("chase");
        assertNull(chaseActions);
        HashSet<GameAction> testActionSet = parser.actionList.get("chop");

        ArrayList<String> actionTriggers = new ArrayList<>();
        String narration = "You cut down the tree with the axe";
        actionTriggers.add("chop");
        actionTriggers.add("cut");
        actionTriggers.add("cut down");
        assertTrue(compareActionSetHelper(testActionSet, actionTriggers, narration));

        testActionSet = parser.actionList.get("drink");
        actionTriggers = new ArrayList<>();
        actionTriggers.add("drink");
        narration = "You drink the potion and your health improves";
        assertTrue(compareActionSetHelper(testActionSet, actionTriggers, narration));
    }


    @Test
    void testMultipleActions(){
        String response = sendCommandToServer("chris: drink");
        assertTrue(response.contains("You drink the potion and your health improves"));
        response = sendCommandToServer("chris: health");
        assertTrue(response.contains("Your current health is 3"));
        sendCommandToServer("chris: get axe");
        sendCommandToServer("chris: get coin");
        sendCommandToServer("chris: goto forest");
        sendCommandToServer("chris: get key");
        sendCommandToServer("chris: goto cabin");

        Location cabin = server.getGameState().getLocations().get("cabin");
        Player player = server.getGameState().getCurrentPlayer();
        assertFalse(cabin.getPaths().containsKey("cellar"));
        response = sendCommandToServer("chris: please kindly open the trapdoor using the key");
        assertTrue(response.contains("You unlock the door and see steps leading down into a cellar"));
        assertTrue(cabin.getPaths().containsKey("cellar"));

        Location cellar = server.getGameState().getLocations().get("cellar");
        sendCommandToServer("chris: goto cellar");
        assertEquals("cellar", player.getCurrentLocation());
        sendCommandToServer("chris: health");
        assertEquals(3, player.getHealthAsInt());
        response = sendCommandToServer("chris: fight elf with all your might");
        assertTrue(response.contains("you lose some health"));
        response = sendCommandToServer("chris: health");
        assertTrue(response.contains("Your current health is 2"));
        assertEquals(2, player.getHealthAsInt());

        sendCommandToServer("chris: fight elf");
        assertTrue(player.getInventory().containsKey("axe"));
        assertTrue(player.getInventory().containsKey("coin"));
        assertTrue(cellar.getCharacters().containsKey("elf"));
        assertFalse(cellar.getArtefacts().containsKey("coin"));
        assertFalse(cellar.getArtefacts().containsKey("axe"));
        sendCommandToServer("chris: fight elf");
        assertEquals("cabin", player.getCurrentLocation());
        assertFalse(player.getInventory().containsKey("axe"));
        assertTrue(cellar.getArtefacts().containsKey("axe"));
        assertTrue(cellar.getArtefacts().containsKey("coin"));


    }






}

