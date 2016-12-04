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

package org.jdesktop.j3d.examples.distort_glyph;

import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;

import org.jdesktop.j3d.examples.Resources;
 
import org.jogamp.java3d.AmbientLight;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.DirectionalLight;
import org.jogamp.java3d.Font3D;
import org.jogamp.java3d.FontExtrusion;
import org.jogamp.java3d.GLSLShaderProgram;
import org.jogamp.java3d.GeometryArray;
import org.jogamp.java3d.GraphicsConfigTemplate3D;
import org.jogamp.java3d.Light;
import org.jogamp.java3d.Material;
import org.jogamp.java3d.PointLight;
import org.jogamp.java3d.Shader;
import org.jogamp.java3d.ShaderAppearance;
import org.jogamp.java3d.ShaderAttributeSet;
import org.jogamp.java3d.ShaderAttributeValue;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.SourceCodeShader;
import org.jogamp.java3d.TexCoordGeneration;
import org.jogamp.java3d.Texture;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.behaviors.mouse.MouseRotate;
import org.jogamp.java3d.utils.behaviors.mouse.MouseTranslate;
import org.jogamp.java3d.utils.behaviors.mouse.MouseZoom;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

public class DistortGlyphTestGL2ES2 extends javax.swing.JFrame
{

	private SimpleUniverse univ = null;
	private BranchGroup scene = null;

	// get a nice graphics config
	private static GraphicsConfiguration getGraphicsConfig()
	{
		GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
		template.setSceneAntialiasing(GraphicsConfigTemplate3D.PREFERRED);
		GraphicsConfiguration gcfg = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getBestConfiguration(template);
		return gcfg;
	}

	private void setupLights(BranchGroup root)
	{
		// set up the BoundingSphere for all the lights
		BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);

		// Set up the ambient light
		AmbientLight lightAmbient = new AmbientLight(new Color3f(0.37f, 0.37f, 0.37f));
		lightAmbient.setInfluencingBounds(bounds);
		root.addChild(lightAmbient);

		// Set up the directional light
		Vector3f lightDirection1 = new Vector3f(0.0f, 0.0f, -1.0f);
		DirectionalLight lightDirectional1 = new DirectionalLight(new Color3f(1.00f, 0.10f, 0.00f), lightDirection1);
		lightDirectional1.setInfluencingBounds(bounds);
		lightDirectional1.setCapability(Light.ALLOW_STATE_WRITE);
		root.addChild(lightDirectional1);

		Point3f lightPos1 = new Point3f(-4.0f, 8.0f, 16.0f);
		Point3f lightAttenuation1 = new Point3f(1.0f, 0.0f, 0.0f);
		PointLight pointLight1 = new PointLight(new Color3f(0.37f, 1.00f, 0.37f), lightPos1, lightAttenuation1);
		pointLight1.setInfluencingBounds(bounds);
		root.addChild(pointLight1);

		Point3f lightPos2 = new Point3f(-16.0f, 8.0f, 4.0f);
		Point3f lightAttenuation2 = new Point3f(1.0f, 0.0f, 0.0f);
		PointLight pointLight2 = new PointLight(new Color3f(0.37f, 0.37f, 1.00f), lightPos2, lightAttenuation2);
		pointLight2.setInfluencingBounds(bounds);
		root.addChild(pointLight2);
	}

	public BranchGroup createSceneGraph()
	{
		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();

		setupLights(objRoot);

		TransformGroup objTransform = new TransformGroup();
		objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

		objRoot.addChild(objTransform);

		// setup a nice textured appearance
		Appearance app = makeShaderAppearance();
		Color3f objColor = new Color3f(1.0f, 0.7f, 0.8f);
		Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
		app.setMaterial(new Material(objColor, black, objColor, black, 80.0f));
		Texture txtr = new TextureLoader(Resources.getResource("main/resources/images/gold.jpg"), this).getTexture();
		app.setTexture(txtr);
		// done in shader see makeShaderAppearance() below
		//TexCoordGeneration tcg = new TexCoordGeneration(TexCoordGeneration.SPHERE_MAP, TexCoordGeneration.TEXTURE_COORDINATE_2);
		//app.setTexCoordGeneration(tcg);

		// use a customized FontExtrusion object to control the depth of the text
		java.awt.geom.GeneralPath gp = new java.awt.geom.GeneralPath();
		gp.moveTo(0, 0);
		gp.lineTo(.01f, .01f);
		gp.lineTo(.2f, .01f);
		gp.lineTo(.21f, 0f);
		FontExtrusion fontEx = new FontExtrusion(gp);

		// our glyph
		Font fnt = new Font("dialog", Font.BOLD, 1);
		Font3D f3d = new Font3D(fnt, .001, fontEx);
		GeometryArray geom = f3d.getGlyphGeometry('A');
		Shape3D shape = new Shape3D(geom, app);
		objTransform.addChild(shape);

		// the DistortBehavior
		DistortBehavior eb = new DistortBehavior(shape, 1000, 1000);
		eb.setSchedulingBounds(new BoundingSphere());
		objTransform.addChild(eb);

		MouseRotate myMouseRotate = new MouseRotate();
		myMouseRotate.setTransformGroup(objTransform);
		myMouseRotate.setSchedulingBounds(new BoundingSphere());
		objRoot.addChild(myMouseRotate);

		MouseTranslate myMouseTranslate = new MouseTranslate();
		myMouseTranslate.setTransformGroup(objTransform);
		myMouseTranslate.setSchedulingBounds(new BoundingSphere());
		objRoot.addChild(myMouseTranslate);

		MouseZoom myMouseZoom = new MouseZoom();
		myMouseZoom.setTransformGroup(objTransform);
		myMouseZoom.setSchedulingBounds(new BoundingSphere());
		objRoot.addChild(myMouseZoom);

		// Let Java 3D perform optimizations on this scene graph.
		objRoot.compile();

		return objRoot;
	}

	private Canvas3D createUniverse()
	{

		// Create a Canvas3D using a nice configuration
		Canvas3D c = new Canvas3D(getGraphicsConfig());

		// Create simple universe with view branch
		univ = new SimpleUniverse(c);

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		univ.getViewingPlatform().setNominalViewingTransform();

		// Ensure at least 5 msec per frame (i.e., < 200Hz)
		univ.getViewer().getView().setMinimumFrameCycleTime(5);

		return c;
	}

	/**
	 * Creates new form DistortGlyphTest2
	 */
	public DistortGlyphTestGL2ES2()
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
		setTitle("DistortGlyphTest");
		drawingPanel.setLayout(new java.awt.BorderLayout());

		drawingPanel.setPreferredSize(new java.awt.Dimension(700, 700));
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
				new DistortGlyphTestGL2ES2().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel drawingPanel;
	// End of variables declaration//GEN-END:variables

	public static ShaderAppearance makeShaderAppearance()
	{
		ShaderAppearance app = new ShaderAppearance();
		GLSLShaderProgram litTextureShaderProgram = new GLSLShaderProgram() {
			@Override
			public String toString()
			{
				return "SimpleShaderAppearance litTextureShaderProgram";
			}
		};
		litTextureShaderProgram.setShaders(makeShaders(vertShader, fragShader));
		litTextureShaderProgram.setShaderAttrNames(new String[] { "EnvMap" });

		app.setShaderProgram(litTextureShaderProgram);

		ShaderAttributeSet shaderAttributeSet = new ShaderAttributeSet();
		shaderAttributeSet.put(new ShaderAttributeValue("EnvMap", new Integer(0)));
		app.setShaderAttributeSet(shaderAttributeSet);
		return app;
	}

	private static Shader[] makeShaders(String vertexProgram, String fragmentProgram)
	{
		Shader[] shaders = new Shader[2];
		shaders[0] = new SourceCodeShader(Shader.SHADING_LANGUAGE_GLSL, Shader.SHADER_TYPE_VERTEX, vertexProgram) {
			@Override
			public String toString()
			{
				return "vertexProgram";
			}
		};
		shaders[1] = new SourceCodeShader(Shader.SHADING_LANGUAGE_GLSL, Shader.SHADER_TYPE_FRAGMENT, fragmentProgram) {
			@Override
			public String toString()
			{
				return "fragmentProgram";
			}
		};
		return shaders;
	}

	public static String fragShader = "uniform sampler2D EnvMap;\n" + //
			"varying vec2 texCoord0;\n" + //
			"void main (void)\n" + //
			"{\n" + //
			"    vec2  tc0 = texCoord0.xy;\n" + //
			"    vec3 color = vec3(texture2D(EnvMap, tc0));\n" + //
			"    gl_FragColor = vec4(color, 1.0);\n" + //    
			"}\n";

	public static String vertShader = "attribute vec4 glVertex;\n" + //
			"attribute vec3 glNormal;  \n" + //
			"uniform mat4 glModelViewProjectionMatrix;\n" + //
			"uniform mat4 glModelViewMatrix;\n" + //
			"uniform mat3 glNormalMatrix;\n" + //
			"varying vec3 Normal;\n" + //
			"varying vec2 texCoord0;\n" + //
			"vec2 sphere_map(vec3 position, vec3 normal)\n" + //
			"{\n" + //
			"    vec3 reflection = reflect(position, normal);\n" + //
			"    float m = 2.0 * sqrt(reflection.x * reflection.x + reflection.y * reflection.y + (reflection.z + 1.0) * (reflection.z + 1.0)); \n"
			+ //
			"    return vec2((reflection.x / m + 0.5), (reflection.y / m + 0.5));\n" + //
			"}\n" + //
			"void main()\n" + //
			"{\n" + //
			"    Normal = normalize(vec3(glNormalMatrix * glNormal));\n" + //
			"    gl_Position = glModelViewProjectionMatrix * glVertex;	\n" + //    
			"	texCoord0 = sphere_map(normalize(vec3(glModelViewMatrix*glVertex)), Normal);\n" + //
			"}\n"; //

}
