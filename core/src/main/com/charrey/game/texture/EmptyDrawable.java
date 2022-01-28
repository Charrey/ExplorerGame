package com.charrey.game.texture;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Drawable that does not do anything / does not draw anything
 */
public class EmptyDrawable extends Drawable {

    /**
     * Singleton instance
     */
    public static final EmptyDrawable instance = new EmptyDrawable();

    private EmptyDrawable() {
    }

    @Override
    public void draw(SpriteBatch batch, int width, int height, int x, int y) {

    }
}
