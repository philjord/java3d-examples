/*
 * $RCSfile$
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. All rights reserved.
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

package org.jdesktop.j3d.examples.sphere_motion;

import java.applet.Applet;
import java.awt.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.Enumeration;

public class SphereMotion extends Applet {

    // Constants for type of light to use
    private static final int DIRECTIONAL_LIGHT = 0;
    private static final int POINT_LIGHT = 1;
    private static final int SPOT_LIGHT = 2;

    // Flag indicates type of lights: directional, point, or spot
    // lights.  This flag is set based on command line argument
    private static int lightType = POINT_LIGHT;

    private SimpleUniverse u = null;

    public BranchGroup createSceneGraph(SimpleUniverse u) {
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

	// Create a Sphere object, generate one copy of the sphere,
	// and add it into the scene graph.
	Material m = new Material(objColor, eColor, objColor, sColor, 100.0f);
	Appearance a = new Appearance();
	m.setLightingEnable(true);
	a.setMaterial(m);
	Sphere sph = new Sphere(1.0f, Sphere.GENERATE_NORMALS, 80, a);
	objScale.addChild(sph);

	// Create the transform group node for the each light and initialize
	// it to the identity.  Enable the TRANSFORM_WRITE capability so that
	// our behavior code can modify it at runtime.  Add them to the root
	// of the subgraph.
	TransformGroup l1RotTrans = new TransformGroup();
	l1RotTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objScale.addChild(l1RotTrans);

	TransformGroup l2RotTrans = new TransformGroup();
	l2RotTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objScale.addChild(l2RotTrans);

	// Create transformations for the positional lights
	t = new Transform3D();
	Vector3d lPos1 =  new Vector3d(0.0, 0.0, 2.0);
	t.set(lPos1);
	TransformGroup l1Trans = new TransformGroup(t);
	l1RotTrans.addChild(l1Trans);

	t = new Transform3D();
	Vector3d lPos2 = new Vector3d(0.5, 0.8, 2.0);
	t.set(lPos2);
	TransformGroup l2Trans = new TransformGroup(t);
	l2RotTrans.addChild(l2Trans);

	// Create Geometry for point lights
	ColoringAttributes caL1 = new ColoringAttributes();
	ColoringAttributes caL2 = new ColoringAttributes();
	caL1.setColor(lColor1);
	caL2.setColor(lColor2);
	Appearance appL1 = new Appearance();
	Appearance appL2 = new Appearance();
	appL1.setColoringAttributes(caL1);
	appL2.setColoringAttributes(caL2);
	l1Trans.addChild(new Sphere(0.05f, appL1));
	l2Trans.addChild(new Sphere(0.05f, appL2));

	// Create lights
	AmbientLight aLgt = new AmbientLight(alColor);

	Light lgt1 = null;
	Light lgt2 = null;

	Point3f lPoint  = new Point3f(0.0f, 0.0f, 0.0f);
	Point3f atten = new Point3f(1.0f, 0.0f, 0.0f);
	Vector3f lDirect1 = new Vector3f(lPos1);
	Vector3f lDirect2 = new Vector3f(lPos2);
	lDirect1.negate();
	lDirect2.negate();

	switch (lightType) {
	case DIRECTIONAL_LIGHT:
	    lgt1 = new DirectionalLight(lColor1, lDirect1);
	    lgt2 = new DirectionalLight(lColor2, lDirect2);
	    break;
	case POINT_LIGHT:
	    lgt1 = new PointLight(lColor1, lPoint, atten);
	    lgt2 = new PointLight(lColor2, lPoint, atten);
	    break;
	case SPOT_LIGHT:
	    lgt1 = new SpotLight(lColor1, lPoint, atten, lDirect1,
				 25.0f * (float)Math.PI / 180.0f, 10.0f);
	    lgt2 = new SpotLight(lColor2, lPoint, atten, lDirect2,
				 25.0f * (float)Math.PI / 180.0f, 10.0f);
	    break;
	}

	// Set the influencing bounds
	aLgt.setInfluencingBounds(bounds);
	lgt1.setInfluencingBounds(bounds);
	lgt2.setInfluencingBounds(bounds);

	// Add the lights into the scene graph
	objScale.addChild(aLgt);
	l1Trans.addChild(lgt1);
	l2Trans.addChild(lgt2);

	// Create a new Behavior object that will perform the desired
	// operation on the specified transform object and add it into the
	// scene graph.
	Transform3D yAxis = new Transform3D();
	Alpha rotor1Alpha = new Alpha(-1, Alpha.INCREASING_ENABLE,
				     0, 0,
				     4000, 0, 0,
				     0, 0, 0);
	RotationInterpolator rotator1 =
	    new RotationInterpolator(rotor1Alpha,
				     l1RotTrans,
				     yAxis,
				     0.0f, (float) Math.PI*2.0f);
	rotator1.setSchedulingBounds(bounds);
	l1RotTrans.addChild(rotator1);

	// Create a new Behavior object that will perform the desired
	// operation on the specified transform object and add it into the
	// scene graph.
	Alpha rotor2Alpha = new Alpha(-1, Alpha.INCREASING_ENABLE,
				     0, 0,
				     1000, 0, 0,
				     0, 0, 0);
	RotationInterpolator rotator2 =
	    new RotationInterpolator(rotor2Alpha,
				     l2RotTrans,
				     yAxis,
				     0.0f, 0.0f);
	bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
	rotator2.setSchedulingBounds(bounds);
	l2RotTrans.addChild(rotator2);

	// Create a position interpolator and attach it to the view
	// platform
	TransformGroup vpTrans =
	    u.getViewingPlatform().getViewPlatformTransform();
	Transform3D axisOfTranslation = new Transform3D();
	Alpha transAlpha = new Alpha(-1,
				      Alpha.INCREASING_ENABLE |
				      Alpha.DECREASING_ENABLE,
				      0, 0,
				      5000, 0, 0,
				      5000, 0, 0);
	axisOfTranslation.rotY(-Math.PI/2.0);
	PositionInterpolator translator =
	    new PositionInterpolator(transAlpha,
				     vpTrans,
				     axisOfTranslation,
				     2.0f, 3.5f);
	translator.setSchedulingBounds(bounds);
	objScale.addChild(translator);

        // Let Java 3D perform optimizations on this scene graph.
        objRoot.compile();

	return objRoot;
    }

    public SphereMotion() {
    }

    public void init() {
	setLayout(new BorderLayout());
        GraphicsConfiguration config =
           SimpleUniverse.getPreferredConfiguration();

        Canvas3D c = new Canvas3D(config);
	add("Center", c);

	u = new SimpleUniverse(c);
	BranchGroup scene = createSceneGraph(u);

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        u.getViewingPlatform().setNominalViewingTransform();

	u.addBranchGraph(scene);
    }

    public void destroy() {
	u.cleanup();
    }

    //
    // The following allows SphereMotion to be run as an application
    // as well as an applet
    //
    public static void main(String[] args) {
        // Parse the Input Arguments
	String usage = "Usage: java SphereMotion [-point | -spot | -dir]";
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                if (args[i].equals("-point")) {
		    System.out.println("Using point lights");
                    lightType = POINT_LIGHT;
                }
		else if (args[i].equals("-spot")) {
		    System.out.println("Using spot lights");
                    lightType = SPOT_LIGHT;
                }
		else if (args[i].equals("-dir")) {
		    System.out.println("Using directional lights");
                    lightType = DIRECTIONAL_LIGHT;
                }
		else {
		    System.out.println(usage);
                    System.exit(0);
                }
            }
	    else {
		System.out.println(usage);
		System.exit(0);
	    }
        }

	new MainFrame(new SphereMotion(), 700, 700);
    }
}
