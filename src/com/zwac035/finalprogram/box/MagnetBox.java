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
package com.zwac035.finalprogram.box;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.zwac035.finalprogram.Player;

/**
 *
 * @author Lewis Chun
 */
public class MagnetBox extends BoxControl {

    private float magnetRadius = 6;
    private float magnetForce = 2.5f;
    
    public MagnetBox() {
    }

    @Override
    public void onTouch() {
    }

    @Override
    protected void controlUpdate(float tpf) {
        // Find the distance between the player and the box
        RigidBodyControl player = Player.getInstance().getPhysicsControl();
        Vector3f vec = player.getPhysicsLocation().subtractLocal(spatial.getLocalTranslation());
        float distance = vec.length();
        if(distance <= magnetRadius){
            distance *= magnetForce;
            distance = distance > 0 ? distance : 0;
            vec.normalizeLocal();
            vec.multLocal(-distance);
            // Pull the player towards the box
            player.applyCentralForce(vec);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    
}
