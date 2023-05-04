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

public final class GameServer {
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

    public GameServer(final File entitiesFile, final File actionsFile) {
        final GameParser parser = new GameParser(entitiesFile, actionsFile);
        this.gameState = parser.getGameState();
    }

    public String handleCommand(String command)  {
        checkNewPlayer(command);
        final UserCommandHandler handler = new UserCommandHandler(command, this.gameState);
        try {
            return handler.parseCommand();
        } catch (Exception e) {
           return e.getMessage();
        }
    }

    /**
     * Checks whether current player is new.
     * If so, adds them as a new player.
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
