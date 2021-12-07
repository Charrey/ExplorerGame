package com.charrey.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.charrey.game.util.SkinUtils;

public class SpeedSlider extends Table {

    public SpeedSlider(float width) {
        Label label = new Label("Steps / second", SkinUtils.getSkin());
        Slider slider = new Slider(0, 100, 1, false, SkinUtils.getSkin());

        Label start = new Label("1", SkinUtils.getSkin());
        Label end = new Label("100", SkinUtils.getSkin());

        add(label).colspan(2).row();
        add(slider).width(width - 40).colspan(2).row();
        add(start).align(Align.topLeft);
        add(end).align(Align.topRight);
    }
}
