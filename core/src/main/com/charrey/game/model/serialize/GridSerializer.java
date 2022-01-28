package com.charrey.game.model.serialize;

import com.charrey.game.model.Grid;

import java.net.URI;

/**
 * Interface used to serialize a model (and probably save it to a save file). Different implementations kan serialize in
 * different ways, such as XML, JSON or other DSLs.
 */
public interface GridSerializer {

    /**
     * Serializes a model to string
     *
     * @param grid           model to serialize
     * @param targetLocation file location where the file will be saved to (used to generate relative path for inter-grid references)
     * @return serialized model
     */
    String serializeToString(Grid grid, URI targetLocation);


}
