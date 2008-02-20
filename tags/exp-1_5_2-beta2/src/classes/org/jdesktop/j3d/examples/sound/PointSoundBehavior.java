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

import java.net.URL;
import java.util.Enumeration;
import javax.media.j3d.*;
import javax.vecmath.*;
import org.jdesktop.j3d.examples.Resources;

public class PointSoundBehavior extends Behavior {
    
    private WakeupCondition condition = new WakeupOnElapsedFrames(0);
 
    /** Creates a new instance of PointSoundBehavior */
    public PointSoundBehavior(PointSound ps, URL url, Point3f pos) {
                
        Bounds b = new BoundingSphere(new Point3d(), 40);
        ps.setSoundData(new MediaContainer(url));
        ps.setPosition(pos);
        float distanceAtZero = 30;
        ps.setDistanceGain(new float []{0, distanceAtZero}, new float []{1, 0});
        ps.setEnable(true);
        ps.setPause(false);
        ps.setContinuousEnable(true);
        ps.setSchedulingBounds(b);
        ps.setLoop(-1);        
    
    }

    public void initialize() {
        
        wakeupOn(condition);
    }
    
    public void processStimulus(Enumeration enumeration) {        
        wakeupOn(condition);
    }
    
}
