package com.charrey.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.charrey.game.util.SkinUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Button that starts the simulation of the specification when clicked, or stops it if it is currently simulating.
 */
public class SimulateButton extends TextButton {


    boolean simulationRunning = false;

    /**
     * Creates a new SimulateButton
     * @param startSimulation Ran when the user wants the simulation to start
     * @param stopSimulation Ran when the user wants the simulation to stop
     */
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

    /**
     * Changes the view of this button to correspond to a simulation state.
     */
    public void setViewSimulating() {
        setText("Stop");
        simulationRunning = true;
    }

    /**
     * Changes the view of this button to correspond to a specification state.
     */
    public void setViewStopped() {
        setText("Simulate");
        simulationRunning = false;
    }
}
