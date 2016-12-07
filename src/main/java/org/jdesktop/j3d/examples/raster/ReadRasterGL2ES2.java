/*
 * Copyright (c) 2016 JogAmp Community. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the JogAmp Community.
 *
 */

package org.jdesktop.j3d.examples.raster;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.util.Iterator;

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
import org.jogamp.java3d.WakeupCriterion;
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
		public void processStimulus(Iterator<WakeupCriterion> criteria)
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