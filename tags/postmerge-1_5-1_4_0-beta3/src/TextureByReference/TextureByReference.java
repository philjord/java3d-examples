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

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.image.*;
import java.awt.color.ColorSpace;
import com.sun.j3d.utils.image.TextureLoader;
import javax.swing.*;
import javax.swing.event.*;


public class TextureByReference extends Applet 
implements ItemListener, ActionListener, ChangeListener {

  // need reference to animation behavior
  private AnimateTexturesBehavior animate;

  // need reference to tetrahedron
  private Tetrahedron tetra;
  
  // the gui buttons
   private JCheckBox flipB;
  private JRadioButton texByRef;
  private JRadioButton texByCopy;
  private JRadioButton geomByRef;
  private JRadioButton geomByCopy;
  private JRadioButton img4ByteABGR;
  private JRadioButton img3ByteBGR;
  private JRadioButton imgIntARGB;
  private JRadioButton imgCustomRGBA;
  private JRadioButton imgCustomRGB;
  private JRadioButton yUp;
  private JRadioButton yDown;
  private JButton animationB;
  private JSlider frameDelay;

    private SimpleUniverse universe = null;

  // image files used for the Texture animation for the applet,
  // or if no parameters are passed in for the application
  public static final String[] defaultFiles = {
    "../images/animation1.gif",
    "../images/animation2.gif",
    "../images/animation3.gif",
    "../images/animation4.gif",
    "../images/animation5.gif",
    "../images/animation6.gif",
    "../images/animation7.gif",
    "../images/animation8.gif",
    "../images/animation9.gif",
    "../images/animation10.gif"};

  private java.net.URL[] urls = null;

  
  public TextureByReference() {
  }

   public TextureByReference(java.net.URL[] fnamesP) {
     urls = fnamesP;
   }

  public void init() {
    if (urls == null) {
      urls = new java.net.URL[defaultFiles.length];
      for (int i = 0; i < defaultFiles.length; i++) {
	try {
	  urls[i] = new java.net.URL(getCodeBase().toString() + 
				       defaultFiles[i]);
	}
	catch (java.net.MalformedURLException ex) {
	  System.out.println(ex.getMessage());
	  System.exit(1);
	}
      }
    }
    setLayout(new BorderLayout());
    GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

    Canvas3D canvas = new Canvas3D(config);

    add("Center", canvas);

    // create a simple scene graph and attach it to a simple universe
    BranchGroup scene = createSceneGraph();
    universe = new SimpleUniverse(canvas);
    universe.getViewingPlatform().setNominalViewingTransform();
    universe.addBranchGraph(scene);

    // create the gui
    JPanel gui = buildGui();
    
    this.add("South", gui);
  }

    public void destroy() {
	universe.cleanup();
    }

  public JPanel buildGui() {
    flipB = new JCheckBox("flip image", true);
    flipB.addItemListener(this);
    javax.swing.Box flipBox = new javax.swing.Box(BoxLayout.Y_AXIS);
    flipBox.add(flipB);
    Component strut1 = flipBox.createVerticalStrut(flipB.getPreferredSize().height);
    Component strut2 = flipBox.createVerticalStrut(flipB.getPreferredSize().height);
    Component strut3 = flipBox.createVerticalStrut(flipB.getPreferredSize().height);
    Component strut4 = flipBox.createVerticalStrut(flipB.getPreferredSize().height);
    Component strut5 = flipBox.createVerticalStrut(flipB.getPreferredSize().height);
    flipBox.add(strut1);
    flipBox.add(strut2);
    flipBox.add(strut3);
    flipBox.add(strut4);
    flipBox.add(strut5);

    yUp = new JRadioButton("y up");
    yUp.addActionListener(this);
    yUp.setSelected(true);
    yDown = new JRadioButton("y down");
    yDown.addActionListener(this);
    ButtonGroup yGroup = new ButtonGroup();
    yGroup.add(yUp);
    yGroup.add(yDown);
    JLabel yLabel = new JLabel("Image Orientation:");
    javax.swing.Box yBox = new javax.swing.Box(BoxLayout.Y_AXIS);
    yBox.add(yLabel);
    yBox.add(yUp);
    yBox.add(yDown);
    strut1 = yBox.createVerticalStrut(yUp.getPreferredSize().height);
    strut2 = yBox.createVerticalStrut(yUp.getPreferredSize().height);
    strut3 = yBox.createVerticalStrut(yUp.getPreferredSize().height);
    yBox.add(strut1);
    yBox.add(strut2);
    yBox.add(strut3);

    texByRef = new JRadioButton("by reference");
    texByRef.addActionListener(this);
    texByRef.setSelected(true);
    texByCopy = new JRadioButton("by copy");
    texByCopy.addActionListener(this);
    ButtonGroup texGroup = new ButtonGroup();
    texGroup.add(texByRef);
    texGroup.add(texByCopy);
    JLabel texLabel = new JLabel("Texture:*");
    javax.swing.Box texBox = new javax.swing.Box(BoxLayout.Y_AXIS);
    texBox.add(texLabel);
    texBox.add(texByRef);
    texBox.add(texByCopy);
    strut1 = texBox.createVerticalStrut(texByRef.getPreferredSize().height);
    strut2 = texBox.createVerticalStrut(texByRef.getPreferredSize().height);
    strut3 = texBox.createVerticalStrut(texByRef.getPreferredSize().height);
    texBox.add(strut1);
    texBox.add(strut2);
    texBox.add(strut3);

    geomByRef = new JRadioButton("by reference");
    geomByRef.addActionListener(this);
    geomByRef.setSelected(true);
    geomByCopy = new JRadioButton("by copy");
    geomByCopy.addActionListener(this);
    ButtonGroup geomGroup = new ButtonGroup();
    geomGroup.add(geomByRef);
    geomGroup.add(geomByCopy);
    JLabel geomLabel = new JLabel("Geometry:");
    javax.swing.Box geomBox = new javax.swing.Box(BoxLayout.Y_AXIS);
    geomBox.add(geomLabel);
    geomBox.add(geomByRef);
    geomBox.add(geomByCopy);
    strut1 = geomBox.createVerticalStrut(geomByRef.getPreferredSize().height);
    strut2 = geomBox.createVerticalStrut(geomByRef.getPreferredSize().height);
    strut3 = geomBox.createVerticalStrut(geomByRef.getPreferredSize().height);
    geomBox.add(strut1);
    geomBox.add(strut2);
    geomBox.add(strut3);
    
    img4ByteABGR = new JRadioButton("TYPE_4BYTE_ABGR");
    img4ByteABGR.addActionListener(this);
    img4ByteABGR.setSelected(true);
    img3ByteBGR = new JRadioButton("TYPE_3BYTE_BGR");
    img3ByteBGR.addActionListener(this);
    imgIntARGB = new JRadioButton("TYPE_INT_ARGB");
    imgIntARGB.addActionListener(this);
    imgCustomRGBA = new JRadioButton("TYPE_CUSTOM RGBA");
    imgCustomRGBA.addActionListener(this);
    imgCustomRGB = new JRadioButton("TYPE_CUSTOM RGB");
    imgCustomRGB.addActionListener(this);
    ButtonGroup imgGroup = new ButtonGroup();
    imgGroup.add(img4ByteABGR);
    imgGroup.add(img3ByteBGR);
    imgGroup.add(imgIntARGB);
    imgGroup.add(imgCustomRGBA);
    imgGroup.add(imgCustomRGB);
    JLabel imgLabel = new JLabel("Image Type:*");
    javax.swing.Box imgBox = new javax.swing.Box(BoxLayout.Y_AXIS);
    imgBox.add(imgLabel);
    imgBox.add(img4ByteABGR);
    imgBox.add(img3ByteBGR);
    imgBox.add(imgIntARGB);
    imgBox.add(imgCustomRGBA);
    imgBox.add(imgCustomRGB);

    javax.swing.Box topBox = new javax.swing.Box(BoxLayout.X_AXIS);
    topBox.add(flipBox);
    topBox.add(texBox);
    topBox.add(geomBox);
    topBox.add(yBox);
    Component strut = topBox.createRigidArea(new Dimension(10, 10));
    topBox.add(strut);
    topBox.add(imgBox);

    frameDelay = new JSlider(0, 50, 0);
    frameDelay.addChangeListener(this);
    frameDelay.setSnapToTicks(true);
    frameDelay.setPaintTicks(true);
    frameDelay.setPaintLabels(true);
    frameDelay.setMajorTickSpacing(10);
    frameDelay.setMinorTickSpacing(1);
    frameDelay.setValue(20);
    JLabel delayL = new JLabel("frame delay");
    javax.swing.Box delayBox = new javax.swing.Box(BoxLayout.X_AXIS);
    delayBox.add(delayL);
    delayBox.add(frameDelay);
    
    animationB = new JButton(" stop animation ");
    animationB.addActionListener(this);

    JLabel texInfo1 = new JLabel("*To use ImageComponent by reference feature, use TYPE_4BYTE_ABGR on Solaris");
    JLabel texInfo2 = new JLabel("and TYPE_3BYTE_BGR on Windows");

    JPanel buttonP = new JPanel();
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    buttonP.setLayout(gridbag);
    c.anchor = GridBagConstraints.CENTER;
    c.gridwidth = GridBagConstraints.REMAINDER;
    gridbag.setConstraints(topBox, c);
    buttonP.add(topBox);
    gridbag.setConstraints(delayBox, c);
    buttonP.add(delayBox);
    gridbag.setConstraints(animationB, c);
    buttonP.add(animationB);
    gridbag.setConstraints(texInfo1, c);
    buttonP.add(texInfo1);
    gridbag.setConstraints(texInfo2, c);
    buttonP.add(texInfo2);

    return buttonP;

  }

  public BranchGroup createSceneGraph() {

    // create the root of the branch group
    BranchGroup objRoot = new BranchGroup();

    // create the transform group node and initialize it
    // enable the TRANSFORM_WRITE capability so that it can be modified
    // at runtime.  Add it to the root of the subgraph
    Transform3D rotate = new Transform3D();
    TransformGroup objTrans = new TransformGroup(rotate);
    objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    objRoot.addChild(objTrans);

    // bounds
    BoundingSphere bounds =
      new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

    // set up some light
    Color3f lColor1 = new Color3f(0.7f, 0.7f, 0.7f);
    Vector3f lDir1  = new Vector3f(-1.0f, -0.5f, -1.0f);
    Color3f alColor = new Color3f(0.2f, 0.2f, 0.2f);
    
    AmbientLight aLgt = new AmbientLight(alColor);
    aLgt.setInfluencingBounds(bounds);
    DirectionalLight lgt1 = new DirectionalLight(lColor1, lDir1);
    lgt1.setInfluencingBounds(bounds);
    objRoot.addChild(aLgt);
    objRoot.addChild(lgt1);
    

    Appearance appearance = new Appearance();

    // enable the TEXTURE_WRITE so we can modify it at runtime
    appearance.setCapability(Appearance.ALLOW_TEXTURE_WRITE);

    // load the first texture
    TextureLoader loader = new TextureLoader(urls[0],
					     TextureLoader.BY_REFERENCE |
 					     TextureLoader.Y_UP,
 					     this);
    // get the texture from the loader
    Texture2D tex = (Texture2D)loader.getTexture();
    
    // get the BufferedImage to convert to TYPE_4BYTE_ABGR and flip
    // get the ImageComponent because we need it anyway
    ImageComponent2D imageComp = (ImageComponent2D)tex.getImage(0);
    BufferedImage bImage = imageComp.getImage();
    // convert the image
    bImage = ImageOps.convertImage(bImage, BufferedImage.TYPE_4BYTE_ABGR);
    // flip the image
    ImageOps.flipImage(bImage);
    imageComp.set(bImage);

    tex.setCapability(Texture.ALLOW_IMAGE_WRITE);
    tex.setBoundaryModeS(Texture.CLAMP);
    tex.setBoundaryModeT(Texture.CLAMP);
    tex.setBoundaryColor(1.0f, 1.0f, 1.0f, 1.0f);
   
    // set the image of the texture
    tex.setImage(0, imageComp);

    // set the texture on the appearance
    appearance.setTexture(tex);

    // set texture attributes
    TextureAttributes texAttr = new TextureAttributes();
    texAttr.setTextureMode(TextureAttributes.MODULATE);
    appearance.setTextureAttributes(texAttr);

    // set material properties
    Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
    Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
    appearance.setMaterial(new Material(white, black, white, black, 1.0f));

    // create a scale transform
    Transform3D scale = new Transform3D();
    scale.set(.6);
    TransformGroup objScale = new TransformGroup(scale);
    objTrans.addChild(objScale);

    tetra = new Tetrahedron(true);
    tetra.setAppearance(appearance);
    objScale.addChild(tetra);

    // create the behavior
    animate = new AnimateTexturesBehavior(tex, 
					  urls,
					  appearance,
					  this);
    animate.setSchedulingBounds(bounds);

    objTrans.addChild(animate);

    // add a rotation behavior so we can see all sides of the tetrahedron
      Transform3D yAxis = new Transform3D();
  	Alpha rotorAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE,
  				     0, 0,
  				     4000, 0, 0,
  				     0, 0, 0);
  	RotationInterpolator rotator =
  	    new RotationInterpolator(rotorAlpha,
  				     objTrans,
  				     yAxis,
  				     0.0f, (float) Math.PI*2.0f);
  	rotator.setSchedulingBounds(bounds);
	objTrans.addChild(rotator);       


    // have java3d perform optimizations on this scene graph
    objRoot.compile();

    return objRoot;
  }

  // callback for the animation button and delay text field
  public void actionPerformed(ActionEvent e) {
    Object o = e.getSource();

    // for the animation button
    if (o == animationB) {
      if (animate.getEnable()) {
	animate.setEnable(false);
	animationB.setText("start animation");
      }
      else {
	animate.setEnable(true);
	animationB.setText(" stop animation ");
      }
    }

    // for the texByRef button
    else if (o == texByRef && texByRef.isSelected()) {
      animate.setByReference(true);
    }
    // texByCopy button
    else if (o == texByCopy && texByCopy.isSelected()) {
      animate.setByReference(false);
    }
    // yUp button
    else if (o == yUp && yUp.isSelected()) {
      animate.setYUp(true);
    }
    // ydown button
    else if (o == yDown && yDown.isSelected()) {
      animate.setYUp(false);
    }
    //geomByRef button
    else if (o == geomByRef) {
      tetra.setByReference(true);
    }
    // geomByCopy button
    else if (o == geomByCopy) {
      tetra.setByReference(false);
    }
    // TYPE_INT_ARGB
    else if (o == imgIntARGB) {
      animate.setImageType(BufferedImage.TYPE_INT_ARGB);
    }
    // TYPE_4BYTE_ABGR
    else if (o == img4ByteABGR) {
      animate.setImageType(BufferedImage.TYPE_4BYTE_ABGR);
    }
    // TYPE_3BYTE_BGR
    else if (o == img3ByteBGR) {
      animate.setImageType(BufferedImage.TYPE_3BYTE_BGR);
    }
    // TYPE_CUSTOM RGBA
    else if (o == imgCustomRGBA) {
      animate.setImageTypeCustomRGBA();
    }
    // TYPE_CUSTOM RGB
    else if (o == imgCustomRGB) {
      animate.setImageTypeCustomRGB();
    }
  }

  // callback for the checkboxes
  public void itemStateChanged(ItemEvent e) {
    Object o = e.getSource();
    // for the flip checkbox
    if (o == flipB) {
      if (e.getStateChange() == ItemEvent.DESELECTED) {
	animate.setFlipImages(false);
      }
      else animate.setFlipImages(true);
    }
  }

  // callback for the slider
  public void stateChanged(ChangeEvent e) {
    Object o = e.getSource();
    // for the frame delay
    if (o == frameDelay) {
      animate.setFrameDelay(frameDelay.getValue());
    }
  }

  // allows TextureByReference to be run as an application as well as an applet
  public static void main(String[] args) {
    java.net.URL fnames[] = null;
    if (args.length > 1) {
      fnames = new java.net.URL[args.length];
      for (int i = 0; i < args.length; i++) {
	try {
	  fnames[i] = new java.net.URL("file:" + args[i]);
	}
	catch (java.net.MalformedURLException ex) {
	  System.out.println(ex.getMessage());
	}
      }
    }
    else {
      fnames = new java.net.URL[TextureByReference.defaultFiles.length];
      for (int i = 0; i < TextureByReference.defaultFiles.length; i++) {
	try {
	  fnames[i] = new java.net.URL("file:" +
				     TextureByReference.defaultFiles[i]);
	}
	catch (java.net.MalformedURLException ex) {
	  System.out.println(ex.getMessage());
	  System.exit(1);
	}
      }
    }
    new MainFrame((new TextureByReference(fnames)), 650, 750);    
  }
}






