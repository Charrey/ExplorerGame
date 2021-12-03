package com.charrey.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.charrey.game.util.SkinUtils;
import org.jetbrains.annotations.NotNull;

public class SimulateButton extends TextButton {


    boolean simulationRunning = false;

    public SimulateButton(@NotNull Runnable startSimulation, @NotNull Runnable stopSimulation) {
        super("Simulate", SkinUtils.getSkin());
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (simulationRunning) {
                    stopSimulation.run();
                } else {
                    startSimulation.run();
                }
                return true;
            }
        });
    }


    public void setViewSimulating() {
        setText("Stop");
        simulationRunning = true;
    }

    public void setViewStopped() {
        setText("Simulate");
        simulationRunning = false;
    }
}
