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
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.control.LightControl;
import com.jme3.scene.shape.Box;

/**
 * Sets up the camera of the game
 * @author Lewis Chun
 */
public class GameCamera {
    
    /**
     * The only instance ever made
     */
    private static GameCamera instance = null;
    private RigidBodyControl centralNodeControl;
    private Node centralNode;
    /**
     * The name associated with centralNode. Static to allow other classes
     * to recognise the node quicker.
     */
    public static String nodeName = "c";
    
    /**
     * private to make this a singleton class
     */
    private GameCamera(){
        
    }
    
    public static GameCamera getInstance(){
        if(instance == null){
            instance = new GameCamera();
        } 
        return instance;
    }
    
    /**
     * Initialises and sets up the camera. This should be called as soon as 
     * possible
     * @param root The node to attach the camera to 
     * @param physics The physics of the physics engine
     * @param cam The camera that we are going to position here
     */
    public void loadCamera(Node root, PhysicsSpace physics, Camera cam){        
        // Create a node that is at the origin
        centralNode = new Node(nodeName);
        // Create a physics control, that we can move the node with
        centralNodeControl = new RigidBodyControl(1);
        // Add the control
        centralNode.addControl(centralNodeControl);
        
        // Set up camera
        CameraNode camNode = new CameraNode("cam", cam);
        // The node moves the camera, instead of the camera moving the node
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        // Move it back and up a bit
        camNode.setLocalTranslation(new Vector3f(0, -10, 20));
        //camNode.setLocalTranslation(new Vector3f(0, -10, 50));
        // Look at the origin
        camNode.lookAt(centralNode.getLocalTranslation(), Vector3f.UNIT_Z);
        
        // Attach the camera to the node at the origin, so that the camera 
        // follows the node
        centralNode.attachChild(camNode);
        
        // Plane that stays at the top of the screen to stop the ball rolling 
        // out of sight
        Plane upperPlane = new Plane(Vector3f.UNIT_Y.negate(), -11f);
        RigidBodyControl upperCollision = new RigidBodyControl(
                                    new PlaneCollisionShape(upperPlane), 1f);
        Node upperPlaneNode = new Node("uplane");
        upperPlaneNode.addControl(upperCollision);
        // And another plane for bottom of the screen
        Plane lowerPlane = new Plane(Vector3f.UNIT_Y, -19f);
        RigidBodyControl lowerCollision = new RigidBodyControl(
                                    new PlaneCollisionShape(lowerPlane), 1f);
        Node lowerPlaneNode = new Node("lplane");
        lowerPlaneNode.addControl(lowerCollision);
        // Put the planes into their own group so that boxes do not collide with them
        upperCollision.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        lowerCollision.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        // Makes the planes un-affected by forces
        upperCollision.setKinematic(true);
        lowerCollision.setKinematic(true);
        
        // Attach the planes to the central point that the camera looks at
        // That way we can move them both by moving the central point.
        centralNode.attachChild(upperPlaneNode);
        centralNode.attachChild(lowerPlaneNode);
        
        // For debug purposes, put a box infront of the center point
        /*Box box = new Box(0.5f, 0.5f, 0.5f);
        Geometry debugBox = new Geometry("Debug Box", box);
        Material debugMat = 
                new Material(Res.assets, "Common/MatDefs/Misc/Unshaded.j3md");
        debugMat.setColor("Color", ColorRGBA.White);
        debugBox.setMaterial(debugMat);
        debugBox.setName("debug");
        // Distance from the centre of the screen, to when the floor goes 
        // offscreen
        float centerOffset = 4 * 4;
        debugBox.setLocalTranslation(Vector3f.UNIT_Y.mult(centerOffset));
        
        centralNode.attachChild(debugBox);*/
        
        // Add some lighting
        PointLight light = new PointLight();
        light.setColor(ColorRGBA.White);
        //light.setRadius(30);
        LightControl lightControl = new LightControl(light);
        // Needs to be added to the camera, as a point in space
        camNode.addControl(lightControl);
        // And added to the root, to have an effect on everything
        root.addLight(light);
        
        // Finally, add the centre point to the root node
        root.attachChild(centralNode);
        
        // And add the planes and the centre point to the physics space
        physics.add(upperPlaneNode);
        physics.add(lowerPlaneNode);
        physics.add(centralNode);
        
        centralNodeControl.setGravity(Vector3f.ZERO); // No gravity
        centralNodeControl.setLinearVelocity(Vector3f.UNIT_Y); // Moves forward
    }
    
    public RigidBodyControl getPhysicsControl(){
        return centralNodeControl;
    }
    public Node getCamNode(){
        return centralNode;
    }
    
    /**
     * Moves the camera to (0, y, 0)
     * @param y 
     */
    public void moveTo(float y){
        centralNodeControl.setEnabled(false);
        centralNode.setLocalTranslation(0, y, 0);
        centralNodeControl.setEnabled(true);
        // Re-enabling the control resets the gravity,
        // so we have to set it back to zero.
        centralNodeControl.setGravity(Vector3f.ZERO);
    }
    
    /**
     * Sets the speed at which the camera should move forward
     */
    public void setSpeed(float speed){
        centralNodeControl.setLinearVelocity(new Vector3f(0, speed, 0));
    }
    
    /**
     * Gets the speed at which the camera is moving forward
     */
    public float getSpeed(){
        return centralNodeControl.getLinearVelocity().y;
    }
}
