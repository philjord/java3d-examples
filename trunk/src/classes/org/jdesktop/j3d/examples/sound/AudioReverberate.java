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

import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.Enumeration;

/*
 * Pick the JavaSound reverb type that matches the input parameters
 * as best as possible.
 *
 *  Hae Reverb Types     Size      Persistance        Delay
 *  ================  -----------  ------------    -----------
 *  1 None (dry)
 *  2 "Closet"         very small  very short <.5   fast smooth
 *  3 "Garage"         med. large    medium 1.0       medium
 *  4 "Acoustic Lab"   med. small     short .5       med. fast
 *  5 "Cavern"           large       long >2.0       very slow
 *  6 "Dungeon"          medium    med. long 1.5     med. slow
 *
 *
 * Order is NOT controllable, nor does it have a natural parallel.
 * For this reason Order and Reflection are tied together as to
 * affect 'Decay Speed'.  This speed paired with the size of the
 * space implied by the Delay parameter determine the JavaSound
 * Reverb type that is set:
 *
 *                      |  Short:               Long:
 *                Speed |    Coeff <= 0.9         Coeff > 0.9
 *  Size                |    Order <= 8           Order > 8
 * ---------------------------------------------------------------
 *  small (<100ms)      |    2 "Closet"           4 "Acoustic Lab"
 *  medium  (<500ms)    |    3 "Garage"           6 "Dungeon"
 *  large   (>500ms)    |    6 "Dungeon"          5 "Cavern"
 */
// User defined audio behavior class
public class AudioReverberate extends Behavior {
	WakeupOnElapsedTime  wt;
	WakeupOnBehaviorPost wp;
        PointSound           psound = new PointSound();
        AuralAttributes           sScape = null;
        static int           WAKEUP_SOUND = 0;
        long                 dur;
        long                 time;
        boolean              firstTime = true;
        String               fileName;
        int                  lCount = 0;
        int                  loopCount = 0;
        
	// Override Behavior's initialize method to setup wakeup criteria
	public void initialize() {
            MediaContainer sample  = new MediaContainer();
            sample.setCacheEnable(true);
            sample.setURLString(fileName);
            psound.setSoundData(sample);
            Point3f soundPos = new Point3f(-23.0f, 0.0f, 0.0f);
            psound.setPosition(soundPos);
            psound.setLoop(3);
            firstTime = true;
            System.out.println("Reverb Name  Size  Reflect  Order  Delay ");
            System.out.println("-----------  ----  -------  -----  ----- ");
	    WakeupOnElapsedTime wp = new WakeupOnElapsedTime(5000);
	    wakeupOn(wp); 
	}

	// Override Behavior's stimulus method to handle the event
	public void processStimulus(Enumeration criteria) {
            // time = System.currentTimeMillis();
            if (firstTime)  {
	        wt = new WakeupOnElapsedTime(10000);
                firstTime = false;
            }
            else
                psound.setEnable(false) ; 

            if (++lCount > 6)
                lCount = 1;

            if (lCount == 1) {
                sScape.setReverbDelay(10.0f) ; 
                sScape.setReflectionCoefficient(0.5f) ; 
                sScape.setReverbOrder(5) ; 
                System.out.println("Closet        sm     0.5      5     10.0 ");
            }
            else if (lCount == 2) {
                sScape.setReverbDelay(10.0f) ; 
                sScape.setReflectionCoefficient(0.999f) ; 
                sScape.setReverbOrder(9) ; 
                System.out.println("Acoustic Lab  sm    0.999     9     10.0 ");
            }
            else if (lCount == 3) {
                sScape.setReverbDelay(200.0f) ; 
                sScape.setReflectionCoefficient(0.4f) ; 
                sScape.setReverbOrder(3) ;
                System.out.println("Garage        med    0.4      3    200.0 ");
            }
            else if (lCount == 4) {
                sScape.setReverbDelay(200.0f) ; 
                sScape.setReflectionCoefficient(0.99f) ; 
                sScape.setReverbOrder(10) ; 
                System.out.println("Dungeon       med   0.99     10    200.0 ");
            }
            else if (lCount == 5) {
                sScape.setReverbDelay(600.0f) ; 
                sScape.setReflectionCoefficient(0.33f) ; 
                sScape.setReverbOrder(7) ; 
                System.out.println("Dungeon       lrg   0.33      7    600.0 ");
            }
            else if (lCount == 6) {
                sScape.setReverbDelay(600.0f) ; 
                sScape.setReflectionCoefficient(1.0f) ; 
                sScape.setReverbOrder(20) ; 
                System.out.println("Cavern        lrg    1.0     20    600.0 ");
            }
            psound.setEnable(true); 
	    wakeupOn(wt);
	}

        //
	// Constructor for rotation behavior.  Parameter: front and back Sound nodes
        //
	public AudioReverberate(PointSound psound, String filename,
                   AuralAttributes sscape) {
            this.psound = psound;
            this.fileName = filename;
            this.sScape = sscape;
	}
}
