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
import com.jme3.scene.Node;

/**
 *
 * @author Lewis Chun
 */
public class ManagerFacade {
    
    private Manager[] managers;
    private int floorHeight = 4;
    private int previousDistanceTravelled = floorHeight * floorHeight;
    private boolean tutorialLevels;
    
    public ManagerFacade(Node boxNode, Node floorNode, Node triangleNode, 
                            PhysicsSpace physics, boolean tutorial){
        tutorialLevels = tutorial;
        managers = new Manager[4];
        LevelManager levels = new LevelManager(tutorialLevels);
        FloorManager floor = new FloorManager(floorNode, physics);
        BoxManager boxes = new BoxManager(boxNode, physics);
        TriangleManager triangles = new TriangleManager(triangleNode);
        
        managers[0] = levels;
        managers[1] = floor;
        managers[2] = boxes;
        managers[3] = triangles;
        
        levels.registerLevelChangeObserver(floor);
        levels.registerLevelChangeObserver(boxes);
        levels.registerLevelChangeObserver(triangles);
        
    }
    
    public void updateManagers(float tpf, float distanceTravelled){
        notifyManagersQuick();
        float diff = distanceTravelled - previousDistanceTravelled;
        if(distanceTravelled - previousDistanceTravelled >= floorHeight){
            // Turn distanceTravelled into a whole number that is a multiple 
            // of floorHeight
            int y = ((int) distanceTravelled / floorHeight) * floorHeight;
            // Check that we won't miss out a section of the floor
            if(diff < floorHeight * 2){
                notifyManagers(tpf, y);
            } else {
                // Else we then need to see how many times floorHeight we are
                // from the previous distance
                int div = (y - (int) previousDistanceTravelled) / floorHeight;
                for(; div > 0; div--){
                    int newY = y - ((div-1) * floorHeight);
                    notifyManagers(tpf, newY);
                }
            }
            previousDistanceTravelled = y;
        }
    }
    
    public void notifyManagersQuick(){
        for(Manager m: managers){
            m.quickUpdate();
        }
    }
    
    public void notifyManagers(float tpf, int dist){
        for(Manager m: managers){
            m.update(dist);
        }
    }
    
    public void initialise(){
        for(Manager m: managers){
            m.initialise();
        }
    }
    
    public void removeEverything(){
        for(Manager m: managers){
            m.removeAll();
        }
    }
}
