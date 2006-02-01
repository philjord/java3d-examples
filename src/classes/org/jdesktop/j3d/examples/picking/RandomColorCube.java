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

package org.jdesktop.j3d.examples.picking;

import javax.media.j3d.*;
import javax.vecmath.*;

class RandomColorCube extends QuadArray {
  RandomColorCube() {
    super(24, GeometryArray.COORDINATES | GeometryArray.COLOR_3);

    Point3f verts[] = new Point3f[8];
    Color3f colors[] = new Color3f[3];
	
    verts[0] = new Point3f(0.5f, 0.5f,  0.5f);
    verts[1] = new Point3f(-0.5f, 0.5f, 0.5f);
    verts[2] = new Point3f(-0.5f,-0.5f, 0.5f);
    verts[3] = new Point3f( 0.5f,-0.5f, 0.5f);
    verts[4] = new Point3f( 0.5f, 0.5f,-0.5f);
    verts[5] = new Point3f( -0.5f, 0.5f,-0.5f);
    verts[6] = new Point3f( -0.5f,-0.5f,-0.5f);
    verts[7] = new Point3f( 0.5f,-0.5f,-0.5f);
	
    colors[0] = new Color3f(1.0f, 0.0f, 0.0f);
    colors[1] = new Color3f(0.0f, 1.0f, 0.0f);
    colors[2] = new Color3f(0.0f, 0.0f, 1.0f);

    Point3f pnts[] = new Point3f[24];
    Color3f clrs[] = new Color3f[24];

    pnts[0] = verts[0];
    clrs[0] = colors[(int)(Math.random()*3.0)];
    pnts[1] = verts[3];
    clrs[1] = colors[(int)(Math.random()*3.0)];
    pnts[2] = verts[7];
    clrs[2] = colors[(int)(Math.random()*3.0)];
    pnts[3] = verts[4];
    clrs[3] = colors[(int)(Math.random()*3.0)];

    pnts[4] = verts[1];
    clrs[4] = colors[(int)(Math.random()*3.0)];
    pnts[5] = verts[5];
    clrs[5] = colors[(int)(Math.random()*3.0)];
    pnts[6] = verts[6];
    clrs[6] = colors[(int)(Math.random()*3.0)];
    pnts[7] = verts[2];
    clrs[7] = colors[(int)(Math.random()*3.0)];

    pnts[8]  = verts[0];
    clrs[8]  = colors[(int)(Math.random()*3.0)];
    pnts[9]  = verts[4];
    clrs[9]  = colors[(int)(Math.random()*3.0)];
    pnts[10] = verts[5];
    clrs[10] = colors[(int)(Math.random()*3.0)];
    pnts[11] = verts[1];
    clrs[11] = colors[(int)(Math.random()*3.0)];

    pnts[12] = verts[3];
    clrs[12] = colors[(int)(Math.random()*3.0)];
    pnts[13] = verts[2];
    clrs[13] = colors[(int)(Math.random()*3.0)];
    pnts[14] = verts[6];
    clrs[14] = colors[(int)(Math.random()*3.0)];
    pnts[15] = verts[7];
    clrs[15] = colors[(int)(Math.random()*3.0)];

    pnts[16] = verts[0];
    clrs[16] = colors[(int)(Math.random()*3.0)];
    pnts[17] = verts[1];
    clrs[17] = colors[(int)(Math.random()*3.0)];
    pnts[18] = verts[2];
    clrs[18] = colors[(int)(Math.random()*3.0)];
    pnts[19] = verts[3];
    clrs[19] = colors[(int)(Math.random()*3.0)];

    pnts[20] = verts[7];
    clrs[20] = colors[(int)(Math.random()*3.0)];
    pnts[21] = verts[6];
    clrs[21] = colors[(int)(Math.random()*3.0)];
    pnts[22] = verts[5];
    clrs[22] = colors[(int)(Math.random()*3.0)];
    pnts[23] = verts[4];
    clrs[23] = colors[(int)(Math.random()*3.0)];


    setCoordinates(0, pnts);
    setColors(0, clrs);
  }
}
