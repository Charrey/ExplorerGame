package com.charrey.game.stage.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.charrey.game.util.SkinUtils;

public class SimulateButton extends TextButton {


    boolean simulationRunning = false;

    public SimulateButton(Runnable startSimulation, Runnable stopSimulation) {
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
