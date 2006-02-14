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

package org.jdesktop.j3d.examples.lightwave;

import java.applet.Applet;
import java.awt.*;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.vecmath.Matrix4d;

import com.sun.j3d.loaders.lw3d.Lw3dLoader;
import com.sun.j3d.loaders.Loader;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.SimpleUniverse;


/**
 * This class loads in a Lightwave3D file and displays it in an applet
 * window.  The application is fairly basic; a more complete version
 * of a Lightwave 3D loader might incorporate features such as
 * settable clip plane distances and animated views (these are both
 * possible with the current Lightwave 3D loader, they just need to
 * be implemented in the application).
 */
public class Viewer extends Applet {

    private java.net.URL filename;
    private SimpleUniverse u;

    public Viewer(java.net.URL url) {
	filename = url;
    }

    public Viewer() {}

    public void init() {
	if (filename == null) {
	    // the path to the file for an applet
	    try {
		java.net.URL path = getCodeBase();
		filename = new java.net.URL(path.toString() +
					    "./ballcone.lws");
	    }
	    catch (java.net.MalformedURLException ex) {
		System.err.println(ex.getMessage());
		ex.printStackTrace();
		System.exit(1);
	    }
	}

	// Construct the Lw3d loader and load the file
	Loader lw3dLoader = new Lw3dLoader(Loader.LOAD_ALL);
	Scene loaderScene = null;
	try {
	    loaderScene = lw3dLoader.load(filename);
	}
	catch (Exception e) {
	    e.printStackTrace();
            System.exit(1);
	}

	// Construct the applet canvas
	setLayout(new BorderLayout());
        GraphicsConfiguration config =
           SimpleUniverse.getPreferredConfiguration();

        Canvas3D c = new Canvas3D(config);
	add("Center", c);

	// Create a basic universe setup and the root of our scene
	u = new SimpleUniverse(c);
	BranchGroup sceneRoot = new BranchGroup();

	// Change the back clip distance; the default is small for
	// some lw3d worlds
	View theView = u.getViewer().getView();
	theView.setBackClipDistance(50000f);
	
	// Now add the scene graph defined in the lw3d file
	if (loaderScene.getSceneGroup() != null) {
	    // Instead of using the default view location (which may be
	    // completely bogus for the particular file you're loading),
	    // let's use the initial view from the file.  We can get
	    // this by getting the  view groups from the scene (there's
	    // only one for Lightwave 3D), then using the inverse of the
	    // transform on that view as the transform for the entire scene.

	    // First, get the view groups (shouldn't be null unless there
	    // was something wrong in the load
	    TransformGroup viewGroups[] = loaderScene.getViewGroups();

	    // Get the Transform3D from the view and invert it
	    Transform3D t = new Transform3D();
	    viewGroups[0].getTransform(t);
	    Matrix4d m = new Matrix4d();
	    t.get(m);
	    m.invert();
	    t.set(m);

	    // Now we've got the transform we want.  Create an
	    // appropriate TransformGroup and parent the scene to it.
	    // Then insert the new group into the main BranchGroup.
	    TransformGroup sceneTransform = new TransformGroup(t);
	    sceneTransform.addChild(loaderScene.getSceneGroup());
	    sceneRoot.addChild(sceneTransform);
	}
	
	// Make the scene graph live by inserting the root into the universe
	u.addBranchGraph(sceneRoot);
    }


    public void destroy() {
	u.cleanup();
    }
    
    private static void usage() {
	System.out.println("Usage: java Viewer <.lws>")  ;
	System.exit(0)  ;
    }
    
    /**
     * The main method of the application takes one argument in the
     * args array; the filname that you want to load.  Note that the
     * file must be reachable from the directory in which you're running
     * this application.
     */
    public static void main(String args[]) {
	java.net.URL url = null;
	java.net.URL pathUrl = null;
	if (args.length > 0) {
	    try {
		if ((args[0].indexOf("file:") == 0) ||
		    (args[0].indexOf("http") == 0)) {
		    url = new java.net.URL(args[0]);
		}
		else if (args[0].charAt(0) != '/') {
		    url = new java.net.URL("file:./" + args[0]);
		}
		else {
		    url = new java.net.URL("file:" + args[0]);
		}
	    }
	    catch (java.net.MalformedURLException ex) {
		System.err.println(ex.getMessage());
		ex.printStackTrace();
		System.exit(1);
	    }
	}
	else {
            usage();
	}
	new MainFrame(new Viewer(url), 500, 500);
    }
}




