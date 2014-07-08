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
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.zwac035.finalprogram.box.BoxType;
import com.zwac035.finalprogram.box.ExplodingBox;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Class that spawns and deletes justBoxes as needed
 * @author Lewis Chun
 */
public class BoxManager extends LevelChangeManager implements Manager {
    
    private Node root, justBoxes, particles;
    private PhysicsSpace physics;
    private BoxPool pool;
    
    public BoxManager(Node rootNode, PhysicsSpace physics){
        root = rootNode;
        this.physics = physics;   
    }
    
    public void initialise(){
        // Create the pool and nodes
        justBoxes = new Node("JustBoxes");
        root.attachChild(justBoxes);
        particles = new Node("particles");
        root.attachChild(particles);
        pool = new BoxPool(10, 10, physics);
    }
    
    public void update(int distanceTravelled){
        checkForRemoval(distanceTravelled);
        
        if(currentLevel != null){
            // Get the boxes at the current distance along the level
            HashMap<Vector3f, BoxType> boxes = currentLevel.getBoxesToSpawn(distanceTravelled);
            if(boxes == null) return;
            Iterator<Entry<Vector3f, BoxType>> i = boxes.entrySet().iterator();
            // Go through the set, adding the boxes to the scene
            while(i.hasNext()){
                Entry<Vector3f, BoxType> entry = i.next();
                Vector3f key = entry.getKey();
                Spatial newBox = null;
                switch(entry.getValue()) {
                    case PLAIN:
                        newBox = pool.takePlainBox(key.x, currentLevel.getStartPoint() + key.y, key.z);
                        break;
                    case EXPLOSION:
                        newBox = pool.takeExplodingBox(key.x, currentLevel.getStartPoint() + key.y, key.z);
                        break;
                    case MAGNET:
                        newBox = pool.takeMagnetBox(key.x, currentLevel.getStartPoint() + key.y, key.z);
                        break;
                    case HEAVY: 
                        newBox = pool.takeHeavyBox(key.x, currentLevel.getStartPoint() + key.y, key.z);
                        break;
                }
                if(newBox != null){
                    physics.add(newBox);
                    justBoxes.attachChild(newBox);
                }
            }

        } else {
            // Spawn some more random boxes.
            if(Math.random() < 0.5d){
                Spatial newBox;
                double r = Math.random();
                if(r < 0.4d){
                    newBox = pool.takePlainBox((float) ((Math.random()*10)-5), distanceTravelled, 2);
                } else if(r < 0.6d) {
                    newBox = pool.takeExplodingBox((float) ((Math.random()*10)-5), distanceTravelled, 2);
                } else if(r < 0.8d){
                    newBox = pool.takeMagnetBox((float) ((Math.random()*10)-5), distanceTravelled, 2);
                } else {
                    newBox = pool.takeHeavyBox((float) ((Math.random()*10)-5), distanceTravelled, 1);
                }
                if(newBox != null){
                    physics.add(newBox);
                    justBoxes.attachChild(newBox);
                }
            }
        }
        
    }
    
    public void checkForRemoval(int distanceTravelled){
        // Check to see if any boxes are out of view and should be removed
        for(Spatial s: justBoxes.getChildren()){
            ExplodingBox eb = s.getControl(ExplodingBox.class);
            if(isOffScreen(s, distanceTravelled)){
                removeBox(s);
            } else if(eb != null && eb.shouldRemove()){
                removeBox(s);
            }
        }
        // Check if any particle emitters are off screen 
        for(Spatial s: particles.getChildren()){
            if(isOffScreen(s, distanceTravelled)){
                s.removeFromParent();
            }
        }
    }
    
    public boolean isOffScreen(Spatial s, int distanceTravelled){
        Vector3f sLoc = s.getLocalTranslation();
        return (sLoc.z < -5 || sLoc.x > 10 || sLoc.x < -10 || sLoc.y - distanceTravelled < -26);
    }
    
    public void removeAll(){
        physics.removeAll(justBoxes);
        justBoxes.detachAllChildren();
        particles.detachAllChildren();
    }
    
    public void removeBox(Spatial b){
        justBoxes.detachChild(b);
        physics.removeAll(b);
        pool.addToPool(b);
    }

    public void quickUpdate() {
        // Nothing needs to be done every frame
    }
    
}
