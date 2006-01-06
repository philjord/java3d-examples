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

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.event.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * Pure immediate mode example program.  In pure immediate mode, the
 * renderer must be stopped on the Canvas being rendered into. In our
 * example, this is done immediately after the canvas is created. A
 * separate thread is started up to do the immediate mode rendering.
 */
public class PureImmediate extends Applet implements Runnable {

    private Canvas3D canvas;
    private GraphicsContext3D gc = null;
    private Geometry cube = null;
    private Transform3D cmt = new Transform3D();

    // One rotation (2*PI radians) every 6 seconds
    private Alpha rotAlpha = new Alpha(-1, 6000);

    private SimpleUniverse u = null;

    //
    // Renders a single frame by clearing the canvas, drawing the
    // geometry, and swapping the draw and display buffer.
    //
    public void render() {
	if (gc == null) {
	    // Set up Graphics context
	    gc = canvas.getGraphicsContext3D();
	    gc.setAppearance(new Appearance());

	    // Set up geometry
	    cube = new ColorCube(0.4).getGeometry();
	}

	// Compute angle of rotation based on alpha value
	double angle = rotAlpha.value() * 2.0*Math.PI;
	cmt.rotY(angle);
 
	// Render the geometry for this frame
	gc.clear();
	gc.setModelTransform(cmt);
	gc.draw(cube);
	canvas.swap();
    }


    //
    // Run method for our immediate mode rendering thread.
    //
    public void run() {
	System.out.println("PureImmediate.run: starting main loop");
	while (true) {
	    render();
	    Thread.yield();
	}
    }


    public PureImmediate() {
    }

    //
    // init: create the canvas, stop the renderer,
    // create the universe, and start the drawing thread.
    //
    public void init() {
	setLayout(new BorderLayout());
        GraphicsConfiguration config =
           SimpleUniverse.getPreferredConfiguration();

        canvas = new Canvas3D(config);
        canvas.stopRenderer();
	add("Center", canvas);

	// Create the universe and viewing branch
	u = new SimpleUniverse(canvas);

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        u.getViewingPlatform().setNominalViewingTransform();

	// Start a new thread that will continuously render
	new Thread(this).start();
    }

    public void destroy() {
	u.cleanup();
    }

    //
    // The following allows PureImmediate to be run as an application
    // as well as an applet
    //
    public static void main(String[] args) {
	new MainFrame(new PureImmediate(), 256, 256);
    }
}
