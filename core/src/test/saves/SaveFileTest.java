package saves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.charrey.game.Explore;
import com.charrey.game.model.Direction;
import com.charrey.game.model.Grid;
import com.charrey.game.model.serialize.GridLoader;
import com.charrey.game.model.serialize.XMLLoader;
import com.charrey.game.model.serialize.XMLSerializer;
import com.charrey.game.model.simulatable.Barrier;
import com.charrey.game.model.simulatable.DefaultBarrier;
import com.charrey.game.model.simulatable.Simulatable;
import com.charrey.game.model.simulatable.SplitExplorer;
import com.charrey.game.util.GridItem;
import com.charrey.game.util.file.filechooser.SaveCallback;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

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
        Grid grid = new Grid(1, 1);
        new SaveCallback(() -> XMLSerializer.get().serialize(grid)).onFileChosen(SAVE_DIRECTORY.child("test.explore"));
        assertTrue(SAVE_DIRECTORY.child("test.explore").exists());
    }

    @Test
    void testEmptySaveFileValid() {
        Grid grid = new Grid(1, 1);
        System.out.println(grid);
        new SaveCallback(() -> XMLSerializer.get().serialize(grid)).onFileChosen(SAVE_DIRECTORY.child("test.explore"));
        String written = SAVE_DIRECTORY.child("test.explore").readString(Charset.defaultCharset().name());
        try {
            Grid gotten = XMLLoader.get().load(written);
            System.out.println(gotten);
        } catch (GridLoader.SaveFormatException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testGridDimensionsValid() {
        Grid grid = new Grid(69, 42);
        new SaveCallback(() -> XMLSerializer.get().serialize(grid)).onFileChosen(SAVE_DIRECTORY.child("test.explore"));
        String written = SAVE_DIRECTORY.child("test.explore").readString(Charset.defaultCharset().name());
        try {
            Grid gotten = XMLLoader.get().load(written);
            assertEquals(42, gotten.getHeight());
            assertEquals(69, gotten.getWidth());
        } catch (GridLoader.SaveFormatException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testBarrierStoredInGrid() {
        Grid grid = new Grid(1, 1);
        grid.add(new DefaultBarrier(new GridItem(0, 0)));
        new SaveCallback(() -> XMLSerializer.get().serialize(grid)).onFileChosen(SAVE_DIRECTORY.child("test.explore"));
        String written = SAVE_DIRECTORY.child("test.explore").readString(Charset.defaultCharset().name());
        try {
            Grid gotten = XMLLoader.get().load(written);
            assertEquals(1, gotten.getSimulatables().size());
            Simulatable simulatable = gotten.getSimulatables().iterator().next();
            assertTrue(simulatable instanceof Barrier);
            assertEquals(0, simulatable.getLocation().x());
            assertEquals(0, simulatable.getLocation().y());
        } catch (GridLoader.SaveFormatException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testSplitExplorerStoredInGrid() {
        Grid grid = new Grid(1, 1);
        grid.add(new SplitExplorer(Direction.RIGHT, new GridItem(0, 0)));
        new SaveCallback(() -> XMLSerializer.get().serialize(grid)).onFileChosen(SAVE_DIRECTORY.child("test.explore"));
        String written = SAVE_DIRECTORY.child("test.explore").readString(Charset.defaultCharset().name());
        try {
            Grid gotten = XMLLoader.get().load(written);
            assertEquals(1, gotten.getSimulatables().size());
            Simulatable simulatable = gotten.getSimulatables().iterator().next();
            assertTrue(simulatable instanceof SplitExplorer);
            assertEquals(0, simulatable.getLocation().x());
            assertEquals(0, simulatable.getLocation().y());
            assertEquals(Direction.RIGHT, ((SplitExplorer) simulatable).getDirection());
        } catch (GridLoader.SaveFormatException e) {
            e.printStackTrace();
            fail();
        }
    }
}
