package com.charrey.game.stage.actor;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.charrey.game.BlockType;
import com.charrey.game.Direction;
import com.charrey.game.model.ModelEntity;
import com.charrey.game.simulator.Simulator;
import com.charrey.game.simulator.SimulatorFactory;
import com.charrey.game.simulator.SimulatorSettings;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGB888;

/**
 * Grid of blocks that can each be specified. The blocks also show the simulation.
 */
public class GameField extends Group {

    private final Supplier<BlockType> newBlockType;
    private final Supplier<Direction> newBlockDirection;
    private final SimulatorSettings simulatorSettings = new SimulatorSettings(SimulatorSettings.ExecutionType.PARALLEL, SimulatorSettings.ExecutionType.PARALLEL);
    private final Supplier<Long> simsPerSecond;
    private int pixelHeight;
    private int pixelWidth;
    private int cellsInRow = 50;
    private int cellsInColumn = 50;
    
    GameFieldBlock[][] columns;
    @NotNull Lock readLock;

    private Simulator simulator;

    private boolean simulating = false;

    @NotNull final Set<GameFieldBlock> toBeSimulatedNextStep;
    @NotNull final Set<GameFieldBlock> haveChangedSinceLastStep;

    /**
     * Creates a new GameField. The actual size in pixels might be slightly different from the requested width and height,
     * since this constructor forces each game block to be squares of the same size.
     * @param pixelWidth requested width in pixels.
     * @param pixelHeight requested height in pixels.
     * @param newBlockType method that returns which block type the user has currently selected
     * @param newBlockDirection method that returns which block direction the user has currently selected
     * @param simsPerSecond method that returns how many steps per second the simulation should perform
     */
    public GameField(int pixelWidth, int pixelHeight, Supplier<BlockType> newBlockType, Supplier<Direction> newBlockDirection, Supplier<Long> simsPerSecond) {
        this.pixelWidth = cellsInRow * (pixelWidth / cellsInRow);
        this.pixelHeight = cellsInColumn * (pixelHeight / cellsInColumn);
        this.newBlockType = newBlockType;
        this.newBlockDirection = newBlockDirection;
        this.simsPerSecond = simsPerSecond;
        this.columns = new GameFieldBlock[cellsInRow][cellsInColumn];
        this.toBeSimulatedNextStep = switch (simulatorSettings.simulationType()) {
            case SERIAL -> new HashSet<>();
            case PARALLEL -> Collections.synchronizedSet(new HashSet<>());
        };
        this.haveChangedSinceLastStep = switch (simulatorSettings.simulationType()) {
            case SERIAL -> new HashSet<>();
            case PARALLEL -> Collections.synchronizedSet(new HashSet<>());
        };
        this.simulator = SimulatorFactory.get(this);
        this.readLock = simulator.getReadLock();
        addBlocks((columnIndex, rowIndex) -> Collections.emptySortedSet());
        Pixmap pixels = new Pixmap(Math.round(getWidth()), Math.round(getHeight()), RGB888);
        pixels.setColor(0.5f, 0.5f, 0.5f, 1);
        pixels.fill();
        this.texture = new Texture(pixels);
        setWidth(this.pixelWidth);
        setHeight(this.pixelHeight);

    }

    private void addBlocks(@NotNull NewBlockSpecifier specifier) {
        int blockWidth = pixelWidth / cellsInRow;
        int blockHeight = pixelHeight / cellsInColumn;
        for (int columnIndex = 0; columnIndex < cellsInRow; columnIndex++) {
            for (int rowIndex = 0; rowIndex < cellsInColumn; rowIndex++) {
                GameFieldBlock block = new GameFieldBlock(toBeSimulatedNextStep::add, haveChangedSinceLastStep::add, simulatorSettings.simulationType());
                block.setName("("+columnIndex+", "+rowIndex+")");
                block.setX(blockWidth * (float) columnIndex);
                block.setY(blockHeight * (float) rowIndex);
                block.setWidth(blockWidth);
                block.setHeight(blockHeight);
                specifier.getEntities(columnIndex, rowIndex).forEach(modelEntity -> block.getSpecification().addModelEntity(modelEntity));
                addBlockAtPos(block, columnIndex, rowIndex);
                block.addCaptureListener(new BlockClickListener(block, newBlockType, newBlockDirection));
            }
        }
        for (int columnIndex = 0; columnIndex < cellsInRow; columnIndex++) {
            for (int rowIndex = 0; rowIndex < cellsInColumn; rowIndex++) {
                GameFieldBlock block = columns[columnIndex][rowIndex];
                block.getSimulation().setUp(columns[columnIndex][Math.floorMod(rowIndex+1, cellsInColumn)].getSimulation());
                block.getSimulation().setDown(columns[columnIndex][Math.floorMod(rowIndex-1, cellsInColumn)].getSimulation());
                block.getSimulation().setLeft(columns[Math.floorMod(columnIndex - 1, cellsInRow)][rowIndex].getSimulation());
                block.getSimulation().setRight(columns[Math.floorMod(columnIndex + 1, cellsInRow)][rowIndex].getSimulation());
            }
        }
        setWidth(blockWidth * (float) cellsInRow);
        setHeight(blockHeight * (float) cellsInColumn);
    }

    void addBlockAtPos(GameFieldBlock block, int column, int row) {
        columns[column][row] = block;
        addActor(block);
    }

    /**
     * Performs an operation on each of the blocks in a game field.
     * @param consumer operation to perform.
     */
    public void forEachBlock(@NotNull Consumer<GameFieldBlock> consumer) {
        for (GameFieldBlock[] column : columns) {
            for (GameFieldBlock gameFieldBlock : column) {
                consumer.accept(gameFieldBlock);
            }
        }
    }

    void removeBlockAtPos(int column, int row) {
        removeActor(columns[column][row]);
        columns[column][row] = null;
    }

    /**
     * Provides a string representation of the current game field. This is a valid save file if written to a file with .explore extension.
     * @return the string representation
     */
    public String serialize() {
        JSONArray jsonColumns = new JSONArray();
        for (int columnIndex = 0; columnIndex < cellsInRow; columnIndex++) {
            JSONArray jsonColumn = new JSONArray();
            for (int rowIndex = 0; rowIndex < cellsInColumn; rowIndex++) {
                GameFieldBlock block = columns[columnIndex][rowIndex];
                JSONArray modelEntitiesJSON = new JSONArray();
                SortedSet<ModelEntity> modelEntities = block.getSpecification().getEntities();
                modelEntities.forEach(modelEntity -> {
                    JSONArray entityData = new JSONArray();
                    entityData.put(modelEntity.type().toString());
                    if (modelEntity.direction() != null) {
                        entityData.put(modelEntity.direction().toString());
                    }
                    modelEntitiesJSON.put(entityData);
                });
                jsonColumn.put(modelEntitiesJSON);
            }
            jsonColumns.put(jsonColumn);
        }
        JSONObject data = new JSONObject(Map.of("pixelWidth", pixelWidth, "pixelHeight", pixelHeight, "cells", jsonColumns));
        return data.toString();
    }

    /**
     * Loads a save game into this gamefield.
     * @param serialized string representation of a save.
     */
    public void load(@NotNull String serialized) {
        try {

            JSONObject data = new JSONObject(serialized);
            this.pixelHeight = (Integer) data.get("pixelHeight");
            this.pixelWidth = (Integer) data.get("pixelWidth");
            setWidth(this.pixelWidth);
            setHeight(this.pixelHeight);
            Pixmap pixels = new Pixmap(Math.round(getWidth()), Math.round(getHeight()), RGB888);
            pixels.setColor(0.5f, 0.5f, 0.5f, 1);
            pixels.fill();
            texture = new Texture(pixels);

            JSONArray columnsJSON = ((JSONArray) data.get("cells"));
            cellsInRow = columnsJSON.length();
            cellsInColumn = -1;
            if (columnsJSON.isEmpty()) {
                throw new JSONException("There are no cells in this save file.");
            }
            for (int columnIndex = 0; columnIndex < columnsJSON.length(); columnIndex++) {
                JSONArray columnJSON = ((JSONArray)columnsJSON.get(columnIndex));
                if (cellsInColumn == -1) {
                    cellsInColumn = columnJSON.length();
                } else if (cellsInColumn != columnJSON.length()) {
                    throw new JSONException("Cells in save file do not correspond to a rectangle.");
                }
            }
            for (int columnIndex = 0; columnIndex < columns.length; columnIndex++) {
                for (int rowIndex = 0; rowIndex < columns[columnIndex].length; rowIndex++) {
                    removeBlockAtPos(columnIndex, rowIndex);
                }
            }
            columns = new GameFieldBlock[cellsInRow][cellsInColumn];
            simulator = SimulatorFactory.get(this);
            readLock = simulator.getReadLock();
            addBlocks((columnIndex, rowIndex) -> {
                SortedSet<ModelEntity> res = new TreeSet<>();
                JSONArray column = (JSONArray) columnsJSON.get(columnIndex);
                JSONArray cell = (JSONArray) column.get(rowIndex);
                for (Object modelEntityJSON : cell) {
                    BlockType type = BlockType.valueOf((String) ((JSONArray)modelEntityJSON).get(0));
                    Direction direction = Direction.valueOf((String) ((JSONArray)modelEntityJSON).get(1));
                    res.add(new ModelEntity(type, direction));
                }
                return res;
            });
        } catch (JSONException e) {
            e.printStackTrace();
            Logger.getLogger(getClass().getName()).severe("Error loading save file");
        }
    }


    private @NotNull Texture texture;

    @Override
    public void draw(@NotNull Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY());
        if (simulating) {
            readLock.lock();
            super.draw(batch, parentAlpha);
            readLock.unlock();
        } else {
            super.draw(batch, parentAlpha);
        }
    }

    /**
     * Resets all blocks to contain no entities.
     */
    public void reset() {
        toBeSimulatedNextStep.clear();
        forEachBlock(block -> {
            block.getSpecification().removeAllModelEntities();
            block.getSimulation().clear(Collections.emptySet());
        });

    }

    /**
     * Starts the simulator in a separate Thread.
     */
    public void startSimulation() {
        simulating = true;
        forEachBlock(gameFieldBlock -> {
            if (gameFieldBlock.getSpecification().getVisibleEntity() != null) {
                toBeSimulatedNextStep.add(gameFieldBlock);
            }
        });
        forEachBlock(GameFieldBlock::switchToSimulation);
        simulator.start();
    }

    /**
     * Stops the simulation
     * @throws InterruptedException Thrown when the thread is interrupted while waiting for the simulation thread to finish
     */
    public void stopSimulation() throws InterruptedException {
        try {
            simulator.stop();
        } finally {
            simulating = false;
            forEachBlock(GameFieldBlock::stopSimulation);
        }
    }

    @Override
    public @NotNull String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = columns[0].length - 1; row >= 0 ; row--) {
            for (GameFieldBlock[] column : columns) {
                sb.append(column[row]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Returns the set of blocks that are meaningful if simulated next step.
     * Warning: this is a mutable Set!
     * @return the set of blocks
     */
    public @NotNull Set<GameFieldBlock> getToBeSimulatedNextStep() {
        return toBeSimulatedNextStep;
    }

    /**
     * Returns the set of blocks that have changed last simulation step.
     * Warning: this is a mutable Set!
     * @return the set of blocks
     */
    public @NotNull Set<GameFieldBlock> getHaveChangedSinceLastStep() {
        return haveChangedSinceLastStep;
    }

    /**
     * Returns the simulation settings used
     * @return simulation settings
     */
    public @NotNull SimulatorSettings getSimulatorSettings() {
        return simulatorSettings;
    }

    /**
     * Returns the supplier of user set simulation speed
     * @return supplier of simulation speed
     */
    public @NotNull Supplier<Long> getSimsPerSecond() {
        return simsPerSecond;
    }

    private interface NewBlockSpecifier {

        @NotNull SortedSet<ModelEntity> getEntities(int columnIndex, int rowIndex);
    }
}
