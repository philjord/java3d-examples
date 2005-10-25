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

import com.sun.j3d.utils.pickfast.behaviors.*;
import com.sun.j3d.utils.pickfast.*;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.Component;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.Point;
import javax.swing.*;
import javax.swing.border.BevelBorder;

/**
 * PickTest shows how to use the Picking utilities on various GeometryArray
 * subclasses and Morph object.
 * Type of Geometry      : CompressedGeometry ( GullCG.java )
 *                         IndexedQuadArray ( CubeIQA.java )
 *                         TriangleArray ( TetrahedronTA.java )
 *                         IndexedTriangleArray ( TetrahedronITA.java )
 *                         TriangleFanArray ( OctahedronTFA.java )
 *                         IndexedTriangleFanArray ( OctahedronITA.java )
 *                         TriangleStripArray ( IcosahedronTFA.java )
 *                         IndexedTriangleStripArray ( IcosahedronITA.java )
 *			   PointArray( TetrahedronPA.java )
 *			   LineArray( TetrahedronLA.java )
 *			   IndexLineArray( TetrahedronILA.java )
 *			   LineStripArray( TetrahedronLSA.java )
 *			   IndexLineStripArray( TetrahedronILSA.java )
 *
 * Morph Object uses :     QuadArray ( ColorCube.java, ColorPyramidDown.java, 
 *				and ColorPyramidUp.java ).
 */

public class PickTest extends Applet implements ActionListener {
  
  private View view = null;
  private QuadArray geomMorph[] = new QuadArray[3];
  private Morph morph;

  private PickRotateBehavior behavior1;
  private PickZoomBehavior   behavior2;
  private PickTranslateBehavior behavior3;

    private SimpleUniverse u = null;

  public BranchGroup createSceneGraph(Canvas3D canvas)
  {
    // Create the root of the branch graph
    BranchGroup objRoot = new BranchGroup();

    // Create a Transformgroup to scale all objects so they
    // appear in the scene.
    TransformGroup objScale = new TransformGroup();
    Transform3D t3d = new Transform3D();
    t3d.setScale(1.0);
    objScale.setTransform(t3d);
    objRoot.addChild(objScale);
    
    // Create a bunch of objects with a behavior and add them
    // into the scene graph.
    
    int row, col;
    int numRows = 4, numCols = 4;
    
    for (int i = 0; i < numRows; i++) {
      double ypos = (double)(i - numRows/2) * 0.45 + 0.25;
      for (int j = 0; j < numCols; j++) {
	double xpos = (double)(j - numCols/2) * 0.45 + 0.25;
	objScale.addChild(createObject(i * numCols + j, 0.1,  xpos, ypos));
      }
    }

    BoundingSphere bounds =
      new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

    // Add a light.
    Color3f lColor = new Color3f(1.0f, 1.0f, 1.0f) ;
    Vector3f lDir  = new Vector3f(0.0f, 0.0f, -1.0f) ;

    DirectionalLight lgt = new DirectionalLight(lColor, lDir) ;
    lgt.setInfluencingBounds(bounds) ;
    objRoot.addChild(lgt) ;


    // Now create the Alpha object that controls the speed of the
    // morphing operation.
    Alpha morphAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE |
				 Alpha.DECREASING_ENABLE,
				 0, 0,
				 4000, 1000, 500,
				 4000, 1000, 500);
      
    // Finally, create the morphing behavior
    MorphingBehavior mBeh = new MorphingBehavior(morphAlpha, morph);  
    mBeh.setSchedulingBounds(bounds);
    objRoot.addChild(mBeh);
        
    behavior1 = new PickRotateBehavior(objRoot, canvas, bounds);
    objRoot.addChild(behavior1);

    behavior2 = new PickZoomBehavior(objRoot, canvas, bounds);
    objRoot.addChild(behavior2);

    behavior3 = new PickTranslateBehavior(objRoot, canvas, bounds);
    objRoot.addChild(behavior3);

    // Let Java 3D perform optimizations on this scene graph.
    objRoot.compile();
 
    return objRoot;
  }
  

  private Group createObject(int index, double scale, double xpos, double ypos){
    
    Shape3D shape = null;
    Geometry geom = null;
     
    // Create a transform group node to scale and position the object.
    Transform3D t = new Transform3D();
    t.set(scale, new Vector3d(xpos, ypos, 0.0));
    TransformGroup objTrans = new TransformGroup(t);
    objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    objTrans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
    
    // Create a second transform group node and initialize it to the
    // identity.  Enable the TRANSFORM_WRITE capability so that
    // our behavior code can modify it at runtime.
    TransformGroup spinTg = new TransformGroup();
    spinTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    spinTg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    spinTg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
 
    Appearance appearance = new Appearance();

    switch(index) {
    case 0:
      geom = new GullCG();
      break;
    case 1:
      geom = new TetrahedronTA();
      break;
    case 2:
      geom = new OctahedronTFA();  
      break;
    case 3:
      geom = new IcosahedronTSA();    
      break;
    case 4:
      geom = new CubeIQA();   
      break;
    case 5:
      geom = new TetrahedronITA();  
      break;
    case 6:
      geom = new OctahedronITFA();  
      break;
    case 7:
      geom = new IcosahedronITSA();  
      break;
    case 8:
      geomMorph[0] = new ColorPyramidUp();
      geomMorph[1] = new ColorCube();
      geomMorph[2] = new ColorPyramidDown();
      break;
    case 9:
      geom = new TetrahedronLA();
      break;
    case 10:
      geom = new TetrahedronILA();
      break;
    case 11:
      geom = new TetrahedronLSA();
      break;
    case 12:
      geom = new TetrahedronILSA();
      break;
    case 13:
      geom = new TetrahedronPA();
      break;
    case 14:
      geom = new TetrahedronIPA();
      break;
    // TODO: other geo types, Text3D?
    case 15:
      geom = new TetrahedronTA();
      break;
    }

    Material m = new Material() ;
        
    if(index == 8) {
	m.setLightingEnable(false) ;
	appearance.setMaterial(m) ;
	morph = new Morph((GeometryArray[]) geomMorph, appearance);
	morph.setCapability(Morph.ALLOW_WEIGHTS_READ);
	morph.setCapability(Morph.ALLOW_WEIGHTS_WRITE);
	//PickTool.setCapabilities(morph, PickTool.INTERSECT_FULL);
	spinTg.addChild(morph); 
    } else {
	// Geometry picking require this to be set.
	if (index == 0)
	    m.setLightingEnable(true) ;
	else
	    m.setLightingEnable(false) ;
	appearance.setMaterial(m) ;

	if ((index == 13) || (index == 14)) {
	    PointAttributes pa = new PointAttributes();
	    pa.setPointSize(4.0f);
	    appearance.setPointAttributes(pa);
	}

	shape = new Shape3D(geom,appearance);
	shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
	shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
	shape.setCapability(Shape3D.ENABLE_PICK_REPORTING);
	//PickTool.setCapabilities(shape, PickTool.INTERSECT_FULL);
	spinTg.addChild(shape);
    } 
     
    // add it to the scene graph. 
    objTrans.addChild(spinTg);    

    return objTrans;
  }

  private void setPickMode(int mode) {
      behavior1.setMode(mode);
      behavior2.setMode(mode);
      behavior3.setMode(mode);
  }

  private void setPickTolerance(float tolerance) {
      behavior1.setTolerance(tolerance);
      behavior2.setTolerance(tolerance);
      behavior3.setTolerance(tolerance);
  }

  private void setViewMode(int mode) {
      view.setProjectionPolicy(mode);
  }

  // GUI stuff

  String pickModeString = new String("Pick Mode");
  String boundsString = new String("BOUNDS");
  String geometryString = new String("GEOMETRY");
    //String geometryIntersectString = new String("GEOMETRY_INTERSECT_INFO");
  String toleranceString = new String("Pick Tolerance");
  String tolerance0String = new String("0");
  String tolerance2String = new String("2");
  String tolerance4String = new String("4");
  String tolerance8String = new String("8");
  String viewModeString = new String("View Mode");
  String perspectiveString = new String("Perspective");
  String parallelString = new String("Parallel");

  private void addRadioButton(JPanel panel, ButtonGroup bg, String ownerName,
		  String buttonName, boolean selected) {
      JRadioButton	item;
      item = new JRadioButton(buttonName);
      item.setName(ownerName);
      item.addActionListener(this);
      if (selected) {
	  item.setSelected(true);
      }
      panel.add(item);
      bg.add(item);
  }

  private void setupGUI(JPanel panel) {
      ButtonGroup 	bg;

      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBorder(new BevelBorder(BevelBorder.RAISED));

      panel.add(new JLabel(pickModeString));
      bg = new ButtonGroup();
      addRadioButton(panel, bg, pickModeString, boundsString, true);
      addRadioButton(panel, bg, pickModeString, geometryString, false);
      //addRadioButton(panel, bg, pickModeString, geometryIntersectString, false);

      panel.add(new JLabel(toleranceString));
      bg = new ButtonGroup();
      addRadioButton(panel, bg, toleranceString, tolerance0String,false);
      addRadioButton(panel, bg, toleranceString, tolerance2String,true);
      addRadioButton(panel, bg, toleranceString, tolerance4String,false);
      addRadioButton(panel, bg, toleranceString, tolerance8String,false);

      panel.add(new JLabel(viewModeString));
      bg = new ButtonGroup();
      addRadioButton(panel, bg, viewModeString, perspectiveString, true);
      addRadioButton(panel, bg, viewModeString, parallelString, false);

  }

  public void actionPerformed(ActionEvent e) {
      String name = ((Component)e.getSource()).getName();
      String value = e.getActionCommand();
      //System.out.println("action: name = " + name + " value = " + value);
      if (name == pickModeString) {
	 if (value == boundsString) {
	     setPickMode(PickInfo.PICK_BOUNDS);
	 } else if (value == geometryString) {
	     setPickMode(PickInfo.PICK_GEOMETRY);
	     //} else if (value == geometryIntersectString) {
	     //setPickMode(PickInfo.PICK_GEOMETRY);
	 } else {
	     System.out.println("Unknown pick mode: " + value); 
	 }
      } else if (name == toleranceString) {
	 if (value == tolerance0String) {
	     setPickTolerance(0.0f);
	 } else if (value == tolerance2String) {
	     setPickTolerance(2.0f);
	 } else if (value == tolerance4String) {
	     setPickTolerance(4.0f);
	 } else if (value == tolerance8String) {
	     setPickTolerance(8.0f);
	 } else {
	     System.out.println("Unknown tolerance: " + value); 
	 }
      } else if (name == viewModeString) {
	 if (value == perspectiveString) {
	     setViewMode(View.PERSPECTIVE_PROJECTION);
	 } else if (value == parallelString) {
	     setViewMode(View.PARALLEL_PROJECTION);
	 } 
      } else {
	 System.out.println("Unknown action name: " + name); 
      }
  }
  
  public PickTest (){
  }

    public void init() {
	setLayout(new BorderLayout());
	Canvas3D c = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
	add("Center", c);
	
	JPanel guiPanel = new JPanel();
	setupGUI(guiPanel);
	add(guiPanel, BorderLayout.EAST);
	
	// Create a scene and attach it to the virtual universe
	BranchGroup scene = createSceneGraph(c);
	u = new SimpleUniverse(c);
	
	// This will move the ViewPlatform back a bit so the
	// objects in the scene can be viewed.
	u.getViewingPlatform().setNominalViewingTransform();
	view = u.getViewer().getView();
	
	u.addBranchGraph(scene);
    }

    public void destroy() {
	u.cleanup();
    }
  
  
  public static void main(String argv[])
  {
    
    BranchGroup group;
    
    new MainFrame(new PickTest(), 750, 550);
  }
}

