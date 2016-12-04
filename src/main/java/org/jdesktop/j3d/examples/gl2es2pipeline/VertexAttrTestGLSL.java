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

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.Background;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.GLSLShaderProgram;
import org.jogamp.java3d.GeometryArray;
import org.jogamp.java3d.J3DBuffer;
import org.jogamp.java3d.Shader;
import org.jogamp.java3d.ShaderAppearance;
import org.jogamp.java3d.ShaderError;
import org.jogamp.java3d.ShaderErrorListener;
import org.jogamp.java3d.ShaderProgram;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.SourceCodeShader;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.TriangleArray;
import org.jogamp.java3d.utils.shader.StringIO;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.universe.ViewingPlatform;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;

public class VertexAttrTestGLSL extends javax.swing.JFrame
{

	SimpleUniverse univ = null;
	BranchGroup scene = null;

	public BranchGroup createSceneGraph(boolean hasVertexAttrs)
	{

		// Bounds for BG and behavior
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);

		// Set up the background
		Color3f bgColor = new Color3f(0.1f, 0.1f, 0.1f);
		Background bg = new Background(bgColor);
		bg.setApplicationBounds(bounds);
		objRoot.addChild(bg);

		// Create the TransformGroup node and initialize it to the
		// identity. Enable the TRANSFORM_WRITE capability so that
		// our behavior code can modify it at run time. Add it to
		// the root of the subgraph.
		TransformGroup objTrans = new TransformGroup();
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objRoot.addChild(objTrans);

		// Create a simple Shape3D node; add it to the scene graph.
		objTrans.addChild(new MyShape(this, hasVertexAttrs));

		return objRoot;
	}

	private Canvas3D initScene()
	{
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

		Canvas3D c = new Canvas3D(config);
		univ = new SimpleUniverse(c);

		// Add a ShaderErrorListener
		univ.addShaderErrorListener(new ShaderErrorListener() {
			@Override
			public void errorOccurred(ShaderError error)
			{
				error.printVerbose();
				JOptionPane.showMessageDialog(VertexAttrTestGLSL.this, error.toString(), "ShaderError", JOptionPane.ERROR_MESSAGE);
			}
		});

		ViewingPlatform viewingPlatform = univ.getViewingPlatform();
		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		viewingPlatform.setNominalViewingTransform();

		return c;
	}

	/**
	 * Creates new form VertexAttrTestGLSL
	 */
	public VertexAttrTestGLSL()
	{
		// Initialize the GUI components
		initComponents();

		// Create the scene and add the Canvas3D to the drawing panel
		Canvas3D c = initScene();
		drawingPanel.add(c, java.awt.BorderLayout.CENTER);
	}

	static class MyShape extends Shape3D
	{

		// Coordinate data
		private static final float[] coords = { 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, };

		private static final int[] sizes = { 1, 3 };
		private static final float[] weights = { 0.45f, 0.15f, 0.95f, };
		private static final float[] temps = { 1.0f, 0.5f, 0.5f, 0.5f, 1.0f, 0.5f, 0.5f, 0.5f, 1.0f, };

		private static final String[] vaNames = { "weight", "temperature" };

		J3DBuffer createDirectFloatBuffer(float[] arr)
		{
			ByteOrder order = ByteOrder.nativeOrder();

			FloatBuffer nioBuf = ByteBuffer.allocateDirect(arr.length * 4).order(order).asFloatBuffer();
			nioBuf.put(arr);
			return new J3DBuffer(nioBuf);
		}

		MyShape(JFrame frame, boolean hasVertexAttrs)
		{

			int vertexFormat = GeometryArray.COORDINATES;
			int vertexAttrCount = 0;
			int[] vertexAttrSizes = null;
			String[] vertexAttrNames = null;
			String[] shaderAttrNames = null;

			if (hasVertexAttrs)
			{
				vertexFormat |= GeometryArray.VERTEX_ATTRIBUTES;
				vertexAttrCount = vaNames.length;
				vertexAttrSizes = sizes;
				vertexAttrNames = vaNames;
			}
			
			//GL2ES2: requires by reference
			vertexFormat |= GeometryArray.BY_REFERENCE;

			TriangleArray tri = new TriangleArray(6, vertexFormat, 0, null, vertexAttrCount, vertexAttrSizes);
			tri.setValidVertexCount(3);
			//tri.setCoordinates(0, coords);
			tri.setCoordRefFloat(coords);

			if (hasVertexAttrs)
			{
				//tri.setVertexAttrs(0, 0, weights);
				//tri.setVertexAttrs(1, 0, temps);
				
				tri.setVertexAttrRefFloat(0, weights);
				tri.setVertexAttrRefFloat(1, temps);

				String vertexProgram = null;
				String fragmentProgram = null;
				try
				{
					vertexProgram = StringIO.readFully(new File(
							System.getProperty("user.dir") + "/src/main/java/org/jdesktop/j3d/examples/gl2es2pipeline/vertexshader.vert"));
					fragmentProgram = StringIO.readFully(new File(
							System.getProperty("user.dir") + "/src/main/java/org/jdesktop/j3d/examples/gl2es2pipeline/vertexshader.frag"));
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
				shaderProgram.setVertexAttrNames(vertexAttrNames);
				shaderProgram.setShaderAttrNames(shaderAttrNames);

				ShaderAppearance app = new ShaderAppearance();
				app.setShaderProgram(shaderProgram);

				this.setGeometry(tri);

				this.setAppearance(app);
			}
			else
			{
				this.setGeometry(tri);
				this.setAppearance(new Appearance());
			}
		}
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
		java.awt.GridBagConstraints gridBagConstraints;

		mainPanel = new javax.swing.JPanel();
		guiPanel = new javax.swing.JPanel();
		vertexCheckBoxPanel = new javax.swing.JPanel();
		jPanel1 = new javax.swing.JPanel();
		jSeparator1 = new javax.swing.JSeparator();
		jSeparator2 = new javax.swing.JSeparator();
		jPanel2 = new javax.swing.JPanel();
		vertexAttrsBox = new javax.swing.JCheckBox();
		geometryPanel = new javax.swing.JPanel();
		createButton = new javax.swing.JButton();
		destroyButton = new javax.swing.JButton();
		drawingPanel = new javax.swing.JPanel();
		jMenuBar1 = new javax.swing.JMenuBar();
		fileMenu = new javax.swing.JMenu();
		exitMenuItem = new javax.swing.JMenuItem();

		setTitle("VertexAttrTestGLSL");
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				exitForm(evt);
			}
		});

		mainPanel.setLayout(new java.awt.BorderLayout());

		guiPanel.setLayout(new java.awt.GridBagLayout());

		guiPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		vertexCheckBoxPanel.setLayout(new java.awt.GridBagLayout());

		vertexCheckBoxPanel.setBorder(
				javax.swing.BorderFactory.createTitledBorder(null, "vertexFormat", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Sans", 0, 10)));
		jPanel1.setLayout(new java.awt.GridBagLayout());

		jSeparator1.setPreferredSize(new java.awt.Dimension(0, 4));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		jPanel1.add(jSeparator1, gridBagConstraints);

		jSeparator2.setPreferredSize(new java.awt.Dimension(0, 4));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		jPanel1.add(jSeparator2, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		vertexCheckBoxPanel.add(jPanel1, gridBagConstraints);

		jPanel2.setLayout(new java.awt.GridBagLayout());

		vertexAttrsBox.setSelected(true);
		vertexAttrsBox.setText("VertexAttrs");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		jPanel2.add(vertexAttrsBox, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		vertexCheckBoxPanel.add(jPanel2, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
		guiPanel.add(vertexCheckBoxPanel, gridBagConstraints);

		geometryPanel.setLayout(new java.awt.GridBagLayout());

		createButton.setText("Create Geometry");
		createButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				createButtonActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		geometryPanel.add(createButton, gridBagConstraints);

		destroyButton.setText("Destroy Geometry");
		destroyButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				destroyButtonActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
		geometryPanel.add(destroyButton, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
		guiPanel.add(geometryPanel, gridBagConstraints);

		mainPanel.add(guiPanel, java.awt.BorderLayout.NORTH);

		drawingPanel.setLayout(new java.awt.BorderLayout());

		drawingPanel.setPreferredSize(new java.awt.Dimension(500, 500));
		mainPanel.add(drawingPanel, java.awt.BorderLayout.CENTER);

		getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

		fileMenu.setText("File");
		exitMenuItem.setText("Exit");
		exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				exitMenuItemActionPerformed(evt);
			}
		});

		fileMenu.add(exitMenuItem);

		jMenuBar1.add(fileMenu);

		setJMenuBar(jMenuBar1);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void destroyButtonActionPerformed(java.awt.event.ActionEvent evt)
	{//GEN-FIRST:event_destroyButtonActionPerformed
		if (scene != null)
		{
			univ.getLocale().removeBranchGraph(scene);
			scene = null;
		}
	}//GEN-LAST:event_destroyButtonActionPerformed

	private void createButtonActionPerformed(java.awt.event.ActionEvent evt)
	{//GEN-FIRST:event_createButtonActionPerformed
		if (scene == null)
		{
			boolean hasVertexAttrs = vertexAttrsBox.isSelected();
			scene = createSceneGraph(hasVertexAttrs);
			univ.addBranchGraph(scene);
		}
	}//GEN-LAST:event_createButtonActionPerformed

	private static void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt)
	{//GEN-FIRST:event_exitMenuItemActionPerformed
		System.exit(0);
	}//GEN-LAST:event_exitMenuItemActionPerformed

	/** Exit the Application */
	private static void exitForm(java.awt.event.WindowEvent evt)
	{//GEN-FIRST:event_exitForm
		System.exit(0);
	}//GEN-LAST:event_exitForm

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
				new VertexAttrTestGLSL().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton createButton;
	private javax.swing.JButton destroyButton;
	private javax.swing.JPanel drawingPanel;
	private javax.swing.JMenuItem exitMenuItem;
	private javax.swing.JMenu fileMenu;
	private javax.swing.JPanel geometryPanel;
	private javax.swing.JPanel guiPanel;
	private javax.swing.JMenuBar jMenuBar1;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSeparator jSeparator2;
	private javax.swing.JPanel mainPanel;
	private javax.swing.JCheckBox vertexAttrsBox;
	private javax.swing.JPanel vertexCheckBoxPanel;
	// End of variables declaration//GEN-END:variables

}
