package com.charrey.game.stage.griddisplay;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.charrey.game.model.Grid;
import com.charrey.game.settings.Settings;
import org.jetbrains.annotations.NotNull;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;

/**
 * Class used to preview block placements in a BlockField when the user hovers their mouse over it.
 */
public class Previewer {

    private static final int thickness = 3;
    private final BlockField blockField;
    private int simulatableWidth;
    private int simulatableHeight;
    private int columnIndexOfMouse;
    private int rowIndexOfMouse;
    private int cellPixelWidth;
    private int cellPixelHeight;
    private int horizontalLeftToWrap;
    private int verticalLeftToWrap;

    /**
     * Creates a new Previewer
     *
     * @param blockField BlockField to be previewed
     */
    public Previewer(BlockField blockField) {
        this.blockField = blockField;
    }

    void drawPreview(@NotNull Batch batch, Vector2 mouseCoordinates) {
        simulatableWidth = Settings.newBlockFactory == null ? 1 : Settings.newBlockFactory.getWidth();
        simulatableHeight = Settings.newBlockFactory == null ? 1 : Settings.newBlockFactory.getHeight();

        columnIndexOfMouse = (int) (blockField.getGrid().getWidth() * (mouseCoordinates.x / blockField.getWidth()));
        rowIndexOfMouse = (int) (blockField.getGrid().getHeight() * (mouseCoordinates.y / blockField.getHeight()));

        cellPixelWidth = (int) (blockField.getWidth() / blockField.getGrid().getWidth());
        cellPixelHeight = (int) (blockField.getHeight() / blockField.getGrid().getHeight());

        horizontalLeftToWrap = blockField.getGrid().getWidth() - columnIndexOfMouse;
        verticalLeftToWrap = rowIndexOfMouse + 1;

        drawOnSpot(batch);
        drawHorizontalOverflow(batch);
        drawVerticalOverflow(batch, blockField.getGrid());
        drawHorizontalVerticalOverflow(batch, blockField.getGrid());
    }

    private void drawHorizontalVerticalOverflow(@NotNull Batch batch, Grid grid) {
        if (horizontalLeftToWrap < simulatableWidth && verticalLeftToWrap < simulatableHeight) { //both overflow
            Pixmap overflowMap = new Pixmap((simulatableWidth - horizontalLeftToWrap) * cellPixelWidth, (int) ((simulatableHeight - verticalLeftToWrap) * blockField.getHeight() / grid.getHeight()), RGBA8888);
            overflowMap.setColor(0, 1, 1, 1);
            overflowMap.fillRectangle(0, overflowMap.getHeight() - thickness, overflowMap.getWidth(), thickness); //draw the bottom line
            overflowMap.fillRectangle(overflowMap.getWidth() - thickness, 0, thickness, overflowMap.getHeight()); //draw the right line
            Texture tex = new Texture(overflowMap);
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            batch.draw(tex, blockField.getX(), blockField.getY() + ((rowIndexOfMouse + grid.getHeight() - simulatableHeight + 1) * cellPixelHeight));
        }
    }

    private void drawOnSpot(@NotNull Batch batch) {
        Pixmap pixOnMouse = new Pixmap((int) (Math.min(simulatableWidth, horizontalLeftToWrap) * blockField.getWidth() / blockField.getGrid().getWidth()), (int) (Math.min(simulatableHeight, verticalLeftToWrap) * blockField.getHeight() / blockField.getGrid().getHeight()), RGBA8888);
        pixOnMouse.setColor(0, 1, 1, 1);
        pixOnMouse.fillRectangle(0, 0, pixOnMouse.getWidth(), thickness); //draw the top line
        pixOnMouse.fillRectangle(0, 0, thickness, pixOnMouse.getHeight()); //draw the left line
        if (horizontalLeftToWrap >= simulatableWidth) {
            pixOnMouse.fillRectangle(pixOnMouse.getWidth() - thickness, 0, thickness, pixOnMouse.getHeight()); //draw right line
        }
        if (verticalLeftToWrap >= simulatableHeight) {
            pixOnMouse.fillRectangle(0, pixOnMouse.getHeight() - thickness, pixOnMouse.getWidth(), thickness); //draw bottom line
        }
        Texture tex = new Texture(pixOnMouse);
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        batch.draw(tex, blockField.getX() + columnIndexOfMouse * cellPixelWidth, blockField.getY() + Math.max(0, rowIndexOfMouse - (simulatableHeight - 1)) * cellPixelHeight);
    }

    private void drawHorizontalOverflow(@NotNull Batch batch) {
        if (horizontalLeftToWrap < simulatableWidth) { //horizontal overflow
            Pixmap horizontalOverflowMap = new Pixmap((simulatableWidth - horizontalLeftToWrap) * cellPixelWidth, Math.min(simulatableHeight, verticalLeftToWrap) * cellPixelHeight, RGBA8888);
            horizontalOverflowMap.setColor(0, 1, 1, 1);
            horizontalOverflowMap.fillRectangle(0, 0, horizontalOverflowMap.getWidth(), thickness); //draw the top line
            horizontalOverflowMap.fillRectangle(horizontalOverflowMap.getWidth() - thickness, thickness, thickness, horizontalOverflowMap.getHeight()); //draw the right line
            if (verticalLeftToWrap >= simulatableHeight) {
                horizontalOverflowMap.fillRectangle(0, horizontalOverflowMap.getHeight() - thickness, horizontalOverflowMap.getWidth(), thickness); //draw the bottom line
            }
            Texture tex = new Texture(horizontalOverflowMap);
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            batch.draw(tex, blockField.getX(), blockField.getY() + Math.max(0, rowIndexOfMouse - (simulatableHeight - 1)) * cellPixelHeight);
        }
    }

    private void drawVerticalOverflow(@NotNull Batch batch, Grid grid) {
        if (verticalLeftToWrap < simulatableHeight) { //vertical overflow
            Pixmap verticalOverflowMap = new Pixmap(Math.min(simulatableWidth, horizontalLeftToWrap) * cellPixelWidth, (simulatableHeight - verticalLeftToWrap) * cellPixelHeight, RGBA8888);
            verticalOverflowMap.setColor(0, 1, 1, 1);
            verticalOverflowMap.fillRectangle(0, 0, thickness, verticalOverflowMap.getHeight()); //draw the left line
            verticalOverflowMap.fillRectangle(0, verticalOverflowMap.getHeight() - thickness, verticalOverflowMap.getWidth(), 3); //draw the bottom line
            if (horizontalLeftToWrap >= simulatableWidth) {
                verticalOverflowMap.fillRectangle(verticalOverflowMap.getWidth() - thickness, 0, thickness, verticalOverflowMap.getHeight()); //draw the right line
            }
            Texture tex = new Texture(verticalOverflowMap);
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            batch.draw(tex, blockField.getX() + columnIndexOfMouse * cellPixelWidth, blockField.getY() + ((rowIndexOfMouse + grid.getHeight() - simulatableHeight + 1) * cellPixelHeight));
        }
    }

}
