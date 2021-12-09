package com.charrey.game.stage.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.charrey.game.simulator.SimulatorSettings;
import com.charrey.game.texture.GameFieldBlockTextureCache;
import com.charrey.game.util.testwrap.TestGenie;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static com.badlogic.gdx.scenes.scene2d.Touchable.enabled;

/**
 * Grid item in the gamefield that contains a specification and can be simulated.
 */
public class GameFieldBlock extends Actor {

    private final @NotNull SimulatedBlockContent simulation;
    private final @NotNull SpecifiedBlockContent specification;

    private BlockContent currentContent;

    @Override
    public String toString() {
        return currentContent.toString();
    }


    /**
     * Creates a new GameFieldBlock
     * @param registerForSimulation called with blocks that may change in the next simulation step.
     * @param registerChanged called with blocks that have changed in the next simulation step
     * @param execution simulation type (serially or parallely)
     */
    public GameFieldBlock(@NotNull Consumer<GameFieldBlock> registerForSimulation, @NotNull Consumer<GameFieldBlock> registerChanged, SimulatorSettings.ExecutionType execution) {
        setTouchable(enabled);
        specification =  new SpecifiedBlockContent();
        simulation = new SimulatedBlockContent(() -> registerForSimulation.accept(this), () -> registerChanged.accept(this), execution);
        currentContent = specification;
    }

    @Override
    public void draw(@NotNull Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (!TestGenie.isAmIUnderTest()) {
            Texture texture = GameFieldBlockTextureCache.get(currentContent.getVisibleEntity(), (int) getWidth(), (int) getHeight());
            batch.draw(texture, getX(), getY(), getWidth(), getHeight());
        }
    }


    /**
     * Returns the specified block contents of this block (unchanged by simulation).
     * @return the specified block content
     */
    public @NotNull SpecifiedBlockContent getSpecification() {
        return specification;
    }

    /**
     * Returns the simulated block contents of this block (changed by simulation).
     * @return the simulated block content
     */
    public @NotNull SimulatedBlockContent getSimulation() {
        return simulation;
    }

    /**
     * Notifies that the simulation starts. From now on, rendering renders the simulated content.
     */
    public void switchToSimulation() {
        currentContent = simulation;
        simulation.clear(specification.getEntities());
    }

    /**
     * Notifies that the simulation starts. From now on, rendering renders the specified content.
     */
    public void stopSimulation() {
        currentContent = specification;
    }
}
