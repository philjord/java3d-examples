/*
 * $RCSfile$
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved.
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

import java.applet.Applet;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.event.WindowAdapter;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;

public class AWTInteraction extends Applet { //implements ActionListener {
    TransformGroup objTrans;
    float angle = 0.0f;
    Transform3D trans = new Transform3D();

    JButton rotateB = new JButton("Rotate");

    private SimpleUniverse u = null;

    public BranchGroup createSceneGraph() {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

	// Create the transform group node and initialize it to the
	// identity.  Enable the TRANSFORM_WRITE capability so that
	// our behavior code can modify it at runtime.  Add it to the
	// root of the subgraph.
	objTrans = new TransformGroup();
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objRoot.addChild(objTrans);

	// Create a simple shape leaf node, add it to the scene graph.
	objTrans.addChild(new ColorCube(0.4));

	// create the AWTInteractionBehavior	
	AWTInteractionBehavior awtBehavior =
	    new AWTInteractionBehavior(objTrans);
	rotateB.addActionListener(awtBehavior);
	BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
						   100.0);
	awtBehavior.setSchedulingBounds(bounds);
	objRoot.addChild(awtBehavior);

	return objRoot;
    }

    public AWTInteraction() {
    }

    public void init() {
	setLayout(new BorderLayout());
        GraphicsConfiguration config =
           SimpleUniverse.getPreferredConfiguration();

        Canvas3D c = new Canvas3D(config);
	add("Center", c);

	JPanel p = new JPanel();
	p.add(rotateB);
	add("North", p);

	// Create a simple scene and attach it to the virtual universe
	BranchGroup scene = createSceneGraph();
	scene.setCapability( BranchGroup.ALLOW_BOUNDS_READ );
	u = new SimpleUniverse(c);

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        u.getViewingPlatform().setNominalViewingTransform();

	u.addBranchGraph(scene);

    }

    public void destroy() {
	u.cleanup();
    }
    
    //
    // The following allows HelloUniverse to be run as an application
    // as well as an applet
    //
    public static void main(String[] args) {
	new MainFrame(new AWTInteraction(), 256, 256);
    }
}
