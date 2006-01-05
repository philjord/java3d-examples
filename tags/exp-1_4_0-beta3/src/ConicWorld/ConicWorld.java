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

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.image.TextureLoader;
import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * ConicWorld creates spheres, cylinders, and cones of different resolutions
 * and colors.  Demonstrates the use of the various geometry creation
 * constructors found in the com.sun.j3d.utils.geometry package.
 */
public class ConicWorld extends Applet {

    private java.net.URL texImage = null;

    private SimpleUniverse u = null;

    public BranchGroup createSceneGraph(Canvas3D c) {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

	// Create a bounds for the background and behaviors
	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

	// Set up the background
	Color3f bgColor = new Color3f(0.05f, 0.05f, 0.2f);
	Background bg = new Background(bgColor);
	bg.setApplicationBounds(bounds);
	objRoot.addChild(bg);

	// Set up the global lights
	Color3f lColor1 = new Color3f(0.7f, 0.7f, 0.7f);
	Vector3f lDir1  = new Vector3f(-1.0f, -1.0f, -1.0f);
	Color3f alColor = new Color3f(0.2f, 0.2f, 0.2f);

	AmbientLight aLgt = new AmbientLight(alColor);
	aLgt.setInfluencingBounds(bounds);
	DirectionalLight lgt1 = new DirectionalLight(lColor1, lDir1);
	lgt1.setInfluencingBounds(bounds);
	objRoot.addChild(aLgt);
	objRoot.addChild(lgt1);

	// Create a bunch of objects with a behavior and add them
	// into the scene graph.

	int row, col;
	int numRows = 3, numCols = 5;
	Appearance[][] app = new Appearance[numRows][numCols];

	for (row = 0; row < numRows; row++)
	    for (col = 0; col < numCols; col++)
		app[row][col] = createAppearance(row * numCols + col);

	// Space between each row/column
	double xspace = 2.0 / ((double)numCols - 1.0);
	double yspace = 2.0 / ((double)numRows - 1.0);

	for (int i = 0; i < numRows; i++) {
	    double ypos = ((double)i * yspace - 1.0) * 0.6;
	    for (int j = 0; j < numCols; j++) {
		double xpos = xpos = ((double)j * xspace - 1.0) * 0.6;
		objRoot.addChild(createObject(i, j, app[i][j],
                                       0.1,  xpos, ypos));
	    }
	}

        // Let Java 3D perform optimizations on this scene graph.
        objRoot.compile();

	return objRoot;
    }


    private Appearance createAppearance(int idx) {
	Appearance app = new Appearance();

	// Globally used colors
	Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
	Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

	idx = idx % 5;

	switch (idx) {
	// Lit solid
	case 0:
	    {
		// Set up the material properties
		Color3f objColor = new Color3f(0.8f, 0.0f, 0.0f);
		app.setMaterial(new Material(objColor, black, objColor,
					     white, 80.0f));
		break;
	    }
	// Lit solid, no specular
	case 1:
	    {
		// Set up the material properties
		Color3f objColor = new Color3f(0.0f, 0.8f, 0.0f);
		app.setMaterial(new Material(objColor, black, objColor,
					     white, 80.0f));
		break;
	    }

	// Lit solid, specular only
	case 2:
	    {
		// Set up the material properties
		Color3f objColor = new Color3f(0.0f, 0.8f, 0.8f);
		app.setMaterial(new Material(black, black, objColor,
					     white, 80.0f));
		break;
	    }

	// Texture mapped, lit solid
	case 3:
	    {
		// Set up the texture map
		TextureLoader tex = new TextureLoader(texImage, this);
		app.setTexture(tex.getTexture());

		// Set up the material properties
		app.setMaterial(new Material(white, black, white, black, 1.0f));
		TextureAttributes texAttr = new TextureAttributes();
		texAttr.setTextureMode(TextureAttributes.MODULATE);
		app.setTextureAttributes(texAttr);

		break;
	    }


	// Another lit solid with a different color
	case 4:
	    {
		// Set up the material properties
		Color3f objColor = new Color3f(1.0f, 1.0f, 0.0f);
		app.setMaterial(new Material(objColor, black, objColor,
					     white, 80.0f));
		break;
	    }

	default:
	    {
		ColoringAttributes ca = new ColoringAttributes();
		ca.setColor(new Color3f(0.0f, 1.0f, 0.0f));
		app.setColoringAttributes(ca);
	    }
	}

	return app;
    }


    private Group createObject(int i, int j, Appearance app, double scale,
			       double xpos, double ypos) {

	// Create a transform group node to scale and position the object.
	Transform3D t = new Transform3D();
	t.set(scale, new Vector3d(xpos, ypos, 0.0));
	TransformGroup objTrans = new TransformGroup(t);

	// Create a second transform group node and initialize it to the
	// identity.  Enable the TRANSFORM_WRITE capability so that
	// our behavior code can modify it at runtime.
	TransformGroup spinTg = new TransformGroup();
	spinTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	spinTg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	Primitive obj = null;

	if (i % 3 == 2){
	  obj = (Primitive) new Sphere(1.0f, 
				       Sphere.GENERATE_NORMALS | 
				       Sphere.GENERATE_TEXTURE_COORDS, 
				       j*8 + 4, app);
	}
	else 
	  if (i % 3 == 1){
	  obj = (Primitive) new Cylinder(1.0f, 2.0f,
			     Cylinder.GENERATE_TEXTURE_COORDS | 
			     Cylinder.GENERATE_NORMALS, 
					 j*8+4,j*8+4,
					 app);
	}
	else 
	  if (i % 3 == 0){
	  obj = (Primitive) new Cone(1.0f, 2.0f, 
			 Cone.GENERATE_NORMALS |
			 Cone.GENERATE_TEXTURE_COORDS, 
				     j*8+4,j*8+4,
				     app);
	}

	// add it to the scene graph.
	spinTg.addChild(obj);

	// Create a new Behavior object that will perform the desired
	// operation on the specified transform object and add it into
	// the scene graph.
	Transform3D yAxis = new Transform3D();
	Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE,
					0, 0,
					5000, 0, 0,
					0, 0, 0);

	RotationInterpolator rotator =
	    new RotationInterpolator(rotationAlpha, spinTg, yAxis,
				     0.0f, (float) Math.PI*2.0f);

	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

	rotator.setSchedulingBounds(bounds);

	// Add the behavior and the transform group to the object
	objTrans.addChild(rotator);
	objTrans.addChild(spinTg);

	return objTrans;
    }


    public ConicWorld() {
    }

    public ConicWorld(java.net.URL url) {
        texImage = url;
    }

    public void init() {
        if (texImage == null) {
	    // the path to the image for an applet
	    try {
	        texImage = new java.net.URL(getCodeBase().toString() +
					  "../images/earth.jpg");
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
	BranchGroup scene = createSceneGraph(c);
	u = new SimpleUniverse(c);

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        u.getViewingPlatform().setNominalViewingTransform();

	u.addBranchGraph(scene);
    }

    public void destroy() {
        u.cleanup();
    }

    //
    // The following allows ConicWorld to be run as an application
    // as well as an applet
    //
    public static void main(String[] args) {
        // the path to the image file for an application
	java.net.URL url = null;
	try {
	    url = new java.net.URL("file:../images/earth.jpg");
	}
	catch (java.net.MalformedURLException ex) {
	    System.out.println(ex.getMessage());
	    System.exit(1);
	}
	new MainFrame(new ConicWorld(url), 700, 700);
    }
}


