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

import com.zwac035.finalprogram.box.BoxControl;
import com.zwac035.finalprogram.managers.ManagerFacade;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.event.TouchEvent;
import static com.jme3.input.event.TouchEvent.Type.DOWN;
import static com.jme3.input.event.TouchEvent.Type.FLING;
import static com.jme3.input.event.TouchEvent.Type.MOVE;
import static com.jme3.input.event.TouchEvent.Type.UP;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import java.util.concurrent.Callable;

/**
 * The main game class that is responsible for updating everything in the game.
 * @author Lewis Chun
 */
public class GameRunningState extends AbstractAppState implements TouchListener, ForceChangeListener {
    
    /**
     * Accelerometer readings
     */
    private float[] sensorValues = new float[]{0, 0, 0};
    /**
     * Node for all the boxes
     */
    private Node boxesNode;
    /**
     * The managers for the boxes, floor and triangles
     */
    private ManagerFacade manager;
    /**
     * Text in the top right corner, for displaying the score
     */
    private BitmapText scoreText;
    /**
     * The physics engine
     */
    private BulletAppState bulletAppState;
    /**
     * The state that is active when the game is paused
     */
    private GamePauseState gamePauseState;
    
    private float screenHeight, screenWidth, pictureDim;
    /**
     * The box the user has just touched
     */
    private Geometry heldBox;
    private Camera cam;
    /**
     * This is the distance from the centre of the screen, to the point the floor
     * goes offscreen. 4 floor pieces * the height of a piece.
     */
    private float centerOffset = 4 * 4;
    private Node guiNode;
    private Node mainRootNode;
    private Node rootNode;
    private Node mainGuiNode;
    private boolean justEnabled = false;
    private boolean shouldEnable;
    private SimpleApplication main;
    
    public boolean shouldStop = false;
    public float score = 0;
    
    public GameRunningState(boolean enabled){
        shouldEnable = enabled;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        
        main = (SimpleApplication) app;
        rootNode = new Node("RunningRoot");
        cam = main.getCamera();
        mainRootNode = main.getRootNode();
        
        bulletAppState = stateManager.getState(BulletAppState.class);
        gamePauseState = stateManager.getState(GamePauseState.class);
        
        // Set up the floor node and the boxes node
        Node floorNode = new Node();
        Node triangleNode = new Node();
        boxesNode = new Node("Boxes");
        
        manager = new ManagerFacade(boxesNode, floorNode, triangleNode, getPhysics(), false);
        
        // Attach all the objects to the world
        rootNode.attachChild(boxesNode);
        rootNode.attachChild(floorNode);
        rootNode.attachChild(triangleNode);
        
        screenWidth = main.getContext().getSettings().getWidth();
        screenHeight = main.getContext().getSettings().getHeight();
          
        // Set up the GUI   
        guiNode = new Node("RunningGUI");

        // Create the score text
        scoreText = new BitmapText(Res.guiFont, false);
        scoreText.setSize(screenWidth * 0.1f);
        scoreText.setColor(ColorRGBA.White);
        scoreText.setText("0.0");
        scoreText.setLocalTranslation(screenWidth - scoreText.getLineWidth(), screenHeight, 0);
        guiNode.attachChild(scoreText);
        // And the pause button
        Picture pauseButton = Res.pauseButton;
        pictureDim = screenWidth * 0.1f;
        pauseButton.setHeight(pictureDim);
        pauseButton.setWidth(pictureDim);
        pauseButton.setPosition(5, screenHeight - pictureDim - 5);
        guiNode.attachChild(pauseButton);
        
        mainGuiNode = main.getGuiNode();
        
        if(shouldEnable){
            begin();
        }
        super.setEnabled(shouldEnable);
        
    }
    
    /**
     * Call to start the game
     */
    public void begin(){
        GameCamera.getInstance().moveTo(0);
        Player.getInstance().moveTo(0, 0, 1);
        mainGuiNode.attachChild(guiNode);
        mainRootNode.attachChild(rootNode);
        manager.initialise();
        unpause();
    }
    
    public void unpause(){
        super.setEnabled(true);
        bulletAppState.setEnabled(true);
        
        guiNode.attachChild(Res.pauseButton);
        justEnabled = true;
        Res.backgroundMusic.play();
    }
    
    public void pause()  {
        super.setEnabled(false);
        bulletAppState.setEnabled(false);
        
        gamePauseState.setEnabled(true);
        // Safely run this on the main thread
        main.enqueue(new Callable<Void>() {
            @Override
            public Void call(){
                guiNode.detachChild(Res.pauseButton);
                Res.backgroundMusic.pause();
                return null;
            }
        });
    }
    
    @Override
    public void update(float tpf) {
        if(isEnabled()){
            // Set the speed of the camera based on how far along it is
            Node centralPoint = GameCamera.getInstance().getCamNode();
            Vector3f screenCenterLocation = centralPoint.getLocalTranslation();
            int hundreds = (int) screenCenterLocation.y / 100;
            if(hundreds < 5){
                int tens = (((int) screenCenterLocation.y) - (hundreds * 100)) / (10 * (hundreds + 1));
                GameCamera.getInstance().setSpeed((1 + ((float) hundreds * 1) + (((float) tens) * 0.1f)));
            }
            RigidBodyControl player = Player.getInstance().getPhysicsControl();
            // Move the player based on the accelerometer readings
            player.applyCentralForce((new Vector3f(sensorValues[0], 
                    sensorValues[1] - (GameCamera.getInstance().getSpeed() / 2), 
                    0)).negateLocal());
            // Test to see if the ball has fallen off the floor
            Vector3f playerLocation = player.getPhysicsLocation();
            if(playerLocation.z < -5){
                // GAME OVER!
                shouldStop = true;
            }
            float screenCenterY = screenCenterLocation.y + centerOffset;
            manager.updateManagers(tpf, screenCenterY);
            // Round the location to 2 decimal places, and see if it's a new score
            float newScore = (Math.round(playerLocation.y * 10f))/10f;
            if(newScore > score){
                score = newScore;
                scoreText.setText(score + "");
                scoreText.setLocalTranslation(screenWidth - scoreText.getLineWidth(), screenHeight, 0);
            }
        }
    }
    
    /**
     * Get the physics world used by the physics engine.
     */
    public PhysicsSpace getPhysics(){
        return bulletAppState.getPhysicsSpace();
    }
    
    public void onTouch(String name, TouchEvent event, float tpf) {
        if(isEnabled() && isInitialized()){
            switch(event.getType()){
                case DOWN:
                    // Cast a line into the world and see if it collides with any 
                    // of the nodes in boxesNode
                    CollisionResults results = new CollisionResults();
                    // The line is based of the cameras position and direction
                    Vector3f click3d = cam.getWorldCoordinates(
                        new Vector2f(event.getX(), event.getY()), 0f).clone();
                    Vector3f dir = cam.getWorldCoordinates(
                        new Vector2f(event.getX(), event.getY()), 1f).subtractLocal(click3d).normalizeLocal();
                    Ray ray = new Ray(click3d, dir);
                    boxesNode.collideWith(ray, results);
                    if(results.size() > 0){
                        heldBox = results.getClosestCollision().getGeometry();
                        heldBox.getControl(BoxControl.class).onTouch();
                    }
                    break;
                case MOVE:
                    if(heldBox != null){
                        RigidBodyControl boxControl = heldBox.getControl(RigidBodyControl.class);
                        if(event.getDeltaY() < 0 && heldBox.getLocalTranslation().z < 2){
                            boxControl.setLinearVelocity(new Vector3f(event.getDeltaX(), event.getDeltaY(), 0));
                        } else {
                            // Move the box. I need to find a better way of moving it though.
                            boxControl.setLinearVelocity(new Vector3f(event.getDeltaX(), 0, event.getDeltaY()));
                        }
                    }
                    break;
                case UP:
                    heldBox = null;
                    break;
                case FLING:
                    // Fling means there has been a quick movement before lifting your finger
                    // So I use it to apply a bit of force.
                    if(heldBox != null){
                        heldBox.getControl(RigidBodyControl.class).applyCentralForce(new Vector3f(event.getDeltaX(), 0, event.getDeltaY()));
                    }
                    break;
                case TAP:
                    if(!justEnabled){
                        if(event.getX() < pictureDim + 5 && event.getY() > screenHeight - pictureDim - 5){
                            pause();   
                        }
                    } else {
                        justEnabled = false;
                    }
            }
        }
    }

    public void onNewForce(float[] newForces) {
        sensorValues = newForces;
    }
    
}
