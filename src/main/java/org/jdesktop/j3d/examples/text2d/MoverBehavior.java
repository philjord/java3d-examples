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

package org.jdesktop.j3d.examples.text2d;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.Bounds;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.WakeupCondition;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnAWTEvent;
import org.jogamp.java3d.WakeupOr;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3f;

// Mover behavior class - used to allow viewer to move using arrow keys
class MoverBehavior extends Behavior
{
    WakeupOnAWTEvent w1 = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
    WakeupCriterion[] w2 = {w1};
    WakeupCondition w = new WakeupOr(w2);
    TransformGroup viewTransformGroup;
    double rotation = 0.0;		// holds current rotation radians
    
    @Override
	public void initialize() {
	// Establish initial wakeup criteria
	wakeupOn(w);
    }


    /**
     *  Override Behavior's stimulus method to handle the event.
     */
    @Override
	public void processStimulus(Iterator<WakeupCriterion> criteria) {
	WakeupOnAWTEvent ev;
	WakeupCriterion genericEvt;
	AWTEvent[] events;
   
	while (criteria.hasNext()) {
	    genericEvt = criteria.next();
	    if (genericEvt instanceof WakeupOnAWTEvent) {
		ev = (WakeupOnAWTEvent) genericEvt;
		events = ev.getAWTEvent();
		processManualEvent(events);
	    }
	}
	// Set wakeup criteria for next time
	wakeupOn(w);
    }

    
    /**
     *  Process a keyboard event to move or rotate the viewer.
     */
    void processManualEvent(AWTEvent[] events) {

	for (int i = 0; i < events.length; ++i) {
	    if (events[i] instanceof KeyEvent) {
		KeyEvent event = (KeyEvent)events[i];
		if (event.getKeyCode() == KeyEvent.VK_EQUALS) {
		    continue;
		}
		Transform3D t = new Transform3D();
		viewTransformGroup.getTransform(t);
		Vector3f viewDir = new Vector3f(0f, 0f, -1f);
		Vector3f translation = new Vector3f();
		t.get(translation);
		t.transform(viewDir);
		if (event.getKeyCode() == KeyEvent.VK_UP) {
		    translation.x += viewDir.x;
		    translation.y += viewDir.y;
		    translation.z += viewDir.z;
		}
		else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
		    translation.x -= viewDir.x;
		    translation.y -= viewDir.y;
		    translation.z -= viewDir.z;
		}
		else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
		    rotation += -.1;
		}
		else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
		    rotation += .1;
		}
		t.rotY(rotation);
		t.setTranslation(translation);
		viewTransformGroup.setTransform(t);
	    }
	}
    }

  
    /**
     *  Constructor 
     */
    public MoverBehavior(TransformGroup trans) {
	viewTransformGroup = trans;
	Bounds bound = new BoundingSphere(new Point3d(0.0,0.0,0.0),10000.0);
	this.setSchedulingBounds(bound);
    }
}



