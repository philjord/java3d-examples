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

package org.jdesktop.j3d.examples.jcanvas3d;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JInternalFrame;

import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.AmbientLight;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.DirectionalLight;
import org.jogamp.java3d.Font3D;
import org.jogamp.java3d.FontExtrusion;
import org.jogamp.java3d.GraphicsConfigTemplate3D;
import org.jogamp.java3d.Material;
import org.jogamp.java3d.RotationInterpolator;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Text3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.exp.swing.JCanvas3D;
import org.jogamp.java3d.utils.behaviors.mouse.MouseRotate;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3d;
import org.jogamp.vecmath.Vector3f;


/**
 * This is a JInternalFrame holding an universe, which can be configured to
 * be interactive -that is, where user can interact with object- or automatic
 * -where the object spins only-. When in automatic mode, spinning speed is
 * changed so that they look less the same. Changing the spinning start angle
 * helps unsynchronizing the rotations too.
 *
 * @author pepe
 */
public class JInternalWorld extends JInternalFrame {
    /** DOCUMENT ME! */
    private Component comp;

    /**
     * Creates a new JInternalWorld object.
     *
     * @param isInteractive tells the world to be constructed as interactive
     * @param isDelayed tells the rotator to start at a random alpha.
     */
    public JInternalWorld(boolean isInteractive, boolean isDelayed, boolean isRandom) {
        super();
        setSize(256, 256);
        setClosable(true);

        JCanvas3D canvas = new JCanvas3D(new GraphicsConfigTemplate3D());

        if (true == isDelayed) {
            canvas.setResizeMode(canvas.RESIZE_DELAYED);
        }

        comp = canvas;

        Dimension dim = new Dimension(256, 256);
        comp.setPreferredSize(dim);
        comp.setSize(dim);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(comp, BorderLayout.CENTER);
        pack();

        // Create a simple scene and attach it to the virtual universe
        BranchGroup scene = createSceneGraph(isInteractive, isRandom);
        SimpleUniverse universe = new SimpleUniverse(canvas.getOffscreenCanvas3D()); //TODO: this is awful and must not be done like that in final version

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        universe.getViewingPlatform().setNominalViewingTransform();
        universe.getViewer().getView().setMinimumFrameCycleTime(30);
        universe.addBranchGraph(scene);
    }

    /**
     * Creates the world. Only exists to cleanup the source a bit
     *
     * @param isInteractive tells the world to be constructed as interactive
     * @param isDelayed tells the rotator to start at a random alpha.
     *
     * @return a global branchgroup containing the world, as desired.
     */
    private BranchGroup createSceneGraph(boolean isInteractive, boolean isRandom) {
        // Create the root of the branch graph
        BranchGroup objRoot = new BranchGroup();

        // Create the TransformGroup node and initialize it to the
        // identity. Enable the TRANSFORM_WRITE capability so that
        // our behavior code can modify it at run time. Add it to
        // the root of the subgraph.
        TransformGroup objTrans = new TransformGroup();
        Transform3D t3dTrans = new Transform3D();
        t3dTrans.setTranslation(new Vector3d(0, 0, -1));
        objTrans.setTransform(t3dTrans);

        TransformGroup objRot = new TransformGroup();
        objRot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objRoot.addChild(objTrans);
        objTrans.addChild(objRot);

        // Create a simple Shape3D node; add it to the scene graph.
        // issue 383: changed the cube to a text, so that any graphical problem related to Yup can be seen.
	Font3D f3d = new Font3D(new Font("dialog", Font.PLAIN, 1),
			     new FontExtrusion());
        Text3D text = new Text3D(f3d, "JCanvas3D",
			     new Point3f( -2.3f, -0.5f, 0.f));

        Shape3D sh = new Shape3D();
	Appearance app = new Appearance();
	Material mm = new Material();
	mm.setLightingEnable(true);
	app.setMaterial(mm);
	sh.setGeometry(text);
	sh.setAppearance(app);

        objRot.addChild( sh );

        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
                100.0);
        
	// Set up the ambient light
	Color3f ambientColor = new Color3f(0.3f, 0.3f, 0.3f);
	AmbientLight ambientLightNode = new AmbientLight(ambientColor);
	ambientLightNode.setInfluencingBounds(bounds);
	objRoot.addChild(ambientLightNode);
	
	// Set up the directional lights
	Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f);
	Vector3f light1Direction  = new Vector3f(1.0f, 1.0f, 1.0f);
	Color3f light2Color = new Color3f(1.0f, 1.0f, 0.9f);
	Vector3f light2Direction  = new Vector3f(-1.0f, -1.0f, -1.0f);
	
	DirectionalLight light1
	    = new DirectionalLight(light1Color, light1Direction);
	light1.setInfluencingBounds(bounds);
	objRoot.addChild(light1);
	
	DirectionalLight light2
	    = new DirectionalLight(light2Color, light2Direction);
	light2.setInfluencingBounds(bounds);
	objRoot.addChild(light2);


        if (true == isInteractive) {
            MouseRotate mr = new MouseRotate(comp, objRot);
            mr.setSchedulingBounds(bounds);
            mr.setSchedulingInterval(1);
            objRoot.addChild(mr);
        } else {
            // Create a new Behavior object that will perform the
            // desired operation on the specified transform and add
            // it into the scene graph.
            Transform3D yAxis = new Transform3D();

            // rotation speed is randomized a bit so that it does not go at the same speed on every canvases,
            // which will make it more natural and express the differences between every present universes
            Alpha rotationAlpha = null;

            if (true == isRandom) {
                int duration = Math.max(2000, (int) (Math.random() * 8000.));
                rotationAlpha = new Alpha(-1,
                        (int) ((double) duration * Math.random()), 0, duration,
                        0, 0);
            } else {
                rotationAlpha = new Alpha(-1, 4000);
            }

            RotationInterpolator rotator = new RotationInterpolator(rotationAlpha,
                    objRot, yAxis, 0.0f, (float) Math.PI * 2.0f);

            rotator.setSchedulingBounds(bounds);
            objRoot.addChild(rotator);
        }

        return objRoot;
    }

}
