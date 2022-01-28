package com.charrey.game.model.condition;

/**
 * Condition that is always false
 */
public class False extends Condition {
    @Override
    public boolean test() {
        return false;
    }

}
