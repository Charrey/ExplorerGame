package com.charrey.game.stage.griddisplay;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.charrey.game.model.condition.BlockExists;
import com.charrey.game.model.condition.NotBlockExists;
import com.charrey.game.model.simulatable.ConditionalBarrier;
import com.charrey.game.model.simulatable.Simulatable;
import com.charrey.game.settings.Settings;
import com.charrey.game.ui.context.ContextMenu;
import com.charrey.game.ui.context.ContextMenuItem;
import com.charrey.game.ui.context.GroupContextMenuItem;
import com.charrey.game.ui.context.LeafContextMenuItem;
import com.charrey.game.util.GridItem;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

class GameFieldClickHandler extends InputListener {

    private final BlockField blockField;
    private Consumer<GridItem> uponSelection = null;

    public GameFieldClickHandler(BlockField blockField) {
        this.blockField = blockField;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (button == 0) {
            leftMouseClick(new Vector2(x, y));
        } else if (button == 1) {
            rightMouseClick(new Vector2(x, y), blockField.localToStageCoordinates(new Vector2(x, y)));
        }
        return super.touchDown(event, x, y, pointer, button);
    }

    private void rightMouseClick(Vector2 localCoordinates, Vector2 stageCoordinates) {
        blockField.setSelectionMode(false);
        ContextMenu contextMenu = new ContextMenu(0);
        int columnIndex = (int) (blockField.getGrid().getWidth() * (localCoordinates.x / blockField.getWidth()));
        int rowIndex = (int) (blockField.getGrid().getHeight() * (localCoordinates.y / blockField.getHeight()));
        Set<Simulatable> simulatables = blockField.getGrid().getAtStrictGridLocation(new GridItem(columnIndex, rowIndex));

        if (!Settings.currentlySimulating) {
            contextMenu.add(new LeafContextMenuItem("Clear blocks", () -> blockField.getGrid().clear(blockField.getGrid().getWidth(), blockField.getGrid().getHeight())));
            if (simulatables.size() == 1) {
                Simulatable simulatable = simulatables.iterator().next();
                if (simulatable instanceof ConditionalBarrier conditionalBarrier) {
                    Supplier<ContextMenuItem> existsBlock = () -> new LeafContextMenuItem("Exists block at...", () -> {
                        blockField.setSelectionMode(true);
                        uponSelection = gridItem -> conditionalBarrier.setCondition(new BlockExists(blockField.getGrid(), gridItem));
                    });
                    Supplier<ContextMenuItem> notExistsBlock = () -> new LeafContextMenuItem("Not exists block at...", () -> {
                        blockField.setSelectionMode(true);
                        uponSelection = gridItem -> conditionalBarrier.setCondition(new NotBlockExists(blockField.getGrid(), gridItem));
                    });
                    contextMenu.add(new GroupContextMenuItem("Set transparent condition", List.of(existsBlock, notExistsBlock)));
                }
            }
        } else {
            contextMenu.add(new LeafContextMenuItem("Stop simulation", blockField::stopSimulation));
        }
        blockField.getStage().getRoot().addActor(contextMenu);
        contextMenu.setX(stageCoordinates.x + 1);
        contextMenu.setY(stageCoordinates.y + 1);
    }

    private void leftMouseClick(Vector2 localCoordinates) {
        if (!Settings.currentlySimulating) {
            int columnIndex = (int) (blockField.getGrid().getWidth() * (localCoordinates.x / blockField.getWidth()));
            int rowIndex = (int) (blockField.getGrid().getHeight() * (localCoordinates.y / blockField.getHeight()));
            if (blockField.getSelectionMode()) {
                uponSelection.accept(new GridItem(columnIndex, rowIndex));
                uponSelection = null;
                blockField.setSelectionMode(false);
            } else {
                if (Settings.newBlockFactory != null) {
                    int blockHeight = Settings.newBlockFactory.getHeight();
                    GridItem heightCorrectedLocation = new GridItem(columnIndex, Math.floorMod(rowIndex - (blockHeight - 1), blockField.getGrid().getHeight()));
                    for (int x = 0; x < Settings.newBlockFactory.getWidth(); x++) {
                        for (int y = 0; y < Settings.newBlockFactory.getHeight(); y++) {
                            int gridx = Math.floorMod(x + heightCorrectedLocation.x(), blockField.getGrid().getWidth());
                            int gridy = Math.floorMod(y + heightCorrectedLocation.y(), blockField.getGrid().getHeight());
                            blockField.getGrid().remove(new GridItem(gridx, gridy));
                        }
                    }

                    Simulatable simulatable = Settings.newBlockFactory.makeSimulatable(heightCorrectedLocation);
                    blockField.getGrid().add(simulatable);
                    blockField.setLastSimulatableAdded(simulatable);
                } else {
                    blockField.getGrid().remove(new GridItem(columnIndex, rowIndex));
                }
            }
        }
    }

}
