package com.charrey.game.settings;

import com.charrey.game.model.simulatable.Simulatable;
import com.charrey.game.util.GridItem;

/**
 * Factory to provide some information about a simulatable's class and a method to create instances of that simulatable.
 *
 * @param <T> Type of the simulatable
 */
public interface NewBlockFactory<T extends Simulatable> {

    /**
     * Returns the width of a placed Simulatable of type T in number of squares
     *
     * @return simulatable width
     */
    int getWidth();

    /**
     * Returns the height of a placed Simulatable of type T in number of squares
     *
     * @return simulatable height
     */
    int getHeight();

    /**
     * Creates a simulatable of type T
     *
     * @param location location of the simulatable
     * @return the simulatable
     */
    T makeSimulatable(GridItem location);
}
