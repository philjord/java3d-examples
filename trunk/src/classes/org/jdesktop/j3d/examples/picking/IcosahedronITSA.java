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

class IcosahedronITSA extends IndexedTriangleStripArray {

  private static final int[] sVertCnt = {
    3, 11, 5, 4, 5, 4
  };

  IcosahedronITSA() {
    super(12, GeometryArray.COORDINATES | GeometryArray.COLOR_3, 32, sVertCnt);  
    
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

    int pntsIndex[] = new int[32];
    int clrsIndex[] = new int[32];
    
    pntsIndex[0] = 4;
    clrsIndex[0] = 4;
    pntsIndex[1] = 5;
    clrsIndex[1] = 5;
    pntsIndex[2] = 11;
    clrsIndex[2] = 11;
    
    pntsIndex[3] = 11;
    clrsIndex[3] = 11;
    pntsIndex[4] = 5;
    clrsIndex[4] = 5; 
    pntsIndex[5] = 7;
    clrsIndex[5] = 7;

    pntsIndex[6] = 6;
    clrsIndex[6] = 6;

    pntsIndex[7] = 0;
    clrsIndex[7] = 0;

    pntsIndex[8] = 2;
    clrsIndex[8] = 2;

    pntsIndex[9] = 1;
    clrsIndex[9] = 1;

    pntsIndex[10] = 8;
    clrsIndex[10] = 8;

    pntsIndex[11] = 9;
    clrsIndex[11] = 9;

    pntsIndex[12] = 4;
    clrsIndex[12] = 4;

    pntsIndex[13] = 11;
    clrsIndex[13] = 11;
    
    pntsIndex[14] = 2;
    clrsIndex[14] = 2;
    pntsIndex[15] = 6;
    clrsIndex[15] = 6; 
    pntsIndex[16] = 3;
    clrsIndex[16] = 3;

    pntsIndex[17] = 5;
    clrsIndex[17] = 5;

    pntsIndex[18] = 4;
    clrsIndex[18] = 4;

    pntsIndex[19] = 4;
    clrsIndex[19] = 4;
    pntsIndex[20] = 8;
    clrsIndex[20] = 8; 
    pntsIndex[21] = 3;
    clrsIndex[21] = 3;

    pntsIndex[22] = 2;
    clrsIndex[22] = 2;

    pntsIndex[23] = 0;
    clrsIndex[23] = 0;
    pntsIndex[24] = 1;
    clrsIndex[24] = 1; 
    pntsIndex[25] = 10;
    clrsIndex[25] = 10;

    pntsIndex[26] = 9;
    clrsIndex[26] = 9;

    pntsIndex[27] = 11;
    clrsIndex[27] = 11;

    pntsIndex[28] = 0;
    clrsIndex[28] = 0;
    pntsIndex[29] = 10;
    clrsIndex[29] = 10; 
    pntsIndex[30] = 7;
    clrsIndex[30] = 7;

    pntsIndex[31] = 11;
    clrsIndex[31] = 11;
    
    setCoordinates(0, verts);
    setCoordinateIndices(0, pntsIndex);
    setColors(0, colors);
    setColorIndices(0, clrsIndex);
  }
}
