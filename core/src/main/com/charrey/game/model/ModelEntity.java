package com.charrey.game.model;



import com.charrey.game.BlockType;
import com.charrey.game.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.ToIntFunction;

/**
 * Class describing an entity that may reside in a block. This is decoupled since multiple objects (for example, moving
 * blocks in crossing directions) may at some point be in the same position.
 */
public record ModelEntity(BlockType type,
                          Direction direction) implements Comparable<ModelEntity> {

    @Override
    public int compareTo(@NotNull ModelEntity o) {
        Comparator<ModelEntity> comparator = Comparator
                .comparingInt((ToIntFunction<ModelEntity>) value -> value.type.ordinal())
                .thenComparingInt(value -> value.direction.ordinal());
        return comparator.compare(this, o);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ModelEntity that = (ModelEntity) o;
        return direction == that.direction && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction, type);
    }
}
