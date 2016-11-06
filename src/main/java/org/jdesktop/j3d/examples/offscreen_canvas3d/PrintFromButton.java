/*
 * $RCSfile$
 *
 * Copyright (c) 2007 Sun Microsystems, Inc. All rights reserved.
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

package org.jdesktop.j3d.examples.offscreen_canvas3d;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import javax.swing.JPopupMenu;

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
import org.jogamp.java3d.utils.geometry.ColorCube;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3d;

/**
 * PrintFromButton programs with simple UI.
 */
public class PrintFromButton extends javax.swing.JFrame {

    private SimpleUniverse univ = null;
    private BranchGroup scene = null;
    private Raster drawRaster = null;    
    private OffScreenCanvas3D offScreenCanvas = null;

    public BranchGroup createSceneGraph() {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

	// trans object has composited transformation matrix
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
	objTrans.addChild(new ColorCube(0.4));

	//Create a raster 
	BufferedImage bImage = new BufferedImage(200, 200 ,
						 BufferedImage.TYPE_INT_ARGB);        
	ImageComponent2D buffer =
	    new ImageComponent2D(ImageComponent.FORMAT_RGBA, bImage, true, true);
	buffer.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);
	
	drawRaster = new Raster(new Point3f(0.0f, 0.0f, 0.0f),
				       Raster.RASTER_COLOR,
				       0, 0, 200, 200, buffer, null);
	
	drawRaster.setCapability(Raster.ALLOW_IMAGE_WRITE);        
	Shape3D shape = new Shape3D(drawRaster);
	objRoot.addChild(shape);

	// Let Java 3D perform optimizations on this scene graph.
	objRoot.compile();

	return objRoot;
    }

    private OnScreenCanvas3D createOnScreenCanvasAndUniverse() {
        // Get the preferred graphics configuration for the default screen
	GraphicsConfiguration config =
	    SimpleUniverse.getPreferredConfiguration();

        // Create a Canvas3D using the preferred configuration
	OnScreenCanvas3D onScrCanvas = new OnScreenCanvas3D(config, false);

        // Create simple universe with view branch
	univ = new SimpleUniverse(onScrCanvas);

	// This will move the ViewPlatform back a bit so the
	// objects in the scene can be viewed.
	univ.getViewingPlatform().setNominalViewingTransform();

	// Ensure at least 5 msec per frame (i.e., < 200Hz)
	univ.getViewer().getView().setMinimumFrameCycleTime(5);

	return onScrCanvas;
    }

    private OffScreenCanvas3D createOffScreenCanvas() {
	// request an offscreen Canvas3D with a single buffer configuration
	GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
	template.setDoubleBuffer(GraphicsConfigTemplate3D.UNNECESSARY);
	GraphicsConfiguration gc = 
                GraphicsEnvironment.getLocalGraphicsEnvironment().
                getDefaultScreenDevice().getBestConfiguration(template);

        // Create a offscreen Canvas3D using the single buffer configuration.
        OffScreenCanvas3D offScrCanvas = 
                new OffScreenCanvas3D(gc, true, drawRaster);

        return offScrCanvas;
    }

    /**
     * Creates new form PrintFromButton
     */
    public PrintFromButton() {
        // Initialize the GUI components
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        initComponents();

        // Create the content branch and add it to the universe
        scene = createSceneGraph();
        
        // Create an OnScreenCanvas3D and SimpleUniverse; add canvas to drawing panel
        OnScreenCanvas3D onScreenCanvas = createOnScreenCanvasAndUniverse();
        drawingPanel.add(onScreenCanvas, java.awt.BorderLayout.CENTER);	

	// Creante an OffScreenCanvas3D
        offScreenCanvas = createOffScreenCanvas();
        
	// set the offscreen to match the onscreen
	Screen3D sOn = onScreenCanvas.getScreen3D();
	Screen3D sOff = offScreenCanvas.getScreen3D();
	sOff.setSize(sOn.getSize());
	sOff.setPhysicalScreenWidth(sOn.getPhysicalScreenWidth());
	sOff.setPhysicalScreenHeight(sOn.getPhysicalScreenHeight());
	
	// attach the same view to the offscreen canvas
	View view = univ.getViewer().getView();
	view.addCanvas3D(offScreenCanvas);
	
        univ.addBranchGraph(scene);
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

        guiPanel = new javax.swing.JPanel();
        myButton = new javax.swing.JButton();
        drawingPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Window Title");
        guiPanel.setLayout(new java.awt.GridBagLayout());

        myButton.setText("Print");
        myButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        guiPanel.add(myButton, gridBagConstraints);

        getContentPane().add(guiPanel, java.awt.BorderLayout.NORTH);

        drawingPanel.setLayout(new java.awt.BorderLayout());

        drawingPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        getContentPane().add(drawingPanel, java.awt.BorderLayout.CENTER);

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
    }// </editor-fold>//GEN-END:initComponents

    private void myButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myButtonActionPerformed
        offScreenCanvas.print(false);
    }//GEN-LAST:event_myButtonActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {System.setProperty("sun.awt.noerasebackground", "true"); 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PrintFromButton().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel drawingPanel;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JPanel guiPanel;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JButton myButton;
    // End of variables declaration//GEN-END:variables
    
}
