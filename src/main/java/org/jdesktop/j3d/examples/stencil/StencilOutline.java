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

package org.jdesktop.j3d.examples.stencil;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.JOptionPane;

import org.jdesktop.j3d.examples.Resources;
import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.AmbientLight;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.Background;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.DirectionalLight;
import org.jogamp.java3d.GraphicsConfigTemplate3D;
import org.jogamp.java3d.Group;
import org.jogamp.java3d.LineAttributes;
import org.jogamp.java3d.Material;
import org.jogamp.java3d.Node;
import org.jogamp.java3d.PolygonAttributes;
import org.jogamp.java3d.RenderingAttributes;
import org.jogamp.java3d.RotationInterpolator;
import org.jogamp.java3d.ShaderAppearance;
import org.jogamp.java3d.ShaderError;
import org.jogamp.java3d.ShaderErrorListener;
import org.jogamp.java3d.ShaderProgram;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.loaders.IncorrectFormatException;
import org.jogamp.java3d.loaders.ParsingErrorException;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.behaviors.vp.OrbitBehavior;
import org.jogamp.java3d.utils.universe.PlatformGeometry;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.universe.ViewingPlatform;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3f;

/**
 * Simple Java 3D example program to display an .obj object with shader programs.
 * And then add a stencil based outline around it
 */
public class StencilOutline extends javax.swing.JFrame
{

	private String shaderName = "polkadot3d";
	private boolean spin = false;
	private boolean noTriangulate = false;
	private boolean noStripify = false;
	private double creaseAngle = 60.0;
	private URL filename = null;

	private SimpleUniverse univ = null;
	private BranchGroup scene = null;

	public BranchGroup createSceneGraph()
	{
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
		if (!noTriangulate)
			flags |= ObjectFile.TRIANGULATE;
		if (!noStripify)
			flags |= ObjectFile.STRIPIFY;
		ObjectFile f = new ObjectFile(flags, (float) (creaseAngle * Math.PI / 180.0));
		Scene s = null;
		try
		{
			s = f.load(filename);
		}
		catch (FileNotFoundException e)
		{
			System.err.println(e);
			System.exit(1);
		}
		catch (ParsingErrorException e)
		{
			System.err.println(e);
			System.exit(1);
		}
		catch (IncorrectFormatException e)
		{
			System.err.println(e);
			System.exit(1);
		}

		//Uncomment to use the gl2es2 pipeline, also see other commented code
		// Set vertex and fragment shader program for all Shape3D nodes in scene
		/*		String vertexProgram = null;
				String fragmentProgram = null;
				try
				{
					vertexProgram = StringIO.readFully(new File(
							System.getProperty("user.dir") + "/src/main/java/org/jdesktop/j3d/examples/gl2es2pipeline/" + shaderName + ".vert"));
					fragmentProgram = StringIO.readFully(new File(
							System.getProperty("user.dir") + "/src/main/java/org/jdesktop/j3d/examples/gl2es2pipeline/" + shaderName + ".frag"));
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
				setShaderProgram(s.getSceneGroup(), shaderProgram);*/

		setOutline(s.getSceneGroup());

		objTrans.addChild(s.getSceneGroup());

		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

		if (spin)
		{
			Transform3D yAxis = new Transform3D();
			Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, 4000, 0, 0, 0, 0, 0);

			RotationInterpolator rotator = new RotationInterpolator(rotationAlpha, objTrans, yAxis, 0.0f, (float) Math.PI * 2.0f);
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

	private Canvas3D createUniverse()
	{
		// Critical!!! notice this is not using this call, but explicitly asks for a stencil buffer
		//GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();		
		GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
		template.setStencilSize(16);
		// Return the GraphicsConfiguration that best fits our needs.
		GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getBestConfiguration(template);

		// Create a Canvas3D using the preferred configuration
		Canvas3D canvas3d = new Canvas3D(config);

		// Create simple universe with view branch
		univ = new SimpleUniverse(canvas3d);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

		// Add a ShaderErrorListener
		univ.addShaderErrorListener(new ShaderErrorListener() {
			@Override
			public void errorOccurred(ShaderError error)
			{
				error.printVerbose();
				JOptionPane.showMessageDialog(StencilOutline.this, error.toString(), "ShaderError", JOptionPane.ERROR_MESSAGE);
			}
		});

		// add mouse behaviors to the ViewingPlatform
		ViewingPlatform viewingPlatform = univ.getViewingPlatform();

		PlatformGeometry pg = new PlatformGeometry();

		// Set up the ambient light
		Color3f ambientColor = new Color3f(0.1f, 0.1f, 0.1f);
		AmbientLight ambientLightNode = new AmbientLight(ambientColor);
		ambientLightNode.setInfluencingBounds(bounds);
		pg.addChild(ambientLightNode);

		// Set up the directional lights
		Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f);
		Vector3f light1Direction = new Vector3f(1.0f, 1.0f, 1.0f);
		Color3f light2Color = new Color3f(1.0f, 1.0f, 1.0f);
		Vector3f light2Direction = new Vector3f(-1.0f, -1.0f, -1.0f);

		DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
		light1.setInfluencingBounds(bounds);
		pg.addChild(light1);

		DirectionalLight light2 = new DirectionalLight(light2Color, light2Direction);
		light2.setInfluencingBounds(bounds);
		pg.addChild(light2);

		viewingPlatform.setPlatformGeometry(pg);

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		viewingPlatform.setNominalViewingTransform();

		if (!spin)
		{
			OrbitBehavior orbit = new OrbitBehavior(canvas3d, OrbitBehavior.REVERSE_ALL);
			orbit.setSchedulingBounds(bounds);
			viewingPlatform.setViewPlatformBehavior(orbit);
		}

		// Ensure at least 5 msec per frame (i.e., < 200Hz)
		univ.getViewer().getView().setMinimumFrameCycleTime(5);

		return canvas3d;
	}

	private static void usage()
	{
		System.out.println("Usage: java ObjLoadGLSL [-s] [-S shaderName] [-n] [-t] [-c degrees] <.obj file>");
		System.out.println("  -s Spin (no user interaction)");
		System.out.println("  -S Set shader name (default is 'simple')");
		System.out.println("  -n No triangulation");
		System.out.println("  -t No stripification");
		System.out.println("  -c Set crease angle for normal generation (default is 60 without");
		System.out.println("     smoothing group info, otherwise 180 within smoothing groups)");
		System.exit(0);
	} // End of usage

	// Set shader program for all nodes in specified branch graph
	private void setShaderProgram(BranchGroup g, ShaderProgram shaderProgram)
	{
		ShaderAppearance myApp = new ShaderAppearance();
		Material mat = new Material();
		myApp.setShaderProgram(shaderProgram);
		myApp.setMaterial(mat);
		setShaderProgram(g, myApp);
	}

	// Recursively set shader program for all children of specified group
	private void setShaderProgram(Group g, ShaderAppearance myApp)
	{

		Enumeration<Node> e = g.getAllChildren();
		while (e.hasMoreElements())
		{
			Node n = e.nextElement();
			if (n instanceof Group)
			{
				setShaderProgram((Group) n, myApp);
			}
			else if (n instanceof Shape3D)
			{
				Shape3D s = (Shape3D) n;
				s.setAppearance(myApp);
			}
		}
	}

	private Color3f c = new Color3f(1.0f, 1.0f, 0);
	private int outlineStencilMask = (int) (c.x * 255) + (int) (c.y * 255) + (int) (c.z * 255);

	// Recursively set an outline onto all Shape3D nodes
	private void setOutline(Group g)
	{

		Enumeration<Node> e = g.getAllChildren();
		while (e.hasMoreElements())
		{
			Node n = e.nextElement();
			if (n instanceof Group)
			{
				setOutline((Group) n);
			}
			else if (n instanceof Shape3D)
			{
				// start by giving the current appearance a rendering attribute
				Shape3D s = (Shape3D) n;
				Appearance sapp = s.getAppearance();

				// get and ensure rend atts exist
				RenderingAttributes ra1 = sapp.getRenderingAttributes();
				if (ra1 == null)
				{
					ra1 = new RenderingAttributes();
					sapp.setRenderingAttributes(ra1);
				}

				ra1.setStencilEnable(true);
				ra1.setStencilWriteMask(outlineStencilMask);
				ra1.setStencilFunction(RenderingAttributes.ALWAYS, outlineStencilMask, outlineStencilMask);
				ra1.setStencilOp(RenderingAttributes.STENCIL_REPLACE, //
						RenderingAttributes.STENCIL_REPLACE, //
						RenderingAttributes.STENCIL_REPLACE);

				sapp.setRenderingAttributes(ra1);

				// now attach an outline shape
				Shape3D outliner = new Shape3D();

				////////////////////////////////
				//Outliner gear, note empty geom should be ignored
				
				//Uncomment to use the gl2es2 pipeline, also see other commented code
				//Appearance app = new SimpleShaderAppearance(c);
				
				//Comment to use the gl2es2 pipeline, also see other commented code
				Appearance app = new Appearance();

				// lineAntialiasing MUST be true, to force this to be done during rendering pass (otherwise it's hidden)
				LineAttributes la = new LineAttributes(4, LineAttributes.PATTERN_SOLID, true);
				app.setLineAttributes(la);
				PolygonAttributes pa = new PolygonAttributes(PolygonAttributes.POLYGON_LINE, PolygonAttributes.CULL_BACK, 0.0f, true, 0.0f);
				app.setPolygonAttributes(pa);
				ColoringAttributes colorAtt = new ColoringAttributes(c, ColoringAttributes.FASTEST);
				app.setColoringAttributes(colorAtt);

				RenderingAttributes ra2 = new RenderingAttributes();
				ra2.setStencilEnable(true);
				ra2.setStencilWriteMask(outlineStencilMask);
				ra2.setStencilFunction(RenderingAttributes.NOT_EQUAL, outlineStencilMask, outlineStencilMask);
				ra2.setStencilOp(RenderingAttributes.STENCIL_KEEP, //
						RenderingAttributes.STENCIL_KEEP, //
						RenderingAttributes.STENCIL_KEEP);

				// draw it even when hidden, which we don't want now
				ra2.setDepthBufferEnable(false);
				ra2.setDepthTestFunction(RenderingAttributes.ALWAYS);

				app.setRenderingAttributes(ra2);

				outliner.setAppearance(app);

				// use the same geometry ass teh shape we are outlining!
				outliner.setGeometry(s.getGeometry());

				g.addChild(outliner);

			}
		}
	}

	/**
	 * Creates new form ObjLoadGLSL
	 */
	public StencilOutline(String args[])
	{
		if (args.length != 0)
		{
			for (int i = 0; i < args.length; i++)
			{
				if (args[i].startsWith("-"))
				{
					if (args[i].equals("-s"))
					{
						spin = true;
					}
					else if (args[i].equals("-n"))
					{
						noTriangulate = true;
					}
					else if (args[i].equals("-t"))
					{
						noStripify = true;
					}
					else if (args[i].equals("-c"))
					{
						if (i < args.length - 1)
						{
							creaseAngle = (new Double(args[++i])).doubleValue();
						}
						else
							usage();
					}
					else if (args[i].equals("-S"))
					{
						if (i < args.length - 1)
						{
							shaderName = args[++i];
						}
						else
							usage();
					}
					else
					{
						usage();
					}
				}
				else
				{
					try
					{
						if ((args[i].indexOf("file:") == 0) || (args[i].indexOf("http") == 0))
						{
							filename = new URL(args[i]);
						}
						else if (args[i].charAt(0) != '/')
						{
							filename = new URL("file:./" + args[i]);
						}
						else
						{
							filename = new URL("file:" + args[i]);
						}
					}
					catch (MalformedURLException e)
					{
						System.err.println(e);
						System.exit(1);
					}
				}
			}
		}

		if (filename == null)
		{
			filename = Resources.getResource("resources/geometry/galleon.obj");
			if (filename == null)
			{
				System.err.println("resources/geometry/galleon.obj not found");
				System.exit(1);
			}
		}

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
		setTitle("StencilOutline");
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

		System.setProperty("j3d.stencilClear", "true");

		//Uncomment to use the gl2es2 pipeline, also see other commented code
		//System.setProperty("j3d.rend", "jogl2es2");

		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				StencilOutline objLoadGLSL = new StencilOutline(args);
				objLoadGLSL.setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel drawingPanel;
	// End of variables declaration//GEN-END:variables

}
