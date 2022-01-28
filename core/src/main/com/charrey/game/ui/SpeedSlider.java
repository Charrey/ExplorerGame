package com.charrey.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.charrey.game.settings.Settings;
import com.charrey.game.util.SkinUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Slider UI element that allows the user to specify the speed of the simulation
 */
public class SpeedSlider extends Table {

    private final @NotNull Slider slider;
    private final Label middle;

    /**
     * Creates a new Slider
     *
     * @param width width of the slider
     */
    public SpeedSlider(float width) {
        Label label = new Label("Steps / second", SkinUtils.getSkin());
        slider = new Slider(1, 1000, 1, false, SkinUtils.getSkin());
        Label start = new Label("1", SkinUtils.getSkin());
        middle = new Label("-/1", SkinUtils.getSkin());
        Label end = new Label("1000", SkinUtils.getSkin());
        add(label).colspan(3).row();
        add(slider).width(width - 40).colspan(3).row();
        add(start).align(Align.topLeft);
        add(middle).align(Align.center);
        add(end).align(Align.topRight);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Settings.requestedSimulationsPerSecond = slider.getValue();
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        String simsPerSecondString = "-";
        if (Settings.actualSimulationsPerSecond != null) {
            simsPerSecondString = Settings.actualSimulationsPerSecond.toString();
        }
        middle.setText(simsPerSecondString + "/" + Settings.requestedSimulationsPerSecond);
    }
}
