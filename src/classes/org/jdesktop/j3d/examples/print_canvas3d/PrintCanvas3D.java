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

package org.jdesktop.j3d.examples.print_canvas3d;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JPopupMenu;

import org.jdesktop.j3d.examples.Resources;
import org.jogamp.java3d.AmbientLight;
import org.jogamp.java3d.Background;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.DirectionalLight;
import org.jogamp.java3d.GraphicsConfigTemplate3D;
import org.jogamp.java3d.Screen3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.loaders.IncorrectFormatException;
import org.jogamp.java3d.loaders.ParsingErrorException;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.behaviors.mouse.MouseRotate;
import org.jogamp.java3d.utils.behaviors.mouse.MouseTranslate;
import org.jogamp.java3d.utils.behaviors.mouse.MouseZoom;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3f;

public class PrintCanvas3D extends javax.swing.JFrame {

    private static final boolean noTriangulate = false;
    private static final boolean noStripify = false;
    private static final double creaseAngle = 60.0;
    private Canvas3D onScreenCanvas3D;
    private OffScreenCanvas3D offScreenCanvas3D;
    private URL filename = null;
    private static final int OFF_SCREEN_SCALE = 3;

    private SimpleUniverse univ = null;

    public BranchGroup createSceneGraph(String args[]) {
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
	if (!noTriangulate) flags |= ObjectFile.TRIANGULATE;
	if (!noStripify) flags |= ObjectFile.STRIPIFY;
	ObjectFile f =
	    new ObjectFile(flags, 
			   (float)(creaseAngle * Math.PI / 180.0));
	Scene scene = null;
	try {
	    scene = f.load(filename);
	}
	catch (FileNotFoundException e) {
	    System.err.println(e);
	    System.exit(1);
	}
	catch (ParsingErrorException e) {
	    System.err.println(e);
	    System.exit(1);
	}
	catch (IncorrectFormatException e) {
	    System.err.println(e);
	    System.exit(1);
	}
	  
	objTrans.addChild(scene.getSceneGroup());

	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

        // Create the rotate behavior node
        MouseRotate behavior = new MouseRotate();
        behavior.setTransformGroup(objTrans);
        objTrans.addChild(behavior);
        behavior.setSchedulingBounds(bounds);
        
        // Create the zoom behavior node
        MouseZoom behavior2 = new MouseZoom();
        behavior2.setTransformGroup(objTrans);
        objTrans.addChild(behavior2);
        behavior2.setSchedulingBounds(bounds);
        
        // Create the translate behavior node
        MouseTranslate behavior3 = new MouseTranslate();
        behavior3.setTransformGroup(objTrans);
        objTrans.addChild(behavior3);
        behavior3.setSchedulingBounds(bounds);
	

	// Set up the background
	Color3f bgColor = new Color3f(0.05f, 0.05f, 0.5f);
	Background bgNode = new Background(bgColor);
	bgNode.setApplicationBounds(bounds);
	objRoot.addChild(bgNode);

	// Set up the ambient light
	Color3f ambientColor = new Color3f(0.1f, 0.1f, 0.1f);
	AmbientLight ambientLightNode = new AmbientLight(ambientColor);
	ambientLightNode.setInfluencingBounds(bounds);
	objRoot.addChild(ambientLightNode);

	// Set up the directional lights
	Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f);
	Vector3f light1Direction  = new Vector3f(4.0f, -7.0f, -12.0f);
	Color3f light2Color = new Color3f(0.3f, 0.3f, 0.4f);
	Vector3f light2Direction  = new Vector3f(-6.0f, -2.0f, -1.0f);

	DirectionalLight light1
	    = new DirectionalLight(light1Color, light1Direction);
	light1.setInfluencingBounds(bounds);
	objRoot.addChild(light1);

	DirectionalLight light2
	    = new DirectionalLight(light2Color, light2Direction);
	light2.setInfluencingBounds(bounds);
	objRoot.addChild(light2);

	return objRoot;
    }

    private void usage() {
	System.out.println("Usage: java PrintCanvas3D <.obj file>");
	System.exit(0);
    } // End of usage

    private OffScreenCanvas3D createOffScreenCanvas(Canvas3D onScreenCanvas3D) {
	// Create the off-screen Canvas3D object
	// request an offscreen Canvas3D with a single buffer configuration
	GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
	template.setDoubleBuffer(GraphicsConfigTemplate3D.UNNECESSARY);
	GraphicsConfiguration gc = 
                GraphicsEnvironment.getLocalGraphicsEnvironment().
                getDefaultScreenDevice().getBestConfiguration(template);

	offScreenCanvas3D = new OffScreenCanvas3D(gc, true);
	// Set the off-screen size based on a scale factor times the
	// on-screen size
	Screen3D sOn = onScreenCanvas3D.getScreen3D();
	Screen3D sOff = offScreenCanvas3D.getScreen3D();
	Dimension dim = sOn.getSize();
	dim.width *= OFF_SCREEN_SCALE;
	dim.height *= OFF_SCREEN_SCALE;
	sOff.setSize(dim);
	sOff.setPhysicalScreenWidth(sOn.getPhysicalScreenWidth() *
				    OFF_SCREEN_SCALE);
	sOff.setPhysicalScreenHeight(sOn.getPhysicalScreenHeight() *
				     OFF_SCREEN_SCALE);

	// attach the offscreen canvas to the view
	univ.getViewer().getView().addCanvas3D(offScreenCanvas3D);        

        return offScreenCanvas3D;
        
    }

    private Canvas3D createUniverse() {
	GraphicsConfiguration config =
	    SimpleUniverse.getPreferredConfiguration();

	Canvas3D c = new Canvas3D(config);

	univ = new SimpleUniverse(c);

	// This will move the ViewPlatform back a bit so the
	// objects in the scene can be viewed.
        univ.getViewingPlatform().setNominalViewingTransform();

	// Ensure at least 5 msec per frame (i.e., < 200Hz)
	univ.getViewer().getView().setMinimumFrameCycleTime(5);

	return c;
    }

    /**
     * Creates new form PrintCanvas3D
     */
    public PrintCanvas3D(String args[]) {
        
        if (args.length == 0) {
            filename = Resources.getResource("resources/geometry/beethoven.obj");
            if (filename == null) {
                System.err.println("resources/geometry/beethoven.obj not found");
                System.exit(1);
            }
        } else {
            for (int i = 0 ; i < args.length ; i++) {
                if (args[i].startsWith("-")) {
                    System.err.println("Argument '" + args[i] + "' ignored.");
                } else {
                    try{
                        filename = new URL(args[i]);
                    }
                    catch (MalformedURLException e) {
                        System.err.println(e.getMessage());
                        System.exit(1);
                    }
                }
            }
        }
        
        if (filename == null) {
            usage();
        }
        
        // Initialize the GUI components
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        initComponents();        

        // Create Canvas3D and SimpleUniverse; add canvas to drawing panel
        onScreenCanvas3D = createUniverse();
        drawingPanel.add(onScreenCanvas3D, java.awt.BorderLayout.CENTER);

        // Create the content branch and add it to the universe
        BranchGroup scene = createSceneGraph(args);
        
	// Create the off-screen Canvas3D object
        createOffScreenCanvas(onScreenCanvas3D);
        
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
        drawingPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        snapShotMenuItem = new javax.swing.JMenuItem();
        printMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Window Title");
        drawingPanel.setLayout(new java.awt.BorderLayout());

        drawingPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        getContentPane().add(drawingPanel, java.awt.BorderLayout.CENTER);

        fileMenu.setText("File");
        snapShotMenuItem.setText("Snapshot");
        snapShotMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                snapShotMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(snapShotMenuItem);

        printMenuItem.setText("Print");
        printMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(printMenuItem);

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

    private void printMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printMenuItemActionPerformed
	Point loc = onScreenCanvas3D.getLocationOnScreen();
	offScreenCanvas3D.setOffScreenLocation(loc);
	Dimension dim = onScreenCanvas3D.getSize();
	dim.width *= OFF_SCREEN_SCALE;
	dim.height *= OFF_SCREEN_SCALE;
	BufferedImage bImage =
	    offScreenCanvas3D.doRender(dim.width, dim.height);

        new ImagePrinter(bImage).print();

    }//GEN-LAST:event_printMenuItemActionPerformed

    private void snapShotMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_snapShotMenuItemActionPerformed
	Point loc = onScreenCanvas3D.getLocationOnScreen();
	offScreenCanvas3D.setOffScreenLocation(loc);
	Dimension dim = onScreenCanvas3D.getSize();
	dim.width *= OFF_SCREEN_SCALE;
	dim.height *= OFF_SCREEN_SCALE;
	BufferedImage bImage =
	    offScreenCanvas3D.doRender(dim.width, dim.height);

	new ImageDisplayer(bImage);


    }//GEN-LAST:event_snapShotMenuItemActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                    new PrintCanvas3D(args).setVisible(true);;
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel drawingPanel;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem printMenuItem;
    private javax.swing.JMenuItem snapShotMenuItem;
    // End of variables declaration//GEN-END:variables
    
}
