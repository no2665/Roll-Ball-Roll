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

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.event.TouchEvent;
import com.jme3.font.Rectangle;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;

/**
 * The state of the game that controls what happens when the user has paused the
 * game
 * @author Lewis Chun
 */
public class GamePauseState extends AbstractAppState implements TouchListener {
    
    /**
     * The main root GUI node
     */
    private Node mainGuiNode;
    /**
     * The GUI node that this state owns
     */
    private Node guiNode;
    private GameRunningState gameRunningState;
    private boolean shouldEnable;
    
    /** 
     * @param enabled Set to true to enable this state immediately after 
     * initialisation, false to disable it
     */
    public GamePauseState(boolean enabled){
        shouldEnable = enabled;
    }
    
    /**
     * Sets up this state
     * @param stateManager
     * @param app 
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        
        gameRunningState = stateManager.getState(GameRunningState.class);
        
        SimpleApplication main = (SimpleApplication) app;
        // Get the main GUI node
        mainGuiNode = main.getGuiNode();
        guiNode = new Node("PausedGUI");
        
        float screenWidth = main.getContext().getSettings().getWidth();
        float screenHeight = main.getContext().getSettings().getHeight();
        
        // Set up the grey overlay
        Picture greyBack = Res.greyBackground;
        greyBack.setHeight(screenHeight);
        greyBack.setWidth(screenWidth);
        greyBack.setPosition(0, 0);
        
        // Set up the text shown when the game is paused
        BitmapText pausedText = new BitmapText(Res.guiFont, false);
        pausedText.setText("Paused \n Tap to continue");
        // This is the of the text, change the number 2 to make it bigger
        // or small
        pausedText.setSize(screenWidth * 0.05f);
        // The texts coordinate start in the top left corner,
        // so if we move it up a bit, the text will be centered
        pausedText.move(0, pausedText.getHeight() / 2, 0);
        pausedText.setBox(new Rectangle(0, screenHeight, screenWidth, screenHeight));
        pausedText.setAlignment(BitmapFont.Align.Center);
        pausedText.setVerticalAlignment(BitmapFont.VAlign.Center);
        
        guiNode.attachChild(greyBack);
        guiNode.attachChild(pausedText);

        super.setEnabled(shouldEnable);
    }
    
    @Override
    public void setEnabled(boolean enabled)  {
        super.setEnabled(enabled);
        // if pause
        if(enabled){
            mainGuiNode.attachChild(guiNode);
        } else { // unpause
            mainGuiNode.detachChild(guiNode);
            gameRunningState.unpause();
        }
    }
    
    @Override
    public void update(float tpf) {
        
    }

    public void onTouch(String name, TouchEvent event, float tpf) {
        if(isEnabled() && isInitialized()){
            if(event.getType() == TouchEvent.Type.UP){
                // unpause us
                setEnabled(false);
            }
        }
    }
    
}
