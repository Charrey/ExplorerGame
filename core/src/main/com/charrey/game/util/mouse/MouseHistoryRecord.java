package com.charrey.game.util.mouse;

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Record that stores a specific mouse click
 */
public record MouseHistoryRecord(int screenX, int screenY, float stageX, float stageY, int pointer, int button,
                                 Actor target) {
}
