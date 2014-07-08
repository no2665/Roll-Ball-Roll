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

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.zwac035.finalprogram.Res;

/**
 *
 * @author Lewis Chun
 */
public class ExplodingBox extends BoxControl implements PhysicsTickListener, PhysicsCollisionListener {

    private PhysicsSpace physics;
    private boolean shouldRemove = false;
    private float explosionRadius = 6;
    private float explosionForce = 1.5f;
    private int id;
    private ParticleEmitter fire;
    
    public ExplodingBox(PhysicsSpace physics, int id){
        this.physics = physics;
        this.id = id;
        // Create the explosion
        // Taken from:
        // http://hub.jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_effects
        fire = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 10);
        Material mat_red = new Material(Res.assets, 
                "Common/MatDefs/Misc/Particle.j3md");
        mat_red.setTexture("Texture", Res.explosionTexture);
        fire.setMaterial(mat_red);
        fire.setImagesX(2); 
        fire.setImagesY(2); // 2x2 texture animation
        fire.setEndColor(  new ColorRGBA(1f, 0f, 0f, 1f));   // red
        fire.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
        fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        fire.setStartSize(1.5f);
        fire.setEndSize(1.5f);
        fire.setGravity(0, -0.5f, 0);
        fire.setLowLife(1f);
        fire.setHighLife(3f);
        fire.setParticlesPerSec(0);
        fire.setShape(new EmitterBoxShape(Vector3f.UNIT_XYZ.negate(), Vector3f.UNIT_XYZ));
        fire.getParticleInfluencer().setVelocityVariation(0.7f);
    }
    
    @Override
    public void onTouch() {
        physics.addTickListener(this);
    }

    public void start(){
        physics.addCollisionListener(this);
        spatial.setCullHint(Spatial.CullHint.Inherit);
        shouldRemove = false;
        doneExploding = false;
    }
    
    public void stop(){
        physics.removeCollisionListener(this);
    }
    
    public boolean shouldRemove(){
        return shouldRemove;
    }
    
    private boolean doneExploding = false;
    
    @Override
    protected void controlUpdate(float tpf) {
        if(shouldRemove && !doneExploding){
            physics.removeCollisionListener(this);
            // Make the box invisible
            spatial.setCullHint(Spatial.CullHint.Always);
            // Play the explosion sound
            Res.explosion.playInstance();
            Node justBoxes = spatial.getParent();
            Node root = justBoxes.getParent();
            Node particles = (Node) root.getChild("particles");
            particles.attachChild(fire);
            fire.setLocalTranslation(spatial.getLocalTranslation());
            fire.emitAllParticles();
            
            doneExploding = true;
        }
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        for(PhysicsRigidBody b: space.getRigidBodyList()){
            // This is testing to see if b is a ball/box or the camera.
            // This is also a really crap way of doing it,
            // but at the moment it's the only way I can stop the camera
            // being affected by the force.
            if(!b.getGravity().equals(Vector3f.ZERO)){
                Vector3f bPos = b.getPhysicsLocation();
                Vector3f sPos = spatial.getLocalTranslation();
                Vector3f vec = bPos.subtractLocal(sPos);
                float force = explosionRadius - vec.length();
                force *= explosionForce;
                force = force > 0 ? force : 0;
                vec.normalizeLocal();
                vec.multLocal(force);
                b.applyImpulse(vec, Vector3f.ZERO);
            }
        }
        space.removeTickListener(this);
        shouldRemove = true;
    }

    public void physicsTick(PhysicsSpace space, float tpf) {
    }

    public void collision(PhysicsCollisionEvent event) {
         if(event.getNodeA() == null || event.getNodeB() == null){
            return;
        }
        String nameA = event.getNodeA().getName();
        String nameB = event.getNodeB().getName();
        if(nameA != null && nameB != null){
            // Check that it is the ball the box is colliding with
            if((nameA.equals(id + "") && nameB.equals("ball"))
                    ||
               (nameA.equals("ball") && nameB.equals(id + ""))){
                physics.addTickListener(this);
            }
        }
    }

}
