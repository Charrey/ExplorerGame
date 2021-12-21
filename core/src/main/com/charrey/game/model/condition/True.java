package com.charrey.game.model.condition;

/**
 * Condition that is always true
 */
public class True extends Condition {
    @Override
    public boolean test() {
        return true;
    }
}
