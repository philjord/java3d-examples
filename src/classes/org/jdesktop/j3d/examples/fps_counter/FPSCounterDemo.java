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

package org.jdesktop.j3d.examples.fps_counter;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.GraphicsConfiguration;
import com.sun.j3d.utils.applet.JMainFrame;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import javax.swing.JOptionPane;

/** This program demonstrates the use of the frames per second counter.
 * The program displays a rotating cube and sets up the FPSCounter to compute
 * the frame rate. The FPSCounter is set up with default values:
 * 	- run indefinitely
 *	- 2 sec. warmup time
 *	- display average frame rate every fifth sampling interval.
 * The default values can be changed through the command line
 * arguments. Use FPSCounterDemo -h for help on the various arguments.
 */

public class FPSCounterDemo extends Applet {
    private SimpleUniverse u = null;
    private FPSCounter fpsCounter = new FPSCounter();

    BranchGroup createSceneGraph() {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

	// Create the TransformGroup node and initialize it to the
	// identity. Enable the TRANSFORM_WRITE capability so that
	// our behavior code can modify it at run time. Add it to
	// the root of the subgraph.
	TransformGroup objTrans = new TransformGroup();
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objRoot.addChild(objTrans);

	// Create a simple Shape3D node; add it to the scene graph.
	objTrans.addChild(new ColorCube(0.4));

	// Create a new Behavior object that will perform the
	// desired operation on the specified transform and add
	// it into the scene graph.
	Transform3D yAxis = new Transform3D();
	Alpha rotationAlpha = new Alpha(-1, 4000);

	RotationInterpolator rotator =
		new RotationInterpolator(rotationAlpha, objTrans, yAxis,
				 0.0f, (float) Math.PI*2.0f);
	BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0),
						100.0);
	rotator.setSchedulingBounds(bounds);
	objRoot.addChild(rotator);

	// Create the Framecounter behavior
	fpsCounter.setSchedulingBounds(bounds);
	objRoot.addChild(fpsCounter);

	return objRoot;
    }


   public FPSCounterDemo(String args[]) {
   }

   public FPSCounterDemo() {
   }

   public void init() {
      setLayout(new BorderLayout());
      GraphicsConfiguration config =
	  SimpleUniverse.getPreferredConfiguration();

      Canvas3D c = new Canvas3D(config);
      add("Center", c);

      // Create a simple scene and attach it to the virtual universe
      BranchGroup scene = createSceneGraph();

      // Parse the command line to set the various parameters

      // Have Java 3D perform optimizations on this scene graph.
      scene.compile();
      u = new SimpleUniverse(c);

      // This will move the ViewPlatform back a bit so the
      // objects in the scene can be viewed.
      u.getViewingPlatform().setNominalViewingTransform();

      u.addBranchGraph(scene);

      JOptionPane.showMessageDialog(this,
	  ("This program measures the number of frames rendered per second.\n" +
	   "Note that the frame rate is limited by the refresh rate of the monitor.\n" +
	   "To get the true frame rate you need to disable vertical retrace.\n\n" +
	   "On Windows(tm) you do this through the Control Panel.\n\n" +
	   "On Solaris set the environment variable OGL_NO_VBLANK"),
	  "Frame Counter",
	   JOptionPane.INFORMATION_MESSAGE);
   }

   public void destroy() {
      u.cleanup();
   }

   //
   // The following allows FPSCounterDemo to be run as an application
   // as well as an applet
   //
   public static void main(String[] args) {
      FPSCounterDemo fp = new FPSCounterDemo();
      fp.parseArgs(args);
      JMainFrame frame = new JMainFrame(fp, 256, 256);
   }

    /** Parses the commandline for the various switches to set the FPSCounter
     * variables.
     * All arguments are of the form <i>-name value</i>.
     * All -name arguments can be shortened to one character. All the value
     * arguments take a number. The arguments accepted are :
     * <ul>
     * <li>warmupTime : Specifies amount of time the FPSCounter should wait
     * for the HotSpot<sup><font size="-2">TM</font></sup> VM to perform
     * initial optimizations. Specified in milliseconds<br>
     * <li>loopCount  : Specifies the number of sampling intervals over which
     * the FPSCounter should calculate the aggregate and average frame rate.
     * Specified as a count. <br>
     * <li>maxLoops   : Specifies that the FPSCounter should run for only
     * these many sampling intervals. Specified as number. If this argument
     * is not specified, the FPSCounter runs indefinitely. <br>
     * <li>help	   : Prints the accepted arguments. <br>
     * </ul>
     */
   private void parseArgs(String args[]) {
      for(int i = 0; i < args.length; i++) {
	  if(args[i].startsWith("-")) {
	      if(args[i].startsWith("w", 1)) {
		  i++;
		  System.out.println("Warmup time : " + args[i]);
		  int w = new Integer(args[i]).intValue();
		  fpsCounter.setWarmupTime(w);
	      }
	      else if(args[i].startsWith("l", 1)) {
		  i++;
		  System.out.println("Loop count : " + args[i]);
		  int l = new Integer(args[i]).intValue();
		  fpsCounter.setLoopCount(l);
	      }
	      else if(args[i].startsWith("m", 1)) {
		  i++;
		  System.out.println("Max Loop Count : " + args[i]);
		  int m = new Integer(args[i]).intValue();
		  fpsCounter.setMaxLoops(m);
	      }
	      else if(args[i].startsWith("h", 1)) {
		  System.out.println("Usage : FPSCounterDemo [-name value]\n" +
		       "All arguments are of the form: -name value. All -name arguments can be\n" +
		       "shortened to one character. All the value arguments take a number. The\n" +
		       "arguments accepted are:\n\n" +
		       "    -warmupTime : Specifies amount of time the FPSCounter should wait\n" +
		       "        for the HotSpot(tm) VM to perform initial\n" +
		       "        optimizations. Specified in milliseconds\n\n" +
		       "    -loopCount : Specifies the number of sampling intervals over which\n" +
		       "        the FPSCounter should calculate the aggregate and average\n" +
		       "        frame rate. Specified as a count\n\n" +
		       "    -maxLoops : Specifies that the FPSCounter should run for only these\n" +
		       "        many sampling intervals. Specified as number. If this argument\n" +
		       "        is not specified, the FPSCounter runs indefinitely.\n\n" +
		       "    -help : Prints this message.");
	      }
	  }
      }
   }

}
