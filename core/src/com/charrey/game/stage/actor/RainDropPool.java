package com.charrey.game.stage.actor;

import com.badlogic.gdx.utils.Pool;

public class RainDropPool extends Pool<RainDrop> {
    @Override
    protected RainDrop newObject() {
        return new RainDrop();
    }
}
