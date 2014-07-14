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
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import java.util.ArrayList;

/**
 * State for taking the user through the tutorial
 * @author Lewis Chun
 */
public class TutorialState extends AbstractAppState implements TouchListener, PhysicsCollisionListener, ForceChangeListener {

    private enum TutState{
        WELCOME,
        FIRST_ROLL,
        MOVE_TO_BOX,
        THIS_IS_A_BOX,
        WHY_NOT_FLING,
        WAIT_FOR_FLING,
        SECOND_ROLE,
        MOVE_TO_HOLE,
        THIS_IS_A_HOLE,
        DONT_FALL_DOWN,
        SAME_FOR_THE_EDGE,
        THIRD_ROLL,
        SHOW_SCORE,
        SHOW_PAUSE,
        FOURTH_ROLL,
        MOVE_TO_BOXES,
        THESE_ARE_BOXES,
        THATS_IT,
        EXIT
    }
    private TutState currentState = TutState.WELCOME;
    private BulletAppState bulletAppState;
    private AppStateManager appStateManager;
    private Node rootNode;
    private Node guiNode;
    private Node boxNode;
    private Node safetyPlaneNode;
    private ManagerFacade manager;
    private BitmapText instructions;
    private BitmapText tapToContinue;
    private BitmapText scoreText;
    private float screenHeight, screenWidth, score = 0;
    private Camera cam;
    private Geometry heldBox;
    private Picture pauseButton;
    private ArrayList<GhostControl> checkpoints = new ArrayList<GhostControl>();
    private float[] playerForces = new float[] {0, 0, 0};
    private Main mainClass;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        appStateManager = stateManager;
        
        SimpleApplication main = (SimpleApplication) app;
        
        mainClass = (Main) main;
        
        bulletAppState = stateManager.getState(BulletAppState.class);
        
        Node mainRootNode = main.getRootNode();
        rootNode = new Node("TutRoot");
        mainRootNode.attachChild(rootNode);
        
        cam = main.getCamera();
        
        PhysicsSpace physics = bulletAppState.getPhysicsSpace();
        
        boxNode = new Node("boxes");
        Node floorNode = new Node("floor");
        Node triangleNode = new Node("triangles");
        
        manager = new ManagerFacade(boxNode, floorNode, triangleNode, physics, true);
        manager.initialise();
        
        // Create a plane at the bottom of the screen to stop the balling 
        // rolling out of view
        Plane lowerPlane = new Plane(Vector3f.UNIT_Y, -8.5f);
        RigidBodyControl lowerCollision = new RigidBodyControl(
                                    new PlaneCollisionShape(lowerPlane), 1f);
        safetyPlaneNode = new Node("safetyplane");
        safetyPlaneNode.addControl(lowerCollision);
        lowerCollision.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        lowerCollision.setKinematic(true);
        // Add it to the camera's node so it moves with it
        GameCamera.getInstance().getCamNode().attachChild(safetyPlaneNode);
        physics.add(safetyPlaneNode);
        
        rootNode.attachChild(floorNode);
        rootNode.attachChild(boxNode);
        rootNode.attachChild(triangleNode);
        
        screenWidth = main.getContext().getSettings().getWidth();
        screenHeight = main.getContext().getSettings().getHeight();
        
        instructions = new BitmapText(Res.guiFont, false);
        instructions.setText("You control this ball. \n"
                + "Tilt your phone to move it.");
        instructions.setSize(screenWidth * 0.08f);
        instructions.setBox(new Rectangle(0, instructions.getLineHeight() * 3, screenWidth, instructions.getHeight() * 3));
        // Move the text up so that there room for 3 lines of text
        instructions.setAlignment(BitmapFont.Align.Center);
        instructions.setVerticalAlignment(BitmapFont.VAlign.Top);
        
        tapToContinue = new BitmapText(Res.guiFont, false);
        tapToContinue.setText("Tap to continue...  ");
        tapToContinue.setSize(screenWidth * 0.04f);
        tapToContinue.setBox(new Rectangle(screenWidth - tapToContinue.getLineWidth(), tapToContinue.getLineHeight(), tapToContinue.getLineWidth(), tapToContinue.getHeight()));
        tapToContinue.setAlignment(BitmapFont.Align.Right);
        tapToContinue.setVerticalAlignment(BitmapFont.VAlign.Top);
        
        scoreText = new BitmapText(Res.guiFont, false);
        scoreText.setSize(screenWidth * 0.1f);
        scoreText.setColor(ColorRGBA.White);
        scoreText.setText("0.0");
        scoreText.setLocalTranslation(screenWidth - scoreText.getLineWidth(), screenHeight, 0);
        
        pauseButton = Res.pauseButton;
        float pictureDim = screenWidth * 0.1f;
        pauseButton.setHeight(pictureDim);
        pauseButton.setWidth(pictureDim);
        pauseButton.setPosition(5, screenHeight - pictureDim - 5);
        
        guiNode = new Node("tutGUI");
        main.getGuiNode().attachChild(guiNode);
        guiNode.attachChild(instructions);
        guiNode.attachChild(tapToContinue);
        
        createCheckPointAt(12);
        createCheckPointAt(32);
        createCheckPointAt(46);
        createCheckPointAt(56);
        
        Player.getInstance().moveTo(0, 0, 1);
        
        physics.addCollisionListener(this);
        bulletAppState.setEnabled(false);
                
    }
    
    /**
     * Used to create an invisible wall that can be used to trigger an event
     */
    private void createCheckPointAt(int location){
        PhysicsSpace physics = bulletAppState.getPhysicsSpace();
        BoxCollisionShape wall = new BoxCollisionShape(new Vector3f(10f, 0.1f, 6));
        GhostControl checkPoint = new GhostControl(wall);
        checkPoint.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_03);
        checkPoint.setPhysicsLocation(new Vector3f(0, location, 0));
        physics.add(checkPoint);
        checkpoints.add(checkPoint);
    }
    
    @Override
    public void update(float tpf) {
        if(isEnabled()){
            
            // If we are currently moving
            if(bulletAppState.isEnabled()){
                // Then apply the forces to the player
                Player player = Player.getInstance();
                RigidBodyControl playerControl = player.getPhysicsControl();
                playerControl.applyCentralForce((new Vector3f(playerForces[0], 
                    playerForces[1] - GameCamera.getInstance().getSpeed(), 
                    0)).negateLocal());
                // Check to see if the player has fallen of the floor,
                // and if they have, reset the ball
                Vector3f playerLoc = playerControl.getPhysicsLocation();
                if(playerLoc.z < -5){
                    player.moveTo(0, playerLoc.y, 1);
                    playerControl.setLinearVelocity(Vector3f.ZERO);
                }
            }
            
            Vector3f screenCenter = GameCamera.getInstance().getCamNode().getLocalTranslation();
            manager.updateManagers(tpf, screenCenter.y + (4*4));
            
            switch(currentState){
                case FIRST_ROLL:
                    tapToContinue.setText("Roll to continue... ");
                    break;
                case MOVE_TO_BOX:
                    if(screenCenter.y > 16){
                        instructions.setText("This is a box");
                        tapToContinue.setText("Tap to continue...  ");
                        moveToNextStep();
                    }
                    break;
                case WHY_NOT_FLING:
                    instructions.setText("Fling it out of your way");
                    tapToContinue.setText("Fling to continue...");
                    moveToNextStep();
                    break;
                case SECOND_ROLE:
                    instructions.setText("Nicely Done");
                    tapToContinue.setText("Roll to continue... ");
                    break;
                case MOVE_TO_HOLE:
                    if(screenCenter.y > 36){
                        instructions.setText("This is a hole");
                        tapToContinue.setText("Tap to continue...  ");
                        moveToNextStep();
                    }
                    break;
                case DONT_FALL_DOWN:
                    instructions.setText("If you fall down, \n"
                            + "it's GAME OVER!");
                    tapToContinue.setText("Tap to continue...  ");
                    break;
                case SAME_FOR_THE_EDGE:
                    instructions.setText("Falling off the edge \n"
                            + "is a bad idea too");
                    tapToContinue.setText("Tap to continue...  ");
                    break;
                case THIRD_ROLL:
                    tapToContinue.setText("Roll to continue... ");
                    break;
                case SHOW_SCORE:
                    instructions.setText("This is your score \n"
                            + "Try to get a high one");
                    tapToContinue.setText("Tap to continue...  ");
                    guiNode.attachChild(scoreText);
                    break;
                case SHOW_PAUSE:
                    instructions.setText("This is the pause button");
                    tapToContinue.setText("Tap to continue...  ");
                    guiNode.attachChild(pauseButton);
                    break;
               case FOURTH_ROLL:
                    tapToContinue.setText("Roll to continue... ");
                    break;
                case MOVE_TO_BOXES:
                    if(screenCenter.y > 60){
                        instructions.setText("These aren't your ordinary \n "
                                + "boxes. Watch out for them.");
                        tapToContinue.setText("Tap to continue...  ");
                        moveToNextStep();
                    }
                    break;
                case THATS_IT:
                    instructions.setText("Well, that's it. This \n "
                            + "tutorial is over. Have fun!");
                    tapToContinue.setText("Tap to exit...      ");
                    moveToNextStep();
                    break;
            }
            
            // Round the location to 2 decimal places, and see if it's a new score
            float newScore = (Math.round(Player.getInstance().getPhysicsControl().getPhysicsLocation().y * 10f))/10f;
            if(newScore > score){
                score = newScore;
                scoreText.setText(score + "");
                scoreText.setLocalTranslation(screenWidth - scoreText.getLineWidth(), screenHeight, 0);
            }
            
        }
    }
    
    public void moveToNextStep(){
        switch(currentState){
            case WELCOME:
                currentState = TutState.FIRST_ROLL;
                bulletAppState.setEnabled(true);
                break;
            case FIRST_ROLL:
                currentState = TutState.MOVE_TO_BOX;
                Player.getInstance().setEnableMovement(false);
                GameCamera.getInstance().setSpeed(4);
                break;
            case MOVE_TO_BOX:
                currentState = TutState.THIS_IS_A_BOX;
                Player.getInstance().setEnableMovement(true);
                bulletAppState.setEnabled(false);
                GameCamera.getInstance().setSpeed(1);
                break;
            // Currently displaying "This is a box"
            // Need a tap to move on
            case THIS_IS_A_BOX:
                currentState = TutState.WHY_NOT_FLING;
                bulletAppState.setEnabled(true);
                GameCamera.getInstance().setSpeed(0);
                break;
            // Currently displaying "Fling it out of way"
            // moves on instantly
            case WHY_NOT_FLING:
                currentState = TutState.WAIT_FOR_FLING;
                bulletAppState.setEnabled(true);
                GameCamera.getInstance().setSpeed(0);
                break;
            // Needs the box to be flung
            case WAIT_FOR_FLING:
                currentState = TutState.SECOND_ROLE;
                GameCamera.getInstance().setSpeed(1);
                bulletAppState.setEnabled(true);
                break;
            case SECOND_ROLE:
                currentState = TutState.MOVE_TO_HOLE;
                Player.getInstance().setEnableMovement(false);
                GameCamera.getInstance().setSpeed(4);
                break;
            case MOVE_TO_HOLE:
                currentState = TutState.THIS_IS_A_HOLE;
                GameCamera.getInstance().setSpeed(0);
                break;
            case THIS_IS_A_HOLE:
                currentState = TutState.DONT_FALL_DOWN;
                break;
            case DONT_FALL_DOWN:
                currentState = TutState.SAME_FOR_THE_EDGE;
                break;
            case SAME_FOR_THE_EDGE:
                currentState = TutState.THIRD_ROLL;
                GameCamera.getInstance().setSpeed(1);
                Player.getInstance().setEnableMovement(true);
                break;
            case THIRD_ROLL:
                currentState = TutState.SHOW_SCORE;
                bulletAppState.setEnabled(false);
                break;
            case SHOW_SCORE:
                currentState = TutState.SHOW_PAUSE;
                break;
            case SHOW_PAUSE:
                currentState = TutState.FOURTH_ROLL;
                bulletAppState.setEnabled(true);
                break;
            case FOURTH_ROLL:
                currentState = TutState.MOVE_TO_BOXES;
                Player.getInstance().setEnableMovement(false);
                GameCamera.getInstance().setSpeed(4);
                break;
            case MOVE_TO_BOXES:
                currentState = TutState.THESE_ARE_BOXES;
                bulletAppState.setEnabled(false);
                break;
            case THESE_ARE_BOXES:
                currentState = TutState.THATS_IT;
                break;
            case THATS_IT:
                currentState = TutState.EXIT;
                break;
            case EXIT:
                GameCamera.getInstance().setSpeed(1);
                Player.getInstance().setEnableMovement(true);
                exitTutorial();
        }
    }
    
    public void exitTutorial(){
        super.setEnabled(false);
        bulletAppState.setEnabled(false);
        guiNode.removeFromParent();
        rootNode.removeFromParent();
        appStateManager.detach(this);
        manager.removeEverything();
        PhysicsSpace physics = bulletAppState.getPhysicsSpace();
        for(GhostControl g: checkpoints){
            physics.remove(g);
        }
        physics.remove(safetyPlaneNode);
        physics.removeCollisionListener(this);
        mainClass.getInputManager().removeListener(this);
        Main.unregisterForceChangeListener(this);
        GameCamera.getInstance().getCamNode().detachChildNamed("safetyplane");
        GameRunningState run = appStateManager.getState(GameRunningState.class);
        run.begin();
    }
    
    public void onTouch(String name, TouchEvent event, float tpf) {
        if(this.isEnabled() && isInitialized()){
            switch(event.getType()){
                case TAP:
                    switch(currentState){
                        case WELCOME:
                        case THIS_IS_A_BOX:
                        case THIS_IS_A_HOLE:
                        case DONT_FALL_DOWN:
                        case SAME_FOR_THE_EDGE:
                        case SHOW_SCORE:
                        case SHOW_PAUSE:
                        case THESE_ARE_BOXES:
                        case EXIT:
                            moveToNextStep();
                            break;
                        default:
                            break;
                    }
                    break;
                case DOWN:
                    if(currentState == TutState.WAIT_FOR_FLING){
                        // Cast a line into the world and see if it collides with any 
                        // of the nodes in boxesNode
                        CollisionResults results = new CollisionResults();
                        // The line is based of the cameras position and direction
                        Vector3f click3d = cam.getWorldCoordinates(
                            new Vector2f(event.getX(), event.getY()), 0f).clone();
                        Vector3f dir = cam.getWorldCoordinates(
                            new Vector2f(event.getX(), event.getY()), 1f).subtractLocal(click3d).normalizeLocal();
                        Ray ray = new Ray(click3d, dir);
                        boxNode.collideWith(ray, results);
                        if(results.size() > 0){
                            heldBox = results.getClosestCollision().getGeometry();
                            heldBox.getControl(BoxControl.class).onTouch();
                        }
                    }
                    break;
                case MOVE:
                    if(currentState == TutState.WAIT_FOR_FLING){
                        if(heldBox != null){
                            RigidBodyControl boxControl = heldBox.getControl(RigidBodyControl.class);
                            if(event.getDeltaY() < 0 && heldBox.getLocalTranslation().z < 2){
                                boxControl.setLinearVelocity(new Vector3f(event.getDeltaX(), event.getDeltaY(), 0));
                            } else {
                                // Move the box. I need to find a better way of moving it though.
                                boxControl.setLinearVelocity(new Vector3f(event.getDeltaX(), 0, event.getDeltaY()));
                            }
                        }
                    }
                    break;
                case UP:
                    if(currentState == TutState.WAIT_FOR_FLING){
                        if(heldBox != null){
                            heldBox = null;
                            moveToNextStep();
                        }
                    }
                    break;
            }
        }
    }
    
    public void collision(PhysicsCollisionEvent event) {
        switch(currentState){
            case FIRST_ROLL:
            case SECOND_ROLE:
            case THIRD_ROLL:
            case FOURTH_ROLL:
                int a = event.getObjectA().getCollisionGroup();
                int b = event.getObjectB().getCollisionGroup();
                if(a == PhysicsCollisionObject.COLLISION_GROUP_03){
                    event.getObjectA().setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_NONE);
                    moveToNextStep();
                } else if(b == PhysicsCollisionObject.COLLISION_GROUP_03){
                    event.getObjectB().setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_NONE);
                    moveToNextStep();
                }
                break;
            default:
                break;
        }
    }
    
    public void onNewForce(float[] newForces) {
        playerForces = newForces;
    }
    
}
