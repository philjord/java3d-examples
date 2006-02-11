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

package org.jdesktop.j3d.examples.glsl_shader;

import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.shader.StringIO;
import java.applet.Applet;
import java.awt.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.Enumeration;
import java.io.*;
import com.sun.j3d.utils.behaviors.vp.*;
import java.net.URL;
import java.net.MalformedURLException;
import org.jdesktop.j3d.examples.Resources;

public class ObjLoadGLSL extends Applet {

    private String shaderName = "polkadot3d";
    private boolean spin = false;
    private boolean noTriangulate = false;
    private boolean noStripify = false;
    private double creaseAngle = 60.0;
    private URL filename = null;
    private SimpleUniverse u;
    private BoundingSphere bounds;

    public BranchGroup createSceneGraph() {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

        // Create a Transformgroup to scale all objects so they
        // appear in the scene.
        TransformGroup objScale = new TransformGroup();
        Transform3D t3d = new Transform3D();
        t3d.setScale(0.7);
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

	int flags = ObjectFile.RESIZE;
	if (!noTriangulate) flags |= ObjectFile.TRIANGULATE;
	if (!noStripify) flags |= ObjectFile.STRIPIFY;
	ObjectFile f = new ObjectFile(flags, 
				      (float)(creaseAngle * Math.PI / 180.0));
	Scene s = null;
	try {
	    s = f.load(filename);
	}
	catch (FileNotFoundException e) {
	    throw new RuntimeException(e);
	}
	catch (ParsingErrorException e) {
	    throw new RuntimeException(e);
	}
	catch (IncorrectFormatException e) {
	    throw new RuntimeException(e);
	}
	  
	// Set vertex and fragment shader program for all Shape3D nodes in scene
	String vertexProgram = null;
	String fragmentProgram = null;
	try {
	    vertexProgram = StringIO.readFully(Resources.getResource("resources/glsl_shader/" + shaderName + ".vert"));
	    fragmentProgram = StringIO.readFully(Resources.getResource("resources/glsl_shader/" + shaderName + ".frag"));
	}
	catch (IOException e) {
	    throw new RuntimeException(e);
	}
	Shader[] shaders = new Shader[2];
	shaders[0] = new SourceCodeShader(Shader.SHADING_LANGUAGE_GLSL,
					  Shader.SHADER_TYPE_VERTEX,
					  vertexProgram);
	shaders[1] = new SourceCodeShader(Shader.SHADING_LANGUAGE_GLSL,
					  Shader.SHADER_TYPE_FRAGMENT,
					  fragmentProgram);
	ShaderProgram shaderProgram = new GLSLShaderProgram();
	shaderProgram.setShaders(shaders);
	setShaderProgram(s.getSceneGroup(), shaderProgram);

	objTrans.addChild(s.getSceneGroup());

	bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

        if (spin) {
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

	return objRoot;
    }

    private void usage()
    {
	System.out.println(
			   "Usage: java ObjLoadGLSL [-s] [-S shaderName] [-n] [-t] [-c degrees] <.obj file>");
	System.out.println("  -s Spin (no user interaction)");
	System.out.println("  -S Set shader name (default is 'simple')");
	System.out.println("  -n No triangulation");
	System.out.println("  -t No stripification");
	System.out.println(
			   "  -c Set crease angle for normal generation (default is 60 without");
	System.out.println(
			   "     smoothing group info, otherwise 180 within smoothing groups)");
	System.exit(0);
    } // End of usage



    public void init() {
	if (filename == null) {
            // Applet
	    filename = Resources.getResource("resources/geometry/galleon.obj");
	    if (filename == null) {
	      System.err.println("resources/geometry/galleon.obj not found");
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
	
	// add mouse behaviors to the ViewingPlatform
	ViewingPlatform viewingPlatform = u.getViewingPlatform();

	PlatformGeometry pg = new PlatformGeometry();

	// Set up the ambient light
	Color3f ambientColor = new Color3f(0.1f, 0.1f, 0.1f);
	AmbientLight ambientLightNode = new AmbientLight(ambientColor);
	ambientLightNode.setInfluencingBounds(bounds);
	pg.addChild(ambientLightNode);

	// Set up the directional lights
	Color3f light1Color = new Color3f(1.0f, 0.2f, 0.4f);
	Vector3f light1Direction  = new Vector3f(-1.0f, -1.0f, -1.0f);
	Color3f light2Color = new Color3f(1.0f, 1.0f, 0.9f);
	Vector3f light2Direction  = new Vector3f(1.0f, 1.0f, 1.0f);

	DirectionalLight light1
	    = new DirectionalLight(light1Color, light1Direction);
	light1.setInfluencingBounds(bounds);
	pg.addChild(light1);

	DirectionalLight light2
	    = new DirectionalLight(light2Color, light2Direction);
	light2.setInfluencingBounds(bounds);
	pg.addChild(light2);

	viewingPlatform.setPlatformGeometry( pg );
      
	// This will move the ViewPlatform back a bit so the
	// objects in the scene can be viewed.
	viewingPlatform.setNominalViewingTransform();

	if (!spin) {
            OrbitBehavior orbit = new OrbitBehavior(c,
						    OrbitBehavior.REVERSE_ALL);
            BoundingSphere bounds =
                new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
            orbit.setSchedulingBounds(bounds);
            viewingPlatform.setViewPlatformBehavior(orbit);	    
	}

	/*
	// Limit the frame rate to 100 Hz
	u.getViewer().getView().setMinimumFrameCycleTime(10);
	*/

	u.addBranchGraph(scene);
    }

    // Set shader program for all nodes in specified branch graph
    private void setShaderProgram(BranchGroup g, ShaderProgram shaderProgram) {
	ShaderAppearance myApp = new ShaderAppearance();
	Material mat = new Material();
	myApp.setShaderProgram(shaderProgram);
	myApp.setMaterial(mat);
	setShaderProgram(g, myApp);
    }

    // Recursively set shader program for all children of specified group
    private void setShaderProgram(Group g,
				  ShaderAppearance myApp) {

	Enumeration e = g.getAllChildren();
	while (e.hasMoreElements()) {
	    Node n = (Node)(e.nextElement());
	    if (n instanceof Group) {
		setShaderProgram((Group)n, myApp);
	    }
	    else if (n instanceof Shape3D) {
		Shape3D s = (Shape3D)n;
		s.setAppearance(myApp);
	    }
	}
    }

    // Caled if running as a program
    public ObjLoadGLSL(String[] args) {
	if (args.length != 0) {
	    for (int i = 0 ; i < args.length ; i++) {
		if (args[i].startsWith("-")) {
		    if (args[i].equals("-s")) {
			spin = true;
		    } else if (args[i].equals("-n")) {
			noTriangulate = true;
		    } else if (args[i].equals("-t")) {
			noStripify = true;
		    } else if (args[i].equals("-c")) {
			if (i < args.length - 1) {
			    creaseAngle = (new Double(args[++i])).doubleValue();
			} else usage();
		    } else if (args[i].equals("-S")) {
			if (i < args.length - 1) {
			    shaderName = args[++i];
			} else usage();
		    } else {
			usage();
		    }
		} else {
		    try {
			if ((args[i].indexOf("file:") == 0) ||
			    (args[i].indexOf("http") == 0)) {
			    filename = new URL(args[i]);
			}
			else if (args[i].charAt(0) != '/') {
			    filename = new URL("file:./" + args[i]);
			}
			else {
			    filename = new URL("file:" + args[i]);
			}
		    }
		    catch (MalformedURLException e) {
			throw new RuntimeException(e);
		    }
		}
	    }
	}
    }



    // Running as an applet
    public ObjLoadGLSL() {
    }

    public void destroy() {
	u.cleanup();
    }



    //
    // The following allows ObjLoadGLSL to be run as an application
    // as well as an applet
    //
    public static void main(String[] args) {
	new MainFrame(new ObjLoadGLSL(args), 700, 700);
    }
}
