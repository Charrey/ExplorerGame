package com.charrey.game.texture;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.charrey.game.BlockType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

/**
 * Class that provides static methods to provide blocks with textures.
 */
public class GameFieldBlockTextureCache {

    private GameFieldBlockTextureCache() {}

    @NotNull
    private static final EnumMap<BlockType, CachedGameFieldBlockTexture> cache = new EnumMap<>(BlockType.class);
    @NotNull
    private static final CachedGameFieldBlockTexture cacheEmptyBlock = new CachedGameFieldBlockTexture(new Color(0.5f, 0.5f, 0.5f, 1));

    static {
        cache.put(BlockType.BARRIER, new CachedGameFieldBlockTexture(new Color(0.3f, 0.3f, 0.3f, 1)));
        cache.put(BlockType.SPLIT_EXPLORER, new CachedGameFieldBlockTexture(new Color(0.3f, 0.8f, 0.3f, 1)));
        cache.put(BlockType.RANDOM_EXPLORER, new CachedGameFieldBlockTexture(new Color(0.8f, 0.3f, 0.3f, 1)));
    }

    /**
     * Provides a texture for a specific block with specific dimensions
     * @param type type of block to be rendered
     * @param width width of the texture in pixels
     * @param height height of the texture in pixels
     * @return the texture
     */
    public static Texture get(@Nullable BlockType type, int width, int height) {
        CachedGameFieldBlockTexture texture = type == null ? cacheEmptyBlock : cache.get(type);
        return texture.get(width, height);
    }
}
