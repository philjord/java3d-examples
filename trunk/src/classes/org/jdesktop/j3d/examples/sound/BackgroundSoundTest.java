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
package org.jdesktop.j3d.examples.sound;

import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.ColorCube;
import java.net.URL;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.GraphicsConfiguration;
import org.jdesktop.j3d.examples.Resources;

/**
 * This is a test for a BackgroundSound. 
 * This program is ported from an earlier version of BackgroundSoundTest, in the j3d-incubator project, 
 * contributed by David Grace (dave@dutchie.net).
 * 
 */
public class BackgroundSoundTest extends javax.swing.JFrame {

    private URL url = null;
    private SimpleUniverse univ = null;
    private BranchGroup scene = null;

    //The activation radius for the ViewPlatform
    private float activationRadius = 1;
    
    private Shape3D getDefaultGrid(int noOfLines, double size, double height){
        
        Shape3D shape = new Shape3D();
        double lineLength = noOfLines * size / 2;
        LineArray la = new LineArray(noOfLines * 4, LineArray.COORDINATES);
        int count = 0;
        for (int i=0; i<noOfLines; i++){
            la.setCoordinate(count, new Point3d(-lineLength, height, i*size - lineLength));
            count++;
            la.setCoordinate(count, new Point3d(lineLength, height, i*size - lineLength));
            count++;
        }
        for (int i=0; i<noOfLines; i++){
            la.setCoordinate(count, new Point3d(i*size - lineLength, height, -lineLength));
            count++;
            la.setCoordinate(count, new Point3d(i*size - lineLength, height, lineLength));
            count++;
        }
        shape.setGeometry(la);
        Appearance a = new Appearance();
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(0.3f, 0.3f, 0.3f);
        a.setColoringAttributes(ca);
        LineAttributes sla = new LineAttributes();
        sla.setLineWidth(1.0f);
        a.setLineAttributes(sla);
        shape.setAppearance(a);
        
        return shape;
    }    

    
   private Sphere createSoundBoundingGeometry(Sound sound){
       Bounds bounds = sound.getSchedulingBounds();
       assert ((bounds != null) && (bounds instanceof BoundingSphere));
       BoundingSphere bs = (BoundingSphere) bounds;
       float radius = (float) bs.getRadius();
       
       return getSphere(radius);
    }
    
   private Sphere getSphere(float radius){
       
       Appearance a = new Appearance();
       Material m = new Material();
       
       m.setDiffuseColor(1, 0, 0);
       m.setAmbientColor(1, 0, 0);
       m.setShininess(8);     
       a.setMaterial(m);
       
       PolygonAttributes pa = new PolygonAttributes();
       pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
       pa.setCullFace(PolygonAttributes.CULL_NONE);
       a.setPolygonAttributes(pa);
       return new Sphere(radius, a);
   }
        
    private TransformGroup createSoundNodeGeometry(float x, float y, float z){
       
        TransformGroup rootTransformGroup = new TransformGroup();
        Transform3D t3D = new Transform3D();
        t3D.setTranslation(new Vector3f(x, y, z));
        rootTransformGroup.setTransform(t3D);
        ColorCube cc = new ColorCube(0.1);
        rootTransformGroup.addChild(cc);
        return rootTransformGroup;
    }
    
    
    public BranchGroup createSceneGraph() {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

 	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);       
  
        AmbientLight al = new AmbientLight();
        al.setInfluencingBounds(bounds);
        
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(-1, -1, -1);
        dl.setInfluencingBounds(bounds);
        objRoot.addChild(al);
        objRoot.addChild(dl);        
               
        /*
	 * Create Sound and Behavior objects that will play the sound
	 */
        BackgroundSound bgs = new BackgroundSound();         
	BackgroundSoundBehavior player = new BackgroundSoundBehavior( bgs, url);
	player.setSchedulingBounds(bounds);
        objRoot.addChild(bgs);   
        objRoot.addChild(player);
        
        objRoot.addChild(getDefaultGrid(40, 1, -1));          
        objRoot.addChild(createSoundNodeGeometry(0, 0, 0));  
        objRoot.addChild(createSoundBoundingGeometry(bgs));
        return objRoot;
    }

    
    private Canvas3D createUniverse() {
        // Get the preferred graphics configuration for the default screen
	GraphicsConfiguration config =
	    SimpleUniverse.getPreferredConfiguration();

        // Create a Canvas3D using the preferred configuration
	Canvas3D c = new Canvas3D(config);

        // Create simple universe with view branch
	univ = new SimpleUniverse(c);       
        
        ViewingPlatform viewingPlatform = univ.getViewingPlatform();
        TransformGroup viewingPlatformTransformGroup = viewingPlatform.getViewPlatformTransform();
        
        // This will move the ViewPlatform back a bit so the
	// objects in the scene can be viewed.
        viewingPlatform.setNominalViewingTransform();

        Viewer viewer = univ.getViewer();        
        
        viewer.createAudioDevice();
        viewer.getView().setBackClipDistance(1000.0f);

        // Ensure at least 50 msec per frame.
        viewer.getView().setMinimumFrameCycleTime(30);  
        
        viewer.getView().getViewPlatform().setActivationRadius(activationRadius);       
        
        BranchGroup bg = new BranchGroup();
        KeyNavigatorBehavior knb = new KeyNavigatorBehavior(c, viewingPlatformTransformGroup);
        Bounds b = new BoundingSphere(new Point3d(), Double.POSITIVE_INFINITY);
        knb.setSchedulingBounds(b);
        bg.addChild(knb);
        univ.addBranchGraph(bg);        
                
	return c;
    }
    
    /**
     * Creates new form BackgroundSoundTest
     */
    public BackgroundSoundTest() {
        // Initialize the GUI components
        initComponents();

        url = Resources.getResource("resources/audio/magic_bells.wav");
        if (url == null) {
            System.err.println("resources/audio/magic_bells.wav not found");
            System.exit(1);
        }
        
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
    private void initComponents() {
        drawingPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BackgroundSoundTest");
        drawingPanel.setLayout(new java.awt.BorderLayout());

        drawingPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        getContentPane().add(drawingPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BackgroundSoundTest().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel drawingPanel;
    // End of variables declaration//GEN-END:variables
    
}
