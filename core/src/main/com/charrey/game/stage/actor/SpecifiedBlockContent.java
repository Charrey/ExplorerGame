package com.charrey.game.stage.actor;

import com.charrey.game.BlockType;
import com.charrey.game.model.ModelEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Contains the specification for a single block
 */
public class SpecifiedBlockContent implements BlockContent {

    @NotNull
    private final SortedSet<ModelEntity> entities = new TreeSet<>();

    /**
     * Adds an entity to the specification of this block
     * @param entity entity to add
     */
    public void addModelEntity(ModelEntity entity) {
        entities.add(entity);
    }

    /**
     * Returns all entities specified for this block
     * @return the entities
     */
    public @NotNull SortedSet<ModelEntity> getEntities() {
        return Collections.unmodifiableSortedSet(entities);
    }

    @Override
    public @Nullable BlockType getVisibleBlockType() {
        return entities.isEmpty() ? null : entities.first().type();
    }

    /**
     * Removes all entities from the specification for this block
     */
    public void removeAllModelEntities() {
        entities.clear();
    }
}
