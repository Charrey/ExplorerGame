package com.charrey.game.stage.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Null;
import com.charrey.game.BlockType;
import com.charrey.game.Direction;
import com.charrey.game.model.ModelEntity;
import com.charrey.game.ui.context.ContextMenu;
import com.charrey.game.ui.context.ContextMenuItem;
import com.charrey.game.ui.context.GroupContextMenuItem;
import com.charrey.game.ui.context.LeafContextMenuItem;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Handles clicks on a block. In simulation mode, this does not do anything. In specification mode, clicking a block removes all entities
 * and replaces them with the currently selected entity in the selection bar.
 */
public class BlockClickListener extends ClickListener {

    private final Supplier<BlockType> newBlockType;
    private final Supplier<Direction> newBlockDirection;
    private final GameFieldBlock block;

    /**
     * Creates a listener for a specific block
     * @param block block to listen to clicks for
     * @param newBlockType indicates which block type is selected by the user to replace this with
     * @param newBlockDirection indicates which block direction is selected by the user to replace this with
     */
    public BlockClickListener(GameFieldBlock block,
                              Supplier<BlockType> newBlockType,
                              Supplier<Direction> newBlockDirection) {
        this.block = block;
        this.newBlockType = newBlockType;
        this.newBlockDirection = newBlockDirection;
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, @Null Actor fromActor) {
        if (Gdx.input.isTouched() && !Gdx.input.justTouched()) {
            replaceBlock();
        }
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        super.touchDown(event, x, y, pointer, button);
        if (button == 1) {
            Arrays.stream(block.getStage().getRoot().getChildren().toArray()).filter(ContextMenu.class::isInstance).forEachOrdered(Actor::remove);
            ContextMenu menu = new ContextMenu(0);
            menu.setName("root");
            block.getStage().addActor(menu);

            Supplier<ContextMenuItem> leftItem = () -> new LeafContextMenuItem("Left", () -> {});
            Supplier<ContextMenuItem> rightItem = () -> new LeafContextMenuItem("Right", () -> {});
            Supplier<ContextMenuItem> upItem = () -> new LeafContextMenuItem("Up", () -> {});
            Supplier<ContextMenuItem> downItem = () -> new LeafContextMenuItem("Down", () -> {});

            menu.add(new GroupContextMenuItem("Set direction to", List.of(leftItem, rightItem, upItem, downItem)));
            menu.add(new LeafContextMenuItem("Clear", () -> block.getSpecification().removeAllModelEntities()));

            Vector2 stageCoordinates = block.localToStageCoordinates(new Vector2(x, y));
            //test whether the menu extends past the top of the screen
            if (block.getStage().getHeight() - (stageCoordinates.y + menu.getHeight()) < 0) {
                stageCoordinates.y -= menu.getHeight() + 2;
            }
            menu.setX(stageCoordinates.x + 1);
            menu.setY(stageCoordinates.y + 1);
        } else if (button == 0) {
            replaceBlock();
        }
        return true;
    }

    private void replaceBlock() {
        block.getSpecification().removeAllModelEntities();
        BlockType type = newBlockType.get();
        if (type != null) {
            block.getSpecification().addModelEntity(new ModelEntity(type, newBlockDirection.get()));
        }
    }
}
