package com.charrey.game.texture;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Drawable that collects more than one Drawable and draws the average image of its inputs
 */
public class MixedDrawable extends Drawable {


    private static final SpriteBatch subBatch = new SpriteBatch();
    private final List<Drawable> sources;


    MixedDrawable(List<Drawable> sources) {
        this.sources = sources;
    }

    @Override
    public void draw(SpriteBatch batch, int width, int height, int x, int y) {
        //make backup of batch settings that we temporarily change
        final int srcFunc = batch.getBlendSrcFunc();
        final int dstFunc = batch.getBlendDstFunc();
        final boolean blendingEnabled = batch.isBlendingEnabled();
        final Color colorbackup = batch.getColor().cpy();
        //stop drawing to the screen
        batch.end();
        //draw separate parts (using a framebuffer) to textures
        List<Texture> textureSet = getParts(width, height);
        //start drawing to screen again
        batch.begin();
        //for the first image drawn, it should completely ignore already present colors.
        //Therefore, the source (first) image is multiplied by 1 and the destination (already present colors) by 0.
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
        batch.enableBlending();
        //since our blending is additive, we first divide each color channel by the number of textures to render
        batch.setColor(Color.BLACK.cpy().lerp(batch.getColor(), 1f / textureSet.size()));
        //draw first texture
        batch.draw(textureSet.get(0), x, y, width, height);
        //Now we do not want to ignore already present colors anymore. Switch to additive blending.
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
        //draw all other textures
        textureSet.subList(1, textureSet.size()).forEach(texture -> batch.draw(texture, x, y, width, height));
        //revert settings to made backup
        batch.setColor(colorbackup);
        batch.setBlendFunction(srcFunc, dstFunc);
        if (!blendingEnabled) {
            batch.disableBlending();
        }
    }


    @SuppressWarnings("GDXJavaFlushInsideLoop")
    @NotNull
    private List<Texture> getParts(int width, int height) {
        List<Texture> textures = new ArrayList<>();
        OrthographicCamera cam1 = new OrthographicCamera();
        cam1.setToOrtho(true, width, height);
        cam1.update();
        subBatch.setProjectionMatrix(cam1.combined);
        for (Drawable source : sources) {
            FrameBuffer buffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
            buffer.begin();
            subBatch.begin();
            source.draw(subBatch, width, height, 0, 0);
            subBatch.end();
            buffer.end();
            textures.add(buffer.getColorBufferTexture());
        }
        return textures;
    }
}
