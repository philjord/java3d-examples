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

package org.jdesktop.j3d.examples.morphing;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.io.*;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.IncorrectFormatException;

public class Morphing extends Applet {

    private java.net.URL[] objFiles = null;

    private SimpleUniverse u = null;

    private BranchGroup createSceneGraph() {
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
	Color3f bgColor = new Color3f(0.05f, 0.05f, 0.2f);
	Background bg = new Background(bgColor);
	bg.setApplicationBounds(bounds);
	objScale.addChild(bg);

	// Set up the global lights
	Color3f lColor1 = new Color3f(0.7f, 0.7f, 0.7f);
	Vector3f lDir1  = new Vector3f(-1.0f, -1.0f, -1.0f);
	Color3f alColor = new Color3f(0.2f, 0.2f, 0.2f);

	AmbientLight aLgt = new AmbientLight(alColor);
	aLgt.setInfluencingBounds(bounds);
	DirectionalLight lgt1 = new DirectionalLight(lColor1, lDir1);
	lgt1.setInfluencingBounds(bounds);
	objScale.addChild(aLgt);
	objScale.addChild(lgt1);

	//
	// Create the transform group nodes for the 3 original objects
	// and the morphed object.  Add them to the root of the
	// branch graph.
	//
	TransformGroup objTrans[] = new TransformGroup[4];

	for(int i=0; i<4; i++) {
	    objTrans[i] = new TransformGroup();
	    objScale.addChild(objTrans[i]);
	}

	Transform3D tr = new Transform3D();
	Transform3D rotX90 = new Transform3D();
	rotX90.rotX(90.0 * Math.PI / 180.0);

	objTrans[0].getTransform(tr);
	tr.setTranslation(new Vector3d(-2.0, 1.5, -2.0));
	tr.mul(rotX90);
	objTrans[0].setTransform(tr);

	objTrans[1].getTransform(tr);
	tr.setTranslation(new Vector3d(0.0, 1.5, -2.0));
	tr.mul(rotX90);
	objTrans[1].setTransform(tr);

	objTrans[2].getTransform(tr);
	tr.setTranslation(new Vector3d(2.0, 1.5, -2.0));
	tr.mul(rotX90);
	objTrans[2].setTransform(tr);

	objTrans[3].getTransform(tr);
	tr.setTranslation(new Vector3d(0.0, -2.0, -2.0));
	tr.mul(rotX90);
	objTrans[3].setTransform(tr);


	// Now load the object files
	Scene s[] = new Scene[3];
	GeometryArray g[] = new GeometryArray[3];
	Shape3D shape[] = new Shape3D[3];
	ObjectFile loader = new ObjectFile(ObjectFile.RESIZE);
	for(int i=0; i<3; i++) {
	    s[i] = null;
	    g[i] = null;
	    shape[i] = null;
	}

	for(int i=0; i<3;i++) {
	    try {
		s[i] = loader.load(objFiles[i]);
	    }
	    catch (FileNotFoundException e) {
	      System.err.println(e);
	      System.exit(1);
	    }
	    catch (ParsingErrorException e) {
	      System.err.println(e);
	      System.exit(1);
	    }
	    catch (IncorrectFormatException e) {
	      System.err.println(e);
	      System.exit(1);
	    }

            BranchGroup b = s[i].getSceneGroup();
	    shape[i] = (Shape3D) b.getChild(0);
	    g[i] = (GeometryArray) shape[i].getGeometry();

	    shape[i].setGeometry(g[i]);
	    objTrans[i].addChild(b);
	}

	//
	// Create a Morph node, and set the appearance and input geometry
	// arrays.  Set the Morph node's capability bits to allow the weights
	// to be modified at runtime.
	//
	Appearance app = new Appearance();
	Color3f objColor = new Color3f(1.0f, 0.7f, 0.8f);
	Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
	app.setMaterial(new Material(objColor, black, objColor, black, 80.0f));
	Morph morph = new Morph(g, app);
	morph.setCapability(Morph.ALLOW_WEIGHTS_READ);
	morph.setCapability(Morph.ALLOW_WEIGHTS_WRITE);

	objTrans[3].addChild(morph);

	// Now create the Alpha object that controls the speed of the
	// morphing operation.
	Alpha morphAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE |
				     Alpha.DECREASING_ENABLE,
				     0, 0,
				     2000, 1000, 200,
				     2000, 1000, 200);

	// Finally, create the morphing behavior
	MorphingBehavior mBeh = new MorphingBehavior(morphAlpha, morph);
	mBeh.setSchedulingBounds(bounds);
	objScale.addChild(mBeh);

	return objRoot;
    }

    public Morphing() {}

    public Morphing(java.net.URL[] urls) {
        objFiles = urls;
    }

    public void init() {
        if (objFiles == null) {
	    objFiles = new java.net.URL[3];
	    // the path to the image for an applet
	    String path = getCodeBase().toString();
	    try {
	        objFiles[0] = new java.net.URL(path + "hand1.obj");
		objFiles[1] = new java.net.URL(path + "hand2.obj");
		objFiles[2] = new java.net.URL(path + "hand3.obj");
	    }
	    catch (java.net.MalformedURLException ex) {
	        System.out.println(ex.getMessage());
		System.exit(1);
	    }
	}

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

    public static void main(String[] args) {
        java.net.URL[] urls = new java.net.URL[3];
	// the path to the image file for an application
	try {
	    urls[0] = new java.net.URL("file:./hand1.obj");
	    urls[1] = new java.net.URL("file:./hand2.obj");
	    urls[2] = new java.net.URL("file:./hand3.obj");
	}
	catch (java.net.MalformedURLException ex) {
	    System.out.println(ex.getMessage());
	    System.exit(1);
	}
	new MainFrame(new Morphing(urls), 700, 700);
    }
}
