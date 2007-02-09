/*
 * $RCSfile$
 *
 * Copyright (c) 2007 Sun Microsystems, Inc. All rights reserved.
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

import javax.media.j3d.*;
import javax.vecmath.*;
import java.net.URL;
import java.util.Enumeration;

// User defined audio behavior class
public class SimpleSoundsBehavior extends Behavior {
	WakeupOnElapsedTime  wt;
	WakeupOnBehaviorPost wp;
        BackgroundSound      sound1 = new BackgroundSound();
        PointSound           sound2 = new PointSound();
        PointSound           sound3 = new PointSound();
        static int           WAKEUP_SOUND = 0;
        int                  soundIndex = 0;
        URL                  URLName1;
        URL                  URLName2;
        URL                  URLName3;
        BoundingSphere       bounds;

	// Override Behavior's initialize method to setup wakeup criteria
	public void initialize() {
            MediaContainer sample1  = new MediaContainer();
            MediaContainer sample2  = new MediaContainer();
            MediaContainer sample3  = new MediaContainer();
            sample1.setCapability(MediaContainer.ALLOW_URL_WRITE);
            sample1.setCapability(MediaContainer.ALLOW_URL_READ);
            sample1.setURLObject(URLName1);
            //sample1.setCacheEnable(false);
            sound1.setLoop(0);
            sound1.setContinuousEnable(false);
            sound1.setReleaseEnable(false);
            sound1.setSoundData(sample1);
            sound1.setInitialGain(0.7f);
            sample2.setCapability(MediaContainer.ALLOW_URL_WRITE);
            sample2.setCapability(MediaContainer.ALLOW_URL_READ);
            sample2.setURLObject(URLName2);
            sound2.setLoop(Sound.INFINITE_LOOPS);
            sound2.setContinuousEnable(false);
            sound2.setReleaseEnable(false);
            sound2.setSoundData(sample2);
            sound2.setInitialGain(2.0f);
            Point3f sound2Pos = new Point3f(-30.0f, 0.0f, 0.0f);
            sound2.setPosition(sound2Pos);
            sample3.setCapability(MediaContainer.ALLOW_URL_WRITE);
            sample3.setCapability(MediaContainer.ALLOW_URL_READ);
            sample3.setURLObject(URLName3);
            sound3.setContinuousEnable(false);
            sound3.setReleaseEnable(false);
            sound3.setSoundData(sample3);
            sound3.setInitialGain(4.0f);
            Point3f sound3Pos = new Point3f(30.0f, 0.0f, 0.0f);
            sound3.setPosition(sound3Pos);

	    wt = new WakeupOnElapsedTime(2000);
	    WakeupOnElapsedTime wp = new WakeupOnElapsedTime(5000);
	    wakeupOn(wp); 
	}

	// Override Behavior's stimulus method to handle the event
	public void processStimulus(Enumeration criteria) {

            switch (soundIndex)
            {
                // Active
                case 0:
                    // System.out.println("****Enable First Sound");
                    sound1.setEnable(true);
	            wakeupOn(wt);
                    break;
                case 1:
                    // System.out.println("********Enable Second Sound");
                    sound2.setEnable(true);
	            wakeupOn(wt);
                    break;
                case 2:
                case 4:
                case 6:
                case 8:
                case 10:
                    // System.out.println("************Enable Third Sound");
                    sound3.setEnable(true);
	            wakeupOn(wt);
                    break;
                case 3:
                case 5:
                case 7:
                case 9:
                    // System.out.println("************Disable Third Sound");
                    sound3.setEnable(false);
	            wakeupOn(wt);
                    break;

                case 11:
                    // System.out.println("********Disable Second Sound");
                    sound2.setEnable(false) ;
	            wakeupOn(wt);
                    break;
                case 12:
                    // System.out.println("****Disable First Sound");
                    sound1.setEnable(false) ;
		    System.out.println("SimpleSounds: test complete");
                    wt = new WakeupOnElapsedTime(400000);
	            wakeupOn(wt);
                    break;

                default:
                    break;  
            }
            soundIndex++;
	}

        //
	// Constructor for rotation behavior.  
        // Parameters: sound node
        //             sample file name
        //             sound node's bounds
        //
	public SimpleSoundsBehavior(BackgroundSound sound1, 
	                            PointSound sound2, 
	                            PointSound sound3, 
                                    URL urlName1,
                                    URL urlName2,
                                    URL urlName3,
                                    BoundingSphere soundBounds) {
            this.sound1 = sound1;
            this.sound2 = sound2;
            this.sound3 = sound3;
            this.URLName1 = urlName1;
            this.URLName2 = urlName2;
            this.URLName3 = urlName3;
            this.bounds = (BoundingSphere)soundBounds.clone();
	}
}
