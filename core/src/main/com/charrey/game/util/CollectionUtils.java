package com.charrey.game.util;

import java.util.Collection;
import java.util.Set;

/**
 * Utility class for methods related to collections
 */
public final class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * Returns whether the provided sets contain distinct elements
     *
     * @param collections collections
     * @param <T>         type that the sets contain
     * @return true iff there exists an element in multiple sets
     */
    @SafeVarargs
    public static <T> boolean separate(Set<T>... collections) {
        for (int i = 0; i < collections.length - 1; i++) {
            Collection<T> a = collections[i];
            for (int j = i + 1; j < collections.length; j++) {
                Collection<T> b = collections[j];
                if (a.stream().anyMatch(b::contains)) {
                    return false;
                }
            }
        }
        return true;
    }
}
