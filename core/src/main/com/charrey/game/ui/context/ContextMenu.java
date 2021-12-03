package com.charrey.game.ui.context;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ContextMenu extends Table {

    private ContextMenu(){}

    int depth = 0;

    public ContextMenu(String name, int depth) {
        this.depth = depth;
        setDebug(true);
        setName(name);
        addClickListener();
    }


    private void addClickListener() {
        addCaptureListener(new InputListener() {
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (getStage() != null && Arrays.stream(getChildren().toArray()).noneMatch(actor -> actor == toActor) && !(toActor instanceof ContextMenuItem)) {
                    Arrays.stream(getStage().getRoot().getChildren().toArray()).filter(actor -> actor instanceof ContextMenu menu && menu.depth > 0).forEach(Actor::remove);
                    super.exit(event, x, y, pointer, toActor);
                }
            }
        });
    }

    @NotNull final List<ContextMenuItem> menuItems = new LinkedList<>();

    public void add(@NotNull ContextMenuItem menuItem) {
        menuItem.setContextMenuParent(this);
        menuItems.add(menuItem);
        super.add(menuItem).row();
        final float[] width = {50};
        final float[] height = {0};
        getCells().forEach(cell -> width[0] = Math.max(cell.getPrefWidth(), width[0]));
        getCells().forEach(cell -> height[0] = cell.getPrefHeight() + height[0]);
        this.getCells().forEach(cell -> cell.width(width[0]));
        setWidth(width[0]);
        setHeight(height[0]);
    }
}
