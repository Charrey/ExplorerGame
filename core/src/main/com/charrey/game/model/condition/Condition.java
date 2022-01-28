package com.charrey.game.model.condition;

/**
 * This class may be used as a parameter of a Simulatable to change behaviour depending on some state of the model,
 * for example by testing the condition in it's simulation step or by testing the condition to determine whether
 * a barrier is blocking or not.
 */
public abstract class Condition {

    /**
     * Tests the condition
     *
     * @return the result
     */
    public abstract boolean test();

}
