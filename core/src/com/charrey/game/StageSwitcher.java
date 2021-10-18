package com.charrey.game;

import com.charrey.game.stage.HideableStage;

public interface StageSwitcher {

    void changeToMainMenu();
    void changeToGame();
    void changeTo(HideableStage stage);
}
