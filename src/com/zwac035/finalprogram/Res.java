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

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.font.BitmapFont;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;
import java.util.Random;

/**
 *
 * @author Lewis Chun
 */
public class Res {
    
    private static Res instance = null;
    
    public static AssetManager assets;
    
    public static Texture bombTexture;
    public static Texture magnetTexture;
    public static Texture explosionTexture;
    public static Texture heavyBoxTexture;
    public static Texture basketBallTexture;
    public static Texture beachBallTexture;
    public static Texture sparkleTexture;
    
    public static Picture greyBackground;
    public static Picture pauseButton;
    
    public static BitmapFont guiFont;
    
    public static Spatial centreHoleModel;
    public static Spatial x1HoleModel;
    public static Spatial x2HoleModel;
    public static Spatial x3HoleModel;
    public static Spatial x4HoleModel;
    
    public static AudioNode chink;
    public static AudioNode backgroundMusic;
    public static AudioNode explosion;
    
    public static Random rnd;
    
    private Res(){  
    }
    
    public static Res getInstance(){
        if(instance == null){
            instance = new Res();
        }
        return instance;
    }
    
    public void loadAssets(AssetManager assetManager, Node audioNode){
        assets = assetManager;
                
        // As this is a static class, nothing gets deallocated until the process
        // is destroyed, so we only have to load everything once.
        if(bombTexture == null){
            
            // Textures
            bombTexture = assetManager.loadTexture("Textures/bomb.png");
            magnetTexture = assetManager.loadTexture("Textures/magnet.png");
            explosionTexture = assetManager.loadTexture("Textures/flame.png");
            basketBallTexture = assetManager.loadTexture("Textures/balldimpled.png");
            heavyBoxTexture = assetManager.loadTexture("Textures/metal.png");
            sparkleTexture = assetManager.loadTexture("Textures/sparkle.png");
            beachBallTexture = assetManager.loadTexture("Textures/BeachBall.jpg");

            // Fonts
            guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

            // Models
            centreHoleModel = assetManager.loadModel("Models/centreHole.j3o");
            centreHoleModel.rotate(FastMath.HALF_PI, 0f, 0f);
            x1HoleModel = assetManager.loadModel("Models/X1Hole.j3o");
            x1HoleModel.rotate(FastMath.HALF_PI, 0f, 0f);
            x2HoleModel = assetManager.loadModel("Models/X2Hole.j3o");
            x2HoleModel.rotate(FastMath.HALF_PI, 0f, 0f);
            x3HoleModel = assetManager.loadModel("Models/X3Hole.j3o");
            x3HoleModel.rotate(FastMath.HALF_PI, 0f, 0f);
            x4HoleModel = assetManager.loadModel("Models/X4Hole.j3o");
            x4HoleModel.rotate(FastMath.HALF_PI, 0f, 0f);

            // Pictures
            greyBackground = new Picture("Background");
            greyBackground.setImage(assetManager, "Textures/Grey.png", true);
            pauseButton = new Picture("Pause");
            pauseButton.setImage(assetManager, "Textures/pause.png", true);
            
            backgroundMusic = new AudioNode(assets, "Sounds/one_0.ogg");
            backgroundMusic.setPositional(false);
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(1);
            
        }
        
        chink = new AudioNode(assets, "Sounds/chink.ogg");
        chink.setPositional(false);
        chink.setLooping(false);
        chink.setVolume(3);
        
        explosion = new AudioNode(assets, "Sounds/boom4.wav");
        explosion.setPositional(false);
        explosion.setLooping(false);
        explosion.setVolume(3);
        
        audioNode.attachChild(chink);
        audioNode.attachChild(backgroundMusic);
        audioNode.attachChild(explosion);
        
        rnd = new Random();
    }
}
