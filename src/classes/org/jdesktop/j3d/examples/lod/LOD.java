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

package org.jdesktop.j3d.examples.lod;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.behaviors.vp.*;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;

public class LOD extends Applet {

    private SimpleUniverse u = null;
    
    public BranchGroup createSceneGraph() {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

	createLights(objRoot);

	// Create the transform group node and initialize it to the
	// identity.  Enable the TRANSFORM_WRITE capability so that
	// our behavior code can modify it at runtime.  Add it to the
	// root of the subgraph.
	TransformGroup objTrans = new TransformGroup();
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	objRoot.addChild(objTrans);

	// Create a switch to hold the different levels of detail
	Switch sw = new Switch(0);
	sw.setCapability(javax.media.j3d.Switch.ALLOW_SWITCH_READ);
	sw.setCapability(javax.media.j3d.Switch.ALLOW_SWITCH_WRITE);


	// Create several levels for the switch, with less detailed
	// spheres for the ones which will be used when the sphere is
	// further away
	sw.addChild(new Sphere(0.4f, Sphere.GENERATE_NORMALS, 40));
	sw.addChild(new Sphere(0.4f, Sphere.GENERATE_NORMALS, 20));
	sw.addChild(new Sphere(0.4f, Sphere.GENERATE_NORMALS, 10));
	sw.addChild(new Sphere(0.4f, Sphere.GENERATE_NORMALS, 3));

	// Add the switch to the main group
	objTrans.addChild(sw);

	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

	// set up the DistanceLOD behavior
	float[] distances = new float[3];
	distances[0] = 5.0f;
	distances[1] = 10.0f;
	distances[2] = 25.0f;
	DistanceLOD lod = new DistanceLOD(distances);
	lod.addSwitch(sw);
	lod.setSchedulingBounds(bounds);
	objTrans.addChild(lod);

        // Have Java 3D perform optimizations on this scene graph.
        objRoot.compile();

	return objRoot;
    }

    private void createLights(BranchGroup graphRoot) {

        // Create a bounds for the light source influence
        BoundingSphere bounds =
            new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

        // Set up the global, ambient light
        Color3f alColor = new Color3f(0.2f, 0.2f, 0.2f);
        AmbientLight aLgt = new AmbientLight(alColor);
        aLgt.setInfluencingBounds(bounds);
        graphRoot.addChild(aLgt);

        // Set up the directional (infinite) light source
        Color3f lColor1 = new Color3f(0.9f, 0.9f, 0.9f);
        Vector3f lDir1  = new Vector3f(1.0f, 1.0f, -1.0f);
        DirectionalLight lgt1 = new DirectionalLight(lColor1, lDir1);
        lgt1.setInfluencingBounds(bounds);
        graphRoot.addChild(lgt1);
    }


    public LOD() {
    }

    public void init() {
	setLayout(new BorderLayout());
        GraphicsConfiguration config =
           SimpleUniverse.getPreferredConfiguration();

        Canvas3D c = new Canvas3D(config);
	add("Center", c);

	// Create a simple scene and attach it to the virtual universe
	BranchGroup scene = createSceneGraph();
	u = new SimpleUniverse(c);

	// only add zoom mouse behavior to viewingPlatform
	ViewingPlatform viewingPlatform = u.getViewingPlatform();
	
        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
	viewingPlatform.setNominalViewingTransform();

	// add orbit behavior to the ViewingPlatform, but disable rotate
	// and translate
	OrbitBehavior orbit = new OrbitBehavior(c,
						OrbitBehavior.REVERSE_ZOOM |
						OrbitBehavior.DISABLE_ROTATE |
						OrbitBehavior.DISABLE_TRANSLATE);
	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
	orbit.setSchedulingBounds(bounds);
	viewingPlatform.setViewPlatformBehavior(orbit);


	u.addBranchGraph(scene);
    }

    public void destroy() {
	u.cleanup();
    }

    //
    // The following allows LOD to be run as an application
    // as well as an applet
    //
    public static void main(String[] args) {
	new MainFrame(new LOD(), 512, 512);
    }
}
