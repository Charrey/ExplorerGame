package com.charrey.game.model;



import com.charrey.game.BlockType;
import com.charrey.game.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.ToIntFunction;

public class ModelEntity implements Comparable<ModelEntity> {

    public final Direction direction;
    public final BlockType type;

    public ModelEntity(BlockType type, Direction direction) {
        this.type = type;
        this.direction = direction;
    }

    @Override
    public int compareTo(@NotNull ModelEntity o) {
        Comparator<ModelEntity> comparator =  Comparator
                .comparingInt((ToIntFunction<ModelEntity>) value -> value.type.ordinal())
                .thenComparingInt(value -> value.direction.ordinal());
        return comparator.compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
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
