package com.charrey.game.stage.griddisplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.charrey.game.settings.Settings;
import com.charrey.game.texture.Drawable;
import com.charrey.game.util.Blocker;
import com.charrey.game.util.GridItem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGB888;
import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;

/**
 * Class used to draw the contents of a specific Grid of a BlockField
 */
public class GridDrawer extends Drawable {
    private final BlockField blockField;
    private final Texture texture;
    private final Previewer previewer;


    /**
     * Creates a new GridDrawer
     *
     * @param blockField BlockField to draw
     */
    public GridDrawer(BlockField blockField) {
        this.blockField = blockField;
        Pixmap pixels = new Pixmap(Math.round(blockField.getWidth()), Math.round(blockField.getHeight()), RGB888);
        pixels.setColor(0.5f, 0.5f, 0.5f, 1);
        pixels.fill();
        this.texture = new Texture(pixels);
        this.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.previewer = new Previewer(blockField);
    }

    @Override
    public void draw(SpriteBatch batch, int width, int height, int x, int y) {
        batch.draw(texture, x, y);
        synchronized (blockField.getGrid()) {
            for (int columnIndex = 0; columnIndex < blockField.getGrid().getWidth(); columnIndex++) {
                for (int rowIndex = 0; rowIndex < blockField.getGrid().getHeight(); rowIndex++) {
                    blockField.getGrid().getEmptyGridDrawable().draw(batch, blockField.getCellWidth(), blockField.getCellHeight(), x + columnIndex * blockField.getCellWidth(), y + rowIndex * blockField.getCellHeight());
                }
            }
            Map<GridItem, Set<Drawable>> toDraw = new HashMap<>();
            blockField.getGrid().getSimulatables().forEach(simulatable -> {
                for (int i = 0; i < simulatable.getWidth(); i++) {
                    for (int j = 0; j < simulatable.getHeight(); j++) {
                        Drawable texture = simulatable.getTexture(i, j, blockField.getCellWidth() * simulatable.getWidth(), blockField.getCellHeight() * simulatable.getHeight());
                        int xToDraw = x + Math.floorMod(i + simulatable.getLocation().x(), blockField.getGrid().getHeight()) * blockField.getCellWidth();
                        int yToDraw = y + Math.floorMod(j + simulatable.getLocation().y(), blockField.getGrid().getHeight()) * blockField.getCellHeight();
                        toDraw.computeIfAbsent(new GridItem(xToDraw, yToDraw), gridItem -> new HashSet<>());
                        toDraw.get(new GridItem(xToDraw, yToDraw)).add(texture);
                    }
                }
            });
            toDraw.forEach((key, value) -> Drawable.mix(value).draw(batch, blockField.getCellWidth(), blockField.getCellHeight(), key.x(), key.y()));
        }
        if (!Settings.currentlySimulating) {
            Vector2 coords = blockField.screenToLocalCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            if (blockField.hit(coords.x, coords.y, false) != null && !Blocker.isBlocking()) {
                previewer.drawPreview(batch, coords);
            }
        }
        if (blockField.getSelectionMode()) {
            Pixmap pixels = new Pixmap(width, height, RGBA8888);
            pixels.setColor(1f, 0f, 0f, 1f);
            pixels.drawRectangle(0, 0, width - 1, height);
            Texture tex = new Texture(pixels);
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            batch.draw(tex, x, y);
        }
    }
}
