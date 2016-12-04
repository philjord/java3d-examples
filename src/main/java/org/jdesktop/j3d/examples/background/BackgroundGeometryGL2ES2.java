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

package org.jdesktop.j3d.examples.background;

import java.awt.GraphicsConfiguration;

import org.jdesktop.j3d.examples.Resources;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.Background;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.DirectionalLight;
import org.jogamp.java3d.Material;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.behaviors.mouse.MouseRotate;
import org.jogamp.java3d.utils.behaviors.mouse.MouseTranslate;
import org.jogamp.java3d.utils.behaviors.mouse.MouseZoom;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Sphere;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.shader.SimpleShaderAppearance;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3f;

public class BackgroundGeometryGL2ES2 extends javax.swing.JFrame {

    private SimpleUniverse univ = null;
    private BranchGroup scene = null;
    private java.net.URL bgImage = null;
    private BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

    public BranchGroup createSceneGraph() {

        // Create the root of the branch graph
        BranchGroup objRoot = new BranchGroup();

        // Create a Transformgroup to scale all objects so they
        // appear in the scene.
        TransformGroup objScale = new TransformGroup();
        Transform3D t3d = new Transform3D();
        t3d.setScale(0.4);
        objScale.setTransform(t3d);
        objRoot.addChild(objScale);

        // Create the transform group node and initialize it to the
        // identity.  Enable the TRANSFORM_WRITE capability so that
        // our behavior code can modify it at runtime.
        TransformGroup objTrans = new TransformGroup();
        objScale.addChild(objTrans);
    
        Background bg = new Background();
        bg.setApplicationBounds(bounds);
        BranchGroup backGeoBranch = new BranchGroup();
        Appearance backgroundApp = new SimpleShaderAppearance(); 
        Sphere sphereObj = new Sphere(1.0f, Sphere.GENERATE_NORMALS |
                Sphere.GENERATE_NORMALS_INWARD |
                Sphere.GENERATE_TEXTURE_COORDS |
                Sphere.GENERATE_TEXTURE_COORDS_Y_UP, 45, backgroundApp);       
        
       
        backGeoBranch.addChild(sphereObj);
        bg.setGeometry(backGeoBranch);
        objTrans.addChild(bg);

        TextureLoader tex = new TextureLoader(bgImage,
                new String("RGB"),
                TextureLoader.BY_REFERENCE | TextureLoader.Y_UP,
                this);
        if (tex != null) 
	    backgroundApp.setTexture(tex.getTexture());

        Vector3f tranlation = new Vector3f(2.0f, 0.0f, 0.0f);
        Transform3D modelTransform = new Transform3D();
        Transform3D tmpTransform = new Transform3D();
        double angleInc = Math.PI/8.0;
        double angle = 0.0;
	int numBoxes = 16;

        float scaleX[] =  {0.1f, 0.2f, 0.2f, 0.3f,
                           0.2f, 0.1f, 0.2f, 0.3f,
                           0.1f, 0.3f, 0.2f, 0.3f,
                           0.1f, 0.3f, 0.2f, 0.3f};

        float scaleY[] =  {0.3f, 0.4f, 0.3f, 0.4f,
                           0.3f, 0.4f, 0.3f, 0.4f,
                           0.3f, 0.3f, 0.3f, 0.3f,
                           0.3f, 0.3f, 0.3f, 0.4f};

        float scaleZ[] =  {0.3f, 0.2f, 0.1f, 0.1f,
                           0.3f, 0.2f, 0.1f, 0.3f,
                           0.3f, 0.2f, 0.1f, 0.3f,
                           0.3f, 0.2f, 0.1f, 0.2f};

        Appearance a1 =  new SimpleShaderAppearance(); 
     
        Color3f eColor    = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f sColor    = new Color3f(0.5f, 0.5f, 1.0f);
        Color3f oColor    = new Color3f(0.5f, 0.5f, 0.3f);

        Material m = new Material(oColor, eColor, oColor, sColor, 100.0f);
        m.setLightingEnable(true);
        a1.setMaterial(m);

        for (int i=0; i<numBoxes; i++, angle += angleInc) {
            modelTransform.rotY(angle);
            tmpTransform.set(tranlation);
            modelTransform.mul(tmpTransform);

            TransformGroup tgroup = new TransformGroup(modelTransform);
            objTrans.addChild(tgroup);

            tgroup.addChild( new Box(scaleX[i],scaleY[i],scaleZ[i],
				Box.GENERATE_NORMALS,a1));
        }


        // Shine it with two lights.
        Color3f lColor1 = new Color3f(0.7f, 0.7f, 0.7f);
        Color3f lColor2 = new Color3f(0.2f, 0.2f, 0.1f);
        Vector3f lDir1  = new Vector3f(-1.0f, -1.0f, -1.0f);
        Vector3f lDir2  = new Vector3f(0.0f, 0.0f, -1.0f);
        DirectionalLight lgt1 = new DirectionalLight(lColor1, lDir1);
        DirectionalLight lgt2 = new DirectionalLight(lColor2, lDir2);
        lgt1.setInfluencingBounds(bounds);
        lgt2.setInfluencingBounds(bounds);
        objScale.addChild(lgt1);
        objScale.addChild(lgt2);
 
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

	// This will move the ViewPlatform back a bit so the
	// objects in the scene can be viewed.
	univ.getViewingPlatform().setNominalViewingTransform();

	// Ensure at least 5 msec per frame (i.e., < 200Hz)
	univ.getViewer().getView().setMinimumFrameCycleTime(5);

        TransformGroup viewTrans =
                univ.getViewingPlatform().getViewPlatformTransform();
        
                // Create the rotate behavior node
        MouseRotate behavior1 = new MouseRotate(viewTrans);
        scene.addChild(behavior1);
        behavior1.setSchedulingBounds(bounds);

        // Create the zoom behavior node
        MouseZoom behavior2 = new MouseZoom(viewTrans);
        scene.addChild(behavior2);
        behavior2.setSchedulingBounds(bounds);

        // Create the translate behavior node
        MouseTranslate behavior3 = new MouseTranslate(viewTrans);
        scene.addChild(behavior3);
        behavior3.setSchedulingBounds(bounds);

	return c;
    }

    /**
     * Creates new form BackgroundGeometry
     */
    public BackgroundGeometryGL2ES2() {

        if (bgImage == null) {
	    // the path to the image for an applet
	    bgImage = Resources.getResource("main/resources/images/bg.jpg");
	    if (bgImage == null) {
	      System.err.println("main/resources/images/bg.jpg not found");
	      System.exit(1);
            }            
	}

	// Initialize the GUI components
	initComponents();

        // Create the content branch and add it to the universe
	scene = createSceneGraph();
 
	// Create Canvas3D and SimpleUniverse; add canvas to drawing panel
	Canvas3D c = createUniverse();
	drawingPanel.add(c, java.awt.BorderLayout.CENTER);       

        // Let Java 3D perform optimizations on this scene graph.
        scene.compile();

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
        setTitle("BackgroundGeometry");
        drawingPanel.setLayout(new java.awt.BorderLayout());

        drawingPanel.setOpaque(false);
        drawingPanel.setPreferredSize(new java.awt.Dimension(700, 700));
        getContentPane().add(drawingPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {System.setProperty("sun.awt.noerasebackground", "true"); 
    System.setProperty("j3d.rend", "jogl2es2");System.setProperty("j3d.displaylist", "false");
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
			public void run() {
                new BackgroundGeometryGL2ES2().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel drawingPanel;
    // End of variables declaration//GEN-END:variables
    
}
