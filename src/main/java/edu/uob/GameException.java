package edu.uob;

import java.io.Serial;

public class GameException extends Exception {
    @Serial
    private static final long serialVersionUID = 1;
    public GameException (String message) {
        super(message);
    }

    public static class ExactlyOneCommandException extends GameException {
        @Serial private static final long serialVersionUID = 1;
        public ExactlyOneCommandException() {
            super("Command does not contain exactly one basic command or action.");
        }
    }

//    public static class MultipleBasicCommandsException extends GameException {
//        @Serial private static final long serialVersionUID = 1;
//        public MultipleBasicCommandsException() {
//            super("Command contains multiple basic commands.");
//        }
//    }

    public static class InvalidCommandStructureException extends GameException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidCommandStructureException() {
            super("Input command structure is invalid.");
        }
    }

    public static class InvalidBasicCommandException extends GameException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidBasicCommandException() {
            super("Input command contains an invalid basic command.");
        }
    }

    public static class InvalidSubjectException extends GameException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidSubjectException() {
            super("Input command contains a subject that doesn't match the chosen action or basic command");
        }
    }

    public static class RequiredEntityException extends GameException {
        @Serial private static final long serialVersionUID = 1;
        public RequiredEntityException() {
            super("User does not have access to all entities required to execute the chosen command");
        }
    }

    public static class MultipleActionException extends GameException {
        @Serial private static final long serialVersionUID = 1;
        public MultipleActionException() {
            super("Command contains multiple actions or duplicate trigger phrases.");
        }
    }

    public static class DuplicateSubjectException extends GameException {
        @Serial private static final long serialVersionUID = 1;
        public DuplicateSubjectException() {
            super("Command contains duplicate subjects.");
        }
    }

    public static class NoCommandException extends GameException {
        @Serial private static final long serialVersionUID = 1;
        public NoCommandException() {
            super("No basic commands or actions were found in input command.");
        }
    }


}
