package com.charrey.game.ui.context;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Context menu similar to a 'right click' in Windows Explorer. Can have sub-contextmenus.
 */
public class ContextMenu extends Table {

    private ContextMenu(){}

    private int depth = 0;

    /**
     * Creates a new ContextMenu
     * @param depth the length of the hierarchy to the root context menu
     */
    public ContextMenu(int depth) {
        this.depth = depth;
        setDebug(true);
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

    /**
     * Adds a contextmenu item to the menu
     * @param menuItem item to add
     */
    public void add(@NotNull ContextMenuItem menuItem) {
        super.add(menuItem).row();
        menuItems.add(menuItem);
        final float[] width = {50};
        final float[] height = {0};
        getCells().forEach(cell -> width[0] = Math.max(cell.getPrefWidth(), width[0]));
        getCells().forEach(cell -> height[0] = cell.getPrefHeight() + height[0]);
        this.getCells().forEach(cell -> cell.width(width[0]));
        setWidth(width[0]);
        setHeight(height[0]);
    }

    /**
     * Returns the length of the hierarchy to the root context menu. If this is the root context menu this is zero.
     * @return the depth of this context menu
     */
    public int getDepth() {
        return depth;
    }
}
