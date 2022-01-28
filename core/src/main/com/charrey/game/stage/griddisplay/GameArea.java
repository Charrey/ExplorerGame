package com.charrey.game.stage.griddisplay;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.charrey.game.model.Checker;
import com.charrey.game.model.Direction;
import com.charrey.game.model.Grid;
import com.charrey.game.model.serialize.GridLoader;
import com.charrey.game.model.serialize.SaveFormatException;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static com.badlogic.gdx.utils.Align.center;
import static com.charrey.game.model.Direction.*;
import static com.charrey.game.ui.ToggleButton.State.THREE;
import static com.charrey.game.ui.ToggleButton.State.TWO;
import static com.charrey.game.util.ErrorUtils.showErrorMessage;

/**
 * UI element that presents a model and allows the user to interact with it
 */
public class GameArea extends Group {

    private final BlockField blockField;
    private final Map<Direction, Table> buttons = new EnumMap<>(Direction.class);

    /**
     * Creates a Game Area with a maximum width and height
     *
     * @param pixelWidth  maximum width
     * @param pixelHeight maximum height
     */
    public GameArea(int pixelWidth, int pixelHeight) {
        blockField = new BlockField(pixelWidth - 40, pixelHeight - 40);
        addActor(blockField);
        blockField.setPosition(0, 0, center);
        addSides();
    }

    private void addSides() {
        for (Direction direction : Direction.values()) {
            buttons.put(direction, new Table());
        }
        for (int columnIndex = 0; columnIndex < blockField.getCellsInRow(); columnIndex++) {
            int finalColumnIndex = columnIndex;
            Set.of(UP, DOWN).forEach(d -> {
                EdgeExportPadButton button = new EdgeExportPadButton(false);
                button.addCaptureListener(new SelectButtonListener(d, finalColumnIndex));
                buttons.get(d).add(button).width(blockField.getWidth() / blockField.getCellsInRow()).height(20);
            });
        }
        buttons.get(DOWN).setPosition(0, -blockField.getHeight() / 2 - 10);
        buttons.get(UP).setPosition(0, blockField.getHeight() / 2 + 10);
        for (int rowIndex = 0; rowIndex < blockField.getCellsInColumn(); rowIndex++) {
            int finalRowIndex = rowIndex;
            Set.of(LEFT, RIGHT).forEach(d -> {
                EdgeExportPadButton button = new EdgeExportPadButton(true);
                button.addCaptureListener(new SelectButtonListener(d, blockField.getCellsInColumn() - 1 - finalRowIndex));
                buttons.get(d).add(button).height(blockField.getHeight() / blockField.getCellsInColumn()).width(20).row();
            });
        }
        buttons.get(LEFT).setPosition(-blockField.getWidth() / 2 - 10, 0);
        buttons.get(RIGHT).setPosition(blockField.getWidth() / 2 + 10, 0);
        buttons.values().forEach(this::addActor);
    }

    /**
     * Resets all contents of a model, resizes the model and refreshes all display information of the model
     *
     * @param width  new width of the model
     * @param height new height of the model
     */
    public void reset(int width, int height) {
        blockField.reset(width, height);
        blockField.setPosition(0, 0, center);
        buttons.values().forEach(Actor::remove);
        addSides();
    }

    /**
     * Loads a grid from a file into the game
     *
     * @param handle Filehandle that contains the grid save file
     * @throws SaveFormatException thrown when the grid contains errors
     */
    public void load(FileHandle handle) throws SaveFormatException {
        Grid grid = GridLoader.get().load(handle);
        Checker checker = new Checker();
        checker.addListener(gridCheckerError -> showErrorMessage(gridCheckerError.getMessage(), getStage()));
        boolean withoutErrors = checker.check(grid);
        if (withoutErrors) {
            blockField.load(grid);
            blockField.setPosition(0, 0, center);
            buttons.values().forEach(Actor::remove);
            addSides();
            for (Direction direction : Direction.values()) {
                Set<Integer> indices = grid.getExport(direction);
                for (Integer index : indices) {
                    if (direction.isHorizontal()) {
                        ((EdgeExportPadButton) buttons.get(direction).getCells().get(grid.getHeight() - 1 - index).getActor()).setState(TWO);
                    } else {
                        ((EdgeExportPadButton) buttons.get(direction).getCells().get(index).getActor()).setState(TWO);
                    }
                }
                indices = grid.getPad(direction);
                for (Integer index : indices) {
                    if (direction.isHorizontal()) {
                        ((EdgeExportPadButton) buttons.get(direction).getCells().get(grid.getHeight() - 1 - index).getActor()).setState(THREE);
                    } else {
                        ((EdgeExportPadButton) buttons.get(direction).getCells().get(index).getActor()).setState(THREE);
                    }
                }
            }
        }
    }

    /**
     * Toggles whether the simulation is currently running
     */
    public void toggleSimulation() {
        blockField.toggleSimulation();
    }

    /**
     * Changes the direction of the Simulatable that was last placed by the user
     *
     * @param direction new direction
     */
    public void setDirectionLastAdded(Direction direction) {
        blockField.setDirectionLastAdded(direction);
    }

    /**
     * Returns the grid displayed in this game area
     *
     * @return the grid
     */
    public Grid getGrid() {
        return blockField.getGrid();
    }


    private class SelectButtonListener extends ChangeListener {
        private final Direction direction;
        private final int index;

        public SelectButtonListener(Direction direction, int index) {
            this.index = index;
            this.direction = direction;
        }

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            switch (((EdgeExportPadButton) actor).getState()) {
                case ONE -> {
                    blockField.getGrid().setPad(index, direction, false);
                    blockField.getGrid().setExport(index, direction, false);
                }
                case TWO -> {
                    blockField.getGrid().setPad(index, direction, false);
                    blockField.getGrid().setExport(index, direction, true);
                }
                case THREE -> {
                    blockField.getGrid().setPad(index, direction, true);
                    blockField.getGrid().setExport(index, direction, false);
                }
            }
        }
    }
}
