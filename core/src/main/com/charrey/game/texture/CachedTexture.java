package com.charrey.game.texture;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;

public abstract class CachedTexture {

    @NotNull
    private final Map<Integer, HashMap<Integer, Texture>> cache = new HashMap<>();

    public Texture get(int width, int height) {
        return cache.computeIfAbsent(width, k -> new HashMap<>()).computeIfAbsent(height, ignored -> {
            Pixmap pixels = new Pixmap(width, height, RGBA8888);
            computeTexture(pixels);
            return new Texture(pixels);
        });
    }

    protected abstract void computeTexture(Pixmap pixels);

}
