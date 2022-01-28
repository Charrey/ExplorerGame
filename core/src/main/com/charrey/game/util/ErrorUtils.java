package com.charrey.game.util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import static com.badlogic.gdx.utils.Align.center;

/**
 * Class that provides utility methods relating to errors and error messages
 */
public class ErrorUtils {

    /**
     * Shows an error message
     *
     * @param error error message
     * @param stage stage to show the message on
     */
    public static void showErrorMessage(String error, Stage stage) {
        Blocker.block(stage);
        QuickDialog dialog = new QuickDialog("Error", error, "Ok", stage);
        dialog.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Blocker.unblock();
            }
        });
    }

    private static class QuickDialog extends Dialog {

        public QuickDialog(String title, String text, String buttonText, Stage stage) {
            super(title, SkinUtils.getSkin());
            Label label = new Label(text, SkinUtils.getSkin());
            getContentTable().add(label).pad(30);

            Button button = new TextButton(buttonText, SkinUtils.getSkin());
            button.pad(10, 40, 10, 40);
            getButtonTable().add(button);
            setObject(button, null);

            show(stage, null);
            setKeepWithinStage(false);
            setMovable(false);
            setPosition(getStage().getWidth() / 2, getStage().getHeight() / 2, center);
        }

        @Override
        public void hide() {
            super.hide(null);
        }
    }

}
