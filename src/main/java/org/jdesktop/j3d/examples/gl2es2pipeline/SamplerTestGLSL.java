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
import java.net.URL;

import javax.swing.JOptionPane;

import org.jdesktop.j3d.examples.Resources;
import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.GLSLShaderProgram;
import org.jogamp.java3d.RotationInterpolator;
import org.jogamp.java3d.Shader;
import org.jogamp.java3d.ShaderAppearance;
import org.jogamp.java3d.ShaderAttribute;
import org.jogamp.java3d.ShaderAttributeSet;
import org.jogamp.java3d.ShaderAttributeValue;
import org.jogamp.java3d.ShaderError;
import org.jogamp.java3d.ShaderErrorListener;
import org.jogamp.java3d.ShaderProgram;
import org.jogamp.java3d.SourceCodeShader;
import org.jogamp.java3d.Texture;
import org.jogamp.java3d.TextureUnitState;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.geometry.Sphere;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.shader.StringIO;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.universe.ViewingPlatform;
import org.jogamp.vecmath.Point3d;

public class SamplerTestGLSL extends javax.swing.JFrame
{

	private static String cloudTexName = "main/resources/images/bg.jpg";
	private static String earthTexName = "main/resources/images/earth.jpg";

	private URL cloudURL = null;
	private URL earthURL = null;
	private static final int CLOUD = 0;
	private static final int EARTH = 1;

	SimpleUniverse univ = null;

	public BranchGroup createSceneGraph()
	{
		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();

		// Create the TransformGroup node and initialize it to the
		// identity. Enable the TRANSFORM_WRITE capability so that
		// our behavior code can modify it at run time. Add it to
		// the root of the subgraph.
		TransformGroup objTrans = new TransformGroup();
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objRoot.addChild(objTrans);

		// Create texture objects
		cloudURL = Resources.getResource(cloudTexName);
		Texture cloudTex = new TextureLoader(cloudURL, this).getTexture();
		earthURL = Resources.getResource(earthTexName);
		Texture earthTex = new TextureLoader(earthURL, this).getTexture();

		// Create the shader program
		String vertexProgram = null;
		String fragmentProgram = null;
		try
		{
			vertexProgram = StringIO.readFully(
					new File(System.getProperty("user.dir") + "/src/main/java/org/jdesktop/j3d/examples/gl2es2pipeline/multitex.vert"));
			fragmentProgram = StringIO.readFully(
					new File(System.getProperty("user.dir") + "/src/main/java/org/jdesktop/j3d/examples/gl2es2pipeline/multitex.frag"));
		}
		catch (IOException e)
		{
			System.err.println(e);
		}
		Shader[] shaders = new Shader[2];
		shaders[0] = new SourceCodeShader(Shader.SHADING_LANGUAGE_GLSL, Shader.SHADER_TYPE_VERTEX, vertexProgram);
		shaders[1] = new SourceCodeShader(Shader.SHADING_LANGUAGE_GLSL, Shader.SHADER_TYPE_FRAGMENT, fragmentProgram);
		final String[] shaderAttrNames = { "cloudFactor", "cloudTex", "earthTex", };
		final Object[] shaderAttrValues = { new Float(0.6f), new Integer(0), new Integer(1), };
		ShaderProgram shaderProgram = new GLSLShaderProgram();
		shaderProgram.setShaders(shaders);
		shaderProgram.setShaderAttrNames(shaderAttrNames);

		// Create the shader attribute set
		ShaderAttributeSet shaderAttributeSet = new ShaderAttributeSet();
		for (int i = 0; i < shaderAttrNames.length; i++)
		{
			ShaderAttribute shaderAttribute = new ShaderAttributeValue(shaderAttrNames[i], shaderAttrValues[i]);
			shaderAttributeSet.put(shaderAttribute);
		}

		// Create shader appearance to hold the shader program and
		// shader attributes
		ShaderAppearance app = new ShaderAppearance();
		app.setShaderProgram(shaderProgram);
		app.setShaderAttributeSet(shaderAttributeSet);

		// GL2ES2: Tex coord gen done in shader now
		//Vector4f plane0S = new Vector4f(3.0f, 1.5f, 0.3f, 0.0f);
		//Vector4f plane0T = new Vector4f(1.0f, 2.5f, 0.24f, 0.0f);
		//TexCoordGeneration tcg0 = new TexCoordGeneration(TexCoordGeneration.OBJECT_LINEAR, TexCoordGeneration.TEXTURE_COORDINATE_2, plane0S,
		//		plane0T);
		//TexCoordGeneration tcg1 = new TexCoordGeneration(TexCoordGeneration.SPHERE_MAP, TexCoordGeneration.TEXTURE_COORDINATE_2);

		// Put the textures in unit 0,1
		TextureUnitState[] tus = new TextureUnitState[2];
		tus[CLOUD] = new TextureUnitState();
		tus[CLOUD].setTexture(cloudTex);
		
		// GL2ES2: Tex coord gen done in shader now
		//tus[CLOUD].setTexCoordGeneration(tcg0);				
		
		tus[EARTH] = new TextureUnitState();
		tus[EARTH].setTexture(earthTex);
		
		// GL2ES2: Tex coord gen done in shader now
		//tus[EARTH].setTexCoordGeneration(tcg1);		
		
		app.setTextureUnitState(tus);

		// Create a Sphere object using the shader appearance,
		// and add it into the scene graph.
		Sphere sph = new Sphere(0.4f, Sphere.GENERATE_NORMALS, 30, app);

		SphereGLSL.makeNIO(sph);
		objTrans.addChild(sph);


		// Create a new Behavior object that will perform the
		// desired operation on the specified transform and add
		// it into the scene graph.
		Transform3D yAxis = new Transform3D();
		Alpha rotationAlpha = new Alpha(-1, 4000);

		RotationInterpolator rotator = new RotationInterpolator(rotationAlpha, objTrans, yAxis, 0.0f, (float) Math.PI * 2.0f);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		rotator.setSchedulingBounds(bounds);
		objRoot.addChild(rotator);

		// Have Java 3D perform optimizations on this scene graph.
		//objRoot.compile();

		return objRoot;
	}
	
	 
	private Canvas3D initScene()
	{
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

		Canvas3D c = new Canvas3D(config);

		BranchGroup scene = createSceneGraph();
		univ = new SimpleUniverse(c);

		// Add a ShaderErrorListener
		univ.addShaderErrorListener(new ShaderErrorListener() {
			@Override
			public void errorOccurred(ShaderError error)
			{
				error.printVerbose();
				JOptionPane.showMessageDialog(SamplerTestGLSL.this, error.toString(), "ShaderError", JOptionPane.ERROR_MESSAGE);
			}
		});

		ViewingPlatform viewingPlatform = univ.getViewingPlatform();
		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		viewingPlatform.setNominalViewingTransform();

		univ.addBranchGraph(scene);

		return c;
	}

	/**
	 * Creates new form SamplerTestGLSL
	 */
	public SamplerTestGLSL()
	{
		// Initialize the GUI components
		initComponents();

		// Create the scene and add the Canvas3D to the drawing panel
		Canvas3D c = initScene();
		drawingPanel.add(c, java.awt.BorderLayout.CENTER);
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
		setTitle("SamplerTestGLSL");
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
		System.setProperty("j3d.rend","jogl2es2");
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				new SamplerTestGLSL().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel drawingPanel;
	// End of variables declaration//GEN-END:variables

}
