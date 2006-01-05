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

import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.behaviors.vp.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.image.BufferedImage;

public class MultiTextureTest extends Applet implements ItemListener{

  Choice choice;
  TextureUnitState textureUnitState[] = new TextureUnitState[2];
  Texture stoneTex;
  Texture skyTex;
  Texture lightTex;

  private java.net.URL stoneImage = null;
  private java.net.URL skyImage = null;

    private SimpleUniverse u = null;

  public Texture createLightMap(){

    int width = 128;
    int height = 128;
    BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    int [] rgbArray = new int[width * height];
    int index, index2;
    int rgbInc = 256 / (width / 2 - 20);
    int rgbValue = 0;
    int k = width/2 - 5;
    int i, j, rgb;

    rgb = 0xff;
    rgbValue = rgb | (rgb << 8) | (rgb << 16) | (rgb << 24);
    for ( i = width / 2 - 1, j = 0; j < 10; j++, i--) {
	 rgbArray[i] = rgbValue;
    }

    for (; i > 8; i--, rgb -= rgbInc) {
	 rgbValue = rgb | (rgb << 8) | (rgb << 16) | (rgb << 24);
	 rgbArray[i] = rgbValue;
    }

    for (; i >= 0; i--) {
	 rgbArray[i] = rgbValue;
    }
	
    for (i = 0; i < width/2; i++) {
	 rgbValue = rgbArray[i];
	 index = i;
	 index2 = (width - i - 1);
	 for (j = 0; j < height; j++) {
	      rgbArray[index] = rgbArray[index2] = rgbValue;
	      index += width;
	      index2 += width;
	 }
    }
	    
    bimage.setRGB(0, 0, width, height, rgbArray, 0, width);

	
    ImageComponent2D grayImage = new ImageComponent2D(ImageComponent.FORMAT_RGB, bimage);

    lightTex = new Texture2D(Texture.BASE_LEVEL, Texture.RGB, width, height);
    lightTex.setImage(0, grayImage);

    return lightTex;
  }

 
  public BranchGroup createSceneGraph() {
    // Create the root of the branch graph
    BranchGroup objRoot = new BranchGroup();

    // Create a Transformgroup to scale all objects so they
    // appear in the scene.
    TransformGroup objScale = new TransformGroup();
    Transform3D t3d = new Transform3D();
    t3d.setScale(0.4);
    objScale.setTransform(t3d);
    objRoot.addChild(objScale);
    
    TransformGroup objTrans = new TransformGroup();
    //write-enable for behaviors
    objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    objTrans.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );
    objTrans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
    objScale.addChild(objTrans);
    
    Appearance ap = new Appearance();
    
    // load textures
    TextureAttributes texAttr1 = new TextureAttributes();
    texAttr1.setTextureMode(TextureAttributes.DECAL);
    TextureAttributes texAttr2 = new TextureAttributes();
    texAttr2.setTextureMode(TextureAttributes.MODULATE);

    TextureLoader tex = new TextureLoader(stoneImage, new String("RGB"), this);
    if (tex == null) 
	return null;
    stoneTex = tex.getTexture();

    tex = new TextureLoader(skyImage, new String("RGB"), this);
    if (tex == null)
	return null;
    skyTex = tex.getTexture();

    lightTex = createLightMap();


    textureUnitState[0] = new TextureUnitState(stoneTex, texAttr1, null);
    textureUnitState[0].setCapability(TextureUnitState.ALLOW_STATE_WRITE);

    textureUnitState[1] = new TextureUnitState(lightTex, texAttr2, null);
    textureUnitState[1].setCapability(TextureUnitState.ALLOW_STATE_WRITE);

    ap.setTextureUnitState(textureUnitState);

    //Create a Box
    Box BoxObj = new Box(1.5f, 1.5f, 0.8f, Box.GENERATE_NORMALS |
			    Box.GENERATE_TEXTURE_COORDS, ap, 2);
    // add it to the scene graph.
    objTrans.addChild(BoxObj);

    BoundingSphere bounds =
      new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
    
    //Shine it with two lights.
    Color3f lColor1 = new Color3f(0.7f, 0.7f, 0.7f);
    Color3f lColor2 = new Color3f(0.2f, 0.2f, 0.1f);
    Vector3f lDir1  = new Vector3f(-1.0f, -1.0f, -1.0f);
    Vector3f lDir2  = new Vector3f(0.0f, 0.0f, -1.0f);
    DirectionalLight lgt1 = new DirectionalLight(lColor1, lDir1);
    DirectionalLight lgt2 = new DirectionalLight(lColor2, lDir2);
    lgt1.setInfluencingBounds(bounds);
    lgt2.setInfluencingBounds(bounds);
    objScale.addChild(lgt1);
    objScale.addChild(lgt2);
   
    // Let Java 3D perform optimizations on this scene graph.
    objRoot.compile();
 
    return objRoot;
  }

  public MultiTextureTest (){
  }

  public MultiTextureTest(java.net.URL stoneURL, java.net.URL skyURL) {
    stoneImage = stoneURL;
    skyImage = skyURL;
  }

  public void init() {
    if (stoneImage == null) {
      // the path to the image for an applet
      try {
	stoneImage = new java.net.URL(getCodeBase().toString() + 
				      "../images/stone.jpg");
      }
      catch (java.net.MalformedURLException ex) {
	System.out.println(ex.getMessage());
	System.exit(1);
      }
    }

    if (skyImage == null) {
      // the path to the image for an applet
      try {
	skyImage = new java.net.URL(getCodeBase().toString() +
				    "../images/bg.jpg");
      }
      catch (java.net.MalformedURLException ex) {
	System.out.println(ex.getMessage());
	System.exit(1);
      }
    }

    setLayout(new BorderLayout());
    GraphicsConfiguration config =
       SimpleUniverse.getPreferredConfiguration();

    Canvas3D c = new Canvas3D(config);
    add("Center", c);
    
    BranchGroup scene = createSceneGraph();
    u = new SimpleUniverse(c);

    ViewingPlatform viewingPlatform = u.getViewingPlatform();
    // This will move the ViewPlatform back a bit so the
    // objects in the scene can be viewed.
    viewingPlatform.setNominalViewingTransform();

    // add orbit behavior but disable translate
    OrbitBehavior orbit =
	new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL |
			  OrbitBehavior.DISABLE_TRANSLATE);
    BoundingSphere bounds =
	new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
    orbit.setSchedulingBounds(bounds);
    viewingPlatform.setViewPlatformBehavior(orbit);

    u.addBranchGraph(scene);

    // create the gui
    choice = new Choice();
    choice.addItem("stone + light");
    choice.addItem("stone");
    choice.addItem("lightMap");
    choice.addItem("sky");
    choice.addItem("stone + sky");
    choice.addItemListener(this);
    add("North", choice);

  }

    public void destroy() {
	// Cleanup reference to Java3D
	textureUnitState = new TextureUnitState[2];
	u.cleanup();
    }
  
  public void itemStateChanged(ItemEvent e)
  {
    int index = choice.getSelectedIndex();
 
    switch (index) {
    case 0 : /* stone + light */
	textureUnitState[0].setTexture(stoneTex);
	textureUnitState[1].setTexture(lightTex);
	break;
    case 1 : /* stone */
	textureUnitState[0].setTexture(stoneTex);
	textureUnitState[1].setTexture(null);
	break;
    case 2 : /* light */
	textureUnitState[0].setTexture(null);
	textureUnitState[1].setTexture(lightTex);
	break;
    case 3 : /* sky */
	textureUnitState[0].setTexture(null);
	textureUnitState[1].setTexture(skyTex);
	break;
    case 4 : /* stone + sky */
	textureUnitState[0].setTexture(stoneTex);
	textureUnitState[1].setTexture(skyTex);
	break;
    default:  /* both */
	break;
    }
  }

  public static void main(String argv[])
  {
    java.net.URL stoneURL = null;
    java.net.URL skyURL = null;
    // the path to the image for an application
    try {
      stoneURL = new java.net.URL("file:../images/stone.jpg");
      skyURL = new java.net.URL("file:../images/bg.jpg");
    }
    catch (java.net.MalformedURLException ex) {
      System.out.println(ex.getMessage());
      System.exit(1);
    }
    new MainFrame(new MultiTextureTest(stoneURL, skyURL), 750, 750);
  }
}

