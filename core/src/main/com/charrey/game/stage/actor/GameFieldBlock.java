package com.charrey.game.stage.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
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


    /**
     * Creates a new GameFieldBlock
     * @param registerForSimulation called with blocks that may change in the next simulation step.
     */
    public GameFieldBlock(@NotNull Consumer<GameFieldBlock> registerForSimulation) {
        setTouchable(enabled);
        specification =  new SpecifiedBlockContent();
        simulation = new SimulatedBlockContent(() -> registerForSimulation.accept(this));
        currentContent = specification;
    }

    @Override
    public void draw(@NotNull Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (!TestGenie.isAmIUnderTest()) {
            batch.draw(GameFieldBlockTextureCache.get(currentContent.getVisibleBlockType(), (int) getWidth(), (int) getHeight()), getX(), getY());
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
