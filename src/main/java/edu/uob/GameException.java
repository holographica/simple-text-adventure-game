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

    public static class InvalidCommandStructureException extends GameException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidCommandStructureException() {
            super("Input command structure is invalid.");
        }
    }

    public static class NoValidContentException extends GameException {
        @Serial private static final long serialVersionUID = 1;
        public NoValidContentException() {
            super("Input command didn't contain any valid entities, commands or actions.");
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
}
