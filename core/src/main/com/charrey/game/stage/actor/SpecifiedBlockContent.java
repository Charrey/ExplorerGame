package com.charrey.game.stage.actor;

import com.charrey.game.BlockType;
import com.charrey.game.model.ModelEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class SpecifiedBlockContent implements BlockContent {

    @NotNull
    private final SortedSet<ModelEntity> entities = new TreeSet<>();

    public void addModelEntity(ModelEntity entity) {
        entities.add(entity);
    }

    public @NotNull SortedSet<ModelEntity> getEntities() {
        return Collections.unmodifiableSortedSet(entities);
    }

    @Override
    public @Nullable BlockType getVisibleBlockType() {
        return entities.isEmpty() ? null : entities.first().type();
    }

    public void removeAllModelEntities() {
        entities.clear();
    }
}
