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
import java.util.Arrays;
import java.util.List;

/** This class implements the STAG server. */
public final class GameServer {

    // Object to hold the current game state
    private final GameState gameState;
    private static final char END_OF_TRANSMISSION = 4;
    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "test-actions.xml").toAbsolutePath().toFile();
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
    public GameServer(final File entitiesFile, final File actionsFile) {
        final GameParser parser = new GameParser(entitiesFile, actionsFile);
        this.gameState = parser.getGameState();
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.GameServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming game commands and carries out the corresponding actions.
    */
    public String handleCommand(String command) {
        checkNewPlayer(command);
        final UserCommandHandler handler = new UserCommandHandler(command, this.gameState);
        return handler.parseCommand();
    }

    /**
     * Check whether current player is new.
     * If so, add them as a new player.
     */
    public void checkNewPlayer(final String command){
        final StringBuilder builder = new StringBuilder();
        final List<String> temp = Arrays.stream(command.split(":")).toList();
        builder.append(temp.get(0));
        final String name = builder.toString();
        if (this.gameState.getPlayerByName(name)==null){
            final Player newPlayer = new Player(name, "Player called "+name);
            this.gameState.addPlayer(newPlayer);
            this.gameState.setCurrentPlayer(newPlayer);
        }
        else {
            this.gameState.setCurrentPlayer(this.gameState.getPlayerByName(name));
        }
    }

    public GameState getGameState(){
        return this.gameState;
    }

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
