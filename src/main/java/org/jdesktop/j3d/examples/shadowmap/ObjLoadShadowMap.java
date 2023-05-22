/*
 * $RCSfile$
 *
 * Copyright (c) 2007 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * - Redistribution of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS
 * AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO
 * EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT
 * OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or intended for use in the design, construction,
 * operation or maintenance of any nuclear facility.
 *
 * $Revision$ $Date$ $State$
 */

package org.jdesktop.j3d.examples.shadowmap;

import java.awt.GraphicsConfiguration;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jdesktop.j3d.examples.Resources;
import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.AmbientLight;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.Background;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.DirectionalLight;
import org.jogamp.java3d.RotationInterpolator;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.loaders.IncorrectFormatException;
import org.jogamp.java3d.loaders.ParsingErrorException;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.behaviors.vp.OrbitBehavior;
import org.jogamp.java3d.utils.shader.SimpleShaderAppearance;
import org.jogamp.java3d.utils.universe.PlatformGeometry;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.universe.ViewingPlatform;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3f;

/**
 * Simple Java 3D example program to display an .obj object.
 */
public class ObjLoadShadowMap extends JFrame {

	private JPanel			drawingPanel;
	private double			creaseAngle	= 60.0;
	private URL				filename	= null;

	private SimpleUniverse	univ		= null;
	private BranchGroup		scene		= null;

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
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objScale.addChild(objTrans);

		int flags = ObjectFile.RESIZE;
		flags |= ObjectFile.TRIANGULATE;
		flags |= ObjectFile.STRIPIFY;
		ObjectFile f = new ObjectFile(flags, (float)(creaseAngle * Math.PI / 180.0)) {
			@Override
			public Appearance createAppearance() {
				return new SimpleShaderAppearance();
			}
		};
		Scene s = null;
		try {
			s = f.load(filename);
		} catch (FileNotFoundException e) {
			System.err.println(e);
			System.exit(1);
		} catch (ParsingErrorException e) {
			System.err.println(e);
			System.exit(1);
		} catch (IncorrectFormatException e) {
			System.err.println(e);
			System.exit(1);
		}

		objTrans.addChild(s.getSceneGroup());

		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

		Transform3D yAxis = new Transform3D();
		Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, 30000, 0, 0, 0, 0, 0);

		RotationInterpolator rotator = new RotationInterpolator(rotationAlpha, objTrans, yAxis, 0.0f,
				(float)Math.PI * 2.0f);
		rotator.setSchedulingBounds(bounds);
		objTrans.addChild(rotator);

		// Set up the background
		Color3f bgColor = new Color3f(0.05f, 0.05f, 0.5f);
		Background bgNode = new Background(bgColor);
		bgNode.setApplicationBounds(bounds);
		objRoot.addChild(bgNode);

		return objRoot;
	}

	public BranchGroup createSceneGraph2() {
		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();

		// Create a TransformGroup to scale all objects so they
		// appear in the scene.
		TransformGroup objScale = new TransformGroup();
		Transform3D t3d = new Transform3D();
		t3d.setScale(0.5);
		objScale.setTransform(t3d);
		objRoot.addChild(objScale);

		TransformGroup objTrans = new TransformGroup();
		Transform3D t3d2 = new Transform3D();
		t3d2.setTranslation(new Vector3f(0, -1.5f, 0));
		objTrans.setTransform(t3d2);
		objScale.addChild(objTrans);

		int flags = ObjectFile.RESIZE;
		flags |= ObjectFile.TRIANGULATE;
		flags |= ObjectFile.STRIPIFY;
		ObjectFile f = new ObjectFile(flags, (float)(creaseAngle * Math.PI / 180.0)) {
			@Override
			public Appearance createAppearance() {
				return new SimpleShaderAppearance();
			}
		};
		Scene s = null;
		try {
			s = f.load(filename);
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}

		objTrans.addChild(s.getSceneGroup());

		TransformGroup objTrans2 = new TransformGroup();
		Transform3D t3d3 = new Transform3D();
		t3d3.setTranslation(new Vector3f(-0.2f, 1.5f, 0));
		objTrans2.setTransform(t3d3);
//		objScale.addChild(objTrans2);
		TransformGroup objTrans3 = new TransformGroup();
		objTrans3.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTrans3.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

		objTrans2.addChild(objTrans3);

		f = new ObjectFile(flags, (float)(creaseAngle * Math.PI / 180.0)) {
			@Override
			public Appearance createAppearance() {
				return new SimpleShaderAppearance();
			}
		};
		s = null;
		try {
			s = f.load(filename);
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

		Transform3D zAxis = new Transform3D();
		zAxis.rotX(Math.PI / 3);
		Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, 5000, 0, 0, 0, 0, 0);

		RotationInterpolator rotator = new RotationInterpolator(rotationAlpha, objTrans3, zAxis, 0.0f,
				(float)Math.PI * 2.0f);
		rotator.setSchedulingBounds(bounds);
		objTrans3.addChild(rotator);

		objTrans3.addChild(s.getSceneGroup());

		Color3f light1Color = new Color3f(1.0f, 1.0f, 0.2f);
		Vector3f light1Direction = new Vector3f(0.0f, -1.0f, 0.0f);
		DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
		light1.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
		light1.setShadowMap(true);
		objRoot.addChild(light1);

		return objRoot;
	}

	private Canvas3D createUniverse() {
		// Get the preferred graphics configuration for the default screen
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

		// Create a Canvas3D using the preferred configuration
		Canvas3D canvas3d = new Canvas3D(config);

		// Create simple universe with view branch
		univ = new SimpleUniverse(canvas3d);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

		// add mouse behaviors to the ViewingPlatform
		ViewingPlatform viewingPlatform = univ.getViewingPlatform();

		PlatformGeometry pg = new PlatformGeometry();

		// Set up the ambient light
		Color3f ambientColor = new Color3f(0.2f, 0.2f, 0.2f);
		AmbientLight ambientLightNode = new AmbientLight(ambientColor);
		ambientLightNode.setInfluencingBounds(bounds);
		pg.addChild(ambientLightNode);

		// Set up the directional lights

		Color3f light2Color = new Color3f(1.0f, 0.0f, 1.0f);
		Vector3f light2Direction = new Vector3f(1.0f, 0.0f, 0.0f);

		DirectionalLight light2 = new DirectionalLight(light2Color, light2Direction);
		light2.setInfluencingBounds(bounds);
		light2.setShadowMap(false);
		pg.addChild(light2);

		viewingPlatform.setPlatformGeometry(pg);

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		viewingPlatform.setNominalViewingTransform();

		OrbitBehavior orbit = new OrbitBehavior(canvas3d, OrbitBehavior.REVERSE_ALL);
		orbit.setSchedulingBounds(bounds);
		viewingPlatform.setViewPlatformBehavior(orbit);

		// Ensure at least 5 msec per frame (i.e., < 200Hz)
		univ.getViewer().getView().setMinimumFrameCycleTime(5);

		return canvas3d;
	}

	private void usage() {
		System.out.println("Usage: java ObjLoad [-s] [-n] [-t] [-c degrees] <.obj file>");
		System.out.println("  -s Spin (no user interaction)");
		System.out.println("  -n No triangulation");
		System.out.println("  -t No stripification");
		System.out.println("  -c Set crease angle for normal generation (default is 60 without");
		System.out.println("     smoothing group info, otherwise 180 within smoothing groups)");
		System.exit(0);
	} // End of usage

	/**
	 * Creates new form ObjLoad
	 */
	public ObjLoadShadowMap(String args[]) {
		if (args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				if (args [i].startsWith("-")) {
				} else {
					try {
						if ((args [i].indexOf("file:") == 0) || (args [i].indexOf("http") == 0)) {
							filename = new URL(args [i]);
						} else if (args [i].charAt(0) != '/') {
							filename = new URL("file:./" + args [i]);
						} else {
							filename = new URL("file:" + args [i]);
						}
					} catch (MalformedURLException e) {
						System.err.println(e);
						System.exit(1);
					}
				}
			}
		}

		if (filename == null) {
			filename = Resources.getResource("main/resources/geometry/galleon.obj");
			if (filename == null) {
				System.err.println("main/resources/geometry/galleon.obj not found");
				System.exit(1);
			}
		}

		System.setProperty("sun.awt.noerasebackground", "true");
		System.setProperty("j3d.rend", "jogl2es2");
		System.setProperty("j3d.displaylist", "false");

		// Initialize the GUI components
		initComponents();

		// Create Canvas3D and SimpleUniverse; add canvas to drawing panel
		Canvas3D c = createUniverse();
		drawingPanel.add(c, java.awt.BorderLayout.CENTER);

		// Create the content branch and add it to the universe
		scene = createSceneGraph();
		univ.addBranchGraph(scene);

		univ.addBranchGraph(createSceneGraph2());

	}

	private void initComponents() {
		drawingPanel = new javax.swing.JPanel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("ObjLoad");
		drawingPanel.setLayout(new java.awt.BorderLayout());

		drawingPanel.setPreferredSize(new java.awt.Dimension(700, 700));
		getContentPane().add(drawingPanel, java.awt.BorderLayout.CENTER);

		pack();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(final String args[]) {
		SimpleShaderAppearance.setVersionES300();
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				ObjLoadShadowMap objLoad = new ObjLoadShadowMap(args);
				objLoad.setVisible(true);
			}
		});
	}

}
