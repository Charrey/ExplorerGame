package com.charrey.game.texture;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract class for something that can be drawn
 */
public abstract class Drawable {

    /**
     * Returns a Drawable that draws each Drawable in its input, averaging their colors.
     *
     * @param sources inputs
     * @return average of inputs
     */
    public static Drawable mix(Collection<Drawable> sources) {
        if (sources.size() == 1) {
            return sources.iterator().next();
        } else if (sources.size() == 0) {
            return EmptyDrawable.instance;
        } else {
            return new MixedDrawable(new ArrayList<>(sources));
        }
    }

    /**
     * Draws the instance of this class
     *
     * @param batch  batch to draw to
     * @param width  with of the drawing
     * @param height height of the drawing
     * @param x      horizontal location
     * @param y      vertical location
     */
    public abstract void draw(SpriteBatch batch, int width, int height, int x, int y);
}
