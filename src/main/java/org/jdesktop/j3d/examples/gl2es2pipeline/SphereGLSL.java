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

package org.jdesktop.j3d.examples.gl2es2pipeline;

import java.awt.GraphicsConfiguration;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.swing.JOptionPane;

import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.AmbientLight;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.Background;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.DirectionalLight;
import org.jogamp.java3d.GLSLShaderProgram;
import org.jogamp.java3d.GeometryArray;
import org.jogamp.java3d.J3DBuffer;
import org.jogamp.java3d.Light;
import org.jogamp.java3d.Material;
import org.jogamp.java3d.PointLight;
import org.jogamp.java3d.PositionInterpolator;
import org.jogamp.java3d.RotationInterpolator;
import org.jogamp.java3d.Shader;
import org.jogamp.java3d.ShaderAppearance;
import org.jogamp.java3d.ShaderError;
import org.jogamp.java3d.ShaderErrorListener;
import org.jogamp.java3d.ShaderProgram;
import org.jogamp.java3d.SourceCodeShader;
import org.jogamp.java3d.SpotLight;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.TriangleStripArray;
import org.jogamp.java3d.utils.geometry.Sphere;
import org.jogamp.java3d.utils.shader.SimpleShaderAppearance;
import org.jogamp.java3d.utils.shader.StringIO;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3d;
import org.jogamp.vecmath.Vector3f;

/**
 * Simple Java 3D example program with programmable shader.
 */
public class SphereGLSL extends javax.swing.JFrame
{

	// Constants for type of light to use
	private static final int DIRECTIONAL_LIGHT = 0;
	private static final int POINT_LIGHT = 1;
	private static final int SPOT_LIGHT = 2;

	// Flag indicates type of lights: directional, point, or spot
	// lights.  This flag is set based on command line argument
	private static int lightType = POINT_LIGHT;//DIRECTIONAL_LIGHT;
	private SimpleUniverse univ = null;
	private BranchGroup scene = null;

	public BranchGroup createSceneGraph()
	{
		Color3f eColor = new Color3f(0.0f, 0.0f, 0.0f);
		Color3f sColor = new Color3f(1.0f, 1.0f, 1.0f);
		Color3f objColor = new Color3f(0.6f, 0.6f, 0.6f);
		Color3f lColor1 = new Color3f(1.0f, 0.0f, 0.0f);
		Color3f lColor2 = new Color3f(0.0f, 1.0f, 0.0f);
		Color3f alColor = new Color3f(0.2f, 0.2f, 0.2f);
		Color3f bgColor = new Color3f(0.05f, 0.05f, 0.2f);

		Transform3D t;

		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();

		// Create a Transformgroup to scale all objects so they
		// appear in the scene.
		TransformGroup objScale = new TransformGroup();
		Transform3D t3d = new Transform3D();
		t3d.setScale(0.4);
		objScale.setTransform(t3d);
		objRoot.addChild(objScale);

		// Create a bounds for the background and lights
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

		// Set up the background
		Background bg = new Background(bgColor);
		bg.setApplicationBounds(bounds);
		objScale.addChild(bg);

		// Create a Sphere object, generate one copy of the sphere,
		// and add it into the scene graph.
		ShaderAppearance a = new ShaderAppearance();
		Material m = new Material(objColor, eColor, objColor, sColor, 100.0f);
		m.setLightingEnable(true);
		String vertexProgram = null;
		String fragmentProgram = null;
		try
		{
			vertexProgram = StringIO.readFully(
					new File(System.getProperty("user.dir") + "/src/main/java/org/jdesktop/j3d/examples/gl2es2pipeline/simple.vert"));
			fragmentProgram = StringIO.readFully(
					new File(System.getProperty("user.dir") + "/src/main/java/org/jdesktop/j3d/examples/gl2es2pipeline/simple.frag"));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		Shader[] shaders = new Shader[2];
		shaders[0] = new SourceCodeShader(Shader.SHADING_LANGUAGE_GLSL, Shader.SHADER_TYPE_VERTEX, vertexProgram);
		shaders[1] = new SourceCodeShader(Shader.SHADING_LANGUAGE_GLSL, Shader.SHADER_TYPE_FRAGMENT, fragmentProgram);
		ShaderProgram shaderProgram = new GLSLShaderProgram();
		shaderProgram.setShaders(shaders);

		a.setShaderProgram(shaderProgram);
		a.setMaterial(m);
		Sphere sph = new Sphere(1.0f, Sphere.GENERATE_NORMALS, 200, a);
		makeNIO(sph);
		objScale.addChild(sph);

		// Create the transform group node for the each light and initialize
		// it to the identity.  Enable the TRANSFORM_WRITE capability so that
		// our behavior code can modify it at runtime.  Add them to the root
		// of the subgraph.
		TransformGroup l1RotTrans = new TransformGroup();
		l1RotTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objScale.addChild(l1RotTrans);

		TransformGroup l2RotTrans = new TransformGroup();
		l2RotTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objScale.addChild(l2RotTrans);

		// Create transformations for the positional lights
		t = new Transform3D();
		Vector3d lPos1 = new Vector3d(0.0, 0.0, 2.0);
		t.set(lPos1);
		TransformGroup l1Trans = new TransformGroup(t);
		l1RotTrans.addChild(l1Trans);

		t = new Transform3D();
		Vector3d lPos2 = new Vector3d(0.5, 0.8, 2.0);
		t.set(lPos2);
		TransformGroup l2Trans = new TransformGroup(t);
		l2RotTrans.addChild(l2Trans);

		// Create Geometry for point lights
		ColoringAttributes caL1 = new ColoringAttributes();
		ColoringAttributes caL2 = new ColoringAttributes();
		caL1.setColor(lColor1);
		caL2.setColor(lColor2);

		Appearance appL1 = new SimpleShaderAppearance(false, false);
		Appearance appL2 = new SimpleShaderAppearance(false, false);
		appL1.setColoringAttributes(caL1);
		appL2.setColoringAttributes(caL2);

		Sphere sph2 = new Sphere(0.05f, appL1);
		makeNIO(sph2);

		l1Trans.addChild(sph2);
		Sphere sph3 = new Sphere(0.05f, appL2);
		makeNIO(sph3);

		l2Trans.addChild(sph3);

		// Create lights
		AmbientLight aLgt = new AmbientLight(alColor);

		Light lgt1 = null;
		Light lgt2 = null;

		Point3f lPoint = new Point3f(0.0f, 0.0f, 0.0f);
		Point3f atten = new Point3f(1.0f, 0.0f, 0.0f);
		Vector3f lDirect1 = new Vector3f(lPos1);
		Vector3f lDirect2 = new Vector3f(lPos2);
		lDirect1.negate();
		lDirect2.negate();

		switch (lightType)
		{
		case DIRECTIONAL_LIGHT:
			lgt1 = new DirectionalLight(lColor1, lDirect1);
			lgt2 = new DirectionalLight(lColor2, lDirect2);
			break;
		case POINT_LIGHT:
			lgt1 = new PointLight(lColor1, lPoint, atten);
			lgt2 = new PointLight(lColor2, lPoint, atten);
			break;
		case SPOT_LIGHT:
			lgt1 = new SpotLight(lColor1, lPoint, atten, lDirect1, 25.0f * (float) Math.PI / 180.0f, 10.0f);
			lgt2 = new SpotLight(lColor2, lPoint, atten, lDirect2, 25.0f * (float) Math.PI / 180.0f, 10.0f);
			break;
		}

		// Set the influencing bounds
		aLgt.setInfluencingBounds(bounds);
		lgt1.setInfluencingBounds(bounds);
		lgt2.setInfluencingBounds(bounds);

		// Add the lights into the scene graph
		objScale.addChild(aLgt);
		l1Trans.addChild(lgt1);
		l2Trans.addChild(lgt2);

		// Create a new Behavior object that will perform the desired
		// operation on the specified transform object and add it into the
		// scene graph.
		Transform3D yAxis = new Transform3D();
		Alpha rotor1Alpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, 4000, 0, 0, 0, 0, 0);
		RotationInterpolator rotator1 = new RotationInterpolator(rotor1Alpha, l1RotTrans, yAxis, 0.0f, (float) Math.PI * 2.0f);
		rotator1.setSchedulingBounds(bounds);
		l1RotTrans.addChild(rotator1);

		// Create a new Behavior object that will perform the desired
		// operation on the specified transform object and add it into the
		// scene graph.
		Alpha rotor2Alpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, 1000, 0, 0, 0, 0, 0);
		RotationInterpolator rotator2 = new RotationInterpolator(rotor2Alpha, l2RotTrans, yAxis, 0.0f, 0.0f);
		bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		rotator2.setSchedulingBounds(bounds);
		l2RotTrans.addChild(rotator2);

		// Create a position interpolator and attach it to the view
		// platform
		TransformGroup vpTrans = univ.getViewingPlatform().getViewPlatformTransform();
		Transform3D axisOfTranslation = new Transform3D();
		Alpha transAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE, 0, 0, 5000, 0, 0, 5000, 0, 0);
		axisOfTranslation.rotY(-Math.PI / 2.0);
		PositionInterpolator translator = new PositionInterpolator(transAlpha, vpTrans, axisOfTranslation, 2.0f, 3.5f);
		translator.setSchedulingBounds(bounds);
		objScale.addChild(translator);

		// Let Java 3D perform optimizations on this scene graph.
		objRoot.compile();

		return objRoot;
	}

	private Canvas3D createUniverse()
	{
		// Get the preferred graphics configuration for the default screen
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

		// Create a Canvas3D using the preferred configuration
		Canvas3D canvas3d = new Canvas3D(config);

		// Create simple universe with view branch
		univ = new SimpleUniverse(canvas3d);
		//BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

		// Add a ShaderErrorListener
		univ.addShaderErrorListener(new ShaderErrorListener() {
			@Override
			public void errorOccurred(ShaderError error)
			{
				error.printVerbose();
				JOptionPane.showMessageDialog(SphereGLSL.this, error.toString(), "ShaderError", JOptionPane.ERROR_MESSAGE);
			}
		});

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		univ.getViewingPlatform().setNominalViewingTransform();

		// Ensure at least 5 msec per frame (i.e., < 200Hz)
		univ.getViewer().getView().setMinimumFrameCycleTime(5);

		return canvas3d;
	}

	/**
	 * Creates new form SphereGLSL
	 */
	public SphereGLSL()
	{

		// Initialize the GUI components
		initComponents();

		// Create Canvas3D and SimpleUniverse; add canvas to drawing panel
		Canvas3D c = createUniverse();
		drawingPanel.add(c, java.awt.BorderLayout.CENTER);

		// Create the content branch and add it to the universe
		scene = createSceneGraph();
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
		setTitle("SphereGLSL");
		drawingPanel.setLayout(new java.awt.BorderLayout());

		drawingPanel.setPreferredSize(new java.awt.Dimension(700, 700));
		getContentPane().add(drawingPanel, java.awt.BorderLayout.CENTER);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	public static void main(final String args[])
	{
		System.setProperty("sun.awt.noerasebackground", "true");
		System.setProperty("j3d.rend", "jogl2es2");
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				SphereGLSL sphereGLSL = new SphereGLSL();
				sphereGLSL.setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel drawingPanel;

	// End of variables declaration//GEN-END:variables
	public static void makeNIO(Sphere sph)
	{
		//Make it NIO
		TriangleStripArray geo = (TriangleStripArray) sph.getShape().getGeometry();
		int[] stripVertexCounts = new int[geo.getNumStrips()];
		geo.getStripVertexCounts(stripVertexCounts);
		TriangleStripArray newGeo = new TriangleStripArray(geo.getVertexCount(), GeometryArray.COORDINATES | GeometryArray.NORMALS
		//| GeometryArray.TEXTURE_COORDINATE_2 
				| GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE, stripVertexCounts);

		float[] coords = new float[geo.getValidVertexCount() * 3];
		geo.getCoordinates(0, coords);
		newGeo.setCoordRefBuffer(new J3DBuffer(makeFloatBuffer(coords)));
		float[] norms = new float[geo.getValidVertexCount() * 3];
		geo.getNormals(0, norms);
		newGeo.setNormalRefBuffer(new J3DBuffer(makeFloatBuffer(norms)));
		sph.getShape().setGeometry(newGeo);

	}

	private static FloatBuffer makeFloatBuffer(float[] arr)
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}
}
