/*
 * $RCSfile$
 *
 * Copyright (c) 2004 Sun Microsystems, Inc. All rights reserved.
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

import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.Scene;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.io.*;
import com.sun.j3d.utils.behaviors.mouse.*;

public class PrintCanvas3D extends JFrame implements ActionListener {

    private static final boolean spin = false;
    private static final boolean noTriangulate = false;
    private static final boolean noStripify = false;
    private static final double creaseAngle = 60.0;

    private JMenuItem snapshotItem;
    private JMenuItem freeBufferItem;
    private JMenuItem printItem;
    private JMenuItem quitItem;

    private SimpleUniverse u;
    private Canvas3D canvas3D;
    private OffScreenCanvas3D offScreenCanvas3D;

    private static final int OFF_SCREEN_SCALE = 3;

    private class AppPanel extends JPanel {

	private String filename = null;

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
	    Scene s = null;
	    try {
		s = f.load(filename);
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
	  
	    objTrans.addChild(s.getSceneGroup());

	    BoundingSphere bounds =
		new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

	    if (spin) {
		Transform3D yAxis = new Transform3D();
		Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE,
						0, 0,
						4000, 0, 0,
						0, 0, 0);

		RotationInterpolator rotator =
		    new RotationInterpolator(rotationAlpha, objTrans, yAxis,
					     0.0f, (float) Math.PI*2.0f);
		rotator.setSchedulingBounds(bounds);
		objTrans.addChild(rotator);
	    } else {
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
	    }

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


	// Create the Canvas3D (both on-screen and off-screen)
	private void createCanvas3D(String[] args) {
	    if (args.length == 0) {
		usage();
	    } else {
		for (int i = 0 ; i < args.length ; i++) {
		    if (args[i].startsWith("-")) {
			System.err.println("Argument '" + args[i] + "' ignored.");
		    }
		    else {
			filename = args[i];
		    }
		}
	    }

	    if (filename == null) {
		usage();
	    }

	    // Create Canvas3D
	    GraphicsConfiguration config =
		SimpleUniverse.getPreferredConfiguration();

	    canvas3D = new Canvas3D(config);
	    canvas3D.setSize(600, 450);

	    // Create a simple scene and attach it to the virtual universe
	    BranchGroup scene = createSceneGraph(args);
	    u = new SimpleUniverse(canvas3D);
	    // This will move the ViewPlatform back a bit so the
	    // objects in the scene can be viewed.
	    u.getViewingPlatform().setNominalViewingTransform();
	    u.addBranchGraph(scene);

	    // Create the off-screen Canvas3D object
	    offScreenCanvas3D = new OffScreenCanvas3D(config, true);
	    // Set the off-screen size based on a scale factor times the
	    // on-screen size
	    Screen3D sOn = canvas3D.getScreen3D();
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
	    u.getViewer().getView().addCanvas3D(offScreenCanvas3D);
	}

	private AppPanel(String args[]) {
	    setLayout(new BorderLayout());

	    // Create Canvas3D and scene graph
	    createCanvas3D(args);
	    add("Center", canvas3D);
	}
    }

    private void doExit() {
	View view = u.getViewer().getView();

	// Free offscreen buffer
	try {
	    offScreenCanvas3D.setOffScreenBuffer(null);
	}
	catch (NullPointerException npe) {
	    //System.err.println(npe + " while freeing off-screen buffer");
	}

	// clear universe resources
	u.removeAllLocales();

	// remove all canvas3Ds from View
	view.removeCanvas3D(offScreenCanvas3D);
	view.removeCanvas3D(canvas3D);

	System.exit(0);
    }

    public void actionPerformed (ActionEvent event) {
	Object target = event.getSource();

	if ((target == snapshotItem) ||
	    (target == printItem)) {
	    Point loc = canvas3D.getLocationOnScreen();
	    offScreenCanvas3D.setOffScreenLocation(loc);
	    Dimension dim = canvas3D.getSize();
	    dim.width *= OFF_SCREEN_SCALE;
	    dim.height *= OFF_SCREEN_SCALE;
	    BufferedImage bImage =
		offScreenCanvas3D.doRender(dim.width, dim.height);

	    if (target == snapshotItem) {
		new ImageDisplayer(bImage);
	    }
	    else { // (target == printItem)
		new ImagePrinter(bImage).print();
	    }
	}
	else if (target == freeBufferItem) {
	    try {
		offScreenCanvas3D.setOffScreenBuffer(null);
	    }
	    catch (NullPointerException npe) {
		//System.err.println(npe + " while freeing off-screen buffer");
	    }
	}
	else if (target == quitItem) {
	    doExit();
	}
    }

    private JMenuBar createMenuBar() {
	JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu("File");
	snapshotItem = new JMenuItem("Snapshot");
	snapshotItem.addActionListener(this);
	freeBufferItem = new JMenuItem("Free OffScreen Buffer");
	freeBufferItem.addActionListener(this);
	printItem = new JMenuItem("Print...");
	printItem.addActionListener(this);
	quitItem = new JMenuItem("Quit");
	quitItem.addActionListener(this);
	fileMenu.add(snapshotItem);
	fileMenu.add(freeBufferItem);
	fileMenu.add(printItem);
	fileMenu.add(new JSeparator());
	fileMenu.add(quitItem);
	menuBar.add(fileMenu);
	return menuBar;
    }

    private PrintCanvas3D(String args[]) {
	this.setTitle("Canvas3D Print Test");

	// Create and initialize menu bar
	JPopupMenu.setDefaultLightWeightPopupEnabled(false);
	this.setJMenuBar(createMenuBar());

	// Handle the close event
	this.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent winEvent) {
		doExit();
	    }
	});

	// Add main panel to top-level frame and make it visible
	this.getContentPane().add(new AppPanel(args));
	this.pack();
	this.setVisible(true);
    }

    public static void main(String[] args) {
	new PrintCanvas3D(args);
    }
}
