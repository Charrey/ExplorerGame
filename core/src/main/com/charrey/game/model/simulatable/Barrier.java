package com.charrey.game.model.simulatable;

import com.charrey.game.util.GridItem;

/**
 * Simulatable that does not move, but serves as an obstacle for other simulatables.
 */
public abstract class Barrier extends Simulatable {

    /**
     * Creates a new Barrier
     *
     * @param location location of the barrier
     */
    public Barrier(GridItem location) {
        super(location, 10, 1, 1);
    }

    @Override
    public void simulateStep() {

    }

    /**
     * Returns whether the barrier is currently blocking movement of other simulatables
     *
     * @return whether this is blocking
     */
    public abstract boolean isBlocking();

    @Override
    public String shortName() {
        return "B";
    }

}
