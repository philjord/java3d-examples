/*
 * $RCSfile$
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 *
 * $Revision$
 * $Date$
 * $State$
 */

package org.jdesktop.j3d.examples.glsl_shader;

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.shader.StringIO;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.GraphicsConfiguration;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.jdesktop.j3d.examples.Resources;

public class ShaderTestGLSL extends javax.swing.JFrame {

    static final int GOLD   = 1;
    static final int SILVER = 2;
    
    static final int DIMPLE_SHADER      = 1;
    static final int BRICK_SHADER       = 2;
    static final int WOOD_SHADER        = 3;
    static final int POLKADOT3D_SHADER  = 4;        
    
    static final String[] shaderAttrNames1 = {
        "Density", "Size", "LightPosition", "Color"
    };

    static final String[] shaderAttrNames2 = {
        "BrickColor", "LightPosition"
    };
    
    private SimpleUniverse univ = null;
    private View view;
    private BranchGroup transpObj;
    private BranchGroup scene = null;
    private int shaderSelected = DIMPLE_SHADER;
    private float density = 16.0f;
    private int color = GOLD; 
    
    private Color3f eColor    = new Color3f(0.2f, 0.2f, 0.2f);
    private Color3f sColor    = new Color3f(0.8f, 0.8f, 0.8f);
    private Color3f objColor  = new Color3f(0.6f, 0.6f, 0.6f);
    private Color3f bgColor   = new Color3f(0.05f, 0.05f, 0.2f);
    private Color3f gold      = new Color3f(0.7f, 0.6f, 0.18f);
    private Color3f silver    = new Color3f(0.75f, 0.75f, 0.75f);
    
    // Handlers for doing update
    private ShaderAppearance sApp1 = null;
    private ShaderAppearance sApp2 = null;    
    private ShaderAppearance sApp3 = null;    
    private ShaderAppearance sApp4 = null;    
    private ShaderProgram sp1 = null;
    private ShaderProgram sp2 = null;
    private ShaderProgram sp3 = null;
    private ShaderProgram sp4 = null;
    private ShaderAttributeSet sas1 = null;
    private ShaderAttributeSet sas2 = null;    
    private ShaderAttributeObject sao1 = null;
    private ShaderAttributeObject sao2 = null;    
    private Sphere sphere = null;
    private Shape3D s3d = null;
    
    private Material createMaterial() {
    	Material m;
        m = new Material(objColor, eColor, objColor, sColor, 100.0f);
	m.setLightingEnable(true);
        return m;
    }
 
    private ShaderProgram createGLSLShaderProgram(int index) {
        String vertexProgram = null;
	String fragmentProgram = null;
	try {
            switch (index) {
                case DIMPLE_SHADER:
                    vertexProgram = StringIO.readFully(Resources.getResource("glsl_shader/dimple.vert"));
                    fragmentProgram = StringIO.readFully(Resources.getResource("glsl_shader/dimple.frag"));
                    break;
                case BRICK_SHADER:
        	    vertexProgram = StringIO.readFully(Resources.getResource("glsl_shader/aabrick.vert"));
                    fragmentProgram = StringIO.readFully(Resources.getResource("glsl_shader/aabrick.frag"));
                    break;
                case WOOD_SHADER:
        	    vertexProgram = StringIO.readFully(Resources.getResource("glsl_shader/wood.vert"));
                    fragmentProgram = StringIO.readFully(Resources.getResource("glsl_shader/wood.frag"));
                    break;
                case POLKADOT3D_SHADER:
        	    vertexProgram = StringIO.readFully(Resources.getResource("glsl_shader/polkadot3d.vert"));
                    fragmentProgram = StringIO.readFully(Resources.getResource("glsl_shader/polkadot3d.frag"));
                    break;                    
                default:
            }
	}
 	catch (IOException e) {
	    throw new RuntimeException(e);
	}
  	Shader[] shaders = new Shader[2];
	shaders[0] = new SourceCodeShader(Shader.SHADING_LANGUAGE_GLSL,
					  Shader.SHADER_TYPE_VERTEX,
					  vertexProgram);
	shaders[1] = new SourceCodeShader(Shader.SHADING_LANGUAGE_GLSL,
					  Shader.SHADER_TYPE_FRAGMENT,
					  fragmentProgram);
	ShaderProgram shaderProgram = new GLSLShaderProgram();
        shaderProgram.setShaders(shaders);
        return shaderProgram;
    }    

    private ShaderAttributeSet createShaderAttributeSet(int index) {
        ShaderAttributeSet shaderAttributeSet = new ShaderAttributeSet();
        ShaderAttributeObject shaderAttribute = null;
        
        switch (index) {
            case DIMPLE_SHADER:
                //  "Density", "Size", "Scale", "Color", "LightPosition"
                shaderAttribute = new ShaderAttributeValue("Size", new Float(0.25));
                shaderAttributeSet.put(shaderAttribute);
                shaderAttribute = new ShaderAttributeValue("LightPosition",
                        new Point3f(0.0f, 0.0f, 0.5f));
                shaderAttributeSet.put(shaderAttribute);
                
                sao1 = new ShaderAttributeValue("Density", new Float(density));
                sao1.setCapability(ShaderAttributeObject.ALLOW_VALUE_READ);
                sao1.setCapability(ShaderAttributeObject.ALLOW_VALUE_WRITE);
                shaderAttributeSet.put(sao1);
                
                if(color == GOLD) {
                    sao2 = new ShaderAttributeValue("Color", gold);
                }
                else if (color == SILVER) {    
                    sao2 = new ShaderAttributeValue("Color", silver);
                }
                sao2.setCapability(ShaderAttributeObject.ALLOW_VALUE_READ);
                sao2.setCapability(ShaderAttributeObject.ALLOW_VALUE_WRITE);
                shaderAttributeSet.put(sao2);                
                break;
                
            case BRICK_SHADER:
                // "BrickColor", "LightPosition"
                shaderAttribute = new ShaderAttributeValue("BrickColor",
                        new Color3f(1.0f, 0.3f, 0.2f));
                shaderAttributeSet.put(shaderAttribute);
                shaderAttribute = new ShaderAttributeValue("LightPosition",
                        new Point3f(0.0f, 0.0f, 0.5f));
                shaderAttributeSet.put(shaderAttribute);
                break;
            default:
                assert false;
        }
        return shaderAttributeSet;
    }
    
    private ShaderAppearance createShaderAppearance() {
        ShaderAppearance sApp = new ShaderAppearance();
        sApp.setMaterial(createMaterial());
        return sApp;
    }
    
    
    private BranchGroup createSubSceneGraph() {
        // Create the sub-root of the branch graph
        BranchGroup subRoot = new BranchGroup();
        
        //
        // Create 1 spheres with a GLSLShader and add it into the scene graph.
        //
        sApp1 = createShaderAppearance();
        sApp1.setCapability(ShaderAppearance.ALLOW_SHADER_PROGRAM_READ);
        sApp1.setCapability(ShaderAppearance.ALLOW_SHADER_PROGRAM_WRITE);
        sApp1.setCapability(ShaderAppearance.ALLOW_SHADER_ATTRIBUTE_SET_READ);
        sApp1.setCapability(ShaderAppearance.ALLOW_SHADER_ATTRIBUTE_SET_WRITE);

        sp1 = createGLSLShaderProgram(1);
        sp1.setShaderAttrNames(shaderAttrNames1);
        sas1 = createShaderAttributeSet(1);
        sas1.setCapability(ShaderAttributeSet.ALLOW_ATTRIBUTES_READ);
        sas1.setCapability(ShaderAttributeSet.ALLOW_ATTRIBUTES_WRITE);
        sApp1.setShaderProgram(sp1);
        sApp1.setShaderAttributeSet(sas1);
                
        // Setup Brick shader
        sp2 = createGLSLShaderProgram(2);
        sp2.setShaderAttrNames(shaderAttrNames2);
        sas2 = createShaderAttributeSet(2);
        sApp2 = createShaderAppearance();
        sApp2.setShaderProgram(sp2);
        sApp2.setShaderAttributeSet(sas2);

        // Setup Wood shader
        sp3 = createGLSLShaderProgram(3);
        sApp3 = createShaderAppearance();
        sApp3.setShaderProgram(sp3);

        // Setup Polkadot3d shader
        sp4 = createGLSLShaderProgram(4);
        sApp4 = createShaderAppearance();
        sApp4.setShaderProgram(sp4);

        sphere = new Sphere(1.5f, Sphere.GENERATE_NORMALS, 200, null);
        s3d = (Shape3D)sphere.getShape();
        s3d.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        s3d.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        s3d.setAppearance(sApp1);
        
        TransformGroup objTG;
        Transform3D t = new Transform3D();
        t.set(new Vector3d(0.0, 0.0, 0.0));
        objTG = new TransformGroup(t);
        objTG.addChild(sphere);
        subRoot.addChild(objTG);
        
        return subRoot;
    }
    
    private BranchGroup createSceneGraph(int selectedScene) {
        // Create the root of the branch graph
        BranchGroup objRoot = new BranchGroup();
        objRoot.setCapability(BranchGroup.ALLOW_DETACH);
        
        // Create a Transformgroup to scale all objects so they
        // appear in the scene.
        TransformGroup objScale = new TransformGroup();
        Transform3D t3d = new Transform3D();
        t3d.setScale(0.4);
        objScale.setTransform(t3d);
        objRoot.addChild(objScale);
        
        // Create a bounds for the background and lights
        BoundingSphere bounds =
                new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
        
        // Set up the background
        Background bg = new Background(bgColor);
        bg.setApplicationBounds(bounds);
        objScale.addChild(bg);
        
        objScale.addChild(createSubSceneGraph());
        
        // Create a position interpolator and attach it to the view
        // platform
        TransformGroup vpTrans =
                univ.getViewingPlatform().getViewPlatformTransform();
        Transform3D axisOfTranslation = new Transform3D();
        Alpha transAlpha = new Alpha(-1,
                Alpha.INCREASING_ENABLE |
                Alpha.DECREASING_ENABLE,
                0, 0,
                5000, 0, 0,
                5000, 0, 0);
        axisOfTranslation.rotY(-Math.PI/2.0);
        PositionInterpolator translator =
                new PositionInterpolator(transAlpha,
                vpTrans,
                axisOfTranslation,
                2.0f, 3.5f);
        translator.setSchedulingBounds(bounds);
        objScale.addChild(translator);
        
        // Let Java 3D perform optimizations on this scene graph.
        objRoot.compile();
        
        return objRoot;
    }
    
    private Canvas3D initScene() {
        GraphicsConfiguration config =
                SimpleUniverse.getPreferredConfiguration();
        
        Canvas3D c = new Canvas3D(config);
        
        univ = new SimpleUniverse(c);

        // Add a ShaderErrorListener
        univ.addShaderErrorListener(new ShaderErrorListener() {
            public void errorOccurred(ShaderError error) {
                error.printVerbose();
                JOptionPane.showMessageDialog(ShaderTestGLSL.this,
                              error.toString(),
                              "ShaderError",
                              JOptionPane.ERROR_MESSAGE);
            }
        });

        ViewingPlatform viewingPlatform = univ.getViewingPlatform();
        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        viewingPlatform.setNominalViewingTransform();
        
        view = univ.getViewer().getView();
        
        return c;
    }
    
    /**
     * Creates new form ShaderTestGLSL
     */
    public ShaderTestGLSL() {
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
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        densityButtonGroup = new javax.swing.ButtonGroup();
        colorButtonGroup = new javax.swing.ButtonGroup();
        sceneGraphButtonGroup = new javax.swing.ButtonGroup();
        mainPanel = new javax.swing.JPanel();
        guiPanel = new javax.swing.JPanel();
        densityPanel = new javax.swing.JPanel();
        zeroButton = new javax.swing.JRadioButton();
        halfButton = new javax.swing.JRadioButton();
        fullButton = new javax.swing.JRadioButton();
        colorPanel = new javax.swing.JPanel();
        goldButton = new javax.swing.JRadioButton();
        silverButton = new javax.swing.JRadioButton();
        sceneGraphPanel = new javax.swing.JPanel();
        DetachButton = new javax.swing.JToggleButton();
        AttachButton = new javax.swing.JToggleButton();
        replaceSPButton = new javax.swing.JButton();
        drawingPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();

        setTitle("Window Title");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        mainPanel.setLayout(new java.awt.BorderLayout());

        guiPanel.setLayout(new javax.swing.BoxLayout(guiPanel, javax.swing.BoxLayout.X_AXIS));

        guiPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        densityPanel.setLayout(new java.awt.GridBagLayout());

        densityPanel.setBorder(new javax.swing.border.TitledBorder("Density"));
        densityButtonGroup.add(zeroButton);
        zeroButton.setText("Zero");
        zeroButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeroButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        densityPanel.add(zeroButton, gridBagConstraints);

        densityButtonGroup.add(halfButton);
        halfButton.setText("Half");
        halfButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                halfButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        densityPanel.add(halfButton, gridBagConstraints);

        densityButtonGroup.add(fullButton);
        fullButton.setSelected(true);
        fullButton.setText("Full");
        fullButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fullButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        densityPanel.add(fullButton, gridBagConstraints);

        guiPanel.add(densityPanel);
        densityPanel.getAccessibleContext().setAccessibleName("ShaderAttributeValue \n");

        colorPanel.setLayout(new java.awt.GridBagLayout());

        colorPanel.setBorder(new javax.swing.border.TitledBorder("Color"));
        colorButtonGroup.add(goldButton);
        goldButton.setSelected(true);
        goldButton.setText("Gold");
        goldButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goldButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        colorPanel.add(goldButton, gridBagConstraints);

        colorButtonGroup.add(silverButton);
        silverButton.setText("Silver");
        silverButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                silverButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        colorPanel.add(silverButton, gridBagConstraints);

        guiPanel.add(colorPanel);

        sceneGraphPanel.setLayout(new java.awt.GridBagLayout());

        sceneGraphPanel.setBorder(new javax.swing.border.TitledBorder("Scene Graph"));
        sceneGraphButtonGroup.add(DetachButton);
        DetachButton.setSelected(true);
        DetachButton.setText("Detach");
        DetachButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DetachButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        sceneGraphPanel.add(DetachButton, gridBagConstraints);

        sceneGraphButtonGroup.add(AttachButton);
        AttachButton.setText("Create");
        AttachButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AttachButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        sceneGraphPanel.add(AttachButton, gridBagConstraints);

        replaceSPButton.setText("Replace Shader");
        replaceSPButton.setEnabled(false);
        replaceSPButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceSPButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        sceneGraphPanel.add(replaceSPButton, gridBagConstraints);

        guiPanel.add(sceneGraphPanel);

        mainPanel.add(guiPanel, java.awt.BorderLayout.NORTH);

        drawingPanel.setLayout(new java.awt.BorderLayout());

        drawingPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        mainPanel.add(drawingPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        fileMenu.setText("File");
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(exitMenuItem);

        jMenuBar1.add(fileMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void silverButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_silverButtonActionPerformed
        color = SILVER;
        if(scene != null) {
            sao2.setValue(silver);
        }
    }//GEN-LAST:event_silverButtonActionPerformed

    private void goldButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goldButtonActionPerformed
        color = GOLD;
        if(scene != null) {
            sao2.setValue(gold);
        }
    }//GEN-LAST:event_goldButtonActionPerformed

    private void replaceSPButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceSPButtonActionPerformed
      if (shaderSelected != DIMPLE_SHADER) {
          goldButton.setEnabled(false);
          silverButton.setEnabled(false);
          zeroButton.setEnabled(false);
          halfButton.setEnabled(false);
          fullButton.setEnabled(false);
      }
      
      switch(shaderSelected) {
          case DIMPLE_SHADER:
             s3d.setAppearance(sApp1);
             goldButton.setEnabled(true);
             silverButton.setEnabled(true);
             zeroButton.setEnabled(true);
             halfButton.setEnabled(true);
             fullButton.setEnabled(true); 
             shaderSelected = BRICK_SHADER;
             break;
          case BRICK_SHADER:
             s3d.setAppearance(sApp2);
             shaderSelected = WOOD_SHADER;
             break;
          case WOOD_SHADER:
              s3d.setAppearance(sApp3);
              shaderSelected = POLKADOT3D_SHADER;
              break;
          case POLKADOT3D_SHADER:
              s3d.setAppearance(sApp4);
              shaderSelected = DIMPLE_SHADER;
              break;
          default:
              assert false;
      }
      
    }//GEN-LAST:event_replaceSPButtonActionPerformed

    private void fullButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullButtonActionPerformed
        density = 16.0f;
        if (scene != null) {
            sao1.setValue(new Float(density));
        }
    }//GEN-LAST:event_fullButtonActionPerformed

    private void DetachButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DetachButtonActionPerformed
        if (scene != null) {
            scene.detach();
            scene = null;
            replaceSPButton.setEnabled(false);
            goldButton.setEnabled(true);
            silverButton.setEnabled(true);
            zeroButton.setEnabled(true);
            halfButton.setEnabled(true);
            fullButton.setEnabled(true);
            shaderSelected = DIMPLE_SHADER;           
        }
    }//GEN-LAST:event_DetachButtonActionPerformed

    private void AttachButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AttachButtonActionPerformed
       if (scene == null) {
            scene = createSceneGraph(1);
            univ.addBranchGraph(scene);
            replaceSPButton.setEnabled(true);
            shaderSelected = BRICK_SHADER;
       }  
    }//GEN-LAST:event_AttachButtonActionPerformed

    private void halfButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_halfButtonActionPerformed
        density = 8.0f;
        if(scene != null) {
            sao1.setValue(new Float(density));
        }
    }//GEN-LAST:event_halfButtonActionPerformed

    private void zeroButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zeroButtonActionPerformed
        density = 0.0f;
        if(scene != null) {
            sao1.setValue(new Float(density));
        }
  
    }//GEN-LAST:event_zeroButtonActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new ShaderTestGLSL().setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton AttachButton;
    private javax.swing.JToggleButton DetachButton;
    private javax.swing.ButtonGroup colorButtonGroup;
    private javax.swing.JPanel colorPanel;
    private javax.swing.ButtonGroup densityButtonGroup;
    private javax.swing.JPanel densityPanel;
    private javax.swing.JPanel drawingPanel;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JRadioButton fullButton;
    private javax.swing.JRadioButton goldButton;
    private javax.swing.JPanel guiPanel;
    private javax.swing.JRadioButton halfButton;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton replaceSPButton;
    private javax.swing.ButtonGroup sceneGraphButtonGroup;
    private javax.swing.JPanel sceneGraphPanel;
    private javax.swing.JRadioButton silverButton;
    private javax.swing.JRadioButton zeroButton;
    // End of variables declaration//GEN-END:variables

}
