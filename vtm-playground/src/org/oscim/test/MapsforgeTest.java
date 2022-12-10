/*
 * Copyright 2016-2022 devemux86
 * Copyright 2018-2019 Gustl22
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.test;

import com.irfandumanx.*;
import org.oscim.backend.canvas.Color;
import org.oscim.core.*;
import org.oscim.event.Event;
import org.oscim.gdx.GdxMap;
import org.oscim.gdx.GdxMapApp;
import org.oscim.gdx.poi3d.Poi3DLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.buildings.S3DBLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.layers.vector.VectorLayer;
import org.oscim.layers.vector.geometries.PointDrawable;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.map.Map;
import org.oscim.renderer.BitmapRenderer;
import org.oscim.renderer.ExtrusionRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.scalebar.*;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.oscim.tiling.source.mapfile.MultiMapFileTileSource;
import org.oscim.utils.ColorUtil;

import java.io.File;
import java.util.*;

public class MapsforgeTest extends GdxMapApp {

    public static double latitude = 40.7135684916949;
    public static double longitude = 29.773173591624182;
    private static Map sMap;
    private final boolean SHADOWS = false;
    private final String mapFile;
    private final boolean poi3d;
    private final boolean s3db;
    private static VectorLayer vectorLayer;
    private static Style.Builder normalPointStyle;
    private static Style.Builder predictPointStyle;

    MapsforgeTest(String mapFile) {
        this(mapFile, true, true);
    }

    MapsforgeTest(String mapFile, boolean s3db, boolean poi3d) {
        this.mapFile = mapFile;
        this.s3db = s3db;
        this.poi3d = poi3d;
        normalPointStyle = Style.builder().buffer(8 / Math.pow(10, 5))
                    .fillColor(Color.BLUE)
                    .fillAlpha(0.2f);
        predictPointStyle = Style.builder().buffer(8 / Math.pow(10, 5))
                .fillColor(Color.RED)
                .fillAlpha(0.2f);
    }

    @Override
    public void createLayers() {
        MultiMapFileTileSource tileSource = new MultiMapFileTileSource();
        MapFileTileSource mapFileTileSource = new MapFileTileSource();
        mapFileTileSource.setMapFile(mapFile);
        tileSource.add(mapFileTileSource);
        //tileSource.setDeduplicate(true);
        //tileSource.setPreferredLanguage("en");

        VectorTileLayer l = mMap.setBaseMap(tileSource);
        loadTheme(null);

        vectorLayer = new VectorLayer(mMap);
        mMap.layers().add(vectorLayer);
        BuildingLayer buildingLayer = s3db ? new S3DBLayer(mMap, l, SHADOWS) : new BuildingLayer(mMap, l, false, SHADOWS);
        mMap.layers().add(buildingLayer);
        if (poi3d)
            mMap.layers().add(new Poi3DLayer(mMap, l));

        mMap.layers().add(new LabelLayer(mMap, l));

        DefaultMapScaleBar mapScaleBar = new DefaultMapScaleBar(mMap);
        mapScaleBar.setScaleBarMode(DefaultMapScaleBar.ScaleBarMode.BOTH);
        mapScaleBar.setDistanceUnitAdapter(MetricUnitAdapter.INSTANCE);
        mapScaleBar.setSecondaryDistanceUnitAdapter(ImperialUnitAdapter.INSTANCE);
        mapScaleBar.setScaleBarPosition(MapScaleBar.ScaleBarPosition.BOTTOM_LEFT);

        MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(mMap, mapScaleBar);
        BitmapRenderer renderer = mapScaleBarLayer.getRenderer();

        Text text = new UseText(mMap, 0.8f);

        TextLayer textLayer = new TextLayer(mMap, text);
        BitmapRenderer textRenderer = textLayer.getRenderer();
        textRenderer.setPosition(GLViewport.Position.TOP_RIGHT);
        textRenderer.setOffset(-150, 0);

        renderer.setPosition(GLViewport.Position.BOTTOM_LEFT);
        renderer.setOffset(5, 0);
        mMap.layers().add(mapScaleBarLayer);
        mMap.layers().add(textLayer);
        //MapPosition pos = MapPreferences.getMapPosition();
        mMap.setMapPosition(40.7135684916949, 29.773173591624182, 1 << 16);
        sMap = mMap;

        if (SHADOWS) {
            final ExtrusionRenderer extrusionRenderer = buildingLayer.getExtrusionRenderer();
            mMap.events.bind(new Map.UpdateListener() {
                Calendar date = Calendar.getInstance();
                long prevTime = System.currentTimeMillis();

                @Override
                public void onMapEvent(Event e, MapPosition mapPosition) {
                    long curTime = System.currentTimeMillis();
                    int diff = (int) (curTime - prevTime);
                    prevTime = curTime;
                    date.add(Calendar.MILLISECOND, diff * 60 * 60); // Every second equates to one hour

                    //extrusionRenderer.getSun().setProgress((curTime % 2000) / 1000f);
                    extrusionRenderer.getSun().setProgress(date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), date.get(Calendar.SECOND));
                    extrusionRenderer.getSun().updatePosition();
                    extrusionRenderer.getSun().updateColor(); // only relevant for shadow implementation

                    mMap.updateMap(true);
                }
            });
        }
    }

    @Override
    public void dispose() {
        //MapPreferences.saveMapPosition(mMap.getMapPosition());
        super.dispose();
    }

    public static void addNormalPoint(float latitude, float longitude) {
        vectorLayer.add(new PointDrawable(latitude,
                longitude,
                normalPointStyle.build()));
        sMap.setMapPosition(latitude, longitude, 1 << 16);
        vectorLayer.update();
    }

    public static void addPredictPoint(float latitude, float longitude) {
        vectorLayer.add(new PointDrawable(latitude,
                longitude,
                predictPointStyle.build()));
        vectorLayer.update();
    }

    void loadTheme(final String styleId) {
        mMap.setTheme(VtmThemes.OSMARENDER);
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new MapsforgeTest("C:\\Users\\erhan\\Desktop\\turkey.map"));
    }
}
