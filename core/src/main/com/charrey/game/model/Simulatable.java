package com.charrey.game.model;

import com.badlogic.gdx.graphics.Texture;
import com.charrey.game.util.GridItem;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Item in a model that can be rendered and can be simulated. Each implementation may behave differently.
 */
public abstract class Simulatable {

    private final int width;
    private final int height;
    private Grid grid;
    private GridItem location;
    private GridItem nextLocation;
    private final int renderPriority;
    private boolean removeInNextStep = false;
    private final Queue<Simulatable> toAddNextStep = new LinkedList<>();

    /**
     * Creates a new Simulatable
     * @param location location of the Simulatable
     * @param renderPriority render priority
     * @param width width (in grid blocks) of this simulatable
     * @param height height (in grid blocks) of this simulatable
     */
    protected Simulatable(GridItem location, int renderPriority, int width, int height) {
        this.location = location;
        this.nextLocation = location;
        this.renderPriority = renderPriority;
        this.width = width;
        this.height = height;
    }

    /**
     * Removes this simulatable from the model in the next step
     */
    protected void removeInNextStep() {
        removeInNextStep = true;
    }

    /**
     * Adds a new simulatable to the model next step
     * @param toAdd simulatable to add
     */
    protected void addInNextStep(Simulatable toAdd) {
        toAddNextStep.add(toAdd);
    }

    /**
     * Computes the next state
     */
    public abstract void simulateStep();

    /**
     * Sets the current state to be the computed state
     */
    public void stateSwitchStep() {
        while (!toAddNextStep.isEmpty()) {
            grid.add(toAddNextStep.poll());
        }
        if (removeInNextStep) {
            grid.remove(this);
        } else {
            location = nextLocation;
        }
    }

    /**
     * Returns a texture of one of the blocks this simulatable occupies
     * @param xOffset if this simulatable is more than 1 block wide, this is the offset of which part of the simulatable to render
     * @param yOffset if this simulatable is more than 1 block high, this is the offset of which part of the simulatable to render
     * @param textureWidth width of the texture in pixels
     * @param textureHeight height of the texture in pixels
     * @return the texture
     */
    public abstract Texture getTexture(int xOffset, int yOffset, int textureWidth, int textureHeight);

    /**
     * Sets the model that houses this simulatable
     * @param grid parent model
     */
    public void setContainingGrid(Grid grid) {
        this.grid = grid;
    }

    /**
     * Returns the set of simulatables that reside in a specific direction of this simulatable (additive across its width/height)
     * @param direction direction of which to request simulatables
     * @return a view of simulatables in that direction
     */
    protected @NotNull Set<Simulatable> getInDirection(Direction direction) {
        Set<Simulatable> toReturn = new HashSet<>();
        switch (direction) {
            case UP -> {
                for (int simulatableColumn = location.x(); simulatableColumn < location.x() + width; simulatableColumn++) {
                    toReturn.addAll(grid.getAtWrappedGridLocation(new GridItem(simulatableColumn, location.y() + height)));
                }
            }
            case DOWN -> {
                for (int simulatableColumn = location.x(); simulatableColumn < location.x() + width; simulatableColumn++) {
                    toReturn.addAll(grid.getAtWrappedGridLocation(new GridItem(simulatableColumn, location.y() - 1)));
                }
            }
            case LEFT -> {
                for (int simulatableRow = location.y(); simulatableRow < location.y() + height; simulatableRow++) {
                    toReturn.addAll(grid.getAtWrappedGridLocation(new GridItem(location.x() - 1, simulatableRow)));
                }
            }
            case RIGHT -> {
                for (int simulatableRow = location.y(); simulatableRow < location.y() + height; simulatableRow++) {
                    toReturn.addAll(grid.getAtWrappedGridLocation(new GridItem(location.x() + width, simulatableRow)));
                }
            }
        }
        return Collections.unmodifiableSet(toReturn);
    }

    /**
     * Changes the simulatable's location in the next simulation step
     * @param horizontalDistance horizontal distance to travel to the right
     * @param verticalDistance horizontal distance to travel upwards
     */
    protected void move(int horizontalDistance, int verticalDistance) {
        nextLocation = new GridItem(Math.floorMod(nextLocation.x() + horizontalDistance, grid.getWidth()), Math.floorMod(nextLocation.y() + verticalDistance, grid.getHeight()));
    }

    /**
     * Changes the simulatable's location immediately (in the current step). This should only be called for simulatables that have
     * not yet been added to a model.
     * @param horizontalDistance horizontal distance to travel to the right
     * @param verticalDistance horizontal distance to travel upwards
     */
    protected void moveNow(int horizontalDistance, int verticalDistance) {
        location = new GridItem(Math.floorMod(nextLocation.x() + horizontalDistance, grid.getWidth()), Math.floorMod(nextLocation.y() + verticalDistance, grid.getHeight()));
        nextLocation = location;
    }

    /**
     * Returns the location of the left-bottommost square of simulatable in its containing grid.
     * @return the location
     */
    public GridItem getLocation() {
        return location;
    }

    /**
     * Returns the priority with which this simulatable is rendered. This is used only if two simulatables occupy the same
     * square of a grid.
     * @return the priority
     */
    public int getRenderPriority() {
        return renderPriority;
    }

    /**
     * Returns the width of this simulatable in its containing grid
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of this simulatable in its containing grid
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the parent model that houses this simulatable
     * @return the model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Creates a semantic copy of this simulatable
     * @return a copy
     */
    public abstract Simulatable copy();

    /**
     * Short string representation (up to three characters) for debugging purposes
     * @return short representation
     */
    public abstract String shortName();


}
