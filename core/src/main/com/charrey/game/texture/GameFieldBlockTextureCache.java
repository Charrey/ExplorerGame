package com.charrey.game.texture;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.charrey.game.BlockType;
import com.charrey.game.Direction;
import com.charrey.game.model.ModelEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

/**
 * Class that provides static methods to provide blocks with textures.
 */
public class GameFieldBlockTextureCache {

    private GameFieldBlockTextureCache() {}

    @NotNull
    private static final EnumMap<BlockType, EnumMap<Direction, CachedGameFieldBlockTexture>> cache = new EnumMap<>(BlockType.class);
    @NotNull
    private static final CachedGameFieldBlockTexture cacheEmptyBlock = new CachedGameFieldBlockTexture(new Color(0.5f, 0.5f, 0.5f, 1), Direction.NOT_APPLICCABLE);

    static {
        EnumMap<Direction, CachedGameFieldBlockTexture> barrierDirectionMap = new EnumMap<>(Direction.class);
        barrierDirectionMap.put(Direction.NOT_APPLICCABLE, new CachedGameFieldBlockTexture(new Color(0.3f, 0.3f, 0.3f, 1), Direction.NOT_APPLICCABLE));
        cache.put(BlockType.BARRIER, barrierDirectionMap);

        EnumMap<Direction, CachedGameFieldBlockTexture> splitExplorerMap = new EnumMap<>(Direction.class);
        Direction.forEachConcrete(direction -> splitExplorerMap.put(direction, new CachedGameFieldBlockTexture(new Color(0.3f, 0.8f, 0.3f, 1), direction)));
        cache.put(BlockType.SPLIT_EXPLORER, splitExplorerMap);

        EnumMap<Direction, CachedGameFieldBlockTexture> randomExplorerMap = new EnumMap<>(Direction.class);
        Direction.forEachConcrete(direction -> randomExplorerMap.put(direction, new CachedGameFieldBlockTexture(new Color(0.8f, 0.3f, 0.3f, 1), direction)));
        cache.put(BlockType.RANDOM_EXPLORER, randomExplorerMap);
    }

    /**
     * Provides a texture for a specific block with specific dimensions
     * @param entity type and direction of block to be rendered
     * @param width width of the texture in pixels
     * @param height height of the texture in pixels
     * @return the texture
     */
    public static Texture get(@Nullable ModelEntity entity, int width, int height) {
        CachedGameFieldBlockTexture texture = entity == null ? cacheEmptyBlock : cache.get(entity.type()).get(entity.direction());
        return texture.get(width, height);
    }
}
