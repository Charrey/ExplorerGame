package com.charrey.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.charrey.game.util.SkinUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Slider UI element that allows the user to specify the speed of the simulation
 */
public class SpeedSlider extends Table {

    private final @NotNull Slider slider;

    /**
     * Creates a new Slider
     * @param width width of the slider
     */
    public SpeedSlider(float width) {
        Label label = new Label("Steps / second", SkinUtils.getSkin());
        slider = new Slider(1, 1000, 1, false, SkinUtils.getSkin());

        Label start = new Label("1", SkinUtils.getSkin());
        Label end = new Label("1000", SkinUtils.getSkin());

        add(label).colspan(2).row();
        add(slider).width(width - 40).colspan(2).row();
        add(start).align(Align.topLeft);
        add(end).align(Align.topRight);
    }

    /**
     * Returns the user requested simulation speed (in steps per second)
     * @return the simulation speed
     */
    public @NotNull Long get() {
        return (long) Math.round(slider.getValue());
    }
}
