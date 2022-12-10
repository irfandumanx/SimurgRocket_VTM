/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 Izumi Kawashima
 * Copyright 2017 Longri
 * Copyright 2017-2020 devemux86
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
package org.oscim.layers.marker;

/**
 * The internal representation of a marker.
 */
public class InternalItem {

    public MarkerInterface item;
    public boolean visible;
    public boolean changes;
    public float x, y;
    public double px, py;
    public float dy;

    @Override
    public String toString() {
        return "\n" + x + ":" + y + " / " + dy + " " + visible;
    }
}
