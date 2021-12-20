package com.charrey.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.charrey.game.settings.Settings;
import com.charrey.game.util.SkinUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Button that starts the simulation of the specification when clicked, or stops it if it is currently simulating.
 */
public class SimulateButton extends TextButton {

    /**
     * Creates a new SimulateButton
     * @param toggleSimulation Ran when the user clicks this button
     *
     */
    public SimulateButton(@NotNull Runnable toggleSimulation) {
        super("Simulate", SkinUtils.getSkin());
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                toggleSimulation.run();
                return true;
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setText(Settings.currentlySimulating ? "Stop" : "Simulate");
    }
}
