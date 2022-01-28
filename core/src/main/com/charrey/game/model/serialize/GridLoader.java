package com.charrey.game.model.serialize;

import com.badlogic.gdx.files.FileHandle;
import com.charrey.game.model.Grid;

/**
 * Interface used to load a model from a string (probably from a save file). Different implementations kan load different
 * serializations, such as XML, JSON or other DSLs.
 */
public abstract class GridLoader {


    private static GridLoader instance;

    /**
     * Returns a GridLoader
     *
     * @return the singleton gridloader
     */
    public static GridLoader get() {
        if (instance == null) {
            instance = new XMLLoader();
        }
        return instance;
    }

    /**
     * Loads a model from its serialization
     *
     * @param fileBeingLoaded file being loaded
     * @return model that corresponds to that serialization
     * @throws SaveFormatException thrown when the string does not conform to the implementation's requirements
     */
    public abstract Grid load(FileHandle fileBeingLoaded) throws SaveFormatException;

}
