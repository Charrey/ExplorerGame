package com.charrey.game.model.serialize;

/**
 * Exception thrown when a loaded String does not conform to the save format required.
 */
public class SaveFormatException extends Exception {

    /**
     * Creates a new SaveFormatException
     *
     * @param message message to be shown
     * @param cause   exception that caused this exception
     */
    public SaveFormatException(String message, Throwable cause) {
        super(message, cause);
    }

}
