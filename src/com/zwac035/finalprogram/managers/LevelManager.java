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

import com.jme3.math.Vector3f;
import com.zwac035.finalprogram.box.BoxType;
import com.zwac035.finalprogram.FloorType;
import com.zwac035.finalprogram.Level;
import com.zwac035.finalprogram.Res;
import static com.zwac035.finalprogram.managers.LevelChangeObservable.observers;

/**
 * Class that tells the box and floor managers where and when to spawn their
 * objects.
 * @author Lewis Chun
 */
public class LevelManager implements Manager, LevelChangeObservable {
    
    private Level currentLevel = null;
    private Level simpleLevel;
    private Level explodingLevel;
    private Level tutorialLevel;
    private boolean tutorialLevels;
    private float rndSteer = 0;
    
    public LevelManager(boolean tutorialLevels){
        this.tutorialLevels = tutorialLevels;
    }
    
    public void update(int distanceTravelled){
        if(!isActive()){
            if(tutorialLevels){
                currentLevel = tutorialLevel;
                int start = ((int) distanceTravelled / 4) * 4;
                currentLevel.setStartPoint(start);
            } else if(Res.rnd.nextFloat() < rndSteer){
                // Pick a random level
                if(Res.rnd.nextFloat() < 0.7d) {
                    currentLevel = simpleLevel;
                } else {
                    currentLevel = explodingLevel;
                }
                int start = ((int) distanceTravelled / 4) * 4;
                currentLevel.setStartPoint(start);
                rndSteer = 0;
            } else {
                rndSteer = rndSteer > 0.5 ? rndSteer : rndSteer + (Res.rnd.nextFloat() / 40);
            }
        } else if(distanceTravelled >= currentLevel.getStartPoint() + currentLevel.getLevelLength()){
            currentLevel = null;
        }
        // Update the observers on the status of the current level
        notifyObservers();
    }
    
    public Level getCurrentLevel(){
        return currentLevel;
    }
    
    public boolean isActive(){
        return currentLevel != null;
    }
    
    public void initialise(){
        if(tutorialLevels){
            createTutorialLevel();
        } else {
            createSimpleLevel();
            createExplodingLevel();
        }
    }
    
    private void createSimpleLevel(){
        simpleLevel = new Level(5 * 4);
        simpleLevel.setBoxData(new Vector3f[] {
                                    new Vector3f(-5, 0, 1),
                                    new Vector3f(-2, 0, 1),
                                    new Vector3f(-4, 16, 1),
                                    new Vector3f(4, 16, 1) }, 
                                new BoxType[] {
                                    BoxType.EXPLOSION,
                                    BoxType.PLAIN,
                                    BoxType.PLAIN,
                                    BoxType.MAGNET });

        simpleLevel.setFloorPieces(new FloorType[] {
                                    FloorType.X4_HOLE,
                                    FloorType.X3_HOLE,
                                    FloorType.X2_HOLE,
                                    FloorType.X1_HOLE,
                                    FloorType.CENTRE_HOLE });
        simpleLevel.setTriangleLocations(new Vector3f[] {
                                    new Vector3f(4, 12, 1),
                                    new Vector3f(4, 8, 1)
        });
    }
    
    private void createExplodingLevel(){
        explodingLevel = new Level(5 * 4);
        explodingLevel.setBoxData(new Vector3f[] {
                    new Vector3f(-4, 0, 1),
                    new Vector3f(-1, 0, 1),
                    new Vector3f(4, 0, 1),
                                       
                    new Vector3f(-4, 8, 1),
                    new Vector3f(1, 8, 1),
                    new Vector3f(4, 8, 1),
                                     
                    new Vector3f(-4, 16, 1),
                    new Vector3f(-1, 16, 1),
                    new Vector3f(4, 16, 1)
            }, new BoxType[] {
                    BoxType.EXPLOSION,
                    BoxType.EXPLOSION,
                    BoxType.EXPLOSION,
                    BoxType.EXPLOSION,
                    BoxType.EXPLOSION,
                    BoxType.EXPLOSION,
                    BoxType.EXPLOSION,
                    BoxType.EXPLOSION,
                    BoxType.EXPLOSION
            });
        explodingLevel.setFloorPieces(new FloorType[] {
                    FloorType.PLAIN,
                    FloorType.PLAIN,
                    FloorType.PLAIN,
                    FloorType.PLAIN,
                    FloorType.PLAIN
        });
    }

    public void removeAll() {
        explodingLevel = null;
        simpleLevel = null;
        tutorialLevel = null;
    }
    
    private void createTutorialLevel(){
        tutorialLevel = new Level(15 * 4);
        tutorialLevel.setBoxData(
                new Vector3f[] {
                    new Vector3f(0, 0, 1),
                    new Vector3f(-4, 44, 1),
                    new Vector3f(0, 44, 1),
                    new Vector3f(4, 44, 1)
                }, 
                new BoxType[] {
                    BoxType.PLAIN,
                    BoxType.EXPLOSION,
                    BoxType.MAGNET,
                    BoxType.HEAVY
                });
        tutorialLevel.setFloorPieces(new FloorType[] {
                    FloorType.PLAIN,
                    FloorType.PLAIN,
                    FloorType.PLAIN,
                    FloorType.PLAIN,
                    FloorType.PLAIN,
                    FloorType.CENTRE_HOLE,
                    FloorType.PLAIN,
                    FloorType.PLAIN,
                    FloorType.PLAIN,
                    FloorType.PLAIN,
                    FloorType.PLAIN,
                    FloorType.PLAIN,
                    FloorType.PLAIN,
                    FloorType.PLAIN,
                    FloorType.PLAIN
        });
    }

    public void registerLevelChangeObserver(LevelChangeObserver lco) {
        observers.add(lco);
    }

    public void unregisterLevelChangerObserver(LevelChangeObserver lco) {
        observers.remove(lco);
    }

    public void notifyObservers() {
        for(LevelChangeObserver lo: observers){
            lo.onLevelChange(currentLevel);
        }
    }

    public void quickUpdate() {
        // Nothing needs to be done every frame
    }
    
}
