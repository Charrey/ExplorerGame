package com.charrey.game.ui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.charrey.game.StageSwitcher;
import com.charrey.game.model.Grid;
import com.charrey.game.settings.Settings;
import com.charrey.game.stage.ExploreStage;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Pane at the bottom of the game view that provides high-level controls to the user.
 */
public class BottomPane extends Table {

    /**
     * Creates a new BottomPane
     *
     * @param width                width of the BottomPane
     * @param stageSwitcher        interface used for switching to the main menu when the user desires
     * @param resetButtonBehaviour what should happen when the reset button is clicked
     * @param grid                 grid that may be saved to a file
     * @param saveLoader           loads a provided string into the game state
     * @param toggleSimulation     ran when the user clicks the start/stop button
     */
    public BottomPane(float width, @NotNull StageSwitcher stageSwitcher, BiConsumer<Integer, Integer> resetButtonBehaviour, Grid grid, Consumer<FileHandle> saveLoader, Runnable toggleSimulation) {
        float buttonWidth = width / 7f;
        add(new MainMenuButton(() -> {
            if (Settings.currentlySimulating) {
                toggleSimulation.run();
            }
            stageSwitcher.changeToStage(ExploreStage.MENU);
        })).width(buttonWidth);
        add(new ClearButton(resetButtonBehaviour, buttonWidth));
        add(new SaveButton(grid));
        add(new SaveAsButton(grid));
        add(new LoadButton(saveLoader));
        add(new SimulateButton(grid, toggleSimulation));
        add(new SpeedSlider(buttonWidth));
        float cellHeight = (float) Arrays.stream(getCells().toArray(Cell.class)).mapToDouble(Cell::getPrefHeight).max().orElse(100);
        getCells().forEach(cell -> cell.width(buttonWidth).height(cellHeight));
    }
}
