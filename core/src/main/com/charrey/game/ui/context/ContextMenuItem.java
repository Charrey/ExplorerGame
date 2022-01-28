package com.charrey.game.ui.context;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.charrey.game.util.SkinUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Item that may be added to a context menu.
 */
public abstract class ContextMenuItem extends TextButton {

    /**
     * Creates a new ContextMenuItem.
     *
     * @param text text to be shown on the item
     */
    protected ContextMenuItem(String text) {
        super(text, SkinUtils.getSkin());
        setDebug(true);
    }


    @Override
    public @Nullable Actor hit(float x, float y, boolean touchable) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            return null;
        } else {
            return this;
        }
    }
}
