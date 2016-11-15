/*
 * Copyright (c) 2016 JogAmp Community. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package org.jdesktop.j3d.examples.raster;

import java.awt.GraphicsConfiguration;
import java.net.URL;

import org.jdesktop.j3d.examples.Resources;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.ImageComponent2D;
import org.jogamp.java3d.Raster;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.behaviors.mouse.MouseRotate;
import org.jogamp.java3d.utils.behaviors.mouse.MouseTranslate;
import org.jogamp.java3d.utils.behaviors.mouse.MouseZoom;
import org.jogamp.java3d.utils.geometry.ColorCube;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3d;

/**
 * OffScreenTest programs with no UI.
 */
public class RasterTest extends javax.swing.JFrame
{

	private SimpleUniverse univ = null;
	private BranchGroup scene = null;
	private Raster drawRaster = null;
	private BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

	private BranchGroup createSceneGraph()
	{
		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();

		// trans object has composited transformation matrix
		Transform3D trans = new Transform3D();
		Transform3D rot = new Transform3D();

		trans.rotX(Math.PI / 4.0d);
		rot.rotY(Math.PI / 5.0d);
		trans.mul(rot);
		trans.setScale(0.7);
		trans.setTranslation(new Vector3d(-0.4, 0.3, 0.0));

		TransformGroup objTrans = new TransformGroup(trans);
		objRoot.addChild(objTrans);

		// Create a simple shape leaf node, add it to the scene graph.
		// ColorCube is a Convenience Utility class
		objTrans.addChild(new ColorCube(0.4));

		//Create a raster 
		URL bgImage = null;
		if (bgImage == null)
		{
			// the path to the image for an applet
			bgImage = Resources.getResource("main/resources/images/bg.jpg");
			if (bgImage == null)
			{
				System.err.println("main/resources/images/bg.jpg not found");
				System.exit(1);
			}
		}

		TextureLoader tex = new TextureLoader(bgImage, new String("RGB"), TextureLoader.BY_REFERENCE | TextureLoader.Y_UP, this);

		ImageComponent2D buffer2 = (ImageComponent2D) tex.getTexture().getImage(0);
		buffer2.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);
		drawRaster = new Raster(new Point3f(0.0f, 0.0f, 0.0f), Raster.RASTER_COLOR, 0, 0, 200, 200, buffer2, null);

		drawRaster.setCapability(Raster.ALLOW_IMAGE_WRITE);
		Shape3D shape = new Shape3D(drawRaster);
		objRoot.addChild(shape);

		return objRoot;
	}

	private Canvas3D createCanvas3DAndUniverse()
	{
		// Get the preferred graphics configuration for the default screen
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

		// Create a Canvas3D using the preferred configuration
		Canvas3D canvas3D = new Canvas3D(config, false);

		// Create simple universe with view branch
		univ = new SimpleUniverse(canvas3D);

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		univ.getViewingPlatform().setNominalViewingTransform();

		// Ensure at least 5 msec per frame (i.e., < 200Hz)
		univ.getViewer().getView().setMinimumFrameCycleTime(5);

		TransformGroup viewTrans = univ.getViewingPlatform().getViewPlatformTransform();

		// Create the rotate behavior node
		MouseRotate behavior1 = new MouseRotate(viewTrans);
		scene.addChild(behavior1);
		behavior1.setSchedulingBounds(bounds);

		// Create the zoom behavior node
		MouseZoom behavior2 = new MouseZoom(viewTrans);
		scene.addChild(behavior2);
		behavior2.setSchedulingBounds(bounds);

		// Create the translate behavior node
		MouseTranslate behavior3 = new MouseTranslate(viewTrans);
		scene.addChild(behavior3);
		behavior3.setSchedulingBounds(bounds);

		return canvas3D;
	}

	/**
	 * Creates new form OffScreenTest
	 */
	public RasterTest()
	{
		// Initialize the GUI components
		initComponents();

		// Create the content branch and add it to the universe
		scene = createSceneGraph();

		// Create an canvas3D3D and SimpleUniverse; add canvas to drawing panel
		Canvas3D canvas3D = createCanvas3DAndUniverse();
		drawingPanel.add(canvas3D, java.awt.BorderLayout.CENTER);

		univ.addBranchGraph(scene);
	}

	// ----------------------------------------------------------------

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents()
	{
		drawingPanel = new javax.swing.JPanel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Window Title");
		drawingPanel.setLayout(new java.awt.BorderLayout());

		drawingPanel.setPreferredSize(new java.awt.Dimension(500, 500));
		getContentPane().add(drawingPanel, java.awt.BorderLayout.CENTER);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[])
	{
		System.setProperty("sun.awt.noerasebackground", "true");
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				new RasterTest().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel drawingPanel;
	// End of variables declaration//GEN-END:variables

}
