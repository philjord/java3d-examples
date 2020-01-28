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

package org.jdesktop.j3d.examples.overlay2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.j3d.examples.Resources;
import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.GeometryArray;
import org.jogamp.java3d.GraphicsConfigTemplate3D;
import org.jogamp.java3d.J3DGraphics2D;
import org.jogamp.java3d.Locale;
import org.jogamp.java3d.PhysicalBody;
import org.jogamp.java3d.PhysicalEnvironment;
import org.jogamp.java3d.PolygonAttributes;
import org.jogamp.java3d.RotationInterpolator;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Texture;
import org.jogamp.java3d.TextureAttributes;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.TriangleArray;
import org.jogamp.java3d.View;
import org.jogamp.java3d.ViewPlatform;
import org.jogamp.java3d.VirtualUniverse;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.shader.SimpleShaderAppearance;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.TexCoord2f;
import org.jogamp.vecmath.Vector3d;
import org.jogamp.vecmath.Vector3f;

/**
 * Simple Java 3D example program to show use of the 2DGraphics overlay.
 * Example provided by Egor Tsinko
 * https://github.com/philjord/java3d-core/pull/7
 */
public class Overlay2DGL2ES2 {
	private static class MyCanvas extends Canvas3D {

	    public MyCanvas(GraphicsConfiguration graphicsConfiguration, boolean offScreen) {
	        super(graphicsConfiguration, offScreen);
	    }
	
	    @Override
	    public void postRender() {
	        J3DGraphics2D g = getGraphics2D();
	
	//        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	        g.setColor(Color.WHITE);
	        g.setFont(g.getFont().deriveFont(25f).deriveFont(Font.BOLD));
	        final AffineTransform t = new AffineTransform(g.getTransform());
	        g.drawString("HiDPI Scale X = " + t.getScaleX(), 20, 20);
	        g.drawString("HiDPI Scale Y = " + t.getScaleY(), 20, 40);
	        g.drawString("Canvas Width = " + getWidth(), 20, 60);
	        g.drawString("Canvas Height = " + getHeight(), 20, 80);
	
	        g.setStroke(new BasicStroke(1f));
	//        g.drawRect(1,1, getWidth()-2, getHeight() - 2);
	//        g.drawLine(1,1, getWidth()-2, getHeight() - 2);
	//        g.drawLine(1,getHeight() - 2, getWidth()-2, 1);
	
	        g.setFont(g.getFont().deriveFont(12f).deriveFont(Font.BOLD));
	        g.drawString("The following lines should be 1px wide each:", 20, 100);
	        g.drawString("The following lines should be 2px wide each:", 20, 250);
	        int x = (int) (g.getTransform().getScaleX() * 290);
	        int y = (int) (g.getTransform().getScaleY() * 80);
	        int x2 = (int) (g.getTransform().getScaleX() * 290);
	        int y2 = (int) (g.getTransform().getScaleY() * 200);
	        g.setTransform(new AffineTransform());
	        // These lines should ALWAYS be 1px thick no matter what HiDPI scale is
	        g.setStroke(new BasicStroke(1f));
	        g.drawLine(x, y, x, y + 100);
	        g.drawLine(x + 2, y, x + 2, y + 100);
	        g.drawLine(x + 4, y, x + 4, y + 100);
	
	        // These lines should ALWAYS be 2px thick no matter what HiDPI scale is
	        g.drawLine(x2, y2, x2, y2 + 100);
	        g.drawLine(x2 + 1, y2, x2 + 1, y2 + 100);
	        g.drawLine(x2 + 3, y2, x2 + 3, y2 + 100);
	        g.drawLine(x2 + 4, y2, x2 + 4, y2 + 100);
	        g.drawLine(x2 + 6, y2, x2 + 6, y2 + 100);
	        g.drawLine(x2 + 7, y2, x2 + 7, y2 + 100);
	
	        g.flush(true);
	    }
	}
	
	public static void main(String[] args) {
		
		System.setProperty("sun.awt.noerasebackground", "true");
		System.setProperty("j3d.rend", "jogl2es2");
		System.setProperty("j3d.displaylist", "false");
		
		// Getting graphics device
	    final GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice device = localGraphicsEnvironment.getDefaultScreenDevice();
	
	    // Creating canvas
	    MyCanvas canvas = new MyCanvas(device.getBestConfiguration(new GraphicsConfigTemplate3D()), false);
	    canvas.setSize(500, 300);
	
	    // Creating scene + view
	    final View view = createView();
	    view.addCanvas3D(canvas);
	
	    final JFrame frame = new JFrame();
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    frame.add(canvas);
	    frame.pack();
	    SwingUtilities.invokeLater(new Runnable() {
	        @Override
	        public void run() {
	            frame.setVisible(true);
	        }
	    });
	}
	
	private static View createView() {
	    Locale locale = new Locale(new VirtualUniverse());
	
	    BranchGroup viewBranchGroup = new BranchGroup();
	    TransformGroup viewPlatformTransformGroup = new TransformGroup();
	    viewPlatformTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    ViewPlatform viewPlatform = new ViewPlatform();
	    viewBranchGroup.addChild(viewPlatformTransformGroup);
	    viewPlatformTransformGroup.addChild(viewPlatform);
	    locale.addBranchGraph(viewBranchGroup);
	    
	    BranchGroup scene = createSceneGraph();	    
	    locale.addBranchGraph(scene);
	
	    View view = new View();
	    view.setPhysicalBody(new PhysicalBody());
	    view.setPhysicalEnvironment(new PhysicalEnvironment());
	    view.attachViewPlatform(viewPlatform);
	    
	    
	    // move the view platform back from 0,0,0 a bit
	    double fieldOfView = view.getFieldOfView();
	    Transform3D t3d = new Transform3D();
	    double viewDistance = 1.0/Math.tan(fieldOfView/2.0);
	    t3d.set(new Vector3d(0.0, 0.0, viewDistance));
	    viewPlatformTransformGroup.setTransform(t3d);

	    return view;
	}
	
 

	public static BranchGroup createSceneGraph()
	{
		final BranchGroup objRoot = new BranchGroup();

		// Create a triangle with each point a different color.  Remember to
		// draw the points in counter-clockwise order.  That is the default
		// way of determining which is the front of a polygon.
		//        o (1)
		//       / \
		//      /   \
		// (2) o-----o (0)
		Shape3D shape = new Shape3D();
		TriangleArray tri = new TriangleArray(3, GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.TEXTURE_COORDINATE_2);
		tri.setCoordinate(0, new Point3f(0.5f, 0.0f, 0.0f));
		tri.setCoordinate(1, new Point3f(0.0f, 0.5f, 0.0f));
		tri.setCoordinate(2, new Point3f(-0.5f, 0.0f, 0.0f));
		tri.setColor(0, new Color3f(1.0f, 0.0f, 0.0f));
		tri.setColor(1, new Color3f(0.0f, 1.0f, 0.0f));
		tri.setColor(2, new Color3f(0.0f, 0.0f, 1.0f));
		tri.setTextureCoordinate(0, 0, new TexCoord2f(1.0f, 0.0f));
		tri.setTextureCoordinate(0, 1, new TexCoord2f(0.0f, 1.0f));
		tri.setTextureCoordinate(0, 2, new TexCoord2f(0.0f, 0.0f));

		// Because we're about to spin this triangle, be sure to draw
		// backfaces.  If we don't, the back side of the triangle is invisible.
		SimpleShaderAppearance ap = new SimpleShaderAppearance();
		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		ap.setPolygonAttributes(pa);

		// Add a transformed texture to the cube, for interest sake					
		URL earthURL = Resources.getResource("main/resources/images/earth.jpg");
		Texture earthTex = new TextureLoader(earthURL, null).getTexture();
		ap.setTexture(earthTex);

		TextureAttributes textureAttributes = new TextureAttributes();
		Transform3D textureTransform = new Transform3D();
		textureTransform.rotZ(Math.PI / 3f);
		textureTransform.setTranslation(new Vector3f(10, 1, 0));
		textureAttributes.setTextureTransform(textureTransform);

		ap.setTextureAttributes(textureAttributes);

		shape.setAppearance(ap);

		// Set up a simple RotationInterpolator
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 5.0);
		TransformGroup tg = new TransformGroup();
		Transform3D yAxis = new Transform3D();
		Alpha rotationAlpha = new Alpha(-1, 4000);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		RotationInterpolator rotator = new RotationInterpolator(rotationAlpha, tg, yAxis, 0.0f, (float) Math.PI * 2.0f);
		rotator.setSchedulingBounds(bounds);

		shape.setGeometry(tri);
		tg.addChild(rotator);
		tg.addChild(shape);
		objRoot.addChild(tg);
		objRoot.compile();
		return objRoot;

	}
}
