package com.charrey.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.charrey.game.texture.CachedTexture;
import com.charrey.game.util.GridItem;

/**
 * Simulatable that does not move, but serves as an obstacle for other simulatables.
 */
public class Barrier extends Simulatable {

    private static final CachedTexture texture;

    static {
        texture = new CachedTexture() {
            @Override
            protected void computeTexture(Pixmap pixels) {
                pixels.setColor(new Color(0.5f, 0.5f, 0.5f, 1));
                pixels.fill();
                pixels.setColor(0, 0, 0, 1);
                pixels.drawRectangle(0, 0, pixels.getWidth(), pixels.getHeight());
            }
        };
    }

    /**
     * Creates a new Barrier
     * @param location location of the barrier
     */
    public Barrier(GridItem location) {
        super(location, 10, 1, 1);
    }

    @Override
    public void simulateStep() {

    }

    @Override
    public Texture getTexture(int xOffset, int yOffset, int textureWidth, int textureHeight) {
        return texture.get(textureWidth, textureHeight);
    }

    @Override
    public Simulatable copy() {
        return new Barrier(getLocation());
    }

    @Override
    public String shortName() {
        return "B";
    }

}
