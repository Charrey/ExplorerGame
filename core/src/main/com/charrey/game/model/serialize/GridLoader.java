package com.charrey.game.model.serialize;

import com.charrey.game.model.Grid;

/**
 * Interface used to load a model from a string (probably from a save file). Different implementations kan load different
 * serializations, such as XML, JSON or other DSLs.
 */
public interface GridLoader {

    /**
     * Loads a model from its serialization
     * @param serialized serialized model
     * @return model that corresponds to that serialization
     * @throws SaveFormatException thrown when the string does not conform to the implementation's requirements
     */
    Grid load(String serialized) throws SaveFormatException;

    /**
     * Exception thrown when a loaded String does not conform to the save format required.
     */
    class SaveFormatException extends Exception {

        /**
         * Creates a new SaveFormatException
         * @param message message to be shown
         * @param cause exception that caused this exception
         */
        public SaveFormatException(String message, Throwable cause) {
            super(message, cause);
        }

    }
}
