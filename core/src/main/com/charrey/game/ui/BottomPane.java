package com.charrey.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.charrey.game.settings.Settings;
import com.charrey.game.StageSwitcher;
import com.charrey.game.stage.ExploreStage;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Pane at the bottom of the game view that provides high-level controls to the user.
 */
public class BottomPane extends Table {

    /**
     * Creates a new BottomPane
     * @param width width of the BottomPane
     * @param stageSwitcher interface used for switching to the main menu when the user desires
     * @param resetButtonBehaviour what should happen when the reset button is clicked
     * @param gameStateStringSupplier provides a way to serialize the specification into a String usable as save file
     * @param saveLoader loads a provided string into the game state
     * @param toggleSimulation ran when the user clicks the start/stop button
     */
    public BottomPane(float width,
                      @NotNull StageSwitcher stageSwitcher,
                      Runnable resetButtonBehaviour,
                      Supplier<String> gameStateStringSupplier,
                      Consumer<String> saveLoader,
                      Runnable toggleSimulation) {
        float buttonWidth = width / 7f;
        add(new MainMenuButton(() -> {
            if (Settings.currentlySimulating) {
                toggleSimulation.run();
            }
            stageSwitcher.changeToStage(ExploreStage.MENU);
        })).width(buttonWidth);
        add(new ClearButton(resetButtonBehaviour));
        add(new SaveButton(gameStateStringSupplier));
        add(new SaveAsButton(gameStateStringSupplier));
        add(new LoadButton(saveLoader));
        add(new SimulateButton(toggleSimulation));
        add(new SpeedSlider(buttonWidth));
        float cellHeight = (float) Arrays.stream(getCells().toArray(Cell.class)).mapToDouble(Cell::getPrefHeight).max().orElse(100);
        getCells().forEach(cell -> cell.width(buttonWidth).height(cellHeight));
    }
}
