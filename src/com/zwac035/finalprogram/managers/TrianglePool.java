/**
 * Copyright (c) 2014 Lewis Chun
 * Android game in which the user controls a ball to get a high score.
 *
 * This file is part of Roll Ball Roll.
 *
 * Roll Ball Roll is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.zwac035.finalprogram.managers;

import com.zwac035.finalprogram.box.Triangle;
import java.util.ArrayList;

/**
 *
 * @author Lewis Chun
 */
public class TrianglePool {
    private ArrayList<Triangle> triangles;
    
    public TrianglePool(int size){
        triangles = new ArrayList<Triangle>(size);
        // Create the pool
        for(int i = 0; i < size; i++){
            triangles.add(new Triangle());
        }
    }
    
    public Triangle getTriangle(int value, int x, int y){
        if(triangles.isEmpty()){
            return null;
        }
        Triangle t = triangles.remove(0);
        resetTriangle(t, value, x, y);
        return t;
    }
    
    private void resetTriangle(Triangle t, int value, int x, int y){
        t.setValue(value);
        t.moveTo(x, y);
        t.setEnabled(true);
    }
    
    public void addToPool(Triangle t){
        triangles.add(t);
        t.setEnabled(false);
    }
}
