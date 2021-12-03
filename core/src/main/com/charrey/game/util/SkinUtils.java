package com.charrey.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.jetbrains.annotations.NotNull;

public class SkinUtils {

    private SkinUtils() {}

    private static final Skin skin = new Skin(Gdx.files.internal("skin2/uiskin.json"));

    public static @NotNull Skin getSkin() {
        return skin;
    }
}
