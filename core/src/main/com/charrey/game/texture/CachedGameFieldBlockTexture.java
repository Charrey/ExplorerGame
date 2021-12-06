package com.charrey.game.texture;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import org.jetbrains.annotations.NotNull;

/**
 * A (cached) texture for a game block that consists of a square of a solid colour with a black border.
 */
public class CachedGameFieldBlockTexture extends CachedTexture {


    private final Color color;

    /**
     * Creates a new texture for a game block that consists of a square of a solid colour with a black border.
     * @param color the colour of the square
     */
    public CachedGameFieldBlockTexture(Color color) {
        this.color = color;
    }

    @Override
    protected void computeTexture(@NotNull Pixmap pixels) {
        pixels.setColor(color);
        pixels.fill();
        pixels.setColor(0, 0, 0, 1);
        pixels.drawRectangle(0, 0, pixels.getWidth(), pixels.getHeight());
    }
}
