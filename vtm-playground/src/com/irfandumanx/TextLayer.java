package com.irfandumanx;

import org.oscim.core.MapPosition;
import org.oscim.event.Event;
import org.oscim.layers.Layer;
import org.oscim.map.Map;
import org.oscim.renderer.BitmapRenderer;
import org.oscim.scalebar.MapScaleBarRenderer;

public class TextLayer extends Layer implements Map.UpdateListener {
    private final Text mapScaleBar;
    private final BitmapRenderer bitmapRenderer;

    public TextLayer(Map map, Text mapScaleBar) {
        super(map);
        this.mapScaleBar = mapScaleBar;

        mRenderer = bitmapRenderer = new MapScaleBarRenderer();
        bitmapRenderer.setBitmap(mapScaleBar.mapScaleBitmap, mapScaleBar.mapScaleBitmap.getWidth(), mapScaleBar.mapScaleBitmap.getHeight());
    }

    @Override
    public BitmapRenderer getRenderer() {
        return bitmapRenderer;
    }

    @Override
    public void onMapEvent(Event e, MapPosition mapPosition) {
        if (e == Map.UPDATE_EVENT)
            return;

        if (mapScaleBar.mapScaleBitmap == null)
            return;

        if (mMap.getHeight() == 0)
            return;

        synchronized (mapScaleBar.mapScaleBitmap) {
            mapScaleBar.drawScaleBar(mapPosition);
        }

        bitmapRenderer.updateBitmap();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
