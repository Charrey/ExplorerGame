package com.charrey.game.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.charrey.game.StageSwitcher;
import com.charrey.game.util.SkinUtils;
import org.jetbrains.annotations.NotNull;

public class ExploreMainMenuStage extends HideableStage {

    final TextButton playButton;

    public ExploreMainMenuStage(@NotNull StageSwitcher stageSwitcher) {
        Table table = new Table();
        playButton = new TextButton("Play", SkinUtils.getSkin());
        playButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                playButton.getClickListener().cancel();
                stageSwitcher.changeToStage(ExploreStage.GAME);
                return true;
            }
        });
        table.add(playButton).width(100).pad(10);
        table.row();
        TextButton quitButton = new TextButton("Quit", SkinUtils.getSkin());
        quitButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                return true;
            }
        });
        table.add(quitButton).width(100).pad(10);
        addActor(table);
        table.setX((getWidth() / 2f) - (table.getWidth() / 2f));
        table.setY((getHeight() / 2f) - (table.getHeight() / 2f));
    }
}
