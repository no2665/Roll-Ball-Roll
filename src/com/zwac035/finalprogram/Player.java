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

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author Lewis Chun
 */
public class Player {
    private static Player instance = null;
    private RigidBodyControl playerControl;
    private Geometry playerGeom;
    private int collectedTriangles;
    private String skinName;
    
    private Player(){ 
    }
    
    public static Player getInstance(){
        if(instance == null){
            instance = new Player();
        }
        return instance;
    }
    
    public void loadPlayer(Node root, PhysicsSpace physics){
        // Set up the ball controlled by the user
        collectedTriangles = 0;
        // As this is a singleton class, nothing gets deallocated until the
        // process is destroyed, so we only have to load everything the first
        // time the game is initialised.
        if(playerGeom == null){
            Sphere playerSphere = new Sphere(20, 20, 1);
            playerGeom = new Geometry("Sphere", playerSphere);
            playerGeom.setName("ball");
            playerGeom.setLocalTranslation(0, 0, 1);
            playerControl = new RigidBodyControl(new SphereCollisionShape(1), 1f);
            // Set the sphere to collide with the boxesNode and the invisible planes
            playerControl.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_03 | 
                                                PhysicsCollisionObject.COLLISION_GROUP_02 | 
                                                PhysicsCollisionObject.COLLISION_GROUP_01);
            playerGeom.addControl(playerControl);
        } else {
            setEnableMovement(true);
        }
        
        setMaterial();
        
        root.attachChild(playerGeom);
        physics.add(playerGeom);
        
    }
    
    public RigidBodyControl getPhysicsControl(){
        return playerControl;
    }
    
    public Geometry getGeometry(){
        return playerGeom;
    }
    
    public void moveTo(float x, float y, float z){
        playerControl.setEnabled(false);
        playerGeom.setLocalTranslation(x, y, z);
        playerControl.setEnabled(true);
    }
    
    public void setEnableMovement(boolean move){
        playerControl.setAngularVelocity(Vector3f.ZERO);
        playerControl.setLinearVelocity(Vector3f.ZERO);
        playerControl.clearForces();
        playerControl.setEnabled(move);
    }

    public Vector3f getLocation(){
        return playerGeom.getLocalTranslation();
    }
    
    public void increaseCollectedTriangles(int value){
        collectedTriangles += value;
    }
    
    public int getCollectedTriangles(){
        return collectedTriangles;
    }
    
    public void setSkin(String s){
        skinName = s;
    }
    
    private void setMaterial(){
        Material sphereMat = new Material(Res.assets, "Common/MatDefs/Light/Lighting.j3md");
        sphereMat.setBoolean("UseMaterialColors",true); 
        if(skinName.equals("Red")) {
            sphereMat.setColor("Diffuse", ColorRGBA.Red);
            sphereMat.setColor("Ambient", ColorRGBA.Red.mult(ColorRGBA.Black).mult(0.5f));
            sphereMat.setColor("Specular", ColorRGBA.White);
        } else if(skinName.equals("Blue")) {
            sphereMat.setColor("Diffuse", ColorRGBA.Blue);
            sphereMat.setColor("Ambient", ColorRGBA.Blue.mult(ColorRGBA.Black).mult(0.5f));
            sphereMat.setColor("Specular", ColorRGBA.White);
        } else if(skinName.equals("Green")) {
            sphereMat.setColor("Diffuse", ColorRGBA.Green);
            sphereMat.setColor("Ambient", ColorRGBA.Green.mult(ColorRGBA.Black).mult(0.5f));
            sphereMat.setColor("Specular", ColorRGBA.White);
        } else if(skinName.equals("Basket")) {
            sphereMat.setTexture("DiffuseMap", Res.basketBallTexture);
            sphereMat.setColor("Diffuse", ColorRGBA.White);
            sphereMat.setColor("Ambient", ColorRGBA.White);
            sphereMat.setColor("Specular", ColorRGBA.White);
        } else if(skinName.equals("Beach")) {
            sphereMat.setTexture("DiffuseMap", Res.beachBallTexture);
            sphereMat.setColor("Diffuse", ColorRGBA.White);
            sphereMat.setColor("Ambient", ColorRGBA.White);
            sphereMat.setColor("Specular", ColorRGBA.White);
        }
        playerGeom.setMaterial(sphereMat);
    }
    
}
