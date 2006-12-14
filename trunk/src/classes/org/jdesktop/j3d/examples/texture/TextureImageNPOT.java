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

package org.jdesktop.j3d.examples.texture;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.util.Map;
import javax.media.j3d.*;
import javax.swing.JOptionPane;
import javax.vecmath.Point3d;
import org.jdesktop.j3d.examples.Resources;

public class TextureImageNPOT extends Applet {
  
    private static final String defaultFileName = "resources/images/Java3d.jpg";
    private java.net.URL texImage = null;

    private SimpleUniverse u = null;
    private boolean allowNonPowerOfTwo = true;
    private boolean mipmap = true;

    public BranchGroup createSceneGraph() {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

	// Create the transform group node and initialize it to the
	// identity.  Enable the TRANSFORM_WRITE capability so that
	// our behavior code can modify it at runtime.  Add it to the
	// root of the subgraph.
	TransformGroup objTrans = new TransformGroup();
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objRoot.addChild(objTrans);

	// Create the scaling transform group node and initialize it to the
	// identity.  Enable the TRANSFORM_WRITE capability so that
	// our behavior code can modify it at runtime.  Add it to the
	// objTrans group
	TransformGroup objScale = new TransformGroup();
	objScale.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objTrans.addChild(objScale);

	// Create appearance object for textured cube
	Appearance app = new Appearance();
	int flags = 0;
	if (allowNonPowerOfTwo) {
	    flags |= TextureLoader.ALLOW_NON_POWER_OF_TWO;
        }
	if (mipmap) {
	    flags |= TextureLoader.GENERATE_MIPMAP;
	}
	Texture tex = new TextureLoader(texImage, flags, this).getTexture();
	tex.setMagFilter(Texture.BASE_LEVEL_LINEAR);
	if (mipmap) {
	    tex.setMinFilter(Texture.MULTI_LEVEL_LINEAR);
	} else {
	    tex.setMinFilter(Texture.BASE_LEVEL_LINEAR);
        }
	app.setTexture(tex);
	TextureAttributes texAttr = new TextureAttributes();
	texAttr.setTextureMode(TextureAttributes.MODULATE);
	app.setTextureAttributes(texAttr);

	// Create textured cube and add it to the scene graph.
	Box textureCube = new Box(0.4f, 0.4f, 0.4f,
				  Box.GENERATE_TEXTURE_COORDS, app);
	objScale.addChild(textureCube);

	// Create a new Behavior object that will perform the desired
	// operation on the specified transform object and add it into
	// the scene graph.
	Transform3D yAxis = new Transform3D();
	Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE,
					0, 0,
					50000, 0, 0,
					0, 0, 0);

	RotationInterpolator rotator =
	    new RotationInterpolator(rotationAlpha, objTrans, yAxis,
				     0.0f, (float) Math.PI*2.0f);
	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
	rotator.setSchedulingBounds(bounds);
	objTrans.addChild(rotator);

	// Create a new Behavior object that will perform the desired
	// operation on the specified transform object and add it into
	// the scene graph.
        Alpha scaleAlpha = new Alpha(-1,
                Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE,
                0, 0,
                8000, 0, 0,
                8000, 0, 0);

	ScaleInterpolator scaler =
	    new ScaleInterpolator(scaleAlpha, objScale, yAxis,
				     0.01f, 1.5f);
	scaler.setSchedulingBounds(bounds);
	objScale.addChild(scaler);

        // Have Java 3D perform optimizations on this scene graph.
        objRoot.compile();

	return objRoot;
    }

    public TextureImageNPOT() {
    }

    public TextureImageNPOT(java.net.URL url) {
        texImage = url;
    }

    public void init() {
        if (texImage == null) {
  	    // the path to the image for an applet
            texImage = Resources.getResource(defaultFileName);
            if (texImage == null) {
                System.err.println(defaultFileName + " not found");
                System.exit(1);
            }
	}
	setLayout(new BorderLayout());
        GraphicsConfiguration config =
           SimpleUniverse.getPreferredConfiguration();

        Canvas3D c = new Canvas3D(config);

        Map map = c.queryProperties();
        Boolean value = (Boolean) map.get("textureNonPowerOfTwoAvailable");
        String errorStr = null;
        if (value == null) {
            errorStr = "Canvas3D: textureNonPowerOfTwoAvailable property not found";
        } else if (!value) {
            errorStr = "Non-power-of-two textures are not available";
        }

        if (errorStr != null) {
            String errorMessage = errorStr + "\n" +
                    "You should expect to see a white cube as a result";
//            System.err.println(errorMessage);
            JOptionPane.showMessageDialog(this,
                    errorMessage,
                    "Insufficient Capabilities",
                    JOptionPane.ERROR_MESSAGE);
        }

	add("Center", c);

	// Create a simple scene and attach it to the virtual universe
	BranchGroup scene = createSceneGraph();
	u = new SimpleUniverse(c);

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
	u.getViewingPlatform().setNominalViewingTransform();

	u.addBranchGraph(scene);
    }

    public void destroy() {
	u.cleanup();
    }

    //
    // The following allows TextureImageNPOT to be run as an application
    // as well as an applet
    //
    public static void main(String[] args) {
        java.net.URL url = null;
        if (args.length > 0) {
            try {
                url = new java.net.URL("file:" + args[0]);
            } catch (java.net.MalformedURLException ex) {
                System.out.println(ex.getMessage());
                System.exit(1);
            }
        } else {
            // the path to the image for an application
            url = Resources.getResource(defaultFileName);
            if (url == null) {
                System.err.println(defaultFileName + " not found");
                System.exit(1);
            }
        }
	new MainFrame(new TextureImageNPOT(url), 512, 512);
    }

}
