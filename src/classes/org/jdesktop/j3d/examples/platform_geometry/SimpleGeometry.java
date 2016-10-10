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

package org.jdesktop.j3d.examples.platform_geometry;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.RotationInterpolator;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.TransparencyAttributes;
import org.jogamp.java3d.utils.applet.MainFrame;
import org.jogamp.java3d.utils.behaviors.mouse.MouseTranslate;
import org.jogamp.java3d.utils.geometry.ColorCube;
import org.jogamp.java3d.utils.geometry.Cylinder;
import org.jogamp.java3d.utils.universe.PlatformGeometry;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.universe.ViewingPlatform;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;

/**
 * This class demonstrates the use of the Universe builder for stand-alone
 * applications along with the use of the PlatformGeometry node that is
 * present in the Java 3D Universe Builder utility.  The standard
 * HelloWorld application is brought up.  A transparent cylinder has been
 * added to the PlatfromGeometry node of the ViewingPlatform and the
 * MouseTranslate utility has been used to allow this sphere to be dragged
 * around the canvas.
 */
public class SimpleGeometry extends Applet {

    SimpleUniverse u = null;

    public BranchGroup createSceneGraph() {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

        // Create a Transformgroup to scale all objects so they
        // appear in the scene.
        TransformGroup objScale = new TransformGroup();
        Transform3D t3d = new Transform3D();
        t3d.setScale(0.4);
        objScale.setTransform(t3d);
        objRoot.addChild(objScale);

	// Create the transform group node and initialize it to the
	// identity.  Enable the TRANSFORM_WRITE capability so that
	// our behavior code can modify it at runtime.  Add it to the
	// root of the subgraph.
	TransformGroup objTrans = new TransformGroup();
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objScale.addChild(objTrans);

	// Create a simple shape leaf node, add it to the scene graph.
	objTrans.addChild(new ColorCube());

	// Create a new Behavior object that will perform the desired
	// operation on the specified transform object and add it into
	// the scene graph.
	Transform3D yAxis = new Transform3D();
	Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE,
					0, 0,
					4000, 0, 0,
					0, 0, 0);

	RotationInterpolator rotator =
	    new RotationInterpolator(rotationAlpha, objTrans, yAxis,
				     0.0f, (float) Math.PI*2.0f);
	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
	rotator.setSchedulingBounds(bounds);
	objTrans.addChild(rotator);


        // Have Java 3D perform optimizations on this scene graph.
        objRoot.compile();

	return objRoot;
    }

    /*
     * Create the geometry to add to the platform geometry. 
     */
    PlatformGeometry createAimer() {

        PlatformGeometry pg = new PlatformGeometry();

        // This TransformGroup will be used by the MouseTranslate
        // utiltiy to move the cylinder around the canvas.  when the
        // the user holds down mouse button 3.
        TransformGroup moveTG = new TransformGroup();
        moveTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        moveTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        MouseTranslate mouseT = new MouseTranslate(moveTG);
	moveTG.addChild(mouseT);
        BoundingSphere bounds =
          new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
        mouseT.setSchedulingBounds(bounds);
        pg.addChild(moveTG);

        // This TransformGroup is used to place the cylinder in the scene.
        // The cylinder will be rotated 90 degrees so it will appear as
        // a circle on the screen (could be made into a nice gun site...).
        // The cylinder is also displaced a little in Z so it is in front
        // of the viewer.
        Transform3D xForm = new Transform3D();
        xForm.rotX(Math.PI/2.0);
        xForm.setTranslation(new Vector3d(0.0, 0.0, -0.7));
        TransformGroup placementTG = new TransformGroup(xForm);
        moveTG.addChild(placementTG);

        // Create the cylinder - make it thin and transparent.
        Appearance cylinderAppearance = new Appearance();
        TransparencyAttributes transAttrs =
           new TransparencyAttributes(TransparencyAttributes.FASTEST, 0.5f);
	//        cylinderAppearance.setTransparencyAttributes(transAttrs);
        Cylinder aimer = new Cylinder(0.06f, 0.005f, 0, cylinderAppearance);
        placementTG.addChild(aimer);

        return pg;
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
        
        PlatformGeometry pg = createAimer();

        // Now set the just created PlatformGeometry.
        ViewingPlatform vp = u.getViewingPlatform();
        vp.setPlatformGeometry(pg);

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        u.getViewingPlatform().setNominalViewingTransform();

        // Add everthing to the scene graph - it will now be displayed.
	u.addBranchGraph(scene);
    }

    public SimpleGeometry(String[] args) {
    }

    public SimpleGeometry() {
    }

    public void destroy() {
	u.cleanup();
    }

    public static void main(String[] args) {System.setProperty("sun.awt.noerasebackground", "true"); 
	new MainFrame(new SimpleGeometry(args), 256, 256);
    }
}
