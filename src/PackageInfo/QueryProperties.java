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

import java.util.Map;
import javax.media.j3d.*;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsConfiguration;
import com.sun.j3d.utils.universe.*;

public class QueryProperties {
    public static void main(String[] args) {
        VirtualUniverse vu = new VirtualUniverse();
	Map vuMap = vu.getProperties();

	System.out.println("version = " +
			   vuMap.get("j3d.version"));
	System.out.println("vendor = " +
			   vuMap.get("j3d.vendor"));
	System.out.println("specification.version = " +
			   vuMap.get("j3d.specification.version"));
	System.out.println("specification.vendor = " +
			   vuMap.get("j3d.specification.vendor"));
	System.out.println("renderer = " +
			   vuMap.get("j3d.renderer") + "\n");

	GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();

	/* We need to set this to force choosing a pixel format
	   that support the canvas.
	*/
	template.setStereo(template.PREFERRED);
	template.setSceneAntialiasing(template.PREFERRED);

        GraphicsConfiguration config =
	    GraphicsEnvironment.getLocalGraphicsEnvironment().
                getDefaultScreenDevice().getBestConfiguration(template);

	Map c3dMap = new Canvas3D(config).queryProperties();

	System.out.println("Renderer version = " +
			   c3dMap.get("native.version"));
	System.out.println("doubleBufferAvailable = " +
			   c3dMap.get("doubleBufferAvailable"));
	System.out.println("stereoAvailable = " +
			   c3dMap.get("stereoAvailable"));
	System.out.println("sceneAntialiasingAvailable = " +
			   c3dMap.get("sceneAntialiasingAvailable"));
	System.out.println("sceneAntialiasingNumPasses = " +
			   c3dMap.get("sceneAntialiasingNumPasses"));
	System.out.println("textureColorTableSize = " +
			   c3dMap.get("textureColorTableSize"));
	System.out.println("textureEnvCombineAvailable = " +
			   c3dMap.get("textureEnvCombineAvailable"));
	System.out.println("textureCombineDot3Available = " +
			   c3dMap.get("textureCombineDot3Available"));
	System.out.println("textureCombineSubtractAvailable = " +
			   c3dMap.get("textureCombineSubtractAvailable"));
	System.out.println("texture3DAvailable = " +
			   c3dMap.get("texture3DAvailable"));
	System.out.println("textureCubeMapAvailable = " +
			   c3dMap.get("textureCubeMapAvailable"));
	System.out.println("textureSharpenAvailable = " +
			   c3dMap.get("textureSharpenAvailable"));
	System.out.println("textureDetailAvailable = " +
			   c3dMap.get("textureDetailAvailable"));
	System.out.println("textureFilter4Available = " +
			   c3dMap.get("textureFilter4Available"));
	System.out.println("textureAnisotropicFilterDegreeMax = " +
			   c3dMap.get("textureAnisotropicFilterDegreeMax"));
	System.out.println("textureBoundaryWidthMax = " +
			   c3dMap.get("textureBoundaryWidthMax"));
	System.out.println("textureWidthMax = " +
			   c3dMap.get("textureWidthMax"));
	System.out.println("textureHeightMax = " +
			   c3dMap.get("textureHeightMax"));
	System.out.println("texture3DWidthMax = " +
			   c3dMap.get("texture3DWidthMax"));
	System.out.println("texture3DHeightMax = " +
			   c3dMap.get("texture3DHeightMax"));
	System.out.println("texture3DDepthMax = " +
			   c3dMap.get("texture3DDepthMax"));
	System.out.println("textureLodOffsetAvailable = " +
			   c3dMap.get("textureLodOffsetAvailable"));
	System.out.println("textureLodRangeAvailable = " +
			   c3dMap.get("textureLodRangeAvailable"));
	System.out.println("textureUnitStateMax = " +
			   c3dMap.get("textureUnitStateMax"));
	System.out.println("compressedGeometry.majorVersionNumber = " +
			   c3dMap.get("compressedGeometry.majorVersionNumber"));
	System.out.println("compressedGeometry.minorVersionNumber = " +
			   c3dMap.get("compressedGeometry.minorVersionNumber"));
	System.out.println("compressedGeometry.minorMinorVersionNumber = " +
		c3dMap.get("compressedGeometry.minorMinorVersionNumber"));

	System.exit(0);
    }
}
