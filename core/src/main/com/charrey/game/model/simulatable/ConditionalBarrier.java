package com.charrey.game.model.simulatable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.charrey.game.model.condition.Condition;
import com.charrey.game.model.condition.False;
import com.charrey.game.settings.NewBlockFactory;
import com.charrey.game.texture.CachedTexture;
import com.charrey.game.texture.Drawable;
import com.charrey.game.util.GridItem;
import com.charrey.game.util.SkinUtils;

/**
 * Barrier that is non-moving and active unless a specific (user-selected) condition is active.
 */
public class ConditionalBarrier extends Barrier {


    private static final CachedTexture blockingTexture;
    private static final CachedTexture transparentTexture;
    private static final int INSET = 6;

    static {
        transparentTexture = new CachedTexture() {
            @Override
            protected void computeTexture(Pixmap pixels) {
                pixels.setColor(new Color(0.3f, 0.3f, 0.3f, 1));
                pixels.fill();
                pixels.setColor(0.1f, 0.1f, 0.1f, 1);
                pixels.drawRectangle(INSET, INSET, pixels.getWidth() - (2 * INSET), pixels.getHeight() - (2 * INSET));
                pixels.drawRectangle(INSET + 1, INSET + 1, pixels.getWidth() - (2 * INSET + 2), pixels.getHeight() - (2 * INSET + 2));
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
                pixels.drawRectangle(INSET, INSET, pixels.getWidth() - (2 * INSET), pixels.getHeight() - (2 * INSET));
                pixels.drawRectangle(INSET + 1, INSET + 1, pixels.getWidth() - (2 * INSET + 2), pixels.getHeight() - (2 * INSET + 2));
                pixels.setColor(0, 0, 0, 1);
                pixels.drawRectangle(0, 0, pixels.getWidth(), pixels.getHeight());
            }
        };
    }

    private Condition condition;


    private ConditionalBarrier(GridItem location, Condition condition) {
        super(location);
        this.condition = condition;
    }

    /**
     * Returns a factory that provides dimensions information of a ConditionalBarrier and can create them.
     *
     * @return the factory
     */
    public static NewBlockFactory<ConditionalBarrier> factory() {
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
            public ConditionalBarrier makeSimulatable(GridItem location) {
                return new ConditionalBarrier(location, new False());
            }
        };
    }

    @Override
    public Drawable getTexture(int xOffset, int yOffset, int textureWidth, int textureHeight) {
        if (condition.test()) {
            return new Drawable() {
                @Override
                public void draw(SpriteBatch batch, int width, int height, int x, int y) {
                    batch.draw(transparentTexture.get(textureWidth, textureHeight), x, y);
                    Color previousColor = SkinUtils.getFont().getColor();
                    SkinUtils.getFont().setColor(0, 0, 0, 1);
                    SkinUtils.getFont().draw(batch, "Open", x, y + height / 2f, width, Align.center, false);
                    SkinUtils.getFont().setColor(previousColor);
                }
            };
        } else {
            return new Drawable() {
                @Override
                public void draw(SpriteBatch batch, int width, int height, int x, int y) {
                    batch.draw(blockingTexture.get(textureWidth, textureHeight), x, y);
                    Color previousColor = SkinUtils.getFont().getColor();
                    SkinUtils.getFont().setColor(0.3f, 0.3f, 0.3f, 1);
                    SkinUtils.getFont().draw(batch, "Closed", x, y + height / 2f, width, Align.center, false);
                    SkinUtils.getFont().setColor(previousColor);
                }
            };
        }
    }

    @Override
    public Simulatable copy() {
        return new ConditionalBarrier(getLocation(), condition);
    }

    /**
     * Returns the condition used to determine whether this barrier is transparent.
     *
     * @return the condition
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * Sets the condition for this barrier to be transparent.
     *
     * @param condition condition
     */
    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public boolean isBlocking() {
        return !condition.test();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
