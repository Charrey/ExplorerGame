package saves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.charrey.game.BlockType;
import com.charrey.game.Direction;
import com.charrey.game.Explore;
import com.charrey.game.stage.actor.GameField;
import com.charrey.game.util.file.filechooser.SaveCallback;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class SaveFileTest {

    static LwjglApplication app;
    private static FileHandle SAVE_DIRECTORY;


    @BeforeAll
    static void setUp() {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Explore";
        config.width = 1920;
        config.height = 1080;
        app = new LwjglApplication(new Explore(), config);
        SAVE_DIRECTORY =  Gdx.files.local("build/testSaves");
    }

    @AfterAll
    static void tearDown() {
        app.exit();
        SAVE_DIRECTORY.deleteDirectory();
    }


    @Test
    void testSaveFileCreation() {
        GameField gameField = new GameField(1000, 1000, () -> null, () -> Direction.NOT_APPLICCABLE, () -> 1L);
        new SaveCallback(gameField::serialize).onFileChosen(SAVE_DIRECTORY.child("test.explore"));
        assertTrue(SAVE_DIRECTORY.child("test.explore").exists());
    }

    @Test
    void testEmptySaveFileValidJSON() {
        GameField gameField = new GameField(1000, 1000, () -> null, () -> Direction.NOT_APPLICCABLE, () -> 1L);
        new SaveCallback(gameField::serialize).onFileChosen(SAVE_DIRECTORY.child("test.explore"));
        String read = SAVE_DIRECTORY.child("test.explore").readString();
        try {
            new JSONObject(read);
        } catch (JSONException e) {
            fail("A JSON error occured, meaning invalid JSON was written to the save file.");
        }
    }

    @Test
    void testRandomSaveFileValidJSON() {
        Random random = new Random(16);
        GameField gameField = new GameField(1000,
                1000,
                () -> BlockType.values()[random.nextInt(BlockType.values().length)],
                () -> Direction.values()[random.nextInt(Direction.values().length)],
                () -> 1L);
        new SaveCallback(gameField::serialize).onFileChosen(SAVE_DIRECTORY.child("test.explore"));
        String read = SAVE_DIRECTORY.child("test.explore").readString();
        try {
            new JSONObject(read);
        } catch (JSONException e) {
            fail("A JSON error occured, meaning invalid JSON was written to the save file.");
        }
    }
}
