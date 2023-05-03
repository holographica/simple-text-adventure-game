package edu.uob;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ActionParser {

    final Map<String, HashSet<GameAction>> actionList;
    File actionsFile;

    public ActionParser(File actionsFile) {
        this.actionsFile = actionsFile;
        this.actionList = new HashMap<>();
    }

    public void parseActions() {
        try {
            final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//            Document document = builder.parse("config" + File.separator + "test-actions.xml");
            final Document document = builder.parse(actionsFile.getAbsolutePath());
            final Element root = document.getDocumentElement();
            final NodeList actions = root.getChildNodes();
            makeNodeList(actions)
                    .forEach(
                            element -> {
                                if (element.getNodeType() == Node.ELEMENT_NODE) {
                                    final GameAction newAction = new GameAction();
                                    parseActionElements((Element) element, newAction);
                                    addActionToSet(this.actionList, newAction);
                                }
                            }
                    );
            GameParser.gameState.setActionList(this.actionList);
        } catch (ParserConfigurationException pce) {
            System.out.println("ParserConfigurationException was thrown when attempting to read basic actions file");
        } catch (SAXException saxe) {
            System.out.println("SAXException was thrown when attempting to read basic actions file");
        } catch (IOException ioe) {
            System.out.println("IOException was thrown when attempting to read basic actions file");
        }
    }

    public void addActionToSet(Map<String, HashSet<GameAction>> allActions, GameAction action){
        action.getTriggers().forEach(
                trigger -> {
                    if (allActions.containsKey(trigger)){
                        this.actionList.get(trigger).add(action);
                    }
                    else {
                        final HashSet<GameAction> actionSet = new HashSet<>();
                        actionSet.add(action);
                        this.actionList.put(trigger, actionSet);
                    }
                }
        );
    }

    public void parseActionElements(Element actionElement, GameAction newAction){
        parseNarration(actionElement, newAction);
        parseTriggers(actionElement, newAction);
        parseSubjects(actionElement, newAction);
        parseConsumed(actionElement, newAction);
        parseProduced(actionElement, newAction);
    }

    public void parseTriggers (Element action, GameAction newAction){
        final Element triggers = (Element) action.getElementsByTagName("triggers").item(0);
        final NodeList keyphrases = triggers.getElementsByTagName("keyphrase");
        makeNodeList(keyphrases)
                .forEach(
                        node -> {
                            final String triggerPhrase = node.getTextContent();
                            newAction.addTrigger(triggerPhrase);
                        }
                );
    }

    public void parseSubjects (Element action, GameAction newAction){
        final Element subjects = (Element) action.getElementsByTagName("subjects").item(0);
        final NodeList entities = subjects.getElementsByTagName("entity");
        makeNodeList(entities)
                .forEach(
                        node -> {
                            final String entityName = node.getTextContent();
                            newAction.addSubjectEntity(entityName);
                        }
                );
    }

    public void parseConsumed (Element action, GameAction newAction){
        final Element consumed = (Element) action.getElementsByTagName("consumed").item(0);
        final NodeList entities = consumed.getElementsByTagName("entity");
        makeNodeList(entities)
                .forEach(
                        node -> {
                            final String entityName = node.getTextContent();
                            newAction.addConsumedEntity(entityName);
                        }
                );
    }

    public void parseProduced (Element action, GameAction newAction){
        final Element produced = (Element) action.getElementsByTagName("produced").item(0);
        final NodeList entities = produced.getElementsByTagName("entity");
        makeNodeList(entities)
                .forEach(
                        node -> {
                            final String entityName = node.getTextContent();
                            newAction.addProducedEntity(entityName);
                        }
                );
    }

    public void parseNarration(Element action, GameAction newAction){
        final Element narrationElement = (Element) action.getElementsByTagName("narration").item(0);
        final String narration = narrationElement.getTextContent();
        newAction.setNarration(narration);
    }

    public List<Node> makeNodeList(NodeList list){
        final ArrayList<Node> nodesToIterateOver = new ArrayList<>();
        for (int i=0; i < list.getLength(); i++){
            nodesToIterateOver.add(i, list.item(i));
        }
        return nodesToIterateOver;
    }
}
