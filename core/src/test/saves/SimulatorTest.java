package saves;

import com.charrey.game.model.*;
import com.charrey.game.simulator.ParallelSemanticSimulationStep;
import com.charrey.game.simulator.ParallelStateSwitchSimulationStep;
import com.charrey.game.simulator.SerialSemanticSimulationStep;
import com.charrey.game.simulator.SerialStateSwitchSimulationStep;
import com.charrey.game.util.GridItem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulatorTest {

    @Test
    void testDuplicateExplorer() {
        Random random = new Random(1904735);
        for (int j = 0; j < 500; j++) {
            Grid grid = new Grid(3, 3);
            grid.add(new SplitExplorer(Direction.RIGHT, new GridItem(0, 1)));
            grid.add(new SplitExplorer(Direction.UP, new GridItem(1, 0)));
            grid.add(new Barrier(new GridItem(2, 1)));
            for (int i = 0; i < 10; i++) {
                List<Simulatable> toBeSimulated = new ArrayList<>(grid.getSimulatables());
                Collections.shuffle(toBeSimulated, random);
                toBeSimulated.forEach(Simulatable::simulateStep);
                toBeSimulated.forEach(Simulatable::stateSwitchStep);
                grid.updateMapAndDeduplicate();
                if (grid.getSimulatables().size() != 3) {
                    System.out.println("j = " + j);
                    System.out.println("i = " + i);
                    System.out.println(grid);
                }
                assertEquals(3, grid.getSimulatables().size());
            }
        }
    }

    @Test
    void testDuplicateExplorer2() {
        Grid grid = new Grid(3, 5);
        grid.add(new SplitExplorer(Direction.UP, new GridItem(1, 1)));
        grid.add(new SplitExplorer(Direction.UP, new GridItem(1, 2)));
        new SerialSemanticSimulationStep().executeOneStep(grid);
        new SerialStateSwitchSimulationStep().nextStep(grid);
        grid.updateMapAndDeduplicate();
    }

    @Test
    void testParallelStateSwitch() {
        for (int j = 0; j < 50; j++) {
            Grid grid = new Grid(5, 5);
            grid.add(new SplitExplorer(Direction.UP, new GridItem(1, 1)));
            grid.add(new Barrier(new GridItem(1, 3)));
            grid.add(new SplitExplorer(Direction.LEFT, new GridItem(2, 2)));
            for (int i = 0; i < 5; i++) {
                new ParallelSemanticSimulationStep().executeOneStep(grid);
                new ParallelStateSwitchSimulationStep().nextStep(grid);
                grid.updateMapAndDeduplicate();
                assertEquals(3, grid.getSimulatables().size());
            }
        }
    }
}
