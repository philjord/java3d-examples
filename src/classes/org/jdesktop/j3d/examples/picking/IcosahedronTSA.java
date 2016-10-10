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

import org.jogamp.java3d.GeometryArray;
import org.jogamp.java3d.TriangleStripArray;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3f;

class IcosahedronTSA extends TriangleStripArray {
  private static final int[] sVertCnt = {
    3, 11, 5, 4, 5, 4
  };

  IcosahedronTSA() {
    super(32, GeometryArray.COORDINATES | GeometryArray.COLOR_3, sVertCnt);  
    
    Point3f verts[] = new Point3f[12];
    Color3f colors[] = new Color3f[12];
    
    verts[0] = new Point3f(0.0f,     1.4f,      0.8652f);
    verts[1] = new Point3f(0.0f,     1.4f,     -0.8652f);
    verts[2] = new Point3f(1.4f,     0.8652f,    0.0f);
    verts[3] = new Point3f(1.4f,    -0.8652f,    0.0f);
    verts[4] = new Point3f(0.0f,    -1.4f,     -0.8652f);
    verts[5] = new Point3f(0.0f,    -1.4f,      0.8652f);
    verts[6] = new Point3f(0.8652f,   0.0f,      1.4f);
    verts[7] = new Point3f(-0.8652f,  0.0f,      1.4f);
    verts[8] = new Point3f(0.8652f,   0.0f,     -1.4f);
    verts[9] = new Point3f(-0.8652f,  0.0f,     -1.4f);
    verts[10] = new Point3f(-1.4f,   0.8652f,    0.0f);
    verts[11] = new Point3f(-1.4f,  -0.8652f,    0.0f);

    colors[0] = new Color3f(1.0f, 0.0f, 0.0f);
    colors[1] = new Color3f(0.0f, 1.0f, 0.0f);
    colors[2] = new Color3f(0.0f, 0.0f, 1.0f);
    colors[3] = new Color3f(1.0f, 1.0f, 0.0f);
    colors[4] = new Color3f(0.0f, 1.0f, 1.0f);
    colors[5] = new Color3f(1.0f, 0.0f, 1.0f);
    colors[6] = new Color3f(0.0f, 0.5f, 0.0f);
    colors[7] = new Color3f(0.0f, 0.0f, 0.5f);
    colors[8] = new Color3f(0.5f, 0.5f, 0.0f);
    colors[9] = new Color3f(0.0f, 0.5f, 0.5f);
    colors[10] = new Color3f(0.5f, 0.0f, 0.5f);
    colors[11] = new Color3f(0.5f, 0.5f, 0.5f);
    
    Point3f pnts[] = new Point3f[32];
    Color3f clrs[] = new Color3f[32];
    
    pnts[0] = verts[4];
    clrs[0] = colors[4];
    pnts[1] = verts[5];
    clrs[1] = colors[5];
    pnts[2] = verts[11];
    clrs[2] = colors[11];
    
    pnts[3] = verts[11];
    clrs[3] = colors[11];
    pnts[4] = verts[5];
    clrs[4] = colors[5]; 
    pnts[5] = verts[7];
    clrs[5] = colors[7];

    pnts[6] = verts[6];
    clrs[6] = colors[6];

    pnts[7] = verts[0];
    clrs[7] = colors[0];

    pnts[8] = verts[2];
    clrs[8] = colors[2];

    pnts[9] = verts[1];
    clrs[9] = colors[1];

    pnts[10] = verts[8];
    clrs[10] = colors[8];

    pnts[11] = verts[9];
    clrs[11] = colors[9];

    pnts[12] = verts[4];
    clrs[12] = colors[4];

    pnts[13] = verts[11];
    clrs[13] = colors[11];
    
    pnts[14] = verts[2];
    clrs[14] = colors[2];
    pnts[15] = verts[6];
    clrs[15] = colors[6]; 
    pnts[16] = verts[3];
    clrs[16] = colors[3];

    pnts[17] = verts[5];
    clrs[17] = colors[5];

    pnts[18] = verts[4];
    clrs[18] = colors[4];

    pnts[19] = verts[4];
    clrs[19] = colors[4];
    pnts[20] = verts[8];
    clrs[20] = colors[8]; 
    pnts[21] = verts[3];
    clrs[21] = colors[3];

    pnts[22] = verts[2];
    clrs[22] = colors[2];

    pnts[23] = verts[0];
    clrs[23] = colors[0];
    pnts[24] = verts[1];
    clrs[24] = colors[1]; 
    pnts[25] = verts[10];
    clrs[25] = colors[10];

    pnts[26] = verts[9];
    clrs[26] = colors[9];

    pnts[27] = verts[11];
    clrs[27] = colors[11];

    pnts[28] = verts[0];
    clrs[28] = colors[0];
    pnts[29] = verts[10];
    clrs[29] = colors[10]; 
    pnts[30] = verts[7];
    clrs[30] = colors[7];

    pnts[31] = verts[11];
    clrs[31] = colors[11];

    setCoordinates(0, pnts);
    setColors(0, clrs);
  }
}
