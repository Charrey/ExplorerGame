package com.charrey.game.util.random;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class RandomUtils {

    private RandomUtils() {}

    private static final Random random = new Random();

    public static <T> T fromCollection(Collection<T> collection) {
        int elementIndex = random.nextInt(collection.size());
        Iterator<T> iterator = collection.iterator();
        while (elementIndex > 0) {
            iterator.next();
            elementIndex--;
        }
        return iterator.next();
    }
}
