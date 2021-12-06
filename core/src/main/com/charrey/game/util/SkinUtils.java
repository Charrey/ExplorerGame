package com.charrey.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.jetbrains.annotations.NotNull;

/**
 * Class that provides utility methods involving the visual 'look' of objects in the game.
 */
public class SkinUtils {

    private SkinUtils() {}

    private static final Skin skin = new Skin(Gdx.files.internal("skin2/uiskin.json"));

    /**
     * Returns a Skin object to be used for Actors in the game.
     * @return the skin
     */
    public static @NotNull Skin getSkin() {
        return skin;
    }
}
