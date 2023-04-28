package edu.uob;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BasicCommand {

    private String command;
    private ArrayList<String> tokens;
    private int commandStartIndex;
    private String currentPlayer;
    public static final String[] basicCommands = {"inventory", "inv","get","drop","goto","look"};

    // raw command
    public BasicCommand(String command) throws IOException{
        this.command = command;
        List<String> tokens = Arrays.stream(command.split(" ")).toList();
        this.tokens = new ArrayList<>(tokens);
        this.currentPlayer = this.tokens.get(0);

        // ASK - IS THIS BAD PRACTICE?
        if (!this.tokens.contains(":")){
            throw new IOException("Invalid command; no username found");
        }

        this.commandStartIndex = this.tokens.indexOf(":");
    }
    // ASK: IS IT POSSIBLE NOT TO HAVE A NAME IN COMMAND??
    // if >1 player in player list and no name given, reject commmand??

    public String getCommand() {
        return command;
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }

    public int getCommandStartIndex() {
        return commandStartIndex;
    }

    public String getCurrentPlayer(){
        return this.currentPlayer;
    }





    // need to check string is valid?



//    public void parseCommand(){
//        if (!singlePlayer){
//            // get token directly after the colon
//            String
//        }
    }







