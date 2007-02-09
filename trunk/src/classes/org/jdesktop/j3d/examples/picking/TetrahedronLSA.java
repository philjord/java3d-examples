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

package org.jdesktop.j3d.examples.picking;

import javax.media.j3d.*;
import javax.vecmath.*;

class TetrahedronLSA extends LineStripArray {
  private static final int[] lineLengths = {
       4, 4
  };
  TetrahedronLSA() {
    super(8, GeometryArray.COORDINATES | GeometryArray.COLOR_3, lineLengths);  
    
    Point3f verts[] = new Point3f[4];
    Color3f colors[] = new Color3f[4];
    
    verts[0] = new Point3f(1.0f,1.0f,1.0f);
    verts[1] = new Point3f(1.0f,-1.0f,-1.0f);
    verts[2] = new Point3f(-1.0f,-1.0f,1.0f);
    verts[3] = new Point3f(-1.0f,1.0f,-1.0f);

    colors[0] = new Color3f(1.0f, 0.0f, 0.0f);
    colors[1] = new Color3f(0.0f, 1.0f, 0.0f);
    colors[2] = new Color3f(0.0f, 0.0f, 1.0f);
    colors[3] = new Color3f(1.0f, 1.0f, 0.0f);
    
    Point3f pnts[] = new Point3f[8];
    Color3f clrs[] = new Color3f[8];

    pnts[0] = verts[0];
    clrs[0] = colors[0];
    pnts[1] = verts[1];
    clrs[1] = colors[1];
    pnts[2] = verts[3];
    clrs[2] = colors[3];
    pnts[3] = verts[2];
    clrs[3] = colors[2];

    pnts[4] = verts[1];
    clrs[4] = colors[1];
    pnts[5] = verts[2];
    clrs[5] = colors[2];
    pnts[6] = verts[0];
    clrs[6] = colors[0];    
    pnts[7] = verts[3];
    clrs[7] = colors[3];
    
    setCoordinates(0, pnts);
    setColors(0, clrs);
  }
}
