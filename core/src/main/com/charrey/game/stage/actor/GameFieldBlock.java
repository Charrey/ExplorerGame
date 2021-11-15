package com.charrey.game.stage.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.charrey.game.util.testwrap.TestGenie;

import java.util.function.Consumer;

import static com.badlogic.gdx.scenes.scene2d.Touchable.enabled;

public class GameFieldBlock extends Actor {

    private final SimulatedBlockContent simulation;
    private final SpecifiedBlockContent specification;
    private final String name;

    private BlockContent currentContent;


    public GameFieldBlock(Consumer<GameFieldBlock> registerForSimulation, String name) {
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
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (!TestGenie.isAmIUnderTest()) {
            batch.draw(GameFieldBlockTexture.get(currentContent.getVisibleBlockType(), (int) getWidth(), (int) getHeight()), getX(), getY());
        }
    }



    public SpecifiedBlockContent getSpecification() {
        return specification;
    }

    public SimulatedBlockContent getSimulation() {
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
