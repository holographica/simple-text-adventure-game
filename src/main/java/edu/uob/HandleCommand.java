package edu.uob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HandleCommand {

    private String command;
    private ArrayList<String> tokens;
    private int commandStartIndex;
    private String currentPlayer;
    public static final String[] basicCommands = {"inventory", "inv","get","drop","goto","look"};

    // raw command
    public HandleCommand(String command) throws IOException{
        this.command = command;
        List<String> tokens = Arrays.stream(command
                            .toLowerCase()
                            .split(" "))
                            .toList();
        this.tokens = new ArrayList<>(tokens);
        this.currentPlayer = this.tokens.get(0);

        // IS IT EVEN POSSIBLE NOT TO HAVE A NAME?
        // PROBABLY NOT?
        if (!this.tokens.contains(":")){
            throw new IOException("Invalid command; no username found");
        }
        this.commandStartIndex = this.tokens.indexOf(":");
    }

    public String getCommand() {
        return this.command;
    }

    public ArrayList<String> getTokens() {
        return this.tokens;
    }

    public int getCommandStartIndex() {
        return this.commandStartIndex;
    }

    public String getCurrentPlayer(){
        return this.currentPlayer;
    }

    public String checkForBasicCommand() throws IOException{
        int commandCount = 0;
        String targetCommand = "";
        // go through tokens
        // check if exactly one of the tokens
        // == one of the basic commands
        // if none or >1, it should print error message
        for (String token: tokens){
            if (Arrays.stream(basicCommands)
                    .anyMatch(command -> command.equals(token))){
                targetCommand=token;
                commandCount++;
            }
        }
        if (commandCount==1){
            // should i set a class variable here and return a boolean instead??
            // or could just check if returned string is empty
            return targetCommand;
        }
        return "";
    }





    // need to check string is valid?



//    public void parseCommand(){
//        if (!singlePlayer){
//            // get token directly after the colon
//            String
//        }
    }







