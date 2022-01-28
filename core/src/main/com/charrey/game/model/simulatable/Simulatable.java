package com.charrey.game.model.simulatable;

import com.charrey.game.model.Direction;
import com.charrey.game.model.Grid;
import com.charrey.game.model.simulatable.subgrid.SubGrid;
import com.charrey.game.texture.Drawable;
import com.charrey.game.util.GridItem;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Item in a model that can be rendered and can be simulated. Each implementation may behave differently.
 */
public abstract class Simulatable {

    private final int width;
    private final int height;
    private final int renderPriority;
    private final Queue<Simulatable> toAddNextStep = new LinkedList<>();
    private Grid grid;
    private Grid nextGrid;
    private GridItem location;
    private GridItem nextLocation;
    private Set<Simulatable> masterSet;
    private boolean removeInNextStep = false;

    /**
     * Creates a new Simulatable
     *
     * @param location       location of the Simulatable
     * @param renderPriority render priority
     * @param width          width (in grid blocks) of this simulatable
     * @param height         height (in grid blocks) of this simulatable
     */
    protected Simulatable(GridItem location, int renderPriority, int width, int height) {
        this.location = location;
        this.nextLocation = location;
        this.renderPriority = renderPriority;
        this.width = width;
        this.height = height;
    }

    /**
     * Changes the simulatable's container grid in the next step.
     *
     * @param nextGrid     grid that will now occupy this simulatable
     * @param nextLocation location within the new grid
     */
    protected void changeGrid(Grid nextGrid, GridItem nextLocation) {
        this.nextGrid = nextGrid;
        this.nextLocation = nextLocation;
    }

    /**
     * Sets the masterset that contains this simulatable. The masterset is the set of all simulatables subject to simulation
     * and interaction with other simulatables in all grids together.
     *
     * @param masterSet the masterset
     */
    public void setMasterSet(Set<Simulatable> masterSet) {
        this.masterSet = masterSet;
    }

    /**
     * Removes this simulatable from the model in the next step
     */
    protected void removeFromMasterInNextStep() {
        removeInNextStep = true;
    }

    /**
     * Adds a new simulatable to the model next step
     *
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
            Simulatable newElement = toAddNextStep.poll();
            grid.add(newElement);
            masterSet.add(newElement);
        }
        if (removeInNextStep) {
            grid.remove(this);
            masterSet.remove(this);
        } else {
            location = nextLocation;
            if (nextGrid != null) {
                assert nextGrid != grid;
                grid.remove(this);
                nextGrid.add(this);
                nextGrid = null;
            }
        }
    }

    private void changeGridNow(Grid nextGrid, GridItem nextLocation) {
        this.location = nextLocation;
        this.nextLocation = nextLocation;
        this.grid.remove(this);
        nextGrid.add(this);
        this.nextGrid = null;
    }

    /**
     * Returns a texture of one of the blocks this simulatable occupies
     *
     * @param xOffset       if this simulatable is more than 1 block wide, this is the offset of which part of the simulatable to render
     * @param yOffset       if this simulatable is more than 1 block high, this is the offset of which part of the simulatable to render
     * @param textureWidth  width of the texture in pixels
     * @param textureHeight height of the texture in pixels
     * @return the texture
     */
    public abstract Drawable getTexture(int xOffset, int yOffset, int textureWidth, int textureHeight);

    /**
     * Sets the model that houses this simulatable
     *
     * @param grid parent model
     */
    public void setContainingGrid(Grid grid) {
        this.grid = grid;
    }

    /**
     * Returns all simulatables that occupy some square adjacent to this simulatable in a specific direction
     *
     * @param direction direction of this simulatable
     * @return set of other simulatables adjacent
     */
    protected @NotNull Set<Simulatable> getInDirection(Direction direction) {
        return getContainerGrid().getInDirection(getLocation(), direction, getWidth(), getHeight());
    }

    /**
     * Changes the simulatable's location in the next simulation step
     *
     * @param horizontalDistance horizontal distance to travel to the right
     * @param verticalDistance   horizontal distance to travel upwards
     */
    protected void move(int horizontalDistance, int verticalDistance) {
        nextLocation = new GridItem(Math.floorMod(nextLocation.x() + horizontalDistance, grid.getWidth()), Math.floorMod(nextLocation.y() + verticalDistance, grid.getHeight()));
    }

    /**
     * Changes the simulatable's location immediately (in the current step). This should only be called for simulatables that have
     * not yet been added to a model.
     *
     * @param horizontalDistance horizontal distance to travel to the right
     * @param verticalDistance   horizontal distance to travel upwards
     */
    protected void moveNow(int horizontalDistance, int verticalDistance) {
        location = new GridItem(Math.floorMod(nextLocation.x() + horizontalDistance, grid.getWidth()), Math.floorMod(nextLocation.y() + verticalDistance, grid.getHeight()));
        nextLocation = location;
    }

    /**
     * Returns the location of the left-bottommost square of simulatable in its containing grid.
     *
     * @return the location
     */
    public GridItem getLocation() {
        return location;
    }

    /**
     * Returns all grid positions that this simulatable occupies
     *
     * @return all grid positions
     */
    public Set<GridItem> getLocations() {
        Set<GridItem> res = new HashSet<>();
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                res.add(new GridItem(Math.floorMod(getLocation().x() + x, getContainerGrid().getWidth()), Math.floorMod(getLocation().y() + y, getContainerGrid().getHeight())));
            }
        }
        return res;
    }


    /**
     * Returns the priority with which this simulatable is rendered. This is used only if two simulatables occupy the same
     * square of a grid.
     *
     * @return the priority
     */
    public int getRenderPriority() {
        return renderPriority;
    }

    /**
     * Returns the width of this simulatable in its containing grid
     *
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of this simulatable in its containing grid
     *
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the parent model that houses this simulatable
     *
     * @return the model
     */
    public Grid getContainerGrid() {
        return grid;
    }

    /**
     * Creates a semantic copy of this simulatable
     *
     * @return a copy
     */
    public abstract Simulatable copy();

    /**
     * Short string representation (up to three characters) for debugging purposes
     *
     * @return short representation
     */
    public abstract String shortName();


    /**
     * Changes the simulatable's location (in the next step) by moving one step in a specific direction.
     *
     * @param direction direction to travel in
     */
    protected void advance(Direction direction) {
        if (getInDirection(direction).stream().anyMatch(obj -> obj instanceof SubGrid)) {
            List<SubGrid> subgrids = getInDirection(direction).stream().filter(obj -> obj instanceof SubGrid).map(x -> (SubGrid) x).toList();
            assert subgrids.size() == 1;
            SubGrid subGrid = subgrids.get(0);
            try {
                GridItem teleportTo = subGrid.getInwardLink(direction.opposite(), getLocation().x() - subGrid.getLocation().x(), getLocation().y() - subGrid.getLocation().y());
                changeGrid(subGrid.getSubgrid(), teleportTo);
                return;
            } catch (SubGrid.NoLinkException ignored) {
                //continue normally
            }
        }
        EdgeType gridEdge = gridEdgeInDirection(direction);
        switch (gridEdge) {
            case EMPTY -> {
                if (getContainerGrid().getParent() == null) {
                    removeFromMasterInNextStep();
                } else {
                    changeGrid(getContainerGrid().getParent().getContainerGrid(), getContainerGrid().getParent().getOutwardLink(direction, direction.isHorizontal() ? getLocation().y() : getLocation().x()));
                }
            }
            case EXPORT, PAD, UNMARKED -> {
                switch (direction) {
                    case UP -> move(0, 1);
                    case DOWN -> move(0, -1);
                    case LEFT -> move(-1, 0);
                    case RIGHT -> move(1, 0);
                }
            }
        }

    }

    /**
     * Changes the simulatable's location immediately (in the current step) by moving one step in the specified direction.
     * This should only be called for simulatables that have not yet been added to a model.
     *
     * @param direction direction to travel in
     */
    protected void advanceNow(Direction direction) {
        if (getInDirection(direction).stream().anyMatch(obj -> obj instanceof SubGrid)) {
            List<SubGrid> subgrids = getInDirection(direction).stream().filter(obj -> obj instanceof SubGrid).map(x -> (SubGrid) x).toList();
            assert subgrids.size() == 1;
            SubGrid subGrid = subgrids.get(0);
            try {
                GridItem teleportTo = subGrid.getInwardLink(direction.opposite(), getLocation().x() - subGrid.getLocation().x(), getLocation().y() - subGrid.getLocation().y());
                changeGridNow(subGrid.getSubgrid(), teleportTo);
                return;
            } catch (SubGrid.NoLinkException e) {
                //continue normally
            }
        }
        EdgeType gridEdge = gridEdgeInDirection(direction);
        switch (gridEdge) {
            case EXPORT -> {
                if (getContainerGrid().getParent() == null) {
                    removeFromMasterInNextStep();
                } else {
                    changeGridNow(getContainerGrid().getParent().getContainerGrid(), getContainerGrid().getParent().getOutwardLink(direction, direction.isHorizontal() ? getLocation().y() : getLocation().x()));
                }
            }
            case EMPTY, UNMARKED, PAD -> {
                switch (direction) {
                    case UP -> moveNow(0, 1);
                    case DOWN -> moveNow(0, -1);
                    case LEFT -> moveNow(-1, 0);
                    case RIGHT -> moveNow(1, 0);
                }
            }
        }

    }

    /**
     * Returns whether movement in a specific direction is blocked (by a barrier, a subgrid or something else)
     *
     * @param direction the direction of moment
     * @return true iff moment would be blocked
     */
    protected boolean blockedInDirection(Direction direction) {
        return getContainerGrid().blockedInDirection(getLocation(), direction, getWidth(), getHeight());
    }

    private EdgeType gridEdgeInDirection(Direction direction) {
        return getContainerGrid().gridEdgeInDirection(getLocation(), direction);
    }
}
