package com.charrey.game.texture;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;

/**
 * Class that provides Textures and caches previously computed textures.
 */
public abstract class CachedTexture {

    @NotNull
    private final Map<Integer, HashMap<Integer, Texture>> cache = new HashMap<>();

    /**
     * Returns a texture with the specified width and height. If this was called before with the same width and height,
     * the previously computed Texture is returned instead. Otherwise, this computes the Texture.
     * @param width width of the texture
     * @param height height of the texture
     * @return the texture
     */
    public Texture get(int width, int height) {
        return cache.computeIfAbsent(width, k -> new HashMap<>()).computeIfAbsent(height, ignored -> {
            Pixmap pixels = new Pixmap(width, height, RGBA8888);
            computeTexture(pixels);
            return new Texture(pixels);
        });
    }

    /**
     * When called, this method should draw a subclass-specific image on the provided PixMap depending on the PixMap's size.
     * The overriding method should not providing caching services- that's done by this class.
     * @param pixels PixMap to draw to
     */
    protected abstract void computeTexture(Pixmap pixels);

}
