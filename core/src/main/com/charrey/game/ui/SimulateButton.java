package com.charrey.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.charrey.game.model.Checker;
import com.charrey.game.model.Grid;
import com.charrey.game.settings.Settings;
import com.charrey.game.util.SkinUtils;
import org.jetbrains.annotations.NotNull;

import static com.charrey.game.util.ErrorUtils.showErrorMessage;

/**
 * Button that starts the simulation of the specification when clicked, or stops it if it is currently simulating.
 */
public class SimulateButton extends TextButton {

    /**
     * Creates a new SimulateButton
     *
     * @param grid             grid to check before starting a simulation
     * @param toggleSimulation Ran when the user clicks this button
     */
    public SimulateButton(Grid grid, @NotNull Runnable toggleSimulation) {
        super("Simulate", SkinUtils.getSkin());
        Checker checker = new Checker();
        checker.addListener(gridCheckerError -> showErrorMessage(gridCheckerError.getMessage(), getStage()));
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (Settings.currentlySimulating || checker.check(grid)) {
                    toggleSimulation.run();
                }
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
