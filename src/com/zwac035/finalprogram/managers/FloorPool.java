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

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.zwac035.finalprogram.FloorType;
import static com.zwac035.finalprogram.FloorType.CENTRE_HOLE;
import static com.zwac035.finalprogram.FloorType.MIN_X1_HOLE;
import static com.zwac035.finalprogram.FloorType.MIN_X2_HOLE;
import static com.zwac035.finalprogram.FloorType.MIN_X3_HOLE;
import static com.zwac035.finalprogram.FloorType.MIN_X4_HOLE;
import static com.zwac035.finalprogram.FloorType.X1_HOLE;
import static com.zwac035.finalprogram.FloorType.X2_HOLE;
import static com.zwac035.finalprogram.FloorType.X3_HOLE;
import static com.zwac035.finalprogram.FloorType.X4_HOLE;
import com.zwac035.finalprogram.Res;
import java.util.ArrayList;

/**
 *
 * @author Lewis Chun
 */
public class FloorPool {
    
    private ArrayList<Spatial> plainPool;
    private ArrayList<Spatial> centrePool;
    private ArrayList<Spatial> x1Pool;
    private ArrayList<Spatial> x2Pool;
    private ArrayList<Spatial> x3Pool;
    private ArrayList<Spatial> x4Pool;
    private ArrayList<Spatial> mX1Pool;
    private ArrayList<Spatial> mX2Pool;
    private ArrayList<Spatial> mX3Pool;
    private ArrayList<Spatial> mX4Pool;
    private int floorHeight = 4, floorWidth = 12;
    
    public FloorPool(int size, int sizeOfSpecials){
        plainPool = new ArrayList<Spatial>(size);
        centrePool = new ArrayList<Spatial>(sizeOfSpecials);
        x1Pool = new ArrayList<Spatial>(sizeOfSpecials);
        x2Pool = new ArrayList<Spatial>(sizeOfSpecials);
        x3Pool = new ArrayList<Spatial>(sizeOfSpecials);
        x4Pool = new ArrayList<Spatial>(sizeOfSpecials);
        mX1Pool = new ArrayList<Spatial>(sizeOfSpecials);
        mX2Pool = new ArrayList<Spatial>(sizeOfSpecials);
        mX3Pool = new ArrayList<Spatial>(sizeOfSpecials);
        mX4Pool = new ArrayList<Spatial>(sizeOfSpecials);
        createPool(size, sizeOfSpecials);
    }
    
    private void createPool(int size, int sizeOfSpecials){
        for(int i = 0; i < size; i++){
            plainPool.add(createPieceWithNoHole());
        }
        for(int i = 0; i < sizeOfSpecials; i++){
            centrePool.add(createPieceWithHole(CENTRE_HOLE));
            x1Pool.add(createPieceWithHole(X1_HOLE));
            x2Pool.add(createPieceWithHole(X2_HOLE));
            x3Pool.add(createPieceWithHole(X3_HOLE));
            x4Pool.add(createPieceWithHole(X4_HOLE));
            mX1Pool.add(createPieceWithHole(MIN_X1_HOLE));
            mX2Pool.add(createPieceWithHole(MIN_X2_HOLE));
            mX3Pool.add(createPieceWithHole(MIN_X3_HOLE));
            mX4Pool.add(createPieceWithHole(MIN_X4_HOLE));
        }
    }
    
    public Spatial takePlainPeice(float x, float y, ColorRGBA clr){
        if(plainPool.isEmpty()){
            return takePeice(x, y, clr, FloorType.random());
        }
        Spatial taken = plainPool.remove(0);
        resetControl(taken.getControl(RigidBodyControl.class), x - (floorWidth/2), y - (floorHeight/2));
        ((Geometry) taken).getMaterial().setColor("Color", clr);
        return taken;
    }
    
    public Spatial takePeice(float x, float y, ColorRGBA clr, FloorType f){
        switch(f){
            case CENTRE_HOLE: return takeCentrePeice(x, y, clr);
            case MIN_X1_HOLE: return takeMinX1Peice(x, y, clr);
            case MIN_X2_HOLE: return takeMinX2Peice(x, y, clr);
            case MIN_X3_HOLE: return takeMinX3Peice(x, y, clr);
            case MIN_X4_HOLE: return takeMinX4Peice(x, y, clr);
            default:
            case PLAIN: return takePlainPeice(x, y, clr);
            case X1_HOLE: return takeX1Peice(x, y, clr);
            case X2_HOLE: return takeX2Peice(x, y, clr);
            case X3_HOLE: return takeX3Peice(x, y, clr);
            case X4_HOLE: return takeX4Peice(x, y, clr);
        }   
    }
    
    public Spatial takeCentrePeice(float x, float y, ColorRGBA clr){
        if(centrePool.isEmpty()){
            return takePlainPeice(x, y, clr);
        }
        Spatial taken = centrePool.remove(0);
        resetControl(taken.getControl(RigidBodyControl.class), x, y);
        updateMaterial(taken, clr);
        return taken;
    }
    
    public Spatial takeX1Peice(float x, float y, ColorRGBA clr){
        if(x1Pool.isEmpty()){
            return takePlainPeice(x, y, clr);
        }
        Spatial taken = x1Pool.remove(0);
        resetControl(taken.getControl(RigidBodyControl.class), x, y);
        updateMaterial(taken, clr);
        return taken;
    }
    
    public Spatial takeX2Peice(float x, float y, ColorRGBA clr){
        if(x2Pool.isEmpty()){
            return takePlainPeice(x, y, clr);
        }
        Spatial taken = x2Pool.remove(0);
        resetControl(taken.getControl(RigidBodyControl.class), x, y);
        updateMaterial(taken, clr);
        return taken;
    }
    
    public Spatial takeX3Peice(float x, float y, ColorRGBA clr){
        if(x3Pool.isEmpty()){
            return takePlainPeice(x, y, clr);
        }
        Spatial taken = x3Pool.remove(0);
        resetControl(taken.getControl(RigidBodyControl.class), x, y);
        updateMaterial(taken, clr);
        return taken;
    }
    
    public Spatial takeX4Peice(float x, float y, ColorRGBA clr){
        if(x4Pool.isEmpty()){
            return takePlainPeice(x, y, clr);
        }
        Spatial taken = x4Pool.remove(0);
        resetControl(taken.getControl(RigidBodyControl.class), x, y);
        updateMaterial(taken, clr);
        return taken;
    }
    
    public Spatial takeMinX1Peice(float x, float y, ColorRGBA clr){
        if(mX1Pool.isEmpty()){
            return takePlainPeice(x, y, clr);
        }
        Spatial taken = mX1Pool.remove(0);
        resetControl(taken.getControl(RigidBodyControl.class), x, y);
        updateMaterial(taken, clr);
        return taken;
    }
    
    public Spatial takeMinX2Peice(float x, float y, ColorRGBA clr){
        if(mX2Pool.isEmpty()){
            return takePlainPeice(x, y, clr);
        }
        Spatial taken = mX2Pool.remove(0);
        resetControl(taken.getControl(RigidBodyControl.class), x, y);
        updateMaterial(taken, clr);
        return taken;
    }
    
    public Spatial takeMinX3Peice(float x, float y, ColorRGBA clr){
        if(mX3Pool.isEmpty()){
            return takePlainPeice(x, y, clr);
        }
        Spatial taken = mX3Pool.remove(0);
        resetControl(taken.getControl(RigidBodyControl.class), x, y);
        updateMaterial(taken, clr);
        return taken;
    }
    
    public Spatial takeMinX4Peice(float x, float y, ColorRGBA clr){
        if(mX4Pool.isEmpty()){
            return takePlainPeice(x, y, clr);
        }
        Spatial taken = mX4Pool.remove(0);
        resetControl(taken.getControl(RigidBodyControl.class), x, y);
        updateMaterial(taken, clr);
        return taken;
    }
    
    // Taken from:
    // http://hub.jmonkeyengine.org/forum/topic/ingame-changing-material/
    private void updateMaterial(Spatial s, ColorRGBA clr){
        if(s instanceof Node){
            Node n = (Node) s;
            for(int i = 0; i < n.getQuantity(); i++){
                updateMaterial(n.getChild(i), clr);
            }
        } else if(s instanceof Geometry){
            ((Geometry) s).getMaterial().setColor("Color", clr);
        }
    }
    
    public void addToPool(Spatial s){
        String name = s.getName();
        if(name.equals("pfloor")){
            plainPool.add(s);
        } else if(name.equals("cfloor")){
            centrePool.add(s);
        } else if(name.equals("x1floor")){
            x1Pool.add(s);
        } else if(name.equals("x2floor")){
            x2Pool.add(s);
        } else if(name.equals("x3floor")){
            x3Pool.add(s);
        } else if(name.equals("x4floor")){
            x4Pool.add(s);
        } else if(name.equals("mx1floor")){
            mX1Pool.add(s);
        } else if(name.equals("mx2floor")){
            mX2Pool.add(s);
        } else if(name.equals("mx3floor")){
            mX3Pool.add(s);
        } else if(name.equals("mx4floor")){
            mX4Pool.add(s);
        }    
    }
    
    private void resetControl(RigidBodyControl control, float x, float y){
        Vector3f currentLocation = control.getPhysicsLocation();
        currentLocation.set(x, y, 0);
        control.setPhysicsLocation(currentLocation);
    }
    
    private Spatial createPieceWithNoHole(){
        Quad floorQuad = new Quad(floorWidth, floorHeight);
        Geometry newPiece = new Geometry("Quad", floorQuad);
        Material floorMat = new Material(Res.assets, "Common/MatDefs/Misc/Unshaded.j3md");
        newPiece.setMaterial(floorMat);
        newPiece.setName("pfloor");
        newPiece.addControl(new RigidBodyControl(0f));
        return newPiece;
    }
    
    private Spatial createPieceWithHole(FloorType f){
        Spatial newPiece;
        switch(f) {
            default:
            case CENTRE_HOLE:
                newPiece = Res.centreHoleModel.clone();
                newPiece.setName("cfloor");
                break;
            case X1_HOLE:
                newPiece = Res.x1HoleModel.clone();
                newPiece.setName("x1floor");
                break;
            case X2_HOLE:
                newPiece = Res.x2HoleModel.clone();
                newPiece.setName("x2floor");
                break;
            case X3_HOLE:
                newPiece = Res.x3HoleModel.clone();
                newPiece.setName("x3floor");
                break;
            case X4_HOLE:
                newPiece = Res.x4HoleModel.clone();
                newPiece.setName("x4floor");
                break;
            case MIN_X1_HOLE:
                newPiece = Res.x1HoleModel.clone();
                newPiece.setName("mx1floor");
                newPiece.rotate(0, FastMath.PI, 0);
                break;
            case MIN_X2_HOLE:
                newPiece = Res.x2HoleModel.clone();
                newPiece.setName("mx2floor");
                newPiece.rotate(0, FastMath.PI, 0);
                break;
            case MIN_X3_HOLE:
                newPiece = Res.x3HoleModel.clone();
                newPiece.setName("mx3floor");
                newPiece.rotate(0, FastMath.PI, 0);
                break;
            case MIN_X4_HOLE:
                newPiece = Res.x4HoleModel.clone();
                newPiece.setName("mx4floor");
                newPiece.rotate(0, FastMath.PI, 0);
                break;
        }
        Material floorMat = new Material(Res.assets, "Common/MatDefs/Misc/Unshaded.j3md");
        newPiece.setMaterial(floorMat);
        newPiece.addControl(new RigidBodyControl(0f));
        return newPiece;
    }
}
