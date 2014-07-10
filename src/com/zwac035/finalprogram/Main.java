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

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.List;

/***************************************************************************
 * TODO NOTES (hopefully I'll remember these):
 *  * Fix the known bugs in the Android main activity
 *  * Remember to remove the debug box for release
 *  * Fix the lighting so that there is no more black on the ball
 *  * Pressing the home button as the game launches, causes it to crash.
 ***************************************************************************/

/**
 * The starting point of the game.
 * @author Lewis Chun
 */
public class Main extends SimpleApplication {
    /**
     * AppState that runs when the game isn't paused
     */
    private GameRunningState gameRunning;
    /**
     * Variable for whether or not we should show the tutorial 
     */
    private boolean tutorial = false;
    /**
     * List of observers that want to know about the values being read from the 
     * accelerometer
     */
    private static List<ForceChangeListener> listeners = new ArrayList<ForceChangeListener>();
        
    /**
     * Classic main method
     * @param args 
     */
    public static void main(String[] args) {
    }

    /**
     * The game is initialised here.
     * Shouldn't be called from anywhere, but the internals of the jMonkey 
     * engine.
     */
    @Override
    public void simpleInitApp() {  
        // Disable the flyCam
        flyCam.setEnabled(false);
        
        Node audioNode = new Node("audio");
        // Load the models and textures
        Res.getInstance().loadAssets(assetManager, audioNode);
        
        // Create physics engine AppState
        BulletAppState bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        // Attach it to the AppStateManager, so that it can be constantly 
        // updated
        stateManager.attach(bulletAppState);
        
        // Get the physics world
        PhysicsSpace physics = bulletAppState.getPhysicsSpace();
        // -10 is accurate enough for a game
        physics.setGravity(Vector3f.UNIT_Z.mult(-10));
        
        // The camera and player are quite universal for all app states
        // so lets load them here
        GameCamera.getInstance().loadCamera(rootNode, physics, cam);
        Player.getInstance().loadPlayer(rootNode, physics);
        
        // Create the AppState that manages the app when it isn't paused
        gameRunning = new GameRunningState(!tutorial);
        
        // Create the AppState that runs when the app is paused
        GamePauseState gamePaused = new GamePauseState(!tutorial);
                
        // Set up the inputs
        inputManager.clearMappings(); // Clear the existing listeners
        inputManager.clearRawInputListeners();
        inputManager.addMapping("Touch", new TouchTrigger(0)); // Add the touch 
        inputManager.addListener(gameRunning, new String[]{"Touch"});//listeners
        inputManager.addListener(gamePaused, new String[]{"Touch"});
        
        // Should we show the tutorial?
        if(tutorial){
            // if so create the tutorial app state
            TutorialState tutState = new TutorialState();
            stateManager.attach(tutState);
            inputManager.addListener(tutState, new String[]{"Touch"});
            // The tutorial wants to know about the sensor readings
            listeners.add(tutState);
        } 
        
        stateManager.attach(gameRunning);
        stateManager.attach(gamePaused);
        
        listeners.add(gameRunning);
        
        // Turn off debug stats
        setDisplayStatView(false);
        setDisplayFps(false);
        
        AmbientLight a = new AmbientLight();
        a.setColor(ColorRGBA.Gray);
        
        rootNode.addLight(a);
        rootNode.attachChild(audioNode);

    }

    /**
     * Called by jMonkey to update the state
     * @param tpf  time per frame
     */
    @Override
    public void simpleUpdate(float tpf) {
        // Check if the game is over
        if(gameRunning.shouldStop){
            stop();
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // Not Used
    }
    
    /**
     * This method is used by the Android activity
     * @return The players score.
     */
    public float getScore(){
        return gameRunning.score;
    }
    
    /**
     * @param ans Set to true to show the tutorial before starting the game,
     * false otherwise.
     */
    public void showTutorial(boolean ans){
        tutorial = ans;
    }
    
    /**
     * Used to pass the accelerometer sensor values to the game
     * @param values The sensor readings
     */
    public void setSensorValues(float[] values){
        for(ForceChangeListener f: listeners){
            f.onNewForce(values);
        }
    }

    public static void registerForceChangeListener(ForceChangeListener lco) {
        listeners.add(lco);
    }

    /**
     * Stops f from receiving any more sensor values from the device
     * @param f The observer to remove
     */
    public static void unregisterForceChangeListener(ForceChangeListener lco) {
        listeners.remove(lco);
    }
    
    public void pause(){
        if(gameRunning != null){
            gameRunning.pause();
        }
    }
    
    public int getCollectedTriangles(){
        return Player.getInstance().getCollectedTriangles();
    }
    
    public void setSkin(String s){
        Player.getInstance().setSkin(s);
    }
    
}
