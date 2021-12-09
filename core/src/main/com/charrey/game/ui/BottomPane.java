package com.charrey.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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

    private final @NotNull ClearButton clearButton;
    private final @NotNull SaveButton saveButton;
    private final @NotNull SaveAsButton saveAsButton;
    private final @NotNull LoadButton loadButton;
    private final @NotNull SimulateButton simulateButton;
    private final @NotNull SpeedSlider simulationSpeed;

    private Supplier<String> saveState;
    private Runnable reset;
    private Runnable startSimulation;
    private Runnable stopSimulation;
    private Consumer<String> saveLoader;

    /**
     * Creates a new BottomPane
     * @param width width of the BottomPane
     * @param stageSwitcher interface used for switching to the main menu when the user desires
     */
    public BottomPane(float width, @NotNull StageSwitcher stageSwitcher) {
        float buttonWidth = width / 7f;
        add(new MainMenuButton(() -> {
            stopSimulation.run();
            stageSwitcher.changeToStage(ExploreStage.MENU);
        })).width(buttonWidth);
        this.clearButton = new ClearButton(() -> reset.run());
        this.saveButton = new SaveButton(() -> saveState.get());
        this.saveAsButton = new SaveAsButton(() -> saveState.get());
        this.loadButton = new LoadButton(s -> saveLoader.accept(s));
        this.simulateButton = new SimulateButton(() -> startSimulation.run(), () -> stopSimulation.run());
        this.simulationSpeed = new SpeedSlider(buttonWidth);
        add(clearButton);
        add(saveButton);
        add(saveAsButton);
        add(loadButton);
        add(simulateButton);
        add(simulationSpeed);
        float cellHeight = (float) Arrays.stream(getCells().toArray(Cell.class)).mapToDouble(Cell::getPrefHeight).max().orElse(100);
        getCells().forEach(cell -> cell.width(buttonWidth).height(cellHeight));
    }

    /**
     * Sets what should happen when the user clicks the reset button.
     * @param reset method executed at button press.
     */
    public void setResetButtonBehaviour(Runnable reset) {
        this.reset = reset;
    }

    /**
     * Sets how to retrieve a game state that can be written to a save file and in a later instance, loaded.
     * @param gameStateStringSupplier provider of a save file string
     */
    public void setSetGameStateString(Supplier<String> gameStateStringSupplier) {
        this.saveState = gameStateStringSupplier;
    }

    /**
     * Sets what should happen with a String loaded from a savefile when the user requests to load it.
     * @param saveLoader method executed at load
     */
    public void setSaveLoader(Consumer<String> saveLoader) {
        this.saveLoader = saveLoader;
    }

    /**
     * Sets the method called when the user wants to start the simulation.
     * @param startSimulation the method that will be called
     */
    public void setStartSimulation(@NotNull Runnable startSimulation) {
        this.startSimulation = () -> {
            clearButton.setDisabled(true);
            saveButton.setDisabled(true);
            saveAsButton.setDisabled(true);
            loadButton.setDisabled(true);
            simulateButton.setViewSimulating();
            startSimulation.run();
        };
    }

    /**
     * Sets the method called when the user wants to stop the current simulation.
     * @param stopSimulation the method that will be called
     */
    public void setStopSimulation(@NotNull Runnable stopSimulation) {
        this.stopSimulation = () -> {
            clearButton.setDisabled(false);
            saveButton.setDisabled(false);
            saveAsButton.setDisabled(false);
            loadButton.setDisabled(false);
            simulateButton.setViewStopped();
            stopSimulation.run();
        };
    }

    /**
     * Returns the user set requested simulation speed
     * @return simulation speed in steps per second
     */
    public Long getSimulationsPerSecond() {
        return simulationSpeed.get();
    }
}
