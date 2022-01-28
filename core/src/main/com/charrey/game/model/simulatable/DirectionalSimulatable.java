package com.charrey.game.model.simulatable;

import com.charrey.game.model.Direction;
import com.charrey.game.util.GridItem;

/**
 * Simulatable class that have a specified direction. This class provides ways for subclasses to manage its direction but does not
 * implement rendering logic based on direction.
 */
public abstract class DirectionalSimulatable extends Simulatable {


    private Direction direction;
    private Direction nextDirection;

    /**
     * Creates a new Simulatable
     *
     * @param location       location of the Simulatable
     * @param direction      direction of the Simulatable
     * @param renderPriority render priority
     * @param width          width (in grid blocks) of this simulatable
     * @param height         height (in grid blocks) of this simulatable
     */
    protected DirectionalSimulatable(GridItem location, Direction direction, int renderPriority, int width, int height) {
        super(location, renderPriority, width, height);
        this.direction = direction;
        this.nextDirection = direction;
    }

    /**
     * Changes the simulatable's direction immediately (in the current step). This should only be called for simulatables that have
     * not yet been added to a model being simulated.
     *
     * @param direction direction to set the simulatable to
     */
    public void setDirectionNow(Direction direction) {
        this.direction = direction;
        this.nextDirection = direction;
    }

    /**
     * Returns the direction this explorer is facing.
     *
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Changes the simulatable's direction (in the next step).
     *
     * @param direction direction to set the simulatable to next step
     */
    public void setDirection(Direction direction) {
        this.nextDirection = direction;
    }

    /**
     * Changes the simulatable's location (in the next step) by moving one step forward.
     */
    protected void advance() {
        super.advance(getDirection());
    }

    /**
     * Changes the simulatable's location immediately (in the current step) by moving one step in the direction this simulatable
     * is facing. This should only be called for simulatables that have not yet been added to a model.
     */
    protected void advanceNow() {
        super.advanceNow(getDirection());
    }

    @Override
    public int hashCode() {
        return direction.hashCode();
    }

    @Override
    public void stateSwitchStep() {
        direction = nextDirection;
        super.stateSwitchStep();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirectionalSimulatable that = (DirectionalSimulatable) o;
        return direction == that.direction;
    }
}
