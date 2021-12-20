package com.charrey.game.util;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.function.ToIntFunction;

/**
 * Stores the location of something inside a 2D grid.
 */
public record GridItem(int x, int y) implements Comparable<GridItem> {

    private static final Comparator<GridItem> comparator = Comparator.comparingInt((ToIntFunction<GridItem>) value -> value.x).thenComparingInt(value -> value.y);

    @Override
    public int compareTo(@NotNull GridItem o) {
        return comparator.compare(this, o);
    }
}
