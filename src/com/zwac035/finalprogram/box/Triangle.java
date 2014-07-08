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

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Dome;
import com.zwac035.finalprogram.Res;

/**
 *
 * @author Lewis Chun
 */
public class Triangle {
    
    private int value = 1;
    private Dome shape;
    private Geometry triangleGeom;
    private Material mat;
    private ParticleEmitter sparkles;
    
    public Triangle(){
        // Triangle shape
        shape = new Dome(Vector3f.ZERO, 2, 3, 0.5f, false);
        triangleGeom = new Geometry("Triangle", shape);
        mat = new Material(Res.assets, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        ColorRGBA clr = new ColorRGBA(255, 215, 0, 0);
        mat.setColor("Diffuse", clr);
        mat.setColor("Ambient", clr);
        mat.setColor("Specular", ColorRGBA.White);
        triangleGeom.setMaterial(mat);
        triangleGeom.rotate(FastMath.HALF_PI, 0, 0);
        
        // Triangle particles
        sparkles = 
                new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 1);
        Material mat_red = new Material(Res.assets, 
                "Common/MatDefs/Misc/Particle.j3md");
        mat_red.setTexture("Texture", Res.sparkleTexture);
        sparkles.setMaterial(mat_red);
        sparkles.setImagesX(2); 
        sparkles.setImagesY(2);
        sparkles.setSelectRandomImage(true);
        sparkles.setEndColor(ColorRGBA.Blue);
        sparkles.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f));
        sparkles.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 1, 0));
        sparkles.setStartSize(0.2f);
        sparkles.setEndSize(0.4f);
        sparkles.setGravity(0, 0, 0);
        sparkles.setLowLife(1f);
        sparkles.setHighLife(3f);
        sparkles.setParticlesPerSec(0.5f);
        sparkles.setShape(new EmitterBoxShape(Vector3f.UNIT_XYZ.mult(-0.5f), Vector3f.UNIT_XYZ.mult(0.5f)));
        sparkles.getParticleInfluencer().setVelocityVariation(0.5f);

    }
    
    public void setValue(int v){
        value = v;
    }
    
    public int getValue(){
        return value;
    }
    
    public void moveTo(int x, int y){
        triangleGeom.setLocalTranslation(x, y, 1);
        sparkles.setLocalTranslation(x, y, 1);
    }
    
    public Vector3f getLocation(){
        return triangleGeom.getLocalTranslation();
    }
    
    public Geometry getGeometry(){
        return triangleGeom;
    }
    
    public ParticleEmitter getParticles(){
        return sparkles;
    }
    
    public void setEnabled(boolean enabled){
        sparkles.setEnabled(enabled);
    }
}
