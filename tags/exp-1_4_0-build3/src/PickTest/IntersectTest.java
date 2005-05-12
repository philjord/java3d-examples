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
import java.awt.event.*;
import java.awt.GraphicsConfiguration;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.behaviors.keyboard.*;
import com.sun.j3d.utils.picking.PickTool;

public class IntersectTest extends Applet {

  BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 1000.0);

    private SimpleUniverse u = null;

  public BranchGroup createSceneGraph () {

    // Create the root of the branch graph
    BranchGroup objRoot = new BranchGroup();

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

    Transform3D t3 = new Transform3D ();

    // Shapes
    for (int x=0;x<3;x++) {
      for (int y=0;y<3;y++) {
	for (int z=0;z<3;z++) {
	  t3.setTranslation (new Vector3d(-4+x*4.0, -4+y*4.0, -20-z*4.0));
	  TransformGroup objTrans = new TransformGroup(t3);

	  objRoot.addChild(objTrans);

	  // Create a simple shape leaf node, add it to the scene graph.
	  GeometryArray geom = null;

	  if (((x+y+z) % 2) == 0) {
	    geom = new RandomColorCube();
	  }
	  else {
	    geom = new RandomColorTetrahedron();
	  }

 	  Shape3D shape = new Shape3D(geom);

	  // use the utility method to set the capabilities
	  PickTool.setCapabilities(shape, PickTool.INTERSECT_FULL);

	  objTrans.addChild(shape);
	}
      }
    }

    // Lines
    Point3f[] verts = {
      new Point3f (-2.0f, 0.0f, 0.0f),new Point3f(2.0f, 0.0f, 0.0f)
    };
    Color3f grey = new Color3f (0.7f, 0.7f, 0.7f);
    Color3f[] colors = {
      grey, grey
    };

    for (int y=0;y<5;y++) {
      for (int z=0;z<5;z++) {
	t3.setTranslation (new Vector3d(7.0, -4+y*2.0, -20.0-z*2.0));
	TransformGroup objTrans = new TransformGroup(t3);

	objRoot.addChild(objTrans);

	LineArray la = new LineArray (verts.length, 
				      LineArray.COORDINATES |
				      LineArray.COLOR_3);
	la.setCoordinates (0, verts);
	la.setColors (0, colors);


	Shape3D shape = new Shape3D();
	shape.setGeometry (la);

        // use the utility method to set the capabilities
	PickTool.setCapabilities(shape, PickTool.INTERSECT_FULL);

	objTrans.addChild(shape);
      }
    }

    // Points
    for (double x=-2.0;x<=2.0;x+=1.0) {
      for (double y=-2.0;y<=2.0;y+=1.0) {
	for (double z=-2.0;z<=2.0;z+=1.0) {
	  t3.setTranslation (new Vector3d(-10.0+2.0*x, 0.0+2.0*y,-20.0+2.0*z));
	  TransformGroup objTrans = new TransformGroup(t3);

	  objRoot.addChild(objTrans);

	  PointArray pa = new PointArray (1, 
					  PointArray.COORDINATES |
					  PointArray.COLOR_3);

	  pa.setCoordinate (0, new Point3d (0.0, 0.0, 0.0));
	  pa.setColor (0, grey);

	  Shape3D shape = new Shape3D();
	  shape.setGeometry (pa);

          // use the utility method to set the capabilities
	  PickTool.setCapabilities(shape, PickTool.INTERSECT_FULL);

	  objTrans.addChild(shape);
	}
      }
    }    

    return objRoot;
  }

  public IntersectTest () {
  }

    public void init() {
	setLayout(new BorderLayout());
	
	GraphicsConfiguration config =
	    SimpleUniverse.getPreferredConfiguration();
	
	Canvas3D c = new Canvas3D(config);
	add("Center", c);
	
	// Create a simple scene and attach it to the virtual universe
	BranchGroup scene = createSceneGraph();
	u = new SimpleUniverse(c);
	
	// Add picking behavior
	IntersectInfoBehavior behavior =
	    new IntersectInfoBehavior (c, scene,0.05f);
	behavior.setSchedulingBounds (bounds);
	scene.addChild (behavior);
	
	TransformGroup vpTrans =
	    u.getViewingPlatform().getViewPlatformTransform();
	
	KeyNavigatorBehavior keybehavior = new KeyNavigatorBehavior (vpTrans);
	keybehavior.setSchedulingBounds (bounds);
	scene.addChild (keybehavior);
	scene.setCapability (Group.ALLOW_CHILDREN_EXTEND);
	scene.compile();
	u.addBranchGraph(scene);
	
	View view = u.getViewer().getView();
	view.setBackClipDistance (100000);
	
    }

    public void destroy() {
	u.cleanup();
    }

  //
  // The following allows IntersectTest to be run as an application
  // as well as an applet
  //
  public static void main(String[] args) {
    String s = "\n\nIntersectTest:\n-----------\n";
    s += "Pick with the mouse over the primitives\n";
    s += "- A sphere will be placed to indicate the picked point.\n";
    s += "If color information is available, the sphere will change color to reflect\n";
    s += "the interpolated color.\n";
    s += "- Other spheres will be placed to show the vertices of the selected polygon\n";
    s += "- Information will be displayed about the picking operation\n\n\n";

    System.out.println (s);

    new MainFrame(new IntersectTest(), 640, 640);
  }
}
