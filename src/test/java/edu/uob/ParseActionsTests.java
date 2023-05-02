package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Paths;
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
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
        parser = new GameParser(entitiesFile, actionsFile);
        parser.parseEntities();
        parser.parseActions();
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



}

