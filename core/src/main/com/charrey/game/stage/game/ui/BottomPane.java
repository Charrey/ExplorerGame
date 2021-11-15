package com.charrey.game.stage.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.charrey.game.StageSwitcher;
import com.charrey.game.stage.ExploreStage;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BottomPane extends Table {

    private final ClearButton clearButton;
    private final SaveButton saveButton;
    private final SaveAsButton saveAsButton;
    private final LoadButton loadButton;
    private final SimulateButton simulateButton;
    private Supplier<String> saveState;
    private Runnable reset;
    private Runnable startSimulation;
    private Runnable stopSimulation;
    private Consumer<String> saveLoader;

    public BottomPane(Skin skin, float width, StageSwitcher stageSwitcher) {
        float buttonWidth = width / 6f;
        add(new MainMenuButton(skin, () -> {
            stopSimulation.run();
            stageSwitcher.changeToStage(ExploreStage.MENU);
        })).width(buttonWidth);
        this.clearButton = new ClearButton(skin, () -> reset.run());
        this.saveButton = new SaveButton(skin, () -> saveState.get());
        this.saveAsButton = new SaveAsButton(skin, () -> saveState.get());
        this.loadButton = new LoadButton(skin, s -> saveLoader.accept(s));
        this.simulateButton = new SimulateButton(skin, () -> startSimulation.run(), () -> stopSimulation.run());
        add(clearButton).width(buttonWidth);
        add(saveButton).width(buttonWidth);
        add(saveAsButton).width(buttonWidth);
        add(loadButton).width(buttonWidth);
        add(simulateButton).width(buttonWidth);
    }

    public void setResetButtonBehaviour(Runnable reset) {
        this.reset = reset;
    }

    public void setSetGameStateString(Supplier<String> gameStateStringSupplier) {
        this.saveState = gameStateStringSupplier;
    }

    public void setSaveLoader(Consumer<String> saveLoader) {
        this.saveLoader = saveLoader;
    }

    public void setStartSimulation(Runnable startSimulation) {
        this.startSimulation = () -> {
            clearButton.setDisabled(true);
            saveButton.setDisabled(true);
            saveAsButton.setDisabled(true);
            loadButton.setDisabled(true);
            simulateButton.setViewSimulating();
            startSimulation.run();
        };
    }

    public void setStopSimulation(Runnable stopSimulation) {
        this.stopSimulation = () -> {
            clearButton.setDisabled(false);
            saveButton.setDisabled(false);
            saveAsButton.setDisabled(false);
            loadButton.setDisabled(false);
            simulateButton.setViewStopped();
            stopSimulation.run();
        };
    }
}
