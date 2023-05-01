package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** This class implements the STAG server. */
public final class GameServer {
    private final File entitiesFile;
    private final File actionsFile;
    private GameState gameState;
    private static final char END_OF_TRANSMISSION = 4;
    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
        BufferedReader commandLine = new BufferedReader(new InputStreamReader(System.in));
        String command = commandLine.readLine();
        server.handleCommand(command);
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.GameServer(File, File)}) otherwise we won't be able to mark
    * your submission correctly.
    *
    * <p>You MUST use the supplied {@code entitiesFile} and {@code actionsFile}
    *
    * @param entitiesFile The game configuration file containing all game entities to use in your game
    * @param actionsFile The game configuration file containing all game actions to use in your game
    *
    */
    public GameServer(File entitiesFile, File actionsFile) {
        this.entitiesFile = entitiesFile;
        this.actionsFile = actionsFile;

        GameParser gp = new GameParser(entitiesFile, actionsFile);
//        gp.parseEntities();
        this.gameState = gp.getGameState();
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.GameServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming game commands and carries out the corresponding actions.
    */
    public String handleCommand(String command) {

        // set current player

        // TODO
        // CLEAN UP AND ENCAPSULATE BELOW INTO METHOD


        StringBuilder strb = new StringBuilder();
        List<String> temp = Arrays.stream(command.split(":")).toList();
        strb.append(temp.get(0));
         String name = strb.toString();
        if (this.gameState.getPlayerByName(name)==null){
            Player newPlayer = new Player(name, "Player called"+name);
            this.gameState.addPlayer(newPlayer);
            this.gameState.setCurrentPlayer(newPlayer);
        }
        else {
            this.gameState.setCurrentPlayer(this.gameState.getPlayerByName(name));
        }

        Artefact aft = this.gameState.getAllArtefacts().get("log");
        this.gameState.getCurrentPlayer().addToInventory(aft);

        CommandHandler handler = new CommandHandler(command, this.gameState);
        return handler.parseCommand();
//
//        // TODO implement your server logic here
//        if (command.contains("inventory")||command.contains("inv")){
//            // list artefacts being carried by player
//        }
//        if (command.contains("get")){
//            // pick up a specified artefact from the current location and adds it into player's inventory
//        }
//        if (command.contains("drop")){
//            // put down an artefact from player's inventory and places it into the current location
//        }
//        if (command.contains("goto")){
//            // moves the player to the specified location (if there is a path to that location)
//        }
//        if (command.contains("look")){
//            // print names and descriptions of entities in the current location and lists paths to other locations
//        }
//
//        return "";
    }

        public GameState getGameState(){
            return this.gameState;
        }



    //  === Methods below are there to facilitate server related operations. ===

    /**
    * Starts a *blocking* socket server listening for new connections. This method blocks until the
    * current thread is interrupted.
    *
    * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
    * you want to.
    *
    * @param portNumber The port to listen on.
    * @throws IOException If any IO related operation fails.
    */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
    * Handles an incoming connection from the socket server.
    *
    * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
    * * you want to.
    *
    * @param serverSocket The client socket to read/write from.
    * @throws IOException If any IO related operation fails.
    */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
