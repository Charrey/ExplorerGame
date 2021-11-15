package com.charrey.game.stage.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Null;
import com.charrey.game.BlockType;
import com.charrey.game.Direction;
import com.charrey.game.model.ModelEntity;


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
        if (Gdx.input.isTouched()) {
            replaceBlock();
        }
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        replaceBlock();
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
