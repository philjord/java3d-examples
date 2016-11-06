/*
 * $RCSfile$
 *
 * Copyright (c) 2007 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 *
 * $Revision$
 * $Date$
 * $State$
 */

package org.jdesktop.j3d.examples.model_clip;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import org.jogamp.java3d.AmbientLight;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.DirectionalLight;
import org.jogamp.java3d.Material;
import org.jogamp.java3d.ModelClip;
import org.jogamp.java3d.PolygonAttributes;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.applet.MainFrame;
import org.jogamp.java3d.utils.behaviors.mouse.MouseRotate;
import org.jogamp.java3d.utils.behaviors.mouse.MouseZoom;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Cylinder;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.AxisAngle4f;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3f;
import org.jogamp.vecmath.Vector4d;

/**
 * ModelClipTest2 draws a cylinder and creates two clip planes
 * to see the interior of the cylinder. It also has a behavior to
 * move the clip planes.
 */
public class ModelClipTest2 extends Applet {

    private SimpleUniverse u;
    
  public BranchGroup createSceneGraph()
  {
    // Create the root of the branch graph
    BranchGroup objRoot = new BranchGroup();

    // Create a Transformgroup to scale all objects so they
    // appear in the scene.
    TransformGroup objScale = new TransformGroup();
    Transform3D t3d = new Transform3D();
    t3d.setScale(0.4);
    objScale.setTransform(t3d);
    objRoot.addChild(objScale);

    // Create lights
    BoundingSphere bounds =
      new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
    
    //Shine it with two colored lights.
    Color3f lColor0 = new Color3f(1.0f, 1.0f, 1.0f);
    Color3f lColor1 = new Color3f(0.5f, 0.0f, 0.5f);
    Color3f lColor2 = new Color3f(0.7f, 0.7f, 0.0f);
    Vector3f lDir1  = new Vector3f(-1.0f, -1.0f, 1.0f);
    Vector3f lDir2  = new Vector3f(0.0f, 0.0f, -1.0f);
    
    AmbientLight     lgt0 = new AmbientLight(true, lColor2);
    DirectionalLight lgt1 = new DirectionalLight(lColor1, lDir1);
    DirectionalLight lgt2 = new DirectionalLight(lColor2, lDir2);
    lgt0.setInfluencingBounds(bounds);
    lgt1.setInfluencingBounds(bounds);
    lgt2.setInfluencingBounds(bounds);
    objScale.addChild(lgt0);
    objScale.addChild(lgt1);
    objScale.addChild(lgt2);

    // Create a Transformgroup for the geometry
    TransformGroup objRot = new TransformGroup();
    Transform3D t3d1 = new Transform3D();
    AxisAngle4f rot1 = new AxisAngle4f(0.0f, 1.0f, 0.0f, 45.0f);
    t3d1.setRotation(rot1);
    objRot.setTransform(t3d1);
    objScale.addChild(objRot);

   
    //Create a cylinder
    PolygonAttributes attr = new PolygonAttributes();
    attr.setCullFace(PolygonAttributes.CULL_NONE);
    Appearance ap = new Appearance();
    Material mat = new Material();
    mat.setLightingEnable(true);
    ap.setMaterial(mat);
    ap.setPolygonAttributes(attr);

    Cylinder CylinderObj = new Cylinder(0.5f, 2.2f, ap);
    objRot.addChild(CylinderObj);
    
    //Create a box
    Box BoxObj = new Box(0.8f, 0.8f, 0.8f, ap);
    objRot.addChild(BoxObj);


    // This Transformgroup is used by the mouse manipulators to
    // move the model clip planes.
    TransformGroup objTrans = new TransformGroup();
    objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    objRot.addChild(objTrans);

    // Create the rotate behavior node
    MouseRotate behavior = new MouseRotate(objTrans);
    objTrans.addChild(behavior);
    behavior.setSchedulingBounds(bounds);
    
    // Create the zoom behavior node
    MouseZoom behavior2 = new MouseZoom(objTrans);
    objTrans.addChild(behavior2);
    behavior2.setSchedulingBounds(bounds);
    
    //Create Model Clip
    ModelClip mc = new ModelClip();
    boolean enables[] = {false, false, false, false, false, false};
    Vector4d eqn = new Vector4d(0.0, 1.0, 1.0, 0.0);
    mc.setEnables(enables);
    mc.setPlane(1, eqn);
    mc.setEnable(1, true);
    mc.setInfluencingBounds(bounds);
    objTrans.addChild(mc);


    // Let Java 3D perform optimizations on this scene graph.
    objRoot.compile();

    return objRoot;
  }
  
  public ModelClipTest2 (){
  }

    public void init() {System.setProperty("sun.awt.noerasebackground", "true"); 
	setLayout(new BorderLayout());
	GraphicsConfiguration config =
	    SimpleUniverse.getPreferredConfiguration();

	Canvas3D c = new Canvas3D(config);
	add("Center", c);
	
	// Create a simple scene and attach it to the virtual universe
	BranchGroup scene = createSceneGraph();
	u = new SimpleUniverse(c);

	// This will move the ViewPlatform back a bit so the
	// objects in the scene can be viewed.
	u.getViewingPlatform().setNominalViewingTransform();
	
	u.addBranchGraph(scene);
    }

    public void destroy() {
	u.cleanup();
    }

  
  
  public static void main(String argv[])
  {
    
    BranchGroup group;
    
    new MainFrame(new ModelClipTest2(), 500, 500);
  }
}

