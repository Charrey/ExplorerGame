package com.charrey.game.model.simulatable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.charrey.game.settings.NewBlockFactory;
import com.charrey.game.texture.CachedTexture;
import com.charrey.game.texture.Drawable;
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
    private DefaultBarrier(GridItem location) {
        super(location);
    }

    /**
     * Returns a factory that provides dimensions information of a DefaultBarrier and can create them.
     *
     * @return the factory
     */
    public static NewBlockFactory<DefaultBarrier> factory() {
        return new NewBlockFactory<>() {
            @Override
            public int getWidth() {
                return 1;
            }

            @Override
            public int getHeight() {
                return 1;
            }

            @Override
            public DefaultBarrier makeSimulatable(GridItem location) {
                return new DefaultBarrier(location);
            }
        };
    }

    @Override
    public boolean isBlocking() {
        return true;
    }

    @Override
    public Drawable getTexture(int xOffset, int yOffset, int textureWidth, int textureHeight) {
        return texture;
    }

    @Override
    public Simulatable copy() {
        return new DefaultBarrier(getLocation());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
