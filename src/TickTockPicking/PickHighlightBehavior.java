/*
 * $RCSfile$
 *
 * Copyright (c) 2004 Sun Microsystems, Inc. All rights reserved.
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
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;
import java.util.*;
import java.awt.*;
import java.awt.Event;
import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import javax.vecmath.*;

public class PickHighlightBehavior extends PickMouseBehavior {
  Appearance savedAppearance = null;
  Shape3D oldShape = null;
  Appearance highlightAppearance;

  public PickHighlightBehavior(Canvas3D canvas, BranchGroup root,
			       Bounds bounds) {
      super(canvas, root, bounds);
      this.setSchedulingBounds(bounds);
      root.addChild(this);
      Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
      Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
      Color3f highlightColor = new Color3f(0.0f, 1.0f, 0.0f);
      Material highlightMaterial = new Material(highlightColor, black,
						highlightColor, white,
						80.0f);
      highlightAppearance = new Appearance();
      highlightAppearance.setMaterial(new Material(highlightColor, black,
						   highlightColor, white,
						   80.0f));

      pickCanvas.setMode(PickTool.BOUNDS);
  }

    public void updateScene(int xpos, int ypos) {
	PickResult pickResult = null;
	Shape3D shape = null;

	pickCanvas.setShapeLocation(xpos, ypos);

	pickResult = pickCanvas.pickClosest();
	if (pickResult != null) {
	    shape = (Shape3D) pickResult.getNode(PickResult.SHAPE3D);
	}

	if (oldShape != null){
	    oldShape.setAppearance(savedAppearance);
	}
	if (shape != null) {
	    savedAppearance = shape.getAppearance();
	    oldShape = shape;
	    shape.setAppearance(highlightAppearance);
	}
    }
}
