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

package org.jdesktop.j3d.examples.jcanvas3d;

import com.sun.j3d.exp.swing.JCanvas3D;
//import com.sun.j3d.exp.swing.Canvas3DWrapper;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.media.j3d.Alpha;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JInternalFrame;
import javax.swing.table.DefaultTableModel;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;


/**
 * DOCUMENT ME!
 *
 * @author pepe
 */
public class JInternalWorld extends JInternalFrame {
    /** DOCUMENT ME! */
    JCanvas3D c;

    /** Creates a new instance of JInternalWorld */
    private SimpleUniverse u = null;

    /** DOCUMENT ME! */
    private Component comp;

    private DefaultTableModel model;

    /**
     * Creates a new JInternalWorld object.
     *
     * @param isDelayed DOCUMENT ME!
     * @param isInteractive DOCUMENT ME!
     */
    public JInternalWorld(boolean isDelayed, boolean isInteractive, DefaultTableModel model) {
        setSize(256, 256);
        setClosable(true);
        this.model = model;
        model.addRow( new Object[]{new Integer( hashCode()), Integer.valueOf(0) });

        c = new JCanvas3D();

        if (true == isDelayed) {
            c.setResizeMode(c.RESIZE_DELAYED);
        }

        setComp(c);
        Dimension dim = new Dimension(256, 256);
        getComp().setPreferredSize( dim );
        getComp().setSize( dim );
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(getComp(), BorderLayout.CENTER);
        pack();

        // Create a simple scene and attach it to the virtual universe
        BranchGroup scene = createSceneGraph(isInteractive);
        u = new SimpleUniverse(c.getOffscreenCanvas3D());//TODO: this is awful and must not be done like that in final version

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        u.getViewingPlatform().setNominalViewingTransform();
//        Canvas3DWrapper wrapper = new Canvas3DWrapper( c );
//        wrapper.setView( u.getViewer().getView() );
//        KeyNavigatorBehavior navig = new KeyNavigatorBehavior(u.getViewingPlatform().getViewPlatformTransform() );
//        BranchGroup navigBG = new BranchGroup();
//        navigBG.addChild( navig );
//        scene.addChild( navigBG );
        u.addBranchGraph(scene);
//        c.getOffscreenCanvas3D().getView().setMinimumFrameCycleTime(200);//TODO: this is awful and must not be done like that in final version
    }

    /**
     * DOCUMENT ME!
     *
     * @param isInteractive DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public BranchGroup createSceneGraph(boolean isInteractive) {
        // Create the root of the branch graph
        BranchGroup objRoot = new BranchGroup();

        // Create the TransformGroup node and initialize it to the
        // identity. Enable the TRANSFORM_WRITE capability so that
        // our behavior code can modify it at run time. Add it to
        // the root of the subgraph.
        TransformGroup objTrans = new TransformGroup();
        Transform3D t3dTrans = new Transform3D();
        t3dTrans.setTranslation( new Vector3d( 0, 0, -1 ) );
        objTrans.setTransform( t3dTrans );
        TransformGroup objRot = new TransformGroup();
        objRot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objRoot.addChild(objTrans);
        objTrans.addChild( objRot );

        // Create a simple Shape3D node; add it to the scene graph.
        ColorCube cube = new ColorCube(0.75);
        objRot.addChild(cube);

        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
                100.0);

        if (true == isInteractive) {
            MouseRotate mr = new MouseRotate(getComp(), objRot);
            mr.setSchedulingBounds(bounds);
            mr.setSchedulingInterval(1);
            objRoot.addChild(mr);
        } else {
            // Create a new Behavior object that will perform the
            // desired operation on the specified transform and add
            // it into the scene graph.
            Transform3D yAxis = new Transform3D();
            Alpha rotationAlpha = new Alpha(-1,
                    Math.max(2000, (int) (Math.random() * 8000.)));

            RotationInterpolator rotator = new RotationInterpolator(rotationAlpha,
                    objRot, yAxis, 0.0f, (float) Math.PI * 2.0f);
            rotator.setSchedulingBounds(bounds);
            objRoot.addChild(rotator);
        }

        return objRoot;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected Component getComp() {
        return comp;
    }

    /**
     * DOCUMENT ME!
     *
     * @param comp DOCUMENT ME!
     */
    protected void setComp(Component comp) {
        this.comp = comp;
    }
}
