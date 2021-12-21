package com.charrey.game.model.simulatable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.charrey.game.texture.CachedTexture;
import com.charrey.game.util.GridItem;

/**
 * Barrier that is always active and non-moving
 */
public class DefaultBarrier extends Barrier {

    private static final CachedTexture texture;

    static {
        texture = new CachedTexture() {
            @Override
            protected void computeTexture(Pixmap pixels) {
                pixels.setColor(new Color(0.1f, 0.1f, 0.1f, 1));
                pixels.fill();
                pixels.setColor(0, 0, 0, 1);
                pixels.drawRectangle(0, 0, pixels.getWidth(), pixels.getHeight());
            }
        };
    }

    /**
     * Creates a new Barrier
     *
     * @param location location of the barrier
     */
    public DefaultBarrier(GridItem location) {
        super(location);
    }

    @Override
    boolean isBlocking() {
        return true;
    }


    @Override
    public Texture getTexture(int xOffset, int yOffset, int textureWidth, int textureHeight) {
        return texture.get(textureWidth, textureHeight);
    }

    @Override
    public Simulatable copy() {
        return new DefaultBarrier(getLocation());
    }
}
