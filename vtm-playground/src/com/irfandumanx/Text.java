package com.irfandumanx;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.core.MapPosition;
import org.oscim.map.Map;
import org.oscim.scalebar.DistanceUnitAdapter;
import org.oscim.scalebar.MapScaleBar;
import org.oscim.scalebar.MetricUnitAdapter;

public abstract class Text {
    private int width;
    private int height;
    private final MapPosition currentMapPosition = new MapPosition();
    protected DistanceUnitAdapter distanceUnitAdapter;
    protected final Map map;
    protected Bitmap mapScaleBitmap;
    protected Canvas mapScaleCanvas;
    protected final MapPosition prevMapPosition = new MapPosition();
    protected boolean redrawNeeded;
    protected final float scale;
    private boolean visible;

    public Text(Map map, int width, int height, float scale) {
        this.map = map;
        this.mapScaleBitmap = CanvasAdapter.newBitmap(width, height, 0);
        this.scale = scale;

        this.width = width;
        this.height = height;

        this.mapScaleCanvas = CanvasAdapter.newCanvas();
        this.mapScaleCanvas.setBitmap(this.mapScaleBitmap);
        this.distanceUnitAdapter = MetricUnitAdapter.INSTANCE;
        this.visible = true;
        this.redrawNeeded = true;
    }
    public void draw(Canvas canvas, MapPosition pos) {
        if (!this.visible) {
            return;
        }

        if (this.map.getHeight() == 0) {
            return;
        }

        if (this.isRedrawNecessary()) {
            redraw(this.mapScaleCanvas, pos);
            this.redrawNeeded = false;
        }

        canvas.drawBitmap(this.mapScaleBitmap, 500, 500);
    }

    /**
     * The scalebar is redrawn now.
     */
    public void drawScaleBar(MapPosition pos) {
        draw(mapScaleCanvas, pos);
    }

    /**
     * The scalebar will be redrawn on the next draw()
     */
    public void redrawScaleBar() {
        this.redrawNeeded = true;
    }

    /**
     * Determines if a redraw is necessary or not
     *
     * @return true if redraw is necessary, false otherwise
     */
    protected boolean isRedrawNecessary() {
        if (this.redrawNeeded) {
            return true;
        }

        this.map.getMapPosition(this.currentMapPosition);
        if (this.currentMapPosition.getScale() != this.prevMapPosition.getScale()) {
            return true;
        }

        double latitudeDiff = Math.abs(this.currentMapPosition.getLatitude() - this.prevMapPosition.getLatitude());
        return latitudeDiff > 0.2;
    }

    /**
     * Redraw the map scale bar.
     * Make sure you always apply scale factor to all coordinates and dimensions.
     *
     * @param canvas The canvas to draw on
     */
    protected abstract void redraw(Canvas canvas, MapPosition position);
}
