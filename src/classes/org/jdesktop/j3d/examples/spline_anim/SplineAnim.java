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

package org.jdesktop.j3d.examples.spline_anim;

import java.io.*;
import java.applet.Applet;
import java.awt.FlowLayout;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.utils.behaviors.vp.*;
import com.sun.j3d.utils.behaviors.interpolators.*;



/*
 * This program demonstrates the use of KBRotPosScaleSplinePathInterpolator 
 * in order to to do spline animation paths using Kochanek-Bartels (also
 * known as TCB or Tension-Continuity-Bias ) splines. A cone red cone 
 * is animated along a spline path specified by 5 knot points, which 
 * are showns as cyan spheres. 
 *
 * Use the left mouse button to changes orientation of scene. 
 * Use the middle mouse button to zoom in/out
 * Use the right mouse button to pan the scene 
 */
public class SplineAnim extends Applet implements ActionListener, 
                                                  AdjustmentListener,
                                                  ItemListener {

    // 3D Canvas
    Canvas3D           canvas;

    // UI Components
    Panel              controlPanel;
    Panel              canvasPanel;
    Button             animateButton;
    Choice             interpChoice; 
    Scrollbar          speedSlider;
    Label              speedLabel;
    Label              interpLabel;

    // Scene Graph
    BoundingSphere     bounds;
    BranchGroup        root;
    BranchGroup        behaviorBranch;
    Transform3D        sceneTransform;
    TransformGroup     sceneTransformGroup;
    Transform3D        objTransform;
    TransformGroup     objTransformGroup;
    Transform3D        lightTransform1;
    Transform3D        lightTransform2;
    TransformGroup     light1TransformGroup;
    TransformGroup     light2TransformGroup;

    // Key Frames & Interpolator
    int                                  duration = 5000;
    Alpha                                animAlpha;
    Transform3D                          yAxis;
    KBKeyFrame[]                         linearKeyFrames = new KBKeyFrame[6];
    KBKeyFrame[]                         splineKeyFrames = new KBKeyFrame[6];
    KBRotPosScaleSplinePathInterpolator  splineInterpolator;
    KBRotPosScaleSplinePathInterpolator  linearInterpolator;

    // Data: Knot positions & transform groups
    Vector3f           pos0 = new Vector3f(-5.0f, -5.0f, 0.0f);
    Vector3f           pos1 = new Vector3f(-5.0f,  5.0f, 0.0f);
    Vector3f           pos2 = new Vector3f( 0.0f,  5.0f, 0.0f);
    Vector3f           pos3 = new Vector3f( 0.0f, -5.0f, 0.0f);
    Vector3f           pos4 = new Vector3f( 5.0f, -5.0f, 0.0f);
    Vector3f           pos5 = new Vector3f( 5.0f,  5.0f, 0.0f);
    TransformGroup     k0TransformGroup;
    TransformGroup     k1TransformGroup;
    TransformGroup     k2TransformGroup;
    TransformGroup     k3TransformGroup;
    TransformGroup     k4TransformGroup;
    TransformGroup     k5TransformGroup;

    // Flags
    boolean            animationOn = true; 
    boolean            linear      = false;

    private SimpleUniverse u = null;
						      
    public SplineAnim() {
    }

    public void init() {
	this.setLayout(new FlowLayout());   

        // Create the canvas and the UI
        canvasPanel = new Panel();
        controlPanel = new Panel();
        createCanvasPanel(canvasPanel); 
        this.add(canvasPanel);
        createControlPanel(controlPanel); 
        this.add(controlPanel);

        // Create the scene. 
        BranchGroup scene = createSceneGraph();

        // Setup keyframe data for our animation
        setupSplineKeyFrames ();
        setupLinearKeyFrames ();

        // Setup alpha, create the interpolators and start them. We
        // create both a linear and a spline interpolator and turn on
        // one depending on user selection. The default is spline.
        setupAnimationData ();
        createInterpolators();
        startInterpolator();

        // Add viewing platform  
        u = new SimpleUniverse(canvas);

	// add mouse behaviors to ViewingPlatform
	ViewingPlatform viewingPlatform = u.getViewingPlatform();
	
        viewingPlatform.setNominalViewingTransform();

	// add orbit behavior to the ViewingPlatform
	OrbitBehavior orbit = new OrbitBehavior(canvas,
						OrbitBehavior.REVERSE_ALL);
	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
	orbit.setSchedulingBounds(bounds);
	viewingPlatform.setViewPlatformBehavior(orbit);
	
        u.addBranchGraph(scene);
    }

    public void destroy() {
	u.cleanup();
    }

    /*
     * This creates the control panel which contains a choice menu to
     * toggle between spline and linear interpolation, a slider to
     * adjust the speed of the animation and a animation start/stop
     * button.
     */
    private void createControlPanel(Panel p) {

        GridBagLayout      gl  = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        p.setLayout (gl);
        gbc.weightx = 100;  gbc.weighty = 100;
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;  gbc.gridy = 0;
        gbc.gridwidth = 1;  gbc.gridheight = 1;
        interpLabel = new Label("Interpolation Type", Label.LEFT);
        p.add(interpLabel, gbc);

        gbc.gridx = 1;  gbc.gridy = 0;
        gbc.gridwidth = 1;  gbc.gridheight = 1;
        interpChoice = new Choice();
        interpChoice.add("Spline");
        interpChoice.add("Linear");
        p.add(interpChoice, gbc);
        interpChoice.addItemListener (this);

        gbc.gridx = 0;  gbc.gridy = 2;
        gbc.gridwidth = 2;  gbc.gridheight = 1;
        speedSlider = new Scrollbar(Scrollbar.HORIZONTAL, 2, 1,  0, 11);
        speedSlider.setUnitIncrement (1);
        p.add(speedSlider, gbc);
        speedSlider.addAdjustmentListener(this);

        gbc.gridx = 0;  gbc.gridy = 3;
        gbc.gridwidth = 2;  gbc.gridheight = 1;
        speedLabel = new Label(" - Animation Speed +", Label.CENTER);
        p.add(speedLabel, gbc);

        gbc.gridx = 0;  gbc.gridy = 5;
        gbc.gridwidth = 2;  gbc.gridheight = 1;
        animateButton = new Button("Stop Animation");
        p.add(animateButton, gbc);
        animateButton.addActionListener (this);


    }

    /*
     * This creates the Java3D canvas
     */
    private void createCanvasPanel(Panel p) {

        GridBagLayout      gl  = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        p.setLayout(gl);
        gbc.gridx = 0;  gbc.gridy = 0;
        gbc.gridwidth = 5;  gbc.gridheight = 5;
        GraphicsConfiguration config =
           SimpleUniverse.getPreferredConfiguration();

        canvas = new Canvas3D(config);
        canvas.setSize(490,490);
        p.add(canvas,gbc);

    }

    /* 
     * This creates the scene with 5 knot points represented by cyan 
     * spheres, a cone obejct that will be transformed, and two directional
     * lights + and ambient light.
     */
    public BranchGroup createSceneGraph() {

      // Colors for lights and objects
      Color3f aColor     = new Color3f(0.2f, 0.2f, 0.2f);
      Color3f eColor     = new Color3f(0.0f, 0.0f, 0.0f);
      Color3f sColor     = new Color3f(1.0f, 1.0f, 1.0f);
      Color3f coneColor  = new Color3f(0.9f, 0.1f, 0.1f);
      Color3f sphereColor= new Color3f(0.1f, 0.7f, 0.9f);
      Color3f bgColor    = new Color3f(0.0f, 0.0f, 0.0f);
      Color3f lightColor = new Color3f(1.0f, 1.0f, 1.0f);

      // Root of the branch grsph
      BranchGroup root = new BranchGroup();

      // Create transforms such that all objects appears in the scene
      sceneTransform = new Transform3D();
      sceneTransform.setScale(0.14f);
      Transform3D yrot = new Transform3D(); 
      yrot.rotY(-Math.PI/5.0d);
      sceneTransform.mul(yrot);
      sceneTransformGroup = new TransformGroup(sceneTransform);
      sceneTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      sceneTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
      root.addChild(sceneTransformGroup);

      // Create bounds for the background and lights
      bounds =  new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0f);
      
      // Set up the background
      Background bg = new Background(bgColor);
      bg.setApplicationBounds(bounds);
      sceneTransformGroup.addChild(bg);

      // Create the transform group node for the lights 
      lightTransform1 = new Transform3D();
      lightTransform2 = new Transform3D();
      Vector3d lightPos1 =  new Vector3d(0.0, 0.0, 2.0);
      Vector3d lightPos2 =  new Vector3d(1.0, 0.0, -2.0);
      lightTransform1.set(lightPos1);
      lightTransform2.set(lightPos2);
      light1TransformGroup = new TransformGroup(lightTransform1);
      light2TransformGroup = new TransformGroup(lightTransform2);
      sceneTransformGroup.addChild(light1TransformGroup);
      sceneTransformGroup.addChild(light2TransformGroup);

      // Create lights
      AmbientLight ambLight = new AmbientLight(aColor);
      Light        dirLight1;
      Light        dirLight2;

      Vector3f lightDir1 = new Vector3f(lightPos1);
      Vector3f lightDir2 = new Vector3f(lightPos2);
      lightDir1.negate();
      lightDir2.negate();
      dirLight1 = new DirectionalLight(lightColor, lightDir1);
      dirLight2 = new DirectionalLight(lightColor, lightDir2);

      // Set the influencing bounds
      ambLight.setInfluencingBounds(bounds);
      dirLight1.setInfluencingBounds(bounds);
      dirLight2.setInfluencingBounds(bounds);

      // Add the lights into the scene graph
      sceneTransformGroup.addChild(ambLight);
      sceneTransformGroup.addChild(dirLight1);
      sceneTransformGroup.addChild(dirLight2);

      // Create a cone and add it to the scene graph.
      objTransform = new Transform3D();
      objTransformGroup = new TransformGroup(objTransform);
      objTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      sceneTransformGroup.addChild(objTransformGroup);

      Material m = new Material(coneColor, eColor, coneColor, sColor, 100.0f);
      Appearance a = new Appearance();
      m.setLightingEnable(true);
      a.setMaterial(m);
      Cone cone = new Cone(0.4f, 1.0f); 
      cone.setAppearance(a);
      objTransformGroup.addChild(cone);

      // Create transform groups for each knot point
      // knot point 0 
      Transform3D t3dKnot = new Transform3D();
      t3dKnot.set (pos0);
      TransformGroup k0TransformGroup = new TransformGroup(t3dKnot);
      sceneTransformGroup.addChild(k0TransformGroup);

      // knot point 1 
      t3dKnot = new Transform3D();
      t3dKnot.set (pos1);
      TransformGroup k1TransformGroup = new TransformGroup(t3dKnot);
      sceneTransformGroup.addChild(k1TransformGroup);

      // knot point 2 
      t3dKnot = new Transform3D();
      t3dKnot.set (pos2);
      TransformGroup k2TransformGroup = new TransformGroup(t3dKnot);
      sceneTransformGroup.addChild(k2TransformGroup);

      // knot point 3 
      t3dKnot = new Transform3D();
      t3dKnot.set (pos3);
      TransformGroup k3TransformGroup = new TransformGroup(t3dKnot);
      sceneTransformGroup.addChild(k3TransformGroup);

      // knot point 4 
      t3dKnot = new Transform3D();
      t3dKnot.set (pos4);
      TransformGroup k4TransformGroup = new TransformGroup(t3dKnot);
      sceneTransformGroup.addChild(k4TransformGroup);

      // knot point 5 
      t3dKnot = new Transform3D();
      t3dKnot.set (pos5);
      TransformGroup k5TransformGroup = new TransformGroup(t3dKnot);
      sceneTransformGroup.addChild(k5TransformGroup);

      // Create spheres for each knot point's transform group
      ColoringAttributes sphereColorAttr = new ColoringAttributes();
      sphereColorAttr.setColor(sphereColor);
      Appearance sphereAppearance = new Appearance();
      sphereAppearance.setColoringAttributes(sphereColorAttr);
      k0TransformGroup.addChild(new Sphere(0.10f, sphereAppearance));
      k1TransformGroup.addChild(new Sphere(0.10f, sphereAppearance));
      k2TransformGroup.addChild(new Sphere(0.10f, sphereAppearance));
      k3TransformGroup.addChild(new Sphere(0.10f, sphereAppearance));
      k4TransformGroup.addChild(new Sphere(0.10f, sphereAppearance));
      k5TransformGroup.addChild(new Sphere(0.10f, sphereAppearance));

      return root;
    }

    /*
     * This sets up the key frame data for the spline interpolator. Each knot
     * point has a scale and rotation component specified. The second argument
     * to KBKeyFrame (in this case 0) tells the interpolator that this is
     * to be interpolated using splines. The last three arguments to 
     * KBKeyFrame are Tension, Continuity, and Bias components for each
     * key frame.
     */
    private void setupSplineKeyFrames () {
      // Prepare spline keyframe data
      Point3f p   = new Point3f (pos0);            // position
      float head  = (float)Math.PI/2.0f;           // heading
      float pitch = 0.0f;                          // pitch 
      float bank  = 0.0f;                          // bank 
      Point3f s   = new Point3f(1.0f, 1.0f, 1.0f); // uniform scale
      splineKeyFrames[0] = 
         new KBKeyFrame(0.0f, 0, p, head, pitch, bank, s, 0.0f, 0.0f, 0.0f); 

      p = new Point3f (pos1);
      head  = 0.0f;                               // heading
      pitch = 0.0f;                               // pitch 
      bank  = (float)-Math.PI/2.0f;               // bank 
      s = new Point3f(1.0f, 1.0f, 1.0f);          // uniform scale
      splineKeyFrames[1] = 
         new KBKeyFrame(0.2f, 0, p, head, pitch, bank, s, 0.0f, 0.0f, 0.0f); 

      p = new Point3f (pos2);
      head  = 0.0f;                               // heading
      pitch = 0.0f;                               // pitch 
      bank  = 0.0f;                               // bank 
      s = new Point3f(0.7f, 0.7f, 0.7f);          // uniform scale
      splineKeyFrames[2] = 
         new KBKeyFrame(0.4f, 0, p, head, pitch, bank, s, 0.0f, 0.0f, 0.0f); 

      p = new Point3f (pos3);
      head  = (float)Math.PI/2.0f;                // heading
      pitch = 0.0f;                               // pitch 
      bank  = (float)Math.PI/2.0f;                // bank 
      s = new Point3f(0.5f, 0.5f, 0.5f);          // uniform scale
      splineKeyFrames[3] = 
         new KBKeyFrame(0.6f, 0, p, head, pitch, bank, s, 0.0f, 0.0f, 0.0f); 

      p = new Point3f (pos4);
      head  = (float)-Math.PI/2.0f;               // heading
      pitch = (float)-Math.PI/2.0f;               // pitch 
      bank  = (float)Math.PI/2.0f;                // bank 
      s = new Point3f(0.4f, 0.4f, 0.4f);          // uniform scale
      splineKeyFrames[4] = 
         new KBKeyFrame(0.8f, 0, p, head, pitch, bank, s, 0.0f, 0.0f, 0.0f); 

      p = new Point3f (pos5);
      head  = 0.0f;                               // heading
      pitch = 0.0f;                               // pitch 
      bank  = 0.0f;                               // bank 
      s = new Point3f(1.0f, 1.0f, 1.0f);          // uniform scale
      splineKeyFrames[5] = 
         new KBKeyFrame(1.0f, 0, p, head, pitch, bank, s, 0.0f, 0.0f, 0.0f); 
    }

    /*
     * This sets up the key frame data for the linear interpolator. Each knot
     * point has a scale and rotation component specified. The second argument
     * to KBKeyFrame (in this case 1) tells the interpolator that this is
     * to be interpolated linearly. The last three arguments to TCBKeyFrame
     * are Tension, Continuity, and Bias components for each key frame.
     */
    private void setupLinearKeyFrames () {
      // Prepare linear keyframe data
      Point3f p = new Point3f (pos0);
      float head  = 0.0f;                          // heading
      float pitch = 0.0f;                          // pitch 
      float bank  = 0.0f;                          // bank 
      Point3f s = new Point3f(1.0f, 1.0f, 1.0f);   // uniform scale
      linearKeyFrames[0] = 
         new KBKeyFrame(0.0f, 1, p, head, pitch, bank, s, 0.0f, 0.0f, 0.0f); 

      p = new Point3f (pos1);
      linearKeyFrames[1] = 
         new KBKeyFrame(0.2f, 1, p, head, pitch, bank, s, 0.0f, 0.0f, 0.0f); 

      p = new Point3f (pos2);
      linearKeyFrames[2] = 
         new KBKeyFrame(0.4f, 1, p, head, pitch, bank, s, 0.0f, 0.0f, 0.0f); 

      p = new Point3f (pos3);
      linearKeyFrames[3] = 
         new KBKeyFrame(0.6f, 1, p, head, pitch, bank, s, 0.0f, 0.0f, 0.0f); 

      p = new Point3f (pos4);
      linearKeyFrames[4] = 
         new KBKeyFrame(0.8f, 1, p, head, pitch, bank, s, 0.0f, 0.0f, 0.0f); 

      p = new Point3f (pos5);
      linearKeyFrames[5] = 
         new KBKeyFrame(1.0f, 1, p, head, pitch, bank, s, 0.0f, 0.0f, 0.0f); 
    }


    /* 
     * This sets up alpha for the interpolator
     */
    private void setupAnimationData () {
      yAxis = new Transform3D();
      animAlpha = new Alpha (-1,Alpha.INCREASING_ENABLE,0,0,duration,0,0,0,0,0);
    }

    /*
     * create a spline and a linear interpolator, but we will activate only
     * one in startInterpolator()
     */
    private void createInterpolators () {

      behaviorBranch = new BranchGroup();

      // create spline interpolator 
      splineInterpolator =
         new KBRotPosScaleSplinePathInterpolator(animAlpha, objTransformGroup,
                                                  yAxis, splineKeyFrames); 
      splineInterpolator.setSchedulingBounds(bounds);
      behaviorBranch.addChild(splineInterpolator);
       
      // create linear interpolator 
      linearInterpolator =
         new KBRotPosScaleSplinePathInterpolator(animAlpha, objTransformGroup,
                                                  yAxis, linearKeyFrames); 
      linearInterpolator.setSchedulingBounds(bounds);
      behaviorBranch.addChild(linearInterpolator);
      objTransformGroup.addChild(behaviorBranch);

    }

    /*
     * This activates one of the interpolators depending on the state of the
     * linear boolean flag which may be toggled by the user using the choice
     * menu.
     */
    public void startInterpolator () {
      if (animationOn) {
        if (linear) {
          splineInterpolator.setEnable(false);
          linearInterpolator.setEnable(true);
        } else {
          linearInterpolator.setEnable(false);
          splineInterpolator.setEnable(true);
        }
      }
    }


    /* 
     * Toggle animation  
     */
    public void actionPerformed (ActionEvent event) {
      Object source = event.getSource();
      if (source == animateButton) {
        try {
          // toggle animation
          if (animationOn) {
            animationOn = false;
            splineInterpolator.setEnable(false);
            linearInterpolator.setEnable(false);
            animateButton.setLabel("Start Animation");
          } else {
            animationOn = true;
            startInterpolator();
            animateButton.setLabel("Stop Animation");
          }
        } catch (Exception e) {
           System.err.println ("Exception " + e);
        }
      }
    }

    /* 
     * Toggle the interpolators  
     */
    public void itemStateChanged (ItemEvent event) {
      Object source = event.getSource();
      ItemSelectable ie = event.getItemSelectable();
      if (source == interpChoice) {
        try {
          if (ie.getSelectedObjects()[0] == "Spline") {
            linear = false;
          }
          if (ie.getSelectedObjects()[0] == "Linear") {
            linear = true;
          }
          startInterpolator();
        } catch (Exception e) {
           System.err.println ("Exception " + e);
        }
      }
    }


    /* 
     * Adjust the speed of the animations 
     */
    public void adjustmentValueChanged (AdjustmentEvent e) {
      int value = e.getValue();
      duration = 6000 - (500 * value);
      animAlpha.setIncreasingAlphaDuration(duration);
    }



    public static void main(String[] args) {
        Frame frame = new MainFrame(new SplineAnim(), 500, 600);
    }
}
