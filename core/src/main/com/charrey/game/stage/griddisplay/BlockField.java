package com.charrey.game.stage.griddisplay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.charrey.game.model.Direction;
import com.charrey.game.model.Grid;
import com.charrey.game.model.simulatable.DirectionalSimulatable;
import com.charrey.game.model.simulatable.Simulatable;
import com.charrey.game.settings.Settings;
import com.charrey.game.simulator.Simulator;
import org.jetbrains.annotations.NotNull;

/**
 * Grid of blocks that can each be specified. The blocks also show the simulation.
 */
public class BlockField extends Group {

    private final Simulator simulator;
    private final int requestedWidth;
    private final int requestedHeight;
    private final Grid grid;
    private final Grid backup;
    private final GridDrawer drawer;
    private int cellWidth;
    private int cellHeight;
    private boolean selectionMode = false;
    private Simulatable lastSimulatableAdded = null;


    /**
     * Creates a new GameField. The actual size in pixels might be slightly different from the requested width and height,
     * since this constructor forces each game block to be squares of the same size.
     *
     * @param pixelWidth  requested width in pixels.
     * @param pixelHeight requested height in pixels.
     */
    public BlockField(int pixelWidth, int pixelHeight) {
        requestedWidth = pixelWidth;
        requestedHeight = pixelHeight;
        this.cellWidth = requestedWidth / Settings.defaultWidth;
        this.cellHeight = requestedHeight / Settings.defaultHeight;
        this.grid = new Grid(Settings.defaultWidth, Settings.defaultHeight);
        this.backup = new Grid(Settings.defaultWidth, Settings.defaultHeight);
        this.simulator = new Simulator(grid);
        setWidth(Settings.defaultWidth * cellWidth);
        setHeight(Settings.defaultHeight * cellHeight);
        addCaptureListener(new GameFieldClickHandler(this));
        drawer = new GridDrawer(this);
    }

    /**
     * Loads a save game into this gamefield.
     *
     * @param toLoad string representation of a save.
     */
    public void load(@NotNull Grid toLoad) {
        cellWidth = requestedWidth / toLoad.getWidth();
        cellHeight = requestedHeight / toLoad.getWidth();
        grid.copy(toLoad);
    }

    @Override
    public void draw(@NotNull Batch batch, float parentAlpha) {
        drawer.draw((SpriteBatch) batch, (int) getWidth(), (int) getHeight(), (int) getX(), (int) getY());
    }

    /**
     * Resets all blocks to contain no entities.
     *
     * @param width  width of the new block field
     * @param height height of the new block field
     */
    public void reset(int width, int height) {
        cellWidth = requestedWidth / width;
        cellHeight = requestedHeight / height;
        grid.clear(width, height);
        backup.clear(width, height);
        setWidth(width * cellWidth);
        setHeight(height * cellHeight);
    }

    /**
     * Starts the simulator in a separate Thread.
     */
    public void startSimulation() {
        Settings.currentlySimulating = true;
        backup.copy(grid);
        simulator.start();
    }

    /**
     * Stops the simulation
     */
    public void stopSimulation() {
        simulator.stop();
        grid.copy(backup);
        Settings.currentlySimulating = false;
    }

    /**
     * Toggles the simulation, i.e. starts it if inactive and stops it if active.
     */
    public void toggleSimulation() {
        if (Settings.currentlySimulating) {
            stopSimulation();
        } else {
            startSimulation();
        }
    }

    /**
     * Sets the direction of the simulatable that was most recently added by the user.
     * Does nothing if no simulatable has been added, the last simulatable has no direction property or the last
     * interaction was removal of a simulatable.
     *
     * @param direction new direction
     */
    public void setDirectionLastAdded(Direction direction) {
        if (lastSimulatableAdded != null && lastSimulatableAdded instanceof DirectionalSimulatable dir) {
            dir.setDirectionNow(direction);
        }
    }

    /**
     * Returns the number of cells in each row
     *
     * @return cells in each row
     */
    public int getCellsInRow() {
        return grid.getWidth();
    }

    /**
     * Returns the number of cells in each column
     *
     * @return cells in each column
     */
    public int getCellsInColumn() {
        return grid.getHeight();
    }

    /**
     * Returns the grid this block field is presenting
     *
     * @return the grid
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Sets which Simulatable was last added by the user (other added simulatables do not count).
     * This is useful for example when changing a placed simulatable by dragging the mouse button
     *
     * @param simulatable the simulatable that was last added
     */
    public void setLastSimulatableAdded(Simulatable simulatable) {
        lastSimulatableAdded = simulatable;
    }

    /**
     * Whether currently in selection mode (for example while picking a location for a ConditionalBarrier's condition)
     *
     * @return true iff in selection mode
     */
    public boolean getSelectionMode() {
        return selectionMode;
    }

    /**
     * Sets whether this BlockField is in selection mode, where no simulatables are placed by clicking but something
     * else is happening (and being handled).
     *
     * @param newSelectionMode true iff set in selection mode.
     */
    void setSelectionMode(boolean newSelectionMode) {
        selectionMode = newSelectionMode;
    }

    /**
     * Provides the pixel width of each block.
     *
     * @return pixel width
     */
    public int getCellWidth() {
        return cellWidth;
    }

    /**
     * Provides the pixel height of each block.
     *
     * @return pixel height
     */
    public int getCellHeight() {
        return cellHeight;
    }
}
