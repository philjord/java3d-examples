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
import javax.vecmath.*;


public class Tetrahedron extends Shape3D {

  private static final float sqrt3 = (float) Math.sqrt(3.0);
  private static final float sqrt3_3 = sqrt3 / 3.0f;
  private static final float sqrt24_3 = (float) Math.sqrt(24.0) / 3.0f;
  
  private static final float ycenter = 0.5f * sqrt24_3;
  private static final float zcenter = -sqrt3_3;
  
  private static final Point3f p1 = new Point3f(-1.0f, -ycenter, -zcenter);
  private static final Point3f p2 = new Point3f(1.0f, -ycenter, -zcenter);
  private static final Point3f p3 = new Point3f(0.0f, -ycenter, -sqrt3 - zcenter);
  private static final Point3f p4 = new Point3f(0.0f, sqrt24_3 - ycenter, 0.0f);
  
  private static final Point3f[] verts = {
    p1, p2, p4,	// front face
    p1, p4, p3,	// left, back face
    p2, p3, p4,	// right, back face
    p1, p3, p2,	// bottom face
  };

  private Point2f texCoord[] = {
    new Point2f(-0.25f, 0.0f),
    new Point2f(1.25f, 0.0f),
    new Point2f(0.5f, 2.0f),
  };

  private TriangleArray geometryByRef;
  private TriangleArray geometryByCopy;

  // for geometry by reference
  private Point3f[] verticesArray = new Point3f[12];
  private TexCoord2f[] textureCoordsArray = new TexCoord2f[12];
  private Vector3f[] normalsArray = new Vector3f[12];

  // default to geometry by copy
  public Tetrahedron() {
    this(false);
  }

  // creates a tetrahedron with geometry by reference or by copy depending on
  // the byRef parameter
  public Tetrahedron(boolean byRef) {
    if (byRef) {
      createGeometryByRef();
      this.setGeometry(geometryByRef);
    }
    else {
      createGeometryByCopy();
      this.setGeometry(geometryByCopy);
    }
    this.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    this.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    setAppearance(new Appearance());
  }

  // create the geometry by reference and 
  // store it in the geometryByRef variable
  public void createGeometryByRef() {
//     System.out.println("createGeometryByRef");
    geometryByRef = new TriangleArray(12, TriangleArray.COORDINATES |
				      TriangleArray.NORMALS |
				      TriangleArray.TEXTURE_COORDINATE_2 |
				      TriangleArray.BY_REFERENCE);

    int i;

    // the coordinates
    for (i = 0; i < 12; i++) {
      verticesArray[i] = new Point3f(verts[i]);
    }
    geometryByRef.setCoordRef3f(verticesArray);
//     System.out.println("coordinates set");
//     Point3f[] temp1 = geometryByRef.getCoordRef3f();
//     for (i = 0; i < 12; i++) {
//        System.out.println(temp1[i]);
//     }

    // the texture coordinates
    for (i = 0; i < 12; i++) {
      textureCoordsArray[i] = new TexCoord2f(texCoord[i%3]);
    }
    geometryByRef.setTexCoordRef2f(0, textureCoordsArray);
//     System.out.println("texture coords set");
//     TexCoord2f[] temp2 = geometryByRef.getTexCoordRef2f(0);
//     for (i = 0; i < 12; i++) {
//       System.out.println(temp2[i]);
//     }

    // the normals
    Vector3f normal = new Vector3f();
    Vector3f v1 = new Vector3f();
    Vector3f v2 = new Vector3f();
    Point3f[] pts = new Point3f[3];
    for (int face = 0; face < 4; face++) {
      pts[0] = new Point3f(verts[face*3]);
      pts[1] = new Point3f(verts[face*3+1]);
      pts[2] = new Point3f(verts[face*3+2]);
      v1.sub(pts[1], pts[0]);
      v2.sub(pts[2], pts[0]);
      normal.cross(v1, v2);
      normal.normalize();
      for (i = 0; i < 3; i++) {
	normalsArray[face*3+i] = new Vector3f(normal);
      }
    }
    geometryByRef.setNormalRef3f(normalsArray);
//     System.out.println("normals set");
//     Vector3f[] temp3 = geometryByRef.getNormalRef3f();
//     for (i = 0; i < 12; i++) {
//       System.out.println(temp3[i]);
//     }
  }

  // create the geometry by copy and store it in the geometryByCopy variable
  public void createGeometryByCopy() {
     int i;
     geometryByCopy = new TriangleArray(12, TriangleArray.COORDINATES |
 				       TriangleArray.NORMALS | 
 				       TriangleArray.TEXTURE_COORDINATE_2);

     geometryByCopy.setCoordinates(0, verts);

     for (i = 0; i < 12; i++) {
       geometryByCopy.setTextureCoordinate(0, i, 
					   new TexCoord2f(texCoord[i%3]));
     }
     
     int face;
     Vector3f normal = new Vector3f();
     Vector3f v1 = new Vector3f();
     Vector3f v2 = new Vector3f();
     Point3f [] pts = new Point3f[3];
     for (i = 0; i < 3; i++) pts[i] = new Point3f();
    
     for (face = 0; face < 4; face++) {
       geometryByCopy.getCoordinates(face*3, pts);
       v1.sub(pts[1], pts[0]);
       v2.sub(pts[2], pts[0]);
       normal.cross(v1, v2);
       normal.normalize();
       for (i = 0; i < 3; i++) {
 	geometryByCopy.setNormal((face * 3 + i), normal);
       }
     }
   }

  // set the geometry to geometryByRef or geometryByCopy depending on the
  // parameter.  Create geometryByRef or geometryByCopy if necessary  
  public void setByReference(boolean b) {
//     System.out.println("Tetrahedron.setByReference " + b);
    // by reference is true
    if (b) {
      // if there is no geometryByRef, create it
      if (geometryByRef == null) {
	createGeometryByRef();
      }
      // set the geometry
      this.setGeometry(geometryByRef);
    }
    // by reference is false 
    else {
      // if there is no geometryByCopy, create it
      if (geometryByCopy == null) {
	createGeometryByCopy();
      }
      // set the geometry
      this.setGeometry(geometryByCopy);
    }      
  }
}
    
    
