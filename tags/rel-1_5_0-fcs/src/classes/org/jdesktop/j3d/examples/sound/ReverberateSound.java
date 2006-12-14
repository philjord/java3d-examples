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

package org.jdesktop.j3d.examples.sound;

/*
 * ReverberateSound
 *
 * Same as MoveSound except this calls UniverseBuilderJS to use the
 * JavaSoundMixer AudioDevice rather than the HolosketchMixer device.
 *
 * NOTE: To run this anywhere but the Solaris Eng Menlo Park network
 * the URL path must be set to the java3d/javaone directory.
 */

import java.applet.Applet;
import java.awt.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.*;
import java.net.URL;
import javax.media.j3d.*;
import javax.swing.JOptionPane;
import javax.vecmath.*;
import org.jdesktop.j3d.examples.Resources;

public class ReverberateSound extends Applet {

    // File name of sound sample
    private static URL  url = null;
    private SimpleUniverse u = null;

    public BranchGroup createSceneGraph() {
	// Create the root of the subgraph
	BranchGroup objRoot = new BranchGroup();

	// Create the transform group node and initialize it to the identity.
	// Enable the TRANSFORM_WRITE capability so that our behavior code
	// can modify it at runtime.  Add it to the root of the subgraph.
	TransformGroup objTrans = new TransformGroup();
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objRoot.addChild(objTrans);

	// Create a simple shape leaf node and add it into the scene graph.
	objTrans.addChild(new ColorCube(0.4));

	// Create a new Behavior object that will perform the desired
	// operation on the specified transform object and add it into the
	// scene graph.
	Transform3D yAxis = new Transform3D();
        Alpha rotation = new Alpha(-1, Alpha.INCREASING_ENABLE,
                                   0, 0,
                                   20000, 0, 0,
                                   0, 0, 0);
        RotationInterpolator rotator =
            new RotationInterpolator(rotation,
                                     objTrans, yAxis,
                                     0.0f, (float) Math.PI*2.0f);
	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
	rotator.setSchedulingBounds(bounds);
	objTrans.addChild(rotator);

        //
        // Create an AuralAttribute with reverb params set
        //
        Soundscape soundScape2 = new Soundscape();
        AuralAttributes attributes2 = new AuralAttributes();
        attributes2.setReverbOrder(6);
        attributes2.setCapability(AuralAttributes.ALLOW_REVERB_ORDER_WRITE);
        attributes2.setCapability(AuralAttributes.ALLOW_REVERB_DELAY_WRITE);
        attributes2.setCapability(AuralAttributes.ALLOW_REFLECTION_COEFFICIENT_WRITE);
        soundScape2.setApplicationBounds(bounds);
        soundScape2.setAuralAttributes(attributes2);
        objRoot.addChild(soundScape2);

        //
        // Create a sound node and add it to the scene graph
        //
        PointSound sound = new PointSound();
        sound.setCapability(PointSound.ALLOW_ENABLE_WRITE);
        sound.setCapability(PointSound.ALLOW_INITIAL_GAIN_WRITE);
        sound.setCapability(PointSound.ALLOW_SOUND_DATA_WRITE);
        sound.setCapability(PointSound.ALLOW_DURATION_READ);
        sound.setCapability(PointSound.ALLOW_POSITION_WRITE);
        sound.setCapability(PointSound.ALLOW_LOOP_WRITE);
        sound.setSchedulingBounds(bounds);

	objTrans.addChild(sound);
        //
	// Create a new Behavior object that will play the sound
	//
	AudioReverberate player = new AudioReverberate(sound, url, attributes2);
	player.setSchedulingBounds(bounds);
	objTrans.addChild(player);

	return objRoot;
    }

    public ReverberateSound() { 
    }

    public void init() {
            url = Resources.getResource("resources/audio/hello_universe.au");
            if (url == null) {
                System.err.println("resources/audio/hello_universe.au not found");
                System.exit(1);
            }

	setLayout(new BorderLayout());
        GraphicsConfiguration config =
           SimpleUniverse.getPreferredConfiguration();

        Canvas3D c = new Canvas3D(config);
	add("Center", c);

        /*
         * Create a simple scene and attach it to the virtual universe
         */
        u = new SimpleUniverse(c);
        AudioDevice audioDev = u.getViewer().createAudioDevice();
        BranchGroup scene = createSceneGraph();

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        u.getViewingPlatform().setNominalViewingTransform();

	u.addBranchGraph(scene);
        
        JOptionPane.showMessageDialog(this,
                ("This program is still a work in progress.\n" +
                "Please check back in Java 3D 1.5.\n"),
                "Incomplete Work",
                JOptionPane.INFORMATION_MESSAGE);                
    }

    public void destroy() {
	u.cleanup();
    }

    //
    // The following allows ReverberateSound to be run as an application
    // as well as an applet
    //
    public static void main(String[] args) {
	new MainFrame(new ReverberateSound(), 256, 256);
    }
}
