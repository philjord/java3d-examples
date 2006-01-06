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

/*
 * This Java3D program:
 *    Creates an instance of the JavaSoundMixer AudioDevice, initializing it
 *         and attaching it to the PhysicalEnvironment with a special version
 *         of UniverseBuilder (UniverseBuildJS).
 *    Creates one Point sound sources.
 *    Creates two Soundscapes with their own aural attributes.  Each
 *         of these Soundscapes defines its own bounding leaf region.
 *    Creates and executes a custom behavior (AudioBehaviorMoveOne) that
 *         starts sound playing and transforms the AuralAttribute positions
 *         by modifying the TransformGroup that contains both soundscape
 *         nodes.
 * Usage: java MoveAppBoundingLeaf [ URLpath [ name ]]
 *
 * The first optional command line parameter is the URL path to directory
 *    containing:
 *       (1) the files that will be named on the command line, or
 *       (2) the javaone/data/sounds directory that contains the default files
 *    If not given the default path is:
 *       file:/net/java3d/export/java3d/javaone/data/sounds
 *    NOTE: This default path is only valid on Solaris Eng Menlo Park network.
 *    A path must be supplied if sound files to be used are in a different
 *    directory location.
 *    NOTE: When running on Java3D Menlo Park Test WinTel platform path should
 *    be set to
 *       file:/java3d/export/java3d/javaone/data/sounds
 *
 * The second thru fourth optional command line parameters are sound file names
 *    If not given the default file name is:
 *       unicycle_close.au
 */

import java.applet.Applet;
import java.net.URL;
import java.awt.*;
import java.awt.event.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.*;
import java.io.File;
import javax.media.j3d.*;
import javax.vecmath.*;

public class MoveAppBoundingLeaf extends Applet {

    // File name of sound sample
    private static  int filenamesGiven = 0;
    private static URL[] url = new URL[1];
    private static String[] filename = new String[1];
    private static String path = null;
    private static boolean filenamesSet = false;

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
	objTrans.addChild(new ColorCube(0.7));

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
        // Create a sound node and add it to the scene graph
        //
        PointSound sound = new PointSound();
        sound.setCapability(PointSound.ALLOW_ENABLE_WRITE);
        sound.setCapability(PointSound.ALLOW_INITIAL_GAIN_WRITE);
        sound.setCapability(PointSound.ALLOW_SOUND_DATA_WRITE);
        sound.setCapability(PointSound.ALLOW_SCHEDULING_BOUNDS_WRITE);
        sound.setCapability(PointSound.ALLOW_CONT_PLAY_WRITE);
        sound.setCapability(PointSound.ALLOW_RELEASE_WRITE);
        sound.setCapability(PointSound.ALLOW_DURATION_READ);
        sound.setCapability(PointSound.ALLOW_IS_PLAYING_READ);
        sound.setCapability(PointSound.ALLOW_POSITION_WRITE);
        sound.setCapability(PointSound.ALLOW_LOOP_WRITE);

	BoundingSphere soundBounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
        sound.setSchedulingBounds(soundBounds);
	objTrans.addChild(sound);

        /*
         * Spheres denoting aural attribute regions
         */
        TransformGroup objTransChild1 = new TransformGroup();
        TransformGroup objTransChild2 = new TransformGroup();
        objTransChild1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTransChild2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        Transform3D translate1 = new Transform3D();
        Transform3D translate2 = new Transform3D();
        Vector3f vector1 = new Vector3f( 2.0f, 0.0f, 0.0f);
        Vector3f vector2 = new Vector3f(-2.0f, 0.0f, 0.0f);
        translate1.setTranslation(vector1);
        translate2.setTranslation(vector2);
        objTransChild1.setTransform(translate1);
        objTransChild2.setTransform(translate2);
        Sphere sphere1 = new Sphere(0.42f);
        Sphere sphere2 = new Sphere(0.38f);

        Appearance app = new Appearance();
        app.setCapability( Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE );
        sphere1.setAppearance( app );
        sphere2.setAppearance( app );
        objTransChild1.addChild(sphere1);
        objTransChild2.addChild(sphere2);

        objTrans.addChild(objTransChild1);
        objTrans.addChild(objTransChild2);
        /**
         * Define Soundscapes/AuralAttributes
         */
        // First Aural Attributes
        Point2f[] distanceFilter = new Point2f[2];
        distanceFilter[0] = new Point2f(6.0f, 6000f);
        distanceFilter[1] = new Point2f(17.0f, 700f);
        AuralAttributes attributes = new AuralAttributes(1.0f, 2.0f, 0.3f,
               4.0f, 5, distanceFilter, 0.8f, 0.0f);
        attributes.setReverbDelay(90.0f) ;
        attributes.setReflectionCoefficient(0.999f) ;
        attributes.setReverbOrder(9) ;

        // SoundScape
	BoundingSphere bounds1 =
	    new BoundingSphere(new Point3d(2.0,0.0,0.0), 3.5);

        BoundingLeaf   sScapeBounds1 = new BoundingLeaf(bounds1);
        Soundscape soundScape = new Soundscape(null, attributes);
        soundScape.setApplicationBoundingLeaf(sScapeBounds1);
        /** addChild of BoundingLeaf as well**/
	objTrans.addChild(sScapeBounds1);

        AuralAttributes queryAttribs = new AuralAttributes();
        if (queryAttribs == null)
            System.out.println(" new AuralAttributes returned NULL");
        // else
        //  System.out.println(" new AuralAttributes returned " + queryAttribs);
        queryAttribs = soundScape.getAuralAttributes();
        if (queryAttribs == null)
            System.out.println("getAuralAttributes returned NULL");
        // else
        //     System.out.println("AuralAttributes for Soundscape 1:");
        float tmpFloat = queryAttribs.getAttributeGain();
        // System.out.println("    Gain = " + tmpFloat);
        tmpFloat = queryAttribs.getRolloff();
        // System.out.println("    Rolloff = " + tmpFloat);
        tmpFloat = queryAttribs.getReflectionCoefficient();
        // System.out.println("    Reflection Coeff = " + tmpFloat);
        tmpFloat = queryAttribs.getReverbDelay();
        // System.out.println("    Delay = " + tmpFloat);
        int tmpInt = queryAttribs.getReverbOrder();
        // System.out.println("    Order = " + tmpInt);
        int length = queryAttribs.getDistanceFilterLength();
        // System.out.println("    Filter length = " + length);
        Point2f[] tmpPoint = new Point2f[length];
        tmpPoint = new Point2f[length];
        for (int i=0; i< length; i++) 
            tmpPoint[i] = new Point2f();
        queryAttribs.getDistanceFilter(tmpPoint);
        // for (int i=0; i< length; i++) 
        //     System.out.println("    Distance Filter = (" + tmpPoint[i].x +
        //         ", " + tmpPoint[i].y + ")" );
        tmpFloat = queryAttribs.getFrequencyScaleFactor();
        // System.out.println("    Freq scalefactor = " + tmpFloat);
        tmpFloat = queryAttribs.getVelocityScaleFactor();
        // System.out.println("    Velocity scalefactor= " + tmpFloat);
	objTrans.addChild(soundScape);

        // System.out.println("SoundScape2**********************************");
        Soundscape soundScape2 = new Soundscape();
        distanceFilter = new Point2f[2];
        distanceFilter[0] = new Point2f(2.0f, 20000.0f);
        distanceFilter[1] = new Point2f(20.0f, 2000.0f);
        AuralAttributes attributes2 = new AuralAttributes();
        attributes2.setAttributeGain(1.2f);
        attributes2.setRolloff(2.2f);
        attributes2.setReverbDelay(1313.0f) ;
        attributes2.setReflectionCoefficient(1.0f) ;
        attributes2.setReverbOrder(15) ;
        distanceFilter[0] = new Point2f(5.0f, 15000.0f);
        distanceFilter[1] = new Point2f(15.0f, 500.0f);
        attributes2.setDistanceFilter(distanceFilter);
        attributes2.setFrequencyScaleFactor(0.8f);
        attributes2.setVelocityScaleFactor(0.0f);
	BoundingSphere bounds2 =
	    new BoundingSphere(new Point3d(-2.0,0.0,0.0), 0.38);
        BoundingLeaf   sScapeBounds2 = new BoundingLeaf(bounds2);
        soundScape2.setApplicationBoundingLeaf(sScapeBounds2);
        // set BoundingLeaf as a child of transform node 
	objTrans.addChild(sScapeBounds2);
        soundScape2.setAuralAttributes(attributes2);

        queryAttribs = soundScape2.getAuralAttributes();
        if (queryAttribs == null)
            System.out.println(" new AuralAttributes returned NULL");
        // else
        //  System.out.println(" new AuralAttributes returned " + queryAttribs);
        // System.out.println("AuralAttributes for Soundscape 2:");
        tmpFloat = queryAttribs.getAttributeGain();
        // System.out.println("    Gain = " + tmpFloat);
        tmpFloat = queryAttribs.getRolloff();
        // System.out.println("    Rolloff = " + tmpFloat);
        tmpFloat = queryAttribs.getReflectionCoefficient();
        // System.out.println("    Reflection Coeff = " + tmpFloat);
        tmpFloat = queryAttribs.getReverbDelay();
        // System.out.println("    Delay = " + tmpFloat);
        tmpInt = queryAttribs.getReverbOrder();
        // System.out.println("    Order = " + tmpInt);
        length = queryAttribs.getDistanceFilterLength();
        // System.out.println("    Filter length = " + length);
        tmpPoint = new Point2f[length];
        for (int i=0; i< length; i++) 
            tmpPoint[i] = new Point2f();
        queryAttribs.getDistanceFilter(tmpPoint);
        // for (int i=0; i< length; i++) 
        //     System.out.println("    Distance Filter = (" + tmpPoint[i].x +
        //           ", " + tmpPoint[i].y + ")" );
        tmpFloat = queryAttribs.getFrequencyScaleFactor();
        // System.out.println("    Freq scalefactor = " + tmpFloat);
        tmpFloat = queryAttribs.getVelocityScaleFactor();
        // System.out.println("    Velocity scalefactor= " + tmpFloat);
	objTrans.addChild(soundScape2);

        //
	// Create a new Behavior object that will play the sound
	//
        AudioBehaviorMoveOne player = new AudioBehaviorMoveOne(sound, 
                filename[0]);
	player.setSchedulingBounds(soundBounds);
	objTrans.addChild(player);

	return objRoot;
    }

    public MoveAppBoundingLeaf() {
    }

    public void init() {
        if (!filenamesSet) {
            // path is null if started from applet
	    if (path == null) {
	        path = getCodeBase().toString();
	    }
    
            /*
             * append given file name to given URL path
             */
            if (filenamesGiven > 0) {
                filename[0] = new String(path + "/" + filename[0]);
            }
            else {
                // fill in default file names if all three not given
                filename[0] = new String(path + "/techno_machine.au");
            }
            filenamesSet = true;
        }

	setLayout(new BorderLayout());
        GraphicsConfiguration config =
           SimpleUniverse.getPreferredConfiguration();

        Canvas3D c = new Canvas3D(config);
	add("Center", c);

        /*
         * Change filenames into URLs
         */
        String substr = filename[0].substring(0,4);
        try {
            url[0] = new URL(filename[0]);
        }
        catch (Exception e) {
                return;
        }
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
    }

    public void destroy() {
	u.cleanup();
    }

    //
    // The following allows AuralAttributes to be run as an application
    // as well as an applet
    //
    public static void main(String[] args) {
        if (args.length > 0) {
            if ( (args[0].startsWith("file"+File.pathSeparator)) ||
                 (args[0].startsWith("http"+File.pathSeparator))   ) {
                path = args[0];
            }
            else {
                path = "file:" + args[0];
            }
        }
        else {
            path = "file:.";
        } 
        
        if (args.length > 1) {
            filename[0] = args[1];
            if (filename[0] != null) {
                filenamesGiven++ ;
            }
        }
 
	new MainFrame(new MoveAppBoundingLeaf(), 256, 256);
    }
}
