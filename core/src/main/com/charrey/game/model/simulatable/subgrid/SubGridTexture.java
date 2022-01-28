package com.charrey.game.model.simulatable.subgrid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.charrey.game.model.simulatable.EdgeType;
import com.charrey.game.texture.CachedTexture;

import java.util.EnumMap;
import java.util.Map;

/**
 * Non-instantiatable class used to supply textures to the Subgrid Simulatable. This is decoupled for this class
 */
public final class SubGridTexture {

    private static final Color backGround = new Color(0.1f, 0.1f, 0.1f, 1);
    private static final Color border = new Color(0f, 0f, 0f, 1);
    private static final Color edgeExported = new Color(1f, 1f, 1f, 1);
    private static final Color edgePadded = new Color(0.3f, 0.3f, 0.3f, 1);
    private static final Map<EdgeType, Map<EdgeType, Map<EdgeType, Map<EdgeType, CachedTexture>>>> textures = new EnumMap<>(EdgeType.class);

    private SubGridTexture() {
    }

    /**
     * Returns a CachedTexture that draws a specific block of a (multi-block) SubGrid.
     *
     * @param top    what kind of edge to draw at the top
     * @param right  what kind of edge to draw at the right
     * @param bottom what kind of edge to draw at the bottom
     * @param left   what kind of edge to draw at the left
     * @return Texture to draw
     */
    public static CachedTexture getTexture(EdgeType top, EdgeType right, EdgeType bottom, EdgeType left) {
        textures.computeIfAbsent(top, x -> new EnumMap<>(EdgeType.class));
        textures.get(top).computeIfAbsent(right, x -> new EnumMap<>(EdgeType.class));
        textures.get(top).get(right).computeIfAbsent(bottom, x -> new EnumMap<>(EdgeType.class));
        textures.get(top).get(right).get(bottom).computeIfAbsent(left, x -> generateCachedTexture(top, right, bottom, left));
        return textures.get(top).get(right).get(bottom).get(left);
    }

    private static CachedTexture generateCachedTexture(EdgeType top, EdgeType right, EdgeType bottom, EdgeType left) {
        return new CachedTexture() {
            @Override
            protected void computeTexture(Pixmap pixels) {
                pixels.setColor(backGround);
                pixels.fill();
                pixels.setColor(border);
                pixels.drawRectangle(0, 0, pixels.getWidth(), pixels.getHeight());

                int outerXStart = Math.round(pixels.getWidth() / 5f);
                int outerXEnd = pixels.getWidth() - outerXStart;
                int outerYStart = Math.round(pixels.getHeight() / 5f);
                int outerYEnd = pixels.getHeight() - outerYStart;

                int innerXStart = outerXStart + 10;
                int innerXEnd = outerXEnd - 10;
                int innerYStart = outerYStart + 10;
                int innerYEnd = outerYEnd - 10;

                if (top != EdgeType.EMPTY) {
                    int startx = 0;
                    int endx = pixels.getWidth();
                    pixels.setColor(top == EdgeType.EXPORT ? edgeExported : edgePadded);
                    if (left != EdgeType.EMPTY) {
                        startx = innerXStart;
                        pixels.fillTriangle(innerXStart, outerYStart, outerXStart, outerYStart, innerXStart, innerYStart);
                    }
                    if (right != EdgeType.EMPTY) {
                        endx = innerXEnd;
                        pixels.fillTriangle(innerXEnd, outerYStart, outerXEnd, outerYStart, innerXEnd, innerYStart);
                    }
                    pixels.fillRectangle(startx, outerYStart, endx - startx, 10);
                }
                if (left != EdgeType.EMPTY) {
                    int starty = 0;
                    int endy = pixels.getHeight();
                    pixels.setColor(left == EdgeType.EXPORT ? edgeExported : edgePadded);
                    if (top != EdgeType.EMPTY) {
                        starty = innerYStart;
                        pixels.fillTriangle(outerXStart, innerYStart, innerXStart, innerYStart, outerXStart, outerYStart);
                    }
                    if (bottom != EdgeType.EMPTY) {
                        endy = innerYEnd;
                        pixels.fillTriangle(outerXStart, innerYEnd, innerXStart, innerYEnd, outerXStart, outerYEnd);
                    }
                    pixels.fillRectangle(outerXStart, starty, 10, endy - starty);
                }
                if (right != EdgeType.EMPTY) {
                    int starty = 0;
                    int endy = pixels.getHeight();
                    pixels.setColor(right == EdgeType.EXPORT ? edgeExported : edgePadded);
                    if (top != EdgeType.EMPTY) {
                        starty = innerYStart;
                        pixels.fillTriangle(outerXEnd, innerYStart, innerXEnd, innerYStart, outerXEnd, outerYStart);
                    }
                    if (bottom != EdgeType.EMPTY) {
                        endy = innerYEnd;
                        pixels.fillTriangle(outerXEnd, innerYEnd, innerXEnd, innerYEnd, outerXEnd, outerYEnd);
                    }
                    pixels.fillRectangle(innerXEnd, starty, 10, endy - starty);
                }
                if (bottom != EdgeType.EMPTY) {
                    int startx = 0;
                    int endx = pixels.getWidth();
                    pixels.setColor(bottom == EdgeType.EXPORT ? edgeExported : edgePadded);
                    if (left != EdgeType.EMPTY) {
                        startx = innerXStart;
                        pixels.fillTriangle(innerXStart, innerYEnd, outerXStart, outerYEnd, innerXStart, outerYEnd);
                    }
                    if (right != EdgeType.EMPTY) {
                        endx = innerXEnd;
                        pixels.fillTriangle(innerXEnd, innerYEnd, outerXEnd, outerYEnd, innerXEnd, outerYEnd);
                    }
                    pixels.fillRectangle(startx, innerYEnd, endx - startx, 10);
                }
            }
        };
    }
}
