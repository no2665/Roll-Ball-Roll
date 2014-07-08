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

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.zwac035.finalprogram.Res;
import com.zwac035.finalprogram.box.ExplodingBox;
import com.zwac035.finalprogram.box.HeavyBox;
import com.zwac035.finalprogram.box.MagnetBox;
import com.zwac035.finalprogram.box.NormalBoxControl;
import java.util.ArrayList;

/**
 *
 * @author Lewis Chun
 */
public class BoxPool {

    private ArrayList<Spatial> plainBoxes;
    private ArrayList<Spatial> magnetBoxes;
    private ArrayList<Spatial> explodingBoxes;
    private ArrayList<Spatial> unMoveableBoxes;

    public BoxPool(int numPlain, int numSpecials, PhysicsSpace physics) {
        plainBoxes = new ArrayList<Spatial>(numPlain);
        magnetBoxes = new ArrayList<Spatial>(numSpecials);
        explodingBoxes = new ArrayList<Spatial>(numSpecials);
        unMoveableBoxes = new ArrayList<Spatial>(numSpecials);
        createPool(numPlain, numSpecials, physics);
    }

    private void createPool(int numPlain, int numSpecials, PhysicsSpace physics) {
        for (int i = 0; i < numPlain; i++) {
            Spatial newBox = createBox(0, 10, 0);
            newBox.setMaterial(createMaterial(ColorRGBA.randomColor()));
            newBox.addControl(new NormalBoxControl());
            plainBoxes.add(newBox);
        }
        Material bombMat = createTexturedMaterial(Res.bombTexture);
        Material magMat = createTexturedMaterial(Res.magnetTexture);
        Material heavyMat = createTexturedMaterial(Res.heavyBoxTexture);
        for (int i = 0; i < numSpecials; i++) {
            Spatial newBox = createBox(0, 10, 0);
            newBox.setMaterial(bombMat);
            newBox.addControl(new ExplodingBox(physics, i));
            newBox.setName(i + "");
            explodingBoxes.add(newBox);

            Spatial anotherBox = createBox(0, 10, 0);
            anotherBox.setMaterial(magMat);
            anotherBox.addControl(new MagnetBox());
            magnetBoxes.add(anotherBox);

            Spatial aThirdBox = createBox(0, 10, 0);
            aThirdBox.setMaterial(heavyMat);
            aThirdBox.addControl(new HeavyBox());
            aThirdBox.getControl(RigidBodyControl.class).setMass(0f);
            unMoveableBoxes.add(aThirdBox);
        }

    }

    public Spatial takePlainBox(float x, float y, float z) {
        if (plainBoxes.isEmpty()) {
            return null;
        }
        Spatial takenBox = plainBoxes.remove(0);
        resetControl(takenBox.getControl(RigidBodyControl.class), x, y, z);
        return takenBox;
    }

    public Spatial takeExplodingBox(float x, float y, float z) {
        if (explodingBoxes.isEmpty()) {
            return null;
        }
        Spatial takenBox = explodingBoxes.remove(0);
        takenBox.getControl(ExplodingBox.class).start();
        resetControl(takenBox.getControl(RigidBodyControl.class), x, y, z);
        return takenBox;
    }

    public Spatial takeMagnetBox(float x, float y, float z) {
        if (magnetBoxes.isEmpty()) {
            return null;
        }
        Spatial takenBox = magnetBoxes.remove(0);
        resetControl(takenBox.getControl(RigidBodyControl.class), x, y, z);
        return takenBox;
    }

    public Spatial takeHeavyBox(float x, float y, float z) {
        if (unMoveableBoxes.isEmpty()) {
            return null;
        }
        Spatial takenBox = unMoveableBoxes.remove(0);
        resetControl(takenBox.getControl(RigidBodyControl.class), x, y, z);
        return takenBox;
    }

    private void resetControl(RigidBodyControl control, float x, float y, float z) {
        control.setAngularVelocity(Vector3f.ZERO);
        control.setLinearVelocity(Vector3f.ZERO);
        control.clearForces();
        // Instead of creating a new Vector3f for the location, lets recycle
        // the old one
        Vector3f currentLocation = control.getPhysicsLocation();
        currentLocation.set(x, y, z);
        control.setPhysicsLocation(currentLocation);
        // This should reset the rotation of the box
        control.setPhysicsRotation(Quaternion.IDENTITY);
    }

    public void addToPool(Spatial s) {
        if (s.getControl(NormalBoxControl.class) != null) {
            addPlainBoxToPool(s);
        } else if (s.getControl(ExplodingBox.class) != null) {
            addExplodingBoxToPool(s);
        } else if (s.getControl(MagnetBox.class) != null) {
            addMagnetBoxToPool(s);
        } else if (s.getControl(HeavyBox.class) != null) {
            addHeavyToPool(s);
        }
    }

    public void addPlainBoxToPool(Spatial s) {
        plainBoxes.add(s);
    }

    public void addExplodingBoxToPool(Spatial s) {
        explodingBoxes.add(s);
        s.getControl(ExplodingBox.class).stop();
    }

    public void addMagnetBoxToPool(Spatial s) {
        magnetBoxes.add(s);
    }

    public void addHeavyToPool(Spatial s) {
        unMoveableBoxes.add(s);
    }

    private Spatial createBox(int x, int y, int z) {
        Box box = new Box(1, 1, 1);
        Geometry newBox = new Geometry("Box", box);
        newBox.setName("a box");
        newBox.setLocalTranslation(x, y, z);
        newBox.addControl(new RigidBodyControl(new BoxCollisionShape(new Vector3f(1, 1, 1)), 1.5f));
        return newBox;
    }

    private Material createMaterial(ColorRGBA clr) {
        Material boxMat = new Material(Res.assets, "Common/MatDefs/Light/Lighting.j3md");
        boxMat.setBoolean("UseMaterialColors", true);
        boxMat.setColor("Diffuse", clr);
        boxMat.setColor("Ambient", clr);
        boxMat.setColor("Specular", ColorRGBA.White);
        return boxMat;
    }

    private Material createTexturedMaterial(Texture t) {
        Material texMat = new Material(Res.assets, "Common/MatDefs/Misc/Unshaded.j3md");
        texMat.setTexture("ColorMap", t);
        return texMat;
    }
}
