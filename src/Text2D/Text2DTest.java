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
import java.awt.*;
import java.awt.Font;
import java.awt.event.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Text2D;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;

public class Text2DTest extends Applet {

    private SimpleUniverse u = null;
    
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

	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

	TransformGroup textTranslationGroup;
	Transform3D textTranslation;
	float yPos = -.5f;
	Shape3D textObject = new Text2D("Rotating Yellow Text",
					new Color3f(1f, 1f, 0f),
					"Serif",
					60,
					Font.BOLD);
	Appearance app = textObject.getAppearance();
	
	PolygonAttributes pa = app.getPolygonAttributes();
	if (pa == null)
	    pa = new PolygonAttributes();
	pa.setCullFace(PolygonAttributes.CULL_NONE);
	if (app.getPolygonAttributes() == null)
	    app.setPolygonAttributes(pa);
	objTrans.addChild(textObject);

	
	textTranslation = new Transform3D();
	textTranslation.setTranslation(new Vector3f(0f, yPos, 0f));
	textTranslationGroup = new TransformGroup(textTranslation);
	textTranslationGroup.addChild(objTrans);
	objScale.addChild(textTranslationGroup);
	yPos += .5f;

	/* Blue 40point text*/
	textObject = new Text2D("Blue 40point Text",
				new Color3f(0f, 0f, 1f),
				"Serif",
				40,
				Font.BOLD);
	textTranslation = new Transform3D();
	textTranslation.setTranslation(new Vector3f(0f, yPos, 0f));
	textTranslationGroup = new TransformGroup(textTranslation);
	textTranslationGroup.addChild(textObject);
	objScale.addChild(textTranslationGroup);
	yPos += .5f;

	/* Green italic text*/
	textObject = new Text2D("Green Italic Text",
				new Color3f(0f, 1f, 0f),
				"Serif",
				70,
				Font.ITALIC);
	textTranslation = new Transform3D();
	textTranslation.setTranslation(new Vector3f(0f, yPos, 0f));
	textTranslationGroup = new TransformGroup(textTranslation);
	textTranslationGroup.addChild(textObject);
	objScale.addChild(textTranslationGroup);
	yPos += .5f;
	
	
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
	rotator.setSchedulingBounds(bounds);
	objTrans.addChild(rotator);

	return objRoot;
    }

    public Text2DTest() {
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
	MoverBehavior navigator =
	   new MoverBehavior(u.getViewingPlatform().getViewPlatformTransform());
	scene.addChild(navigator);

        // Have Java 3D perform optimizations on this scene graph.
        scene.compile();

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        u.getViewingPlatform().setNominalViewingTransform();

	u.addBranchGraph(scene);
    }

    public void destroy() {
	u.cleanup();
    }

    //
    // The following allows HelloUniverse to be run as an application
    // as well as an applet
    //
    public static void main(String[] args) {
	new MainFrame(new Text2DTest(), 256, 256);
    }
}
