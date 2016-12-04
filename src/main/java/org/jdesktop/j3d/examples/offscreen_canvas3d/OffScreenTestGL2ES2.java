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

package org.jdesktop.j3d.examples.offscreen_canvas3d;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.GraphicsConfigTemplate3D;
import org.jogamp.java3d.ImageComponent;
import org.jogamp.java3d.ImageComponent2D;
import org.jogamp.java3d.Raster;
import org.jogamp.java3d.Screen3D;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.View;
import org.jogamp.java3d.utils.shader.Cube;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3d;

/**
 * OffScreenTest programs with no UI.
 */
public class OffScreenTestGL2ES2 extends javax.swing.JFrame
{

	private SimpleUniverse univ = null;
	private BranchGroup scene = null;
	private Raster drawRaster = null;

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
		objTrans.addChild(new Cube(0.4));

		//Create a raster 
		BufferedImage bImage = new BufferedImage(200, 200, BufferedImage.TYPE_3BYTE_BGR);
		ImageComponent2D buffer = new ImageComponent2D(ImageComponent.FORMAT_RGB, bImage, true, true);
		buffer.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);

		drawRaster = new Raster(new Point3f(0.0f, 0.0f, 0.0f), Raster.RASTER_COLOR, 0, 0, 200, 200, buffer, null);

		drawRaster.setCapability(Raster.ALLOW_IMAGE_WRITE);
		Shape3D shape = new Shape3D(drawRaster);
		objRoot.addChild(shape);

		// Let Java 3D perform optimizations on this scene graph.
		objRoot.compile();

		return objRoot;
	}

	private OnScreenCanvas3D createOnScreenCanvasAndUniverse()
	{
		// Get the preferred graphics configuration for the default screen
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

		// Create a Canvas3D using the preferred configuration
		OnScreenCanvas3D onScrCanvas = new OnScreenCanvas3D(config, false);

		// Create simple universe with view branch
		univ = new SimpleUniverse(onScrCanvas);

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		univ.getViewingPlatform().setNominalViewingTransform();

		// Ensure at least 5 msec per frame (i.e., < 200Hz)
		univ.getViewer().getView().setMinimumFrameCycleTime(5);

		/*BranchGroup objRoot = new BranchGroup();
		Transform3D trans = new Transform3D();
		Transform3D rot = new Transform3D();
		
		trans.rotX(Math.PI/4.0d);
		rot.rotY(Math.PI/5.0d);
		trans.mul(rot);
		trans.setScale(0.7);
		trans.setTranslation(new Vector3d(-0.4, 0.3, 0.0));
		
		TransformGroup objTrans = new TransformGroup(trans);
		objRoot.addChild(objTrans);
		
		// Create a simple shape leaf node, add it to the scene graph.
		// ColorCube is a Convenience Utility class
		objTrans.addChild(new Cube(0.4));
		
		univ.addBranchGraph(objRoot);*/

		return onScrCanvas;
	}

	private OffScreenCanvas3D createOffScreenCanvas()
	{
		// request an offscreen Canvas3D with a single buffer configuration
		GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
		template.setDoubleBuffer(GraphicsConfigTemplate3D.UNNECESSARY);
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getBestConfiguration(template);

		// Create a offscreen Canvas3D using the single buffer configuration.
		OffScreenCanvas3D offScrCanvas = new OffScreenCanvas3D(gc, true, drawRaster);

		return offScrCanvas;
	}

	/**
	 * Creates new form OffScreenTest
	 */
	public OffScreenTestGL2ES2()
	{
		// Initialize the GUI components
		initComponents();

		// Create the content branch and add it to the universe
		scene = createSceneGraph();

		// Create an OnScreenCanvas3D and SimpleUniverse; add canvas to drawing panel
		OnScreenCanvas3D onScreenCanvas = createOnScreenCanvasAndUniverse();
		drawingPanel.add(onScreenCanvas, java.awt.BorderLayout.CENTER);

		// Creante an OffScreenCanvas3D
		OffScreenCanvas3D offScreenCanvas = createOffScreenCanvas();

		// set the offscreen to match the onscreen
		Screen3D sOn = onScreenCanvas.getScreen3D();
		Screen3D sOff = offScreenCanvas.getScreen3D();
		sOff.setSize(sOn.getSize());
		sOff.setPhysicalScreenWidth(sOn.getPhysicalScreenWidth());
		sOff.setPhysicalScreenHeight(sOn.getPhysicalScreenHeight());

		// attach the same view to the offscreen canvas
		View view = univ.getViewer().getView();
		view.addCanvas3D(offScreenCanvas);

		// tell onscreen about the offscreen so it knows to
		// render to the offscreen at postswap
		onScreenCanvas.setOffScreenCanvas(offScreenCanvas);

		univ.addBranchGraph(scene);

		view.stopView();
		// Make sure that image are render completely
		// before grab it in postSwap().
		onScreenCanvas.setImageReady();
		view.startView();
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
		System.setProperty("j3d.rend", "jogl2es2");
		System.setProperty("j3d.displaylist", "false");
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				new OffScreenTestGL2ES2().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel drawingPanel;
	// End of variables declaration//GEN-END:variables

}
