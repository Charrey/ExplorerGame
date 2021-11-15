package com.charrey.game.stage.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.charrey.game.BlockType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGB888;

public class GameFieldBlockTexture {

    private GameFieldBlockTexture() {}


    private static EnumMap<BlockType, HashMap<Integer, HashMap<Integer, Texture>>> cache = new EnumMap<>(BlockType.class);
    private static HashMap<Integer, HashMap<Integer, Texture>> cacheEmptyBlock = new HashMap<>();

    public static final Map<BlockType, Color> textures;

    static {
        textures = Map.of(BlockType.BARRIER, new Color(0.3f, 0.3f, 0.3f, 1),
                BlockType.SPLIT_EXPLORER, new Color(0.3f, 0.8f, 0.3f, 1),
                BlockType.RANDOM_EXPLORER, new Color(0.8f, 0.3f, 0.3f, 1));
    }

    public static Texture get(BlockType type, int width, int height) {
        HashMap<Integer, HashMap<Integer, Texture>> byType = type == null ? cacheEmptyBlock : cache.computeIfAbsent(type, k -> new HashMap<>());
        return byType.computeIfAbsent(width, k -> new HashMap<>()).computeIfAbsent(height, ignored -> {
            Pixmap pixels = new Pixmap(width, height, RGB888);
            if (type == null) {
                pixels.setColor(new Color(0.5f, 0.5f, 0.5f, 1));
            } else {
                pixels.setColor(textures.get(type));
            }
            pixels.fill();
            pixels.setColor(0, 0, 0, 1);
            pixels.drawRectangle(0, 0, width, height);
            return new Texture(pixels);
        });

    }
}
