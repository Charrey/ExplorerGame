package com.charrey.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Class that provides utility methods involving the visual 'look' of objects in the game.
 */
public class SkinUtils {

    private static final BitmapFont font = new BitmapFont();
    private static Skin skin;
    private SkinUtils() {
    }

    /**
     * Returns a Skin object to be used for Actors in the game.
     *
     * @return the skin
     */
    public static Skin getSkin() {
        if (skin == null) {
            skin = new Skin(Gdx.files.internal("skin2/uiskin.json"));
        }
        return skin;
    }

    /**
     * Returns a Font to be used for Actors in the game.
     *
     * @return the skin
     */
    public static BitmapFont getFont() {
        return font;
    }
}
