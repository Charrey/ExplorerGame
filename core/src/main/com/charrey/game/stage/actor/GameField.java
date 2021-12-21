package com.charrey.game.stage.actor;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.charrey.game.model.BlockType;
import com.charrey.game.model.Direction;
import com.charrey.game.model.Grid;
import com.charrey.game.model.condition.BlockExists;
import com.charrey.game.model.condition.NotBlockExists;
import com.charrey.game.model.serialize.GridLoader;
import com.charrey.game.model.serialize.XMLLoader;
import com.charrey.game.model.serialize.XMLSerializer;
import com.charrey.game.model.simulatable.ConditionalBarrier;
import com.charrey.game.model.simulatable.DirectionalSimulatable;
import com.charrey.game.model.simulatable.Simulatable;
import com.charrey.game.settings.Settings;
import com.charrey.game.simulator.Simulator;
import com.charrey.game.ui.context.ContextMenu;
import com.charrey.game.ui.context.ContextMenuItem;
import com.charrey.game.ui.context.GroupContextMenuItem;
import com.charrey.game.ui.context.LeafContextMenuItem;
import com.charrey.game.util.GridItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGB888;
import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;

/**
 * Grid of blocks that can each be specified. The blocks also show the simulation.
 */
public class GameField extends Group {


    private final int pixelHeight;
    private final int pixelWidth;
    private static final int cellsInRow = 8;
    private static final int cellsInColumn = 8;
    private final Simulator simulator;
    private final Grid grid;
    private final Grid backup;

    private boolean selectionMode = false;
    private Consumer<GridItem> uponSelection;



    /**
     * Creates a new GameField. The actual size in pixels might be slightly different from the requested width and height,
     * since this constructor forces each game block to be squares of the same size.
     * @param pixelWidth requested width in pixels.
     * @param pixelHeight requested height in pixels.
     */
    public GameField(int pixelWidth, int pixelHeight) {
        this.pixelWidth = cellsInRow * (pixelWidth / cellsInRow);
        this.pixelHeight = cellsInColumn * (pixelHeight / cellsInColumn);
        this.grid = new Grid(cellsInRow, cellsInColumn);
        this.backup = new Grid(cellsInRow, cellsInColumn);
        this.simulator = new Simulator(grid);
        Pixmap pixels = new Pixmap(Math.round(getWidth()), Math.round(getHeight()), RGB888);
        pixels.setColor(0.5f, 0.5f, 0.5f, 1);
        pixels.fill();
        this.texture = new Texture(pixels);
        setWidth(this.pixelWidth);
        setHeight(this.pixelHeight);
        addCaptureListener(new GameFieldClickHandler());
    }


    /**
     * Provides a string representation of the current game field. This is a valid save file if written to a file with .explore extension.
     * @return the string representation
     */
    public String serialize() {
        return XMLSerializer.get().serialize(grid);
    }


    /**
     * Loads a save game into this gamefield.
     * @param serialized string representation of a save.
     * @throws GridLoader.SaveFormatException thrown if the save game was corrupted / not according to specification
     */
    public void load(@NotNull String serialized) throws GridLoader.SaveFormatException {
        grid.copy(XMLLoader.get().load(serialized));
    }


    @NotNull
    private final Texture texture;

    @Override
    public void draw(@NotNull Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY());
        synchronized (grid) {
            for (int columnIndex = 0; columnIndex < cellsInRow; columnIndex++) {
                for (int rowIndex = 0; rowIndex < cellsInColumn; rowIndex++) {
                    Texture texture = grid.getTextureAtLocation(new GridItem(columnIndex, rowIndex), pixelWidth / cellsInRow, pixelHeight / cellsInColumn);
                    batch.draw(texture, getX() + columnIndex * (pixelWidth / cellsInRow), getY() + rowIndex * (pixelHeight / cellsInColumn));
                }

            }
            super.draw(batch, parentAlpha);
        }
        if (selectionMode) {
            Pixmap pixels = new Pixmap(pixelWidth, pixelHeight, RGBA8888);
            pixels.setColor(1f, 0f, 0f, 1f);
            pixels.drawRectangle(0, 0, pixelWidth - 1, pixelHeight);
            batch.draw(new Texture(pixels), getX(), getY());
        }
    }

    /**
     * Resets all blocks to contain no entities.
     */
    public void reset() {
        grid.clear();
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
     * @param direction new direction
     */
    public void setDirectionLastAdded(Direction direction) {
        if (lastClick != null) {
            grid.getAtWrappedGridLocation(lastClick).forEach(simulatable -> {
                if (simulatable instanceof DirectionalSimulatable dir) {
                    dir.setDirectionNow(direction);
                }
            });
        }

    }

    private GridItem lastClick = null;


    private class GameFieldClickHandler extends InputListener {

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (button == 0) {
                leftMouseClick(new Vector2(x, y));
            } else if (button == 1) {
                rightMouseClick(new Vector2(x, y), localToStageCoordinates(new Vector2(x, y)));
            }
            return super.touchDown(event, x, y, pointer, button);
        }

        private void rightMouseClick(Vector2 localCoordinates, Vector2 stageCoordinates) {
            selectionMode = false;
            ContextMenu contextMenu = new ContextMenu(0);
            int columnIndex = (int) (cellsInRow * (localCoordinates.x / getWidth()));
            int rowIndex = (int) (cellsInColumn * (localCoordinates.y / getHeight()));
            Set<Simulatable> simulatables = grid.getAtStrictGridLocation(new GridItem(columnIndex, rowIndex));

            if (!Settings.currentlySimulating) {
                contextMenu.add(new LeafContextMenuItem("Clear", grid::clear));
                if (simulatables.size() == 1) {
                    Simulatable simulatable = simulatables.iterator().next();
                    if (simulatable instanceof ConditionalBarrier conditionalBarrier) {
                        Supplier<ContextMenuItem> existsBlock = () -> new LeafContextMenuItem("Exists block at...", () -> {
                            selectionMode = true;
                            uponSelection = gridItem -> conditionalBarrier.setCondition(new BlockExists(grid, gridItem));
                        });
                        Supplier<ContextMenuItem> notExistsBlock = () -> new LeafContextMenuItem("Not exists block at...", () -> {
                            selectionMode = true;
                            uponSelection = gridItem -> conditionalBarrier.setCondition(new NotBlockExists(grid, gridItem));
                        });
                        contextMenu.add(new GroupContextMenuItem("Set condition", List.of(existsBlock, notExistsBlock)));
                    }
                }
            } else {
                contextMenu.add(new LeafContextMenuItem("Stop simulation", GameField.this::stopSimulation));
            }
            getStage().getRoot().addActor(contextMenu);
            contextMenu.setX(stageCoordinates.x + 1);
            contextMenu.setY(stageCoordinates.y + 1);
        }

        private void leftMouseClick(Vector2 localCoordinates) {
            if (!Settings.currentlySimulating) {
                int columnIndex = (int) (cellsInRow * (localCoordinates.x / getWidth()));
                int rowIndex = (int) (cellsInColumn * (localCoordinates.y / getHeight()));
                lastClick = new GridItem(columnIndex, rowIndex);
                if (selectionMode) {
                    uponSelection.accept(lastClick);
                    uponSelection = null;
                    selectionMode = false;
                } else {
                    BlockType type = Settings.newBlockType;
                    if (type != null) {
                        grid.add(type.getSimple(Settings.newBlockDirection, lastClick));
                    } else {
                        grid.remove(lastClick);
                    }
                }
            }
        }

    }
}
