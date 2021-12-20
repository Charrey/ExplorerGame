package com.charrey.game.model.serialize;

import com.charrey.game.model.Grid;

/**
 * Interface used to serialize a model (and probably save it to a save file). Different implementations kan serialize in
 * different ways, such as XML, JSON or other DSLs.
 */
public interface GridSerializer {

    /**
     * Serializes a model to string
     * @param grid model to serialize
     * @return serialized model
     */
    String serialize(Grid grid);


}
