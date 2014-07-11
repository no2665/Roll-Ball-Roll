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

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.zwac035.finalprogram.FloorType;
import com.zwac035.finalprogram.Res;
import java.util.ArrayList;

/**
 * Class the spawns and deletes the floor pieces as needed.
 * @author Lewis Chun
 */
public class FloorManager extends LevelChangeManager implements Manager {
 
    private Node rootNode;
    private PhysicsSpace physics;
    private int floorHeight = 4;
    private ArrayList<Spatial> listOfPieces;
    private ColorRGBA floorColour, beginColour, finalColour;
    private FloorPool pool;
    
    public FloorManager(Node root, PhysicsSpace physics){
        rootNode = root;
        this.physics = physics;
        beginColour = ColorRGBA.randomColor();
        floorColour = beginColour;
        finalColour = ColorRGBA.randomColor();
        listOfPieces = new ArrayList<Spatial>();
    }
    
    public void initialise(){
        // Create pool, and build the start floor
        pool = new FloorPool(10, 3);
        for(int i = -4; i < 5; i++){
            Spatial newPiece = pool.takePlainPeice(0, i * floorHeight, floorColour.clone());
            changeFloorColour();
            listOfPieces.add(newPiece);
            rootNode.attachChild(newPiece);
            physics.add(newPiece);
        }
    }
     
    public void update(int distanceTravelled){
        Spatial newPiece;
        if(currentLevel != null){
            FloorType f = currentLevel.getFloorPiece(distanceTravelled);
            newPiece = pool.takePeice(0, distanceTravelled, floorColour.clone(), f);
        } else {
            // Pick a random piece
            if(Res.rnd.nextFloat() < 0.2f){
                newPiece = pool.takePeice(0, distanceTravelled, floorColour.clone(), FloorType.random());
            } else {
                newPiece = pool.takePlainPeice(0, distanceTravelled, floorColour.clone());
            }
        }
        
        listOfPieces.add(newPiece);
        rootNode.attachChild(newPiece);
        physics.add(newPiece);
        
        changeFloorColour();

        // Delete the nodes going offscreen
        Spatial s = listOfPieces.remove(0);
        rootNode.detachChild(s);
        physics.remove(s);
        pool.addToPool(s);
    }
    
    private float percent = 0.1f;
    
    private void changeFloorColour(){
        floorColour.interpolate(beginColour, finalColour, percent);
        percent += 0.1f;
        if(percent >= 1){
            percent = 0;
            beginColour = finalColour;
            floorColour = beginColour;
            finalColour = ColorRGBA.randomColor();
        }
    }
    
    public void removeAll(){
        physics.removeAll(rootNode);
        rootNode.detachAllChildren();
        listOfPieces.clear();
    }

    public void quickUpdate() {
        // Nothing needs to be done every frame
    }
}
