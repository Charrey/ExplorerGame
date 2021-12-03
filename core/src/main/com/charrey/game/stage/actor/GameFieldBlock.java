package com.charrey.game.stage.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.charrey.game.texture.GameFieldBlockTextureCache;
import com.charrey.game.util.testwrap.TestGenie;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static com.badlogic.gdx.scenes.scene2d.Touchable.enabled;

public class GameFieldBlock extends Actor {

    private final @NotNull SimulatedBlockContent simulation;
    private final @NotNull SpecifiedBlockContent specification;
    private final String name;

    private BlockContent currentContent;


    public GameFieldBlock(@NotNull Consumer<GameFieldBlock> registerForSimulation, String name) {
        setTouchable(enabled);
        specification =  new SpecifiedBlockContent();
        simulation = new SimulatedBlockContent(() -> registerForSimulation.accept(this));
        currentContent = specification;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void draw(@NotNull Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (!TestGenie.isAmIUnderTest()) {
            batch.draw(GameFieldBlockTextureCache.get(currentContent.getVisibleBlockType(), (int) getWidth(), (int) getHeight()), getX(), getY());
        }
    }



    public @NotNull SpecifiedBlockContent getSpecification() {
        return specification;
    }

    public @NotNull SimulatedBlockContent getSimulation() {
        return simulation;
    }

    public void switchToSimulation() {
        currentContent = simulation;
        simulation.clear(specification.getEntities());
    }

    public void stopSimulation() {
        currentContent = specification;
    }
}
