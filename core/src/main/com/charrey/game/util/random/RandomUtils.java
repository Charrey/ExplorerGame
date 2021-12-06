package com.charrey.game.util.random;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

/**
 * Class providing utility method involving randomness
 */
public class RandomUtils {

    private RandomUtils() {}

    private static final Random random = new Random();

    /**
     * Selects a random item from a collection.
     * @param collection collection to sample from
     * @param <T> type of object stored in the collection
     * @return a random sample
     */
    public static <T> T fromCollection(@NotNull Collection<T> collection) {
        int elementIndex = random.nextInt(collection.size());
        Iterator<T> iterator = collection.iterator();
        while (elementIndex > 0) {
            iterator.next();
            elementIndex--;
        }
        return iterator.next();
    }
}
