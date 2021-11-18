package com.charrey.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class SkinUtils {

    private SkinUtils() {}

    private static final Skin skin = new Skin(Gdx.files.internal("skin2/uiskin.json"));

    public static Skin getSkin() {
        return skin;
    }
}
