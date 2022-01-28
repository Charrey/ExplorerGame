package com.charrey.game.util.file.filechooser;

import com.badlogic.gdx.files.FileHandle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Class providing utility methods to show file chooser prompts for the user.
 */
public class FileChooser {

    private FileChooser() {
    }


    /**
     * Presents a file chooser for the user to select a file that may exists or not.
     *
     * @param configuration configuration for the file chooser
     * @param callback      callback to be called with the results of the file choosing.
     */
    public static void chooseAnyFile(final @NotNull FileChooserConfiguration configuration, @NotNull FileChooserCallback callback) {
        Objects.requireNonNull(configuration, "Given argument \"configuration\" must not be null");
        Objects.requireNonNull(callback, "Given argument \"callback\" must not be null");

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(Objects.requireNonNullElse(configuration.title(), ""));

        if (configuration.nameFilter() != null) {
            fileChooser.setFileFilter(configuration.nameFilter());
            fileChooser.setAcceptAllFileFilterUsed(false);
        }

        fileChooser.setCurrentDirectory(Objects.requireNonNullElse(configuration.directory(), Paths.get(".").toFile()));
        fileChooser.setApproveButtonText(Objects.requireNonNullElse(configuration.approveText(), "Select"));
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            FileHandle result = new FileHandle(file);
            callback.onFileChosen(result);
        } else {
            callback.onCancellation();
        }
    }

    /**
     * Presents a file chooser for the user to select a file that exists.
     *
     * @param configuration configuration for the file chooser
     * @param callback      callback to be called with the results of the file choosing.
     */
    public static void chooseExistingFile(final @NotNull FileChooserConfiguration configuration, @NotNull FileChooserCallback callback) {
        Objects.requireNonNull(configuration, "Given argument \"configuration\" must not be null");
        Objects.requireNonNull(callback, "Given argument \"callback\" must not be null");

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(Objects.requireNonNullElse(configuration.title(), ""));

        if (configuration.nameFilter() != null) {
            fileChooser.setFileFilter(configuration.nameFilter());
            fileChooser.setAcceptAllFileFilterUsed(false);
        }

        fileChooser.setCurrentDirectory(Objects.requireNonNullElse(configuration.directory(), Paths.get(".").toFile()));
        fileChooser.setApproveButtonText(Objects.requireNonNullElse(configuration.approveText(), "Select"));

        boolean done = false;
        while (!done) {
            int result = fileChooser.showOpenDialog(null);
            switch (result) {
                case JFileChooser.APPROVE_OPTION:
                    if (fileChooser.getSelectedFile().exists()) {
                        callback.onFileChosen(new FileHandle(fileChooser.getSelectedFile()));
                        done = true;
                    }
                    break;
                case JFileChooser.ERROR_OPTION:
                    Logger.getLogger(FileChooser.class.getName()).severe("The file chooser gave an error.");
                    callback.onCancellation();
                    done = true;
                    break;
                case JFileChooser.CANCEL_OPTION:
                    callback.onCancellation();
                    done = true;
                    break;
                default:
                    throw new IllegalStateException("Unknown result from JFileChooser: " + result);
            }
        }
    }
}
