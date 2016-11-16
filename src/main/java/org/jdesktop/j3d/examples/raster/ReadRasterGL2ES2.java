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

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.util.Enumeration;

import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.GraphicsContext3D;
import org.jogamp.java3d.ImageComponent;
import org.jogamp.java3d.ImageComponent2D;
import org.jogamp.java3d.Raster;
import org.jogamp.java3d.RotationInterpolator;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.applet.MainFrame;
import org.jogamp.java3d.utils.shader.Cube;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3d;

public class ReadRasterGL2ES2 extends Applet
{

	private SimpleUniverse u = null;

	public BranchGroup createSceneGraph(BufferedImage bImage, Raster readRaster)
	{

		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();

		// Create a Raster shape. Add it to the root of the subgraph

		ImageComponent2D drawImageComponent = new ImageComponent2D(ImageComponent.FORMAT_RGB, bImage, true, true);

		Raster drawRaster = new Raster(new Point3f(0.0f, 0.0f, 0.0f), Raster.RASTER_COLOR, 0, 0, bImage.getWidth(), bImage.getHeight(),
				drawImageComponent, null);
		Shape3D shape = new Shape3D(drawRaster);
		drawRaster.setCapability(Raster.ALLOW_IMAGE_WRITE);
		objRoot.addChild(shape);

		// Ceate the transform greup node and initialize it to the
		// identity.  Enable the TRANSFORM_WRITE capability so that
		// our behavior code can modify it at runtime.  Add it to the
		// root of the subgraph.
		TransformGroup objTrans = new TransformGroup();
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		TransformGroup cubeScale = new TransformGroup();
		Transform3D t3d = new Transform3D();
		t3d.setTranslation(new Vector3d(-0.5, 0.5, 0.0));
		cubeScale.setTransform(t3d);

		cubeScale.addChild(objTrans);
		objRoot.addChild(cubeScale);

		// Create a simple shape leaf node, add it to the scene graph.
		objTrans.addChild(new Cube(0.3));

		// Create a new Behavior object that will perform the desired
		// operation on the specified transform object and add it into
		// the scene graph.
		Transform3D yAxis = new Transform3D();
		Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, 4000, 0, 0, 0, 0, 0);
		myRotationInterpolator rotator = new myRotationInterpolator(drawRaster, readRaster, rotationAlpha, objTrans, yAxis, 0.0f,
				(float) Math.PI * 2.0f);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		rotator.setSchedulingBounds(bounds);
		objTrans.addChild(rotator);

		// Have Java 3D perform optimizations on this scene graph.
		objRoot.compile();

		return objRoot;
	}

	public ReadRasterGL2ES2()
	{
	}

	@Override
	public void init()
	{
		System.setProperty("sun.awt.noerasebackground", "true");
		System.setProperty("j3d.rend", "jogl2es2");
		System.setProperty("j3d.displaylist", "false");

		int width = 128;
		int height = 128;

		ImageComponent2D readImageComponent = new ImageComponent2D(ImageComponent.FORMAT_RGB, width, height, false, true);

		Raster readRaster = new Raster(new Point3f(0.0f, 0.0f, 0.0f), Raster.RASTER_COLOR, 0, 0, width, height, readImageComponent, null);

		setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

		Canvas3D c = new myCanvas3D(config, readRaster);
		add("Center", c);

		// Create a simple scene and attach it to the virtual universe
		BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		BranchGroup scene = createSceneGraph(bImage, readRaster);
		u = new SimpleUniverse(c);

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		u.getViewingPlatform().setNominalViewingTransform();

		u.addBranchGraph(scene);
	}

	@Override
	public void destroy()
	{
		u.cleanup();
	}

	//
	// The following allows ReadRaster to be run as an application
	// as well as an applet
	//
	public static void main(String[] args)
	{
		System.setProperty("sun.awt.noerasebackground", "true");
		System.setProperty("j3d.rend", "jogl2es2");
		System.setProperty("j3d.displaylist", "false");
		new MainFrame(new ReadRasterGL2ES2(), 256, 256);
	}

	class myCanvas3D extends Canvas3D
	{

		Raster readRaster;
		GraphicsContext3D gc;

		public myCanvas3D(GraphicsConfiguration graphicsConfiguration, Raster readRaster)
		{

			super(graphicsConfiguration);
			this.readRaster = readRaster;
			gc = getGraphicsContext3D();
		}

		@Override
		public void postSwap()
		{
			super.postSwap();
			synchronized (readRaster)
			{
				gc.readRaster(readRaster);
			}
		}
	}

	class myRotationInterpolator extends RotationInterpolator
	{
		Point3f wPos = new Point3f(0.025f, -0.025f, 0.0f);
		Raster drawRaster;
		Raster readRaster;
		BufferedImage bImage;
		ImageComponent2D newImageComponent;

		public myRotationInterpolator(Raster drawRaster, Raster readRaster, Alpha alpha, TransformGroup target, Transform3D axisOfRotation,
				float minimumAngle, float maximumAngle)
		{

			super(alpha, target, axisOfRotation, minimumAngle, maximumAngle);
			this.drawRaster = drawRaster;
			this.readRaster = readRaster;
		}

		@Override
		public void processStimulus(Enumeration criteria)
		{

			synchronized (readRaster)
			{
				bImage = readRaster.getImage().getImage();
			}
			newImageComponent = new ImageComponent2D(ImageComponent.FORMAT_RGB, bImage, true, true);
			drawRaster.setImage(newImageComponent);
			super.processStimulus(criteria);
		}
	}
}