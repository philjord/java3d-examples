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
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.*;
import java.util.Enumeration;
import javax.media.j3d.*;
import javax.vecmath.*;

public class ListenerTest extends Applet {

    private SimpleUniverse u = null;
    private ChangeListener changeListener;
    private BranchGroup scene;
    
    public BranchGroup createSceneGraph() {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        objRoot.setCapability(BranchGroup.ALLOW_DETACH);

	// Create the TransformGroup node and initialize it to the
	// identity. Enable the TRANSFORM_WRITE capability so that
	// our behavior code can modify it at run time. Add it to
	// the root of the subgraph.
	TransformGroup objTrans = new TransformGroup();
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objRoot.addChild(objTrans);

	// Create a simple Shape3D node; add it to the scene graph.
	objTrans.addChild(new ColorCube(0.1));

	// Create a new Behavior object that will perform the
	// desired operation on the specified transform and add
	// it into the scene graph.
	Transform3D yAxis = new Transform3D();
	Alpha rotationAlpha = new Alpha(-1, 4000);

	RotationInterpolator rotator =
	    new RotationInterpolator(rotationAlpha, objTrans, yAxis,
				     0.0f, (float) Math.PI*2.0f);
	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
	rotator.setSchedulingBounds(bounds);
	objRoot.addChild(rotator);
        
        objRoot.addChild(new TestBehavior(objRoot));

	return objRoot;
    }

    public ListenerTest() {
    }

    public void init() {
	setLayout(new BorderLayout());
        GraphicsConfiguration config =
           SimpleUniverse.getPreferredConfiguration();

	Canvas3D c = new Canvas3D(config);
	add("Center", c);

	// Create a simple scene and attach it to the virtual universe
        scene = createSceneGraph();
        scene.setName("scene");
	u = new SimpleUniverse(c);
        changeListener = new ChangeListener();
        u.addGraphStructureChangeListener(changeListener);

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        u.getViewingPlatform().setNominalViewingTransform();

	u.addBranchGraph(scene);
    }

    public void destroy() {
	u.cleanup();
    }
    
    BranchGroup createShape(Vector3f pos) {
        Transform3D t3d = new Transform3D();
        t3d.set(1f,pos);
        TransformGroup tg = new TransformGroup(t3d);
        
        tg.addChild(new Sphere(0.02f));
        
        BranchGroup bg = new BranchGroup();
        bg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        bg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        bg.setCapability(BranchGroup.ALLOW_DETACH);
        bg.addChild(tg);
        return bg;
    }

    //
    // The following allows HelloUniverse to be run as an application
    // as well as an applet
    //
    public static void main(String[] args) {
	new MainFrame(new ListenerTest(), 256, 256);
    }
    
    class TestBehavior extends Behavior {
        private BranchGroup root;
        private WakeupCriterion wakeup = new WakeupOnElapsedFrames(0);
        private int count = 0;
        private BranchGroup moveTo;
        private BranchGroup bg1;
        
        public TestBehavior(BranchGroup root) {
            this.root = root;
            this.setSchedulingBounds(new BoundingSphere(new Point3d(), Double.POSITIVE_INFINITY));
            
            moveTo = new BranchGroup();
            moveTo.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
            moveTo.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
            moveTo.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            root.addChild(moveTo);
        }
        
        public void initialize() {
            wakeupOn(wakeup);
        }
        
        public void processStimulus(Enumeration e) {
            System.out.println("Wakeup");
            
            switch(count) {
                case 0:
                    bg1=createShape(new Vector3f(0.02f,0f,0f));
                    bg1.setName("bg1.1");
                    root.addChild(bg1);
                    break;
                case 1:
                    moveTo.moveTo(bg1);
                    break;
                case 2:
                    bg1=createShape(new Vector3f(0.03f, 0.03f, 0f));
                    bg1.setName("bg1.2");
                    moveTo.setChild(bg1,0);
                    break;
                case 3:
                    bg1.detach();
                    break;
                case 4:
                    u.getLocale().removeBranchGraph(scene);
                    break;
            }
                    
            count++;
            
            if(count<5)
                wakeupOn(wakeup);
        }
    }
    
    class ChangeListener implements GraphStructureChangeListener {
        public void branchGroupAdded(Object parent, BranchGroup child) {
            System.out.println("Add "+parent+"  "+child.getName()+" "+child.isLive());
        }

        public void branchGroupMoved(Object oldParent, Object newParent, BranchGroup child) {
            System.out.println("Move "+oldParent+" "+newParent+" "+child.getName()+" "+child.isLive());
        }

        public void branchGroupRemoved(Object parent, BranchGroup child) {
            System.out.println("Removed "+parent+" "+child.getName()+" "+child.isLive());
        }
        
    }
}
