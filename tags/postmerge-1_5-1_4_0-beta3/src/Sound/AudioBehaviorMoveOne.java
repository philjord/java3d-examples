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

import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.Enumeration;

// User defined audio behavior class
public class AudioBehaviorMoveOne extends Behavior {
	WakeupOnElapsedTime  wt;
	WakeupOnBehaviorPost wp;
        PointSound           psound = new PointSound();
        static int           WAKEUP_SOUND = 0;
        long                 dur;
        // long                 time;
        // long                 lastTime = -1;
        boolean              first_loop = true;
        String               fileName;
        
	// Override Behavior's initialize method to setup wakeup criteria
	public void initialize() {
            MediaContainer sample  = new MediaContainer();
            sample.setCapability(MediaContainer.ALLOW_URL_WRITE);
            sample.setCapability(MediaContainer.ALLOW_URL_READ);
            sample.setURLString(fileName);
            psound.setSoundData(sample);
            // exaggerate the sound position now that viewPlatform
            // tranform is taken into account
            Point3f soundPos = new Point3f(-20.0f, 0.0f, 0.0f);
            psound.setPosition(soundPos);
	    WakeupOnElapsedTime wp = new WakeupOnElapsedTime(5000);
	    wakeupOn(wp); 
	}

	// Override Behavior's stimulus method to handle the event
	public void processStimulus(Enumeration criteria) {
            // time = System.currentTimeMillis();
            if (first_loop) {
                first_loop = false;
                dur = psound.getDuration();
                if (dur == Sound.DURATION_UNKNOWN)
                     dur = 2000;  // Force iterations every 2 seconds
                // System.out.println("     sound duration time " + dur);
	        wt = new WakeupOnElapsedTime(dur);
            }
            else {
                // System.out.println("  time between setEnables calls "+
                // (time - lastTime));
                psound.setEnable(false) ; 
            }
            psound.setEnable(true);
            // lastTime = time;
	    wakeupOn(wt);
	}

        //
	// Constructor for rotation behavior.  Parameter: front and back Sound nodes
        //
	public AudioBehaviorMoveOne(PointSound psound, String filename) {
            this.psound = psound;
            this.fileName = filename;
	}
}
