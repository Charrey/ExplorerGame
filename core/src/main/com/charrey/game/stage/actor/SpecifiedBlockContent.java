package com.charrey.game.stage.actor;

import com.charrey.game.BlockType;
import com.charrey.game.model.ModelEntity;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class SpecifiedBlockContent implements BlockContent {

    private SortedSet<ModelEntity> entities = new TreeSet<>();

    public void addModelEntity(ModelEntity entity) {
        entities.add(entity);
    }

    public void removeModelEntity(ModelEntity entity) {
        entities.remove(entity);
    }

    public SortedSet<ModelEntity> getEntities() {
        return Collections.unmodifiableSortedSet(entities);
    }

    @Override
    public BlockType getVisibleBlockType() {
        return entities.isEmpty() ? null : entities.first().type;
    }

    public void removeAllModelEntities() {
        entities.clear();
    }
}
