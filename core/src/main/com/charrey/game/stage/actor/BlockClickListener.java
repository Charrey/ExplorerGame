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

public class BlockClickListener extends ClickListener {

    private final Supplier<BlockType> newBlockType;
    private final Supplier<Direction> newBlockDirection;
    private final GameFieldBlock block;

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
            ContextMenu menu = new ContextMenu("root", 0);
            block.getStage().addActor(menu);
            Supplier<ContextMenuItem> leafItem = () -> new LeafContextMenuItem("D", () -> {});
            Supplier<ContextMenuItem> lastGroupItem = () -> new GroupContextMenuItem("C", List.of(leafItem));
            Supplier<ContextMenuItem> firstToLastGroupItem = () -> new GroupContextMenuItem("B", List.of(lastGroupItem));
            menu.add(new GroupContextMenuItem("A", List.of(firstToLastGroupItem)));
            menu.add(new LeafContextMenuItem("Clear", () -> block.getSpecification().removeAllModelEntities()));
            menu.setX(block.localToStageCoordinates(new Vector2(x, y)).x + 1);
            menu.setY(block.localToStageCoordinates(new Vector2(x, y)).y + 1);
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
