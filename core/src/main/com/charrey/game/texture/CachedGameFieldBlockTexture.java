package com.charrey.game.texture;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.charrey.game.Direction;
import org.jetbrains.annotations.NotNull;

import static com.charrey.game.Direction.*;

/**
 * A (cached) texture for a game block that consists of a square of a solid colour with a black border.
 */
public class CachedGameFieldBlockTexture extends CachedTexture {


    private final Color color;
    private final Direction direction;

    /**
     * Creates a new texture for a game block that consists of a square of a solid colour with a black border.
     * @param color the colour of the square
     */
    public CachedGameFieldBlockTexture(Color color, Direction direction) {
        this.color = color;
        this.direction = direction;
    }

    @Override
    protected void computeTexture(@NotNull Pixmap pixels) {
        pixels.setColor(color);
        pixels.fill();
        pixels.setColor(0, 0, 0, 1);
        float width = pixels.getWidth();
        float height = pixels.getHeight();
        //draw the arrow
        if (direction == UP || direction == DOWN) {
            pixels.drawLine(Math.round(width / 2f), Math.round(height / 3f), Math.round(width / 2f), Math.round(height * (2f/3f)));
        } else if (direction == LEFT || direction == RIGHT) {
            pixels.drawLine(Math.round(width / 3f), Math.round(height / 2f), Math.round(width * (2f/3f)), Math.round(height / 2f));
        }
        if (direction == DOWN || direction == LEFT) {
            pixels.drawLine(Math.round(width / 3f), Math.round(height / 2f), Math.round(width / 2f), Math.round(height * (2f/3f)));
        }
        if (direction == UP || direction == LEFT) {
            pixels.drawLine(Math.round(width / 3f), Math.round(height / 2f), Math.round(width / 2f), Math.round(height * (1f/3f)));
        }
        if (direction == DOWN || direction == RIGHT) {
            pixels.drawLine(Math.round(width * (2f/3f)), Math.round(height / 2f), Math.round(width / 2f), Math.round(height * (2f/3f)));
        }
        if (direction == UP || direction == RIGHT) {
            pixels.drawLine(Math.round(width * (2f/3f)), Math.round(height / 2f), Math.round(width / 2f), Math.round(height * (1f/3f)));
        }
        pixels.drawRectangle(0, 0, pixels.getWidth(), pixels.getHeight());
    }
}
