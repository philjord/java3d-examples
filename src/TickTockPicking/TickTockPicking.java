/*
 * $RCSfile$
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved.
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
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.image.TextureLoader;
import javax.media.j3d.*;
import javax.vecmath.*;

public class TickTockPicking extends Applet {

    // path the the texture map image
    private java.net.URL texImage = null;

    private SimpleUniverse u = null;
    
    public BranchGroup createSceneGraph(Canvas3D c) {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

        // Create a Transformgroup to scale all objects so they
        // appear in the scene.
        TransformGroup objScale = new TransformGroup();
        Transform3D t3d = new Transform3D();
        t3d.setScale(0.4);
        objScale.setTransform(t3d);
        objRoot.addChild(objScale);

	// Create a bounds for the background and behaviors
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

	// Create a pair of transform group nodes and initialize them to
	// identity.  Enable the TRANSFORM_WRITE capability so that
	// our behaviors can modify them at runtime.  Add them to the
	// root of the subgraph.
	TransformGroup objTrans1 = new TransformGroup();
	objTrans1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objScale.addChild(objTrans1);

	TransformGroup objTrans2 = new TransformGroup();
	objTrans2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objTrans1.addChild(objTrans2);

	// Create the positioning and scaling transform group node.
	Transform3D t = new Transform3D();
	t.set(0.3, new Vector3d(0.0, -1.5, 0.0));
	TransformGroup objTrans3 = new TransformGroup(t);
	objTrans2.addChild(objTrans3);

	// Create a simple shape leaf node, set it's appearance, and
	// add it to the scene graph.
	Shape3D shape = new Cube();
	Appearance a = new Appearance();
	Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
	Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
	Color3f objColor = new Color3f(0.8f, 0.0f, 0.0f);
	a.setMaterial(new Material(objColor, black, objColor,
				   white, 80.0f));
	shape.setAppearance(a);
	shape.setCapability(shape.ALLOW_APPEARANCE_READ);
	shape.setCapability(shape.ALLOW_APPEARANCE_WRITE);
	objTrans3.addChild(shape);

	// Create a new Behavior object that will perform the desired
	// rotation on the specified transform object and add it into
	// the scene graph.
	Transform3D yAxis1 = new Transform3D();
	yAxis1.rotX(Math.PI/2.0);
	Alpha tickTockAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE |
					Alpha.DECREASING_ENABLE,
					0, 0,
					5000, 2500, 200,
					5000, 2500, 200);

	RotationInterpolator tickTock =
	    new RotationInterpolator(tickTockAlpha, objTrans1, yAxis1,
				     -(float) Math.PI/2.0f,
				     (float) Math.PI/2.0f);
	tickTock.setSchedulingBounds(bounds);
	objTrans2.addChild(tickTock);

	// Create a new Behavior object that will perform the desired
	// rotation on the specified transform object and add it into
	// the scene graph.
	Transform3D yAxis2 = new Transform3D();
	Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE,
					0, 0,
					4000, 0, 0,
					0, 0, 0);

	RotationInterpolator rotator =
	    new RotationInterpolator(rotationAlpha, objTrans2, yAxis2,
				     0.0f, (float) Math.PI*2.0f);
	rotator.setSchedulingBounds(bounds);
	objTrans2.addChild(rotator);

	// Now create the simple picking behavior
	PickHighlightBehavior pickBeh = new 
	  PickHighlightBehavior(c, objRoot, bounds);
 
	// Create a bunch of objects with a behavior and add them
	// into the scene graph.

	int row, col;
	Appearance[][] app = new Appearance[3][3];

	for (row = 0; row < 3; row++)
	    for (col = 0; col < 3; col++)
		app[row][col] = createAppearance(row * 3 + col);

	for (int i = 0; i < 3; i++) {
	    double ypos = (double)(i - 1) * 1.5;
	    for (int j = 0; j < 3; j++) {
		double xpos = (double)(j - 1) * 1.5;
		objScale.addChild(createObject(app[i][j], 0.3,  xpos, ypos));
	    }
	}

        // Have Java 3D perform optimizations on this scene graph.
        objRoot.compile();

	return objRoot;
    }


    private Appearance createAppearance(int idx) {
	Appearance app = new Appearance();

	// Globally used colors
	Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
	Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

	switch (idx) {
	// Unlit solid
	case 0:
	    {
		// Set up the coloring properties
		Color3f objColor = new Color3f(1.0f, 0.2f, 0.4f);
		ColoringAttributes ca = new ColoringAttributes();
		ca.setColor(objColor);
		app.setColoringAttributes(ca);
		break;
	    }


	// Unlit wire frame
	case 1:
	    {
		// Set up the coloring properties
		Color3f objColor = new Color3f(1.0f, 0.4f, 0.0f);
		ColoringAttributes ca = new ColoringAttributes();
		ca.setColor(objColor);
		app.setColoringAttributes(ca);

		// Set up the polygon attributes
		PolygonAttributes pa = new PolygonAttributes();
		pa.setPolygonMode(pa.POLYGON_LINE);
		pa.setCullFace(pa.CULL_NONE);
		app.setPolygonAttributes(pa);
		break;
	    }

	// Unlit points
	case 2:
	    {
		// Set up the coloring properties
		Color3f objColor = new Color3f(1.0f, 1.0f, 0.0f);
		ColoringAttributes ca = new ColoringAttributes();
		ca.setColor(objColor);
		app.setColoringAttributes(ca);

		// Set up the polygon attributes
		PolygonAttributes pa = new PolygonAttributes();
		pa.setPolygonMode(pa.POLYGON_POINT);
		pa.setCullFace(pa.CULL_NONE);
		app.setPolygonAttributes(pa);

		// Set up point attributes
		PointAttributes pta = new PointAttributes();
		pta.setPointSize(5.0f);
		app.setPointAttributes(pta);
		break;
	    }

	// Lit solid
	case 3:
	    {
		// Set up the material properties
		Color3f objColor = new Color3f(0.8f, 0.0f, 0.0f);
		app.setMaterial(new Material(objColor, black, objColor,
					     white, 80.0f));
		break;
	    }

	// Texture mapped, lit solid
	case 4:
	    {
		// Set up the texture map
		TextureLoader tex = new TextureLoader(texImage, this);
		app.setTexture(tex.getTexture());

		TextureAttributes texAttr = new TextureAttributes();
		texAttr.setTextureMode(TextureAttributes.MODULATE);
		app.setTextureAttributes(texAttr);

		// Set up the material properties
		app.setMaterial(new Material(white, black, white, black, 1.0f));
		break;
	    }

	// Transparent, lit solid
	case 5:
	    {
		// Set up the transparency properties
		TransparencyAttributes ta = new TransparencyAttributes();
		ta.setTransparencyMode(ta.BLENDED);
		ta.setTransparency(0.6f);
		app.setTransparencyAttributes(ta);

		// Set up the polygon attributes
		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(pa.CULL_NONE);
		app.setPolygonAttributes(pa);

		// Set up the material properties
		Color3f objColor = new Color3f(0.7f, 0.8f, 1.0f);
		app.setMaterial(new Material(objColor, black, objColor,
					     black, 1.0f));
		break;
	    }

	// Lit solid, no specular
	case 6:
	    {
		// Set up the material properties
		Color3f objColor = new Color3f(0.8f, 0.0f, 0.0f);
		app.setMaterial(new Material(objColor, black, objColor,
					     black, 80.0f));
		break;
	    }

	// Lit solid, specular only
	case 7:
	    {
		// Set up the material properties
		Color3f objColor = new Color3f(0.8f, 0.0f, 0.0f);
		app.setMaterial(new Material(black, black, black,
					     white, 80.0f));
		break;
	    }

	// Another lit solid with a different color
	case 8:
	    {
		// Set up the material properties
		Color3f objColor = new Color3f(0.8f, 0.8f, 0.0f);
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


    private Group createObject(Appearance app, double scale,
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

	// Create a simple shape leaf node and set the appearance
	Shape3D shape = new Tetrahedron();
	shape.setAppearance(app);
	shape.setCapability(shape.ALLOW_APPEARANCE_READ);
	shape.setCapability(shape.ALLOW_APPEARANCE_WRITE);

	// add it to the scene graph.
	spinTg.addChild(shape);

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


    public TickTockPicking() {
    }

    public TickTockPicking(java.net.URL url) {
	texImage = url;
    }

    public void init() {
	if (texImage == null) {
	    // the path to the image for an applet
	    try {
		texImage = new java.net.URL(getCodeBase().toString() +
				       "../images/apimage.jpg");
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
    // The following allows TickTockPicking to be run as an application
    // as well as an applet
    //
    public static void main(String[] args) {
	// the path the the texture map for an application
	java.net.URL url = null;
	try {
	    url = new java.net.URL("file:../images/apimage.jpg");
	}
	catch (java.net.MalformedURLException ex) {
	    System.out.println(ex.getMessage());
	    System.exit(1);
	}
	new MainFrame(new TickTockPicking(url), 700, 700);
    }
}
