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

package org.jdesktop.j3d.examples.oriented_shape3d;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.behaviors.mouse.*;

/**
 * MouseRotateY is a Java3D behavior object that lets users control the 
 * rotation of an object via a mouse.
 * <p>
 * To use this utility, first create a transform group that this 
 * rotate behavior will operate on. Then,
 *<blockquote><pre>
 * 
 *   MouseRotateY behavior = new MouseRotateY();
 *   behavior.setTransformGroup(objTrans);
 *   objTrans.addChild(behavior);
 *   behavior.setSchedulingBounds(bounds);
 *
 *</pre></blockquote>
 * The above code will add the rotate behavior to the transform
 * group. The user can rotate any object attached to the objTrans.
 */

public class MouseRotateY extends MouseBehavior {
  double  y_angle;
  double  y_factor;

  /**
   * Creates a rotate behavior given the transform group.
   * @param transformGroup The transformGroup to operate on.
   */
  public MouseRotateY(TransformGroup transformGroup) {
    super(transformGroup);
  }

  /**
   * Creates a default mouse rotate behavior.
   **/
  public MouseRotateY() {
      super(0);
   }

  /**
   * Creates a rotate behavior.
   * Note that this behavior still needs a transform
   * group to work on (use setTransformGroup(tg)) and
   * the transform group must add this behavior.
   * @param flags interesting flags (wakeup conditions).
   */
  public MouseRotateY(int flags) {
      super(flags);
   }

  public void initialize() {
    super.initialize();
    y_angle = 0;
    y_factor = .03;
    if ((flags & INVERT_INPUT) == INVERT_INPUT) {
       invert = true;
       y_factor *= -1;
    }
  }

  public double getYFactor() {
    return y_factor;
  }
  
  public void setFactor( double factor) {
    y_factor = factor;
    
  }
  

  public void processStimulus (Enumeration criteria) {
      WakeupCriterion wakeup;
      AWTEvent[] event;
      int id;
      int  dx;

      while (criteria.hasMoreElements()) {
         wakeup = (WakeupCriterion) criteria.nextElement();
         if (wakeup instanceof WakeupOnAWTEvent) {
            event = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
            for (int i=0; i<event.length; i++) { 
	      processMouseEvent((MouseEvent) event[i]);

	      if (((buttonPress)&&((flags & MANUAL_WAKEUP) == 0)) ||
		  ((wakeUp)&&((flags & MANUAL_WAKEUP) != 0))){
		
		id = event[i].getID();
		if ((id == MouseEvent.MOUSE_DRAGGED) && 
		    !((MouseEvent)event[i]).isMetaDown() && 
		    !((MouseEvent)event[i]).isAltDown()){
		  
                  x = ((MouseEvent)event[i]).getX();

                  dx = x - x_last;

		  if (!reset){	    
		    y_angle = dx * y_factor;
		    
		    transformY.rotY(y_angle);
		    
		    transformGroup.getTransform(currXform);
		    
		    //Vector3d translation = new Vector3d();
		    //Matrix3f rotation = new Matrix3f();
		    Matrix4d mat = new Matrix4d();
		    
		    // Remember old matrix
		    currXform.get(mat);
		    
		    // Translate to origin
		    currXform.setTranslation(new Vector3d(0.0,0.0,0.0));
		    if (invert) {
			currXform.mul(currXform, transformX);
			currXform.mul(currXform, transformY);
		    } else {
			currXform.mul(transformX, currXform);
			currXform.mul(transformY, currXform);
		    }
		    
		    // Set old translation back
		    Vector3d translation = new 
		      Vector3d(mat.m03, mat.m13, mat.m23);
		    currXform.setTranslation(translation);
		    
		    // Update xform
		    transformGroup.setTransform(currXform);
		  }
		  else {
		    reset = false;
		  }

                  x_last = x;
               }
               else if (id == MouseEvent.MOUSE_PRESSED) {
                  x_last = ((MouseEvent)event[i]).getX();
               }
	      }
	    }
         }
      }

      wakeupOn (mouseCriterion);
      
   }
}
