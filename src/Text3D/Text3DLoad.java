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

import com.sun.j3d.loaders.objectfile.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.io.*;
import com.sun.j3d.utils.behaviors.vp.*;
import com.sun.j3d.utils.behaviors.keyboard.*;

public class Text3DLoad extends Applet implements ActionListener {

    private String fontName = "TestFont";
    private String textString = null;
    private double tessellation = 0.0;

    private SimpleUniverse u;

    private Button button;
    private boolean behaviorsOn = false;
    private OrbitBehavior orbit;

    public BranchGroup createSceneGraph() {
	float sl = textString.length();
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

        // Create a Transformgroup to scale all objects so they
        // appear in the scene.
        TransformGroup objScale = new TransformGroup();
        Transform3D t3d = new Transform3D();
        // Assuming uniform size chars, set scale to fit string in view
	t3d.setScale(1.2/sl);
        objScale.setTransform(t3d);
        objRoot.addChild(objScale);

	// Create the transform group node and initialize it to the
	// identity.  Enable the TRANSFORM_WRITE capability so that
	// our behavior code can modify it at runtime.  Add it to the
	// root of the subgraph.
	TransformGroup objTrans = new TransformGroup();
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	objScale.addChild(objTrans);

	Font3D f3d;
	if (tessellation > 0.0) {
	    f3d = new Font3D(new Font(fontName, Font.PLAIN, 2),
			     tessellation,
			     new FontExtrusion());
	}
	else {
	    f3d = new Font3D(new Font(fontName, Font.PLAIN, 2),
			     new FontExtrusion());
	}
	Text3D txt = new Text3D(f3d, textString, 
	     new Point3f( -sl/2.0f, -1.f, -1.f));
	Shape3D sh = new Shape3D();
	Appearance app = new Appearance();
	Material mm = new Material();
	mm.setLightingEnable(true);
	app.setMaterial(mm);
	sh.setGeometry(txt);
	sh.setAppearance(app);
	objTrans.addChild(sh);

	BoundingSphere bounds =
	  new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

        if (false) {
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
	}

        // Set up the background
        Color3f bgColor = new Color3f(0.05f, 0.05f, 0.5f);
        Background bgNode = new Background(bgColor);
        bgNode.setApplicationBounds(bounds);
        objRoot.addChild(bgNode);

	// Set up the ambient light
	Color3f ambientColor = new Color3f(0.3f, 0.3f, 0.3f);
	AmbientLight ambientLightNode = new AmbientLight(ambientColor);
	ambientLightNode.setInfluencingBounds(bounds);
	objRoot.addChild(ambientLightNode);
	
	// Set up the directional lights
	Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f);
	Vector3f light1Direction  = new Vector3f(1.0f, 1.0f, 1.0f);
	Color3f light2Color = new Color3f(1.0f, 1.0f, 0.9f);
	Vector3f light2Direction  = new Vector3f(-1.0f, -1.0f, -1.0f);
	
	DirectionalLight light1
	    = new DirectionalLight(light1Color, light1Direction);
	light1.setInfluencingBounds(bounds);
	objRoot.addChild(light1);
	
	DirectionalLight light2
	    = new DirectionalLight(light2Color, light2Direction);
	light2.setInfluencingBounds(bounds);
	objRoot.addChild(light2);

	return objRoot;
    }

    private void usage()
    {
      System.out.println(
	"Usage: java Text3DLoad [-f fontname] [-t tessellation] [<text>]");
      System.exit(0);
    } // End of usage

    public Text3DLoad() {}

    public Text3DLoad(String args[]) {

        if (args.length == 0) {
// 	  usage();
	  textString = "Java3D";
	}
	else {
	    for (int i = 0 ; i < args.length ; i++) {
		if (args[i].startsWith("-")) {
		    if (args[i].equals("-f")) {
			if (i < args.length - 1) {
			    fontName = args[++i];
			}
			else {
			    usage();
			}
		    }
		    else if (args[i].equals("-t")) {
			if (i < args.length - 1) {
			    tessellation = Double.parseDouble(args[++i]);
			}
			else {
			    usage();
			}
		    }
		    else {
			System.err.println("Argument '" + args[i] +
					   "' ignored.");
		    }
		}
		else {
		    textString = args[i];
		}
	    }
	}

	if (textString == null) {
	  usage();
	}

    }

    public void init() {

        if (textString == null) {
	    textString = "Java3D";
	}
	setLayout(new BorderLayout());

	button = new Button("remove behaviors");
	button.addActionListener(this);
	Panel p = new Panel();
	p.add(button);
	add("South", p);
	
        GraphicsConfiguration config =
           SimpleUniverse.getPreferredConfiguration();

        Canvas3D c = new Canvas3D(config);
	add("Center", c);

	// Create a simple scene and attach it to the virtual universe
	BranchGroup scene = createSceneGraph();
	
	// create a SimpleUniverse with 4 TransformGroups for the mouse
	// behaviors
	u = new SimpleUniverse(c);

	// add the behaviors to the ViewingPlatform
	ViewingPlatform viewingPlatform = u.getViewingPlatform();
	
 	viewingPlatform.setNominalViewingTransform();

	// add orbit behavior to ViewingPlatform
	orbit = new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL |
				  OrbitBehavior.STOP_ZOOM);
	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
	orbit.setSchedulingBounds(bounds);
	viewingPlatform.setViewPlatformBehavior(orbit);

	behaviorsOn = true;

	
	u.addBranchGraph(scene);
    }

  public void destroy() {
      u.cleanup();
  }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == button) {
	    ViewingPlatform v = u.getViewingPlatform();
	    if (behaviorsOn) {
		v.setViewPlatformBehavior(null);
		button.setLabel("add behaviors");
		behaviorsOn = false;
	    }
	    else {
		v.setViewPlatformBehavior(orbit);
		button.setLabel("remove behaviors");
		behaviorsOn = true;
	    }
	}
    }

    //
    // The following allows Text3DLoad to be run as an application
    // as well as an applet
    //
    public static void main(String[] args) {
	new MainFrame(new Text3DLoad(args), 700, 700);
    }
}
