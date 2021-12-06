package com.charrey.game.util;

/**
 * Utility class that stores two values of any type.
 * @param <K> The first value
 * @param <V> The second value
 */
public record Pair<K, V>(K first, V second) {
}
