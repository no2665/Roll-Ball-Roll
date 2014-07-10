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
package com.zwac035.finalprogram;

import com.zwac035.finalprogram.box.BoxType;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Object that holds data about how a level is set out.
 * @author Lewis Chun
 */
public class Level {
    private float levelStartPoint;
    private float levelLength;
    private FloorType[] floorPieces = null;
    private HashMap<Vector3f, BoxType> boxes;
    private List<Vector3f> triangleLocs;
    
    public Level(float length, Vector3f[] boxLoc, BoxType[] boxTyp, 
            FloorType[] floor, ArrayList<Vector3f> triangles) {
        levelLength = length;
        floorPieces = floor;
        this.triangleLocs = triangles;
        boxes = new HashMap<Vector3f, BoxType>();
        setBoxData(boxLoc, boxTyp);
    }
    
    public Level(float length) {
        levelLength = length;
        boxes = new HashMap<Vector3f, BoxType>();
    }
    
    public final void setBoxData(Vector3f[] boxLocations, BoxType[] boxTypes) {
        if(boxLocations.length != boxTypes.length) return;
        for(int i = 0; i < boxLocations.length; i++){
            boxes.put(boxLocations[i], boxTypes[i]);
        }
    }
    
    public void setBoxMap(HashMap<Vector3f, BoxType> boxMap){
        boxes = boxMap;
    }
    
    public void setFloorPieces(FloorType[] pieces) {
        floorPieces = pieces;
    }
    
    public void setTriangleLocations(Vector3f[] triangles){
        triangleLocs = (List<Vector3f>) Arrays.asList(triangles);
    }
    
    public FloorType[] getFloorPieces() {
        return floorPieces;
    }
    
    public float getStartPoint(){
        return levelStartPoint;
    }
    
    public float getLevelLength(){
        return levelLength;
    }
    
    public void setStartPoint(float start){
        levelStartPoint = start;
    }
    
    public HashMap<Vector3f, BoxType> getBoxesToSpawn(float nearLoc){
        if(boxes.isEmpty()) return null;
        float lowerLoc = nearLoc - 2;
        float upperLoc = nearLoc + 2;
        HashMap<Vector3f, BoxType> newMap = new HashMap<Vector3f, BoxType>();
        // Loop through the map, finding the boxes that close to nearLoc
        Iterator<Vector3f> i = boxes.keySet().iterator();
        while(i.hasNext()) {
            Vector3f v = i.next();
            float vLoc = levelStartPoint + v.y;
            if(vLoc > lowerLoc && vLoc <= upperLoc){
                newMap.put(v, boxes.get(v));
            }
        }
        return newMap;
    }
    
    public FloorType getFloorPiece(float atLoc){
        float diff = atLoc - levelStartPoint;
        int index = (int) (diff / 4f);
        if(floorPieces == null || index >= floorPieces.length){
            return FloorType.PLAIN;
        }
        return floorPieces[index];
    }
    
    public Vector3f getTriangleLoc(float atLoc){
        if(triangleLocs == null || triangleLocs.isEmpty()) return null;
        for(int i = 0; i < triangleLocs.size(); i++){
            Vector3f loc = triangleLocs.get(i);
            float levelY = levelStartPoint + loc.y;
            if(levelY < atLoc + 2 && levelY >= atLoc - 2){
                return loc;
            }
        }
        return null;
    }

}
