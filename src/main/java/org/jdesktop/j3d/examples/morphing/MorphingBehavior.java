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

package org.jdesktop.j3d.examples.morphing;

import java.util.Enumeration;

import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.Morph;
import org.jogamp.java3d.WakeupOnElapsedFrames;

// User-defined morphing behavior class
public class MorphingBehavior extends Behavior {

    Alpha alpha;
    Morph morph;
    double weights[];

    WakeupOnElapsedFrames w = new WakeupOnElapsedFrames(0);

    // Override Behavior's initialize method to setup wakeup criteria
    public void initialize() {
	alpha.setStartTime(System.currentTimeMillis());

	// Establish initial wakeup criteria
	wakeupOn(w);
    }

    // Override Behavior's stimulus method to handle the event
    public void processStimulus(Enumeration criteria) {

	// NOTE: This assumes 3 objects.  It should be generalized to
	// "n" objects.

	double val = alpha.value();
	if (val < 0.5) {
	    double a = val * 2.0;
	    weights[0] = 1.0 - a;
	    weights[1] = a;
	    weights[2] = 0.0;
	}
	else {
	    double a = (val - 0.5) * 2.0;
	    weights[0] = 0.0;
	    weights[1] = 1.0f - a;
	    weights[2] = a;
	}

	morph.setWeights(weights);

	// Set wakeup criteria for next time
	wakeupOn(w);
    }

    public MorphingBehavior(Alpha a, Morph m) {
	alpha = a;
	morph = m;
	weights = morph.getWeights();
    }
}
