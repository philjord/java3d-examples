/*
 * $RCSfile$
 *
 * Copyright (c) 2004 Sun Microsystems, Inc. All rights reserved.
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

import java.applet.Applet;
import java.awt.BorderLayout;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.behaviors.*;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.Enumeration;
import java.awt.*;
import java.lang.String;

public class PickText3DBounds extends Applet {

    private SimpleUniverse u = null;
    
  public BranchGroup createSceneGraph(Canvas3D canvas) {
    Color3f eColor    = new Color3f(0.0f, 0.0f, 0.0f);
    Color3f sColor    = new Color3f(1.0f, 1.0f, 1.0f);
    Color3f objColor  = new Color3f(0.6f, 0.6f, 0.6f);
    Color3f lColor1   = new Color3f(1.0f, 0.0f, 0.0f);
    Color3f lColor2   = new Color3f(0.0f, 1.0f, 0.0f);
    Color3f alColor   = new Color3f(0.2f, 0.2f, 0.2f);
    Color3f bgColor   = new Color3f(0.05f, 0.05f, 0.2f);
    
    Transform3D t;
    
    // Create the root of the branch graph
    BranchGroup objRoot = new BranchGroup();

    // Create a Transformgroup to scale all objects so they
    // appear in the scene.
    TransformGroup objScale = new TransformGroup();
    Transform3D t3d = new Transform3D();
    t3d.setScale(0.4);
    objScale.setTransform(t3d);
    objRoot.addChild(objScale);
    
    // Create a bounds for the background and lights
    BoundingSphere bounds =
      new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
    
    // Set up the background
    Background bg = new Background(bgColor);
    bg.setApplicationBounds(bounds);
    objScale.addChild(bg);
    
    Material m = new Material(objColor, eColor, objColor, sColor, 100.0f);
    Appearance a = new Appearance();
    m.setLightingEnable(true);
    a.setMaterial(m);
    Font3D f3d = new Font3D(new Font("TestFont", Font.PLAIN, 1),
			    new FontExtrusion());
    Text3D txt = new Text3D(f3d, new String("TEXT3D"),
			    new Point3f(-2.0f, 0.0f, 0.0f));
    //    txt.setCapability(Geometry.ALLOW_INTERSECT);
    Shape3D s3D = new Shape3D();
    s3D.setGeometry(txt);
    s3D.setAppearance(a);

    // Create a transform group node and initialize it to the
    // identity.  Enable the TRANSFORM_WRITE capability so that
    // our behavior code can modify it at runtime.
    TransformGroup spinTg = new TransformGroup();
    spinTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    spinTg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    spinTg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);

    spinTg.addChild(s3D);
    objScale.addChild(spinTg);
    
    // Create the transform group node for the each light and initialize
    // it to the identity.  Enable the TRANSFORM_WRITE capability so that
    // our behavior code can modify it at runtime.  Add them to the root
    // of the subgraph.

    // Create transformations for the positional lights
    t = new Transform3D();
    Vector3d lPos1 =  new Vector3d(0.0, 0.0, 2.0);
    t.set(lPos1);
    TransformGroup l1Trans = new TransformGroup(t);
    l1Trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    l1Trans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    l1Trans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
    objScale.addChild(l1Trans);
    
    t = new Transform3D();
    Vector3d lPos2 = new Vector3d(0.5, 0.8, 2.0);
    t.set(lPos2);
    TransformGroup l2Trans = new TransformGroup(t);
    l2Trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    l2Trans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    l2Trans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);

    objScale.addChild(l2Trans);

    // Create Geometry for point lights
    ColoringAttributes caL1 = new ColoringAttributes();
    ColoringAttributes caL2 = new ColoringAttributes();
    caL1.setColor(lColor1);
    caL2.setColor(lColor2);
    Appearance appL1 = new Appearance();
    Appearance appL2 = new Appearance();
    appL1.setColoringAttributes(caL1);
    appL2.setColoringAttributes(caL2);
    l1Trans.addChild(new Sphere(0.05f,
	Sphere.GENERATE_NORMALS, 15, appL1));
    l2Trans.addChild(new Sphere(0.05f,
	Sphere.GENERATE_NORMALS, 15, appL2));
    
    // Create lights
    AmbientLight aLgt = new AmbientLight(alColor);
    
    Light lgt1;
    Light lgt2;

    Point3f lPoint  = new Point3f(0.0f, 0.0f, 0.0f);
    Point3f atten = new Point3f(1.0f, 0.0f, 0.0f);
    lgt1 = new PointLight(lColor1, lPoint, atten);
    lgt2 = new PointLight(lColor2, lPoint, atten);
    
    // Set the influencing bounds
    aLgt.setInfluencingBounds(bounds);
    lgt1.setInfluencingBounds(bounds);
    lgt2.setInfluencingBounds(bounds);
    
    // Add the lights into the scene graph
    objScale.addChild(aLgt);
    l1Trans.addChild(lgt1);
    l2Trans.addChild(lgt2);


    PickRotateBehavior behavior1 =
	new PickRotateBehavior(objRoot, canvas, bounds);
    behavior1.setMode(PickTool.BOUNDS);
    objRoot.addChild(behavior1);

    PickZoomBehavior behavior2 =
	new PickZoomBehavior(objRoot, canvas, bounds);
    behavior2.setMode(PickTool.BOUNDS);
    objRoot.addChild(behavior2);

    PickTranslateBehavior behavior3 =
	new PickTranslateBehavior(objRoot, canvas, bounds);
    behavior3.setMode(PickTool.BOUNDS);
    objRoot.addChild(behavior3);

    
    // Let Java 3D perform optimizations on this scene graph.
    objRoot.compile();
    
    return objRoot;
  }
  
  public PickText3DBounds() {
  }

    public void init() {
	setLayout(new BorderLayout());
	GraphicsConfiguration config =
	    SimpleUniverse.getPreferredConfiguration();
	
	Canvas3D c = new Canvas3D(config);
	add("Center", c);
	
	u = new SimpleUniverse(c);
	BranchGroup scene = createSceneGraph(c);
	
	// This will move the ViewPlatform back a bit so the
	// objects in the scene can be viewed.
	u.getViewingPlatform().setNominalViewingTransform();
	
	u.addBranchGraph(scene);
    }

    public void destroy() {
	u.cleanup();
    }
  
  //
  // The following allows Text3DMotion to be run as an application
  // as well as an applet
    //
  public static void main(String[] args) {
    new MainFrame(new PickText3DBounds(), 700, 700);
  }
}
