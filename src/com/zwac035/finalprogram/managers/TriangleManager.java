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
import com.jme3.scene.Node;
import com.zwac035.finalprogram.Player;
import com.zwac035.finalprogram.Res;
import com.zwac035.finalprogram.box.Triangle;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Lewis Chun
 */
public class TriangleManager extends LevelChangeManager implements Manager {

    private TrianglePool pool;
    private Node triangleNode;
    private Node particleNode;
    private Node root;
    private ArrayList<Triangle> trianglesOnScreen;
    private int rndSteerPos;
    private float rndSteer;
    
    public TriangleManager(Node rootNode){
        root = rootNode;
    }
    
    public void update(int distanceTravelled) {
        checkForRemoval(distanceTravelled);
        Triangle t = null;
        // If we are going through a level
        if(this.currentLevel != null){
            // Get the triangles level location
            Vector3f loc = currentLevel.getTriangleLoc(distanceTravelled);
            if(loc != null){
                // loc.z acts as the triangles value, as the z coord is fixed.
                t = pool.getTriangle((int) loc.z, (int) loc.x, (int) (loc.y + currentLevel.getStartPoint()));
            }
        } else {
            // Randomly pick a triangle, and a location for it
            if(Res.rnd.nextFloat() < rndSteer){
                rndSteerPos += Res.rnd.nextInt(5) - 2;
                if(rndSteerPos < -5) rndSteerPos = -5;
                else if(rndSteerPos > 5) rndSteerPos = 5;
                t = pool.getTriangle(1, rndSteerPos, distanceTravelled);
                rndSteer -= 0.15f;
            } else {
                rndSteer = 0.75f;
            }
        }
        if(t != null){
            triangleNode.attachChild(t.getGeometry());
            particleNode.attachChild(t.getParticles());
            trianglesOnScreen.add(t);
        }
    }

    public void removeAll() {
    }

    public void initialise() {
        // Create the pool and nodes
        pool = new TrianglePool(10);
        trianglesOnScreen = new ArrayList<Triangle>();
        triangleNode = new Node("Triangles");
        root.attachChild(triangleNode);
        particleNode = new Node("tparts");
        root.attachChild(particleNode);
    }
    
    public void removeTriangle(Triangle t){
        triangleNode.detachChild(t.getGeometry());
        pool.addToPool(t);
        t.getParticles().removeFromParent();
    }
    
    public void checkForRemoval(int distanceTravelled){
        // Check to see if any triangles are out of view and should be removed
        Iterator<Triangle> iterator = trianglesOnScreen.iterator();
        while(iterator.hasNext()){
            Triangle t = iterator.next();
            Vector3f loc = t.getLocation();
            if(isOffScreen(loc, distanceTravelled)){
                removeTriangle(t);
                iterator.remove();
            }
        }
    }
    
    public boolean isTouchingBall(Vector3f loc){
        float dist = loc.distance(Player.getInstance().getLocation());
        return dist <= 1.5f;
    }
    
    public boolean isOffScreen(Vector3f loc, int distanceTravelled){
        return (loc.z < -5 || loc.x > 10 || loc.x < -10 || loc.y - distanceTravelled < -26);
    }

    public void quickUpdate() {
        // Check if the player is collecting any of the triangles
        Iterator<Triangle> iterator = trianglesOnScreen.iterator();
        while(iterator.hasNext()){
            Triangle t = iterator.next();
            Vector3f tLoc = t.getLocation();
            if(isTouchingBall(tLoc)){
                Player.getInstance().increaseCollectedTriangles((int) tLoc.z);
                removeTriangle(t);
                iterator.remove();
            }
        }
    }
    
}
