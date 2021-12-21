package com.charrey.game.model.simulatable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.charrey.game.model.condition.Condition;
import com.charrey.game.texture.CachedTexture;
import com.charrey.game.util.GridItem;

/**
 * Barrier that is non-moving and active unless a specific (user-selected) condition is active.
 */
public class ConditionalBarrier extends Barrier {


    private Condition condition;


    private static final CachedTexture blockingTexture;
    private static final CachedTexture transparentTexture;

    /**
     * Sets the condition for this barrier to be transparent.
     * @param condition condition
     */
    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    static {
        transparentTexture = new CachedTexture() {
            @Override
            protected void computeTexture(Pixmap pixels) {
                pixels.setColor(new Color(0.3f, 0.3f, 0.3f, 1));
                pixels.fill();
                pixels.setColor(0.1f, 0.1f, 0.1f, 1);
                pixels.drawRectangle(3, 3, pixels.getWidth() - 6, pixels.getHeight() - 6);
                pixels.setColor(0, 0, 0, 1);
                pixels.drawRectangle(0, 0, pixels.getWidth(), pixels.getHeight());
            }
        };
        blockingTexture = new CachedTexture() {
            @Override
            protected void computeTexture(Pixmap pixels) {
                pixels.setColor(new Color(0.1f, 0.1f, 0.1f, 1));
                pixels.fill();
                pixels.setColor(0.3f, 0.3f, 0.3f, 1);
                pixels.drawRectangle(3, 3, pixels.getWidth() - 6, pixels.getHeight() - 6);
                pixels.setColor(0, 0, 0, 1);
                pixels.drawRectangle(0, 0, pixels.getWidth(), pixels.getHeight());
            }
        };
    }

    /**
     * Creates a new Barrier
     *
     * @param location location of the barrier
     * @param condition condition to disable the barrier
     */
    public ConditionalBarrier(GridItem location, Condition condition) {
        super(location);
        this.condition = condition;
    }


    @Override
    public Texture getTexture(int xOffset, int yOffset, int textureWidth, int textureHeight) {
        if (condition.test()) {
            return transparentTexture.get(textureWidth, textureHeight);
        } else {
            return blockingTexture.get(textureWidth, textureHeight);
        }
    }

    @Override
    public Simulatable copy() {
        return new ConditionalBarrier(getLocation(), condition);
    }

    /**
     * Returns the condition used to determine whether this barrier is transparent.
     * @return the condition
     */
    public Condition getCondition() {
        return condition;
    }

    @Override
    boolean isBlocking() {
        return !condition.test();
    }
}
