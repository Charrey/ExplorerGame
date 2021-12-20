package com.charrey.game.util.mouse;

/**
 * Class that records the history of mouse interaction.
 */
public class MouseHistory {

    private MouseHistory() {}

    private static MouseHistoryRecord lastTouchDown;

    /**
     * Returns the last mouse click recorded
     * @return last mouse click
     */
    public static MouseHistoryRecord lastTouchDown() {
        return lastTouchDown;
    }

    /**
     * Stores the last mouse click in a one-item cache
     * @param record mouse click to store
     */
    public static void touchDown(MouseHistoryRecord record) {
        lastTouchDown = record;
    }
}