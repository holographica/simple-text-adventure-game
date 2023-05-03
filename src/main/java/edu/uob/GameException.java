//package edu.uob;
//
//import java.io.Serial;
//
//public class GameException extends Exception {
//
//    @Serial
//    private static final long serialVersionUID = 1;
//
//    public GameException (String message) {
//        super(message);
//    }
//
//
//    // invalid basic commands
//    // invalid subject - subjejct belongs in diff command
//    // multiple actions
//    // no command detected
//    // bad command structure

// enforce basic command action ordering??
//
//
//
//
//    public static class  extends GameException {
//        @Serial private static final long serialVersionUID = 1;
//
//        public OutsideCellRangeException(RowOrColumn dimension, int pos) {
//            super("Position " + pos + " is out of range for " + dimension.name());
//        }
//    }
//
//    public static class InvalidIdentifierLengthException extends GameException {
//        @Serial private static final long serialVersionUID = 1;
//
//        public InvalidIdentifierLengthException(int length) {
//            super("Identifier of size " + length + " is invalid");
//        }
//    }
//
//    public static class InvalidIdentifierCharacterException extends GameException {
//        @Serial private static final long serialVersionUID = 1;
//
//        public InvalidIdentifierCharacterException(RowOrColumn problemDimension, char character) {
//            super(character + " is not a valid character for a " + problemDimension.name());
//        }
//    }
//
//    public static class CellAlreadyTakenException extends GameException {
//        @Serial private static final long serialVersionUID = 1;
//
//        public CellAlreadyTakenException(int row, int column) {
//            super("Cell [" + row + "," + column + "] has already been claimed");
//        }
//    }
//
//
//}
