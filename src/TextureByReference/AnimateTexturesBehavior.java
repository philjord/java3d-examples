/*
 * $RCSfile$
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved.
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
import java.awt.image.BufferedImage;
import java.awt.*;
import com.sun.j3d.utils.image.TextureLoader;
import java.util.Enumeration;

public class AnimateTexturesBehavior extends Behavior {


  // what image are we on
  private int current;
  private int max;

  // the images
  private ImageComponent2D[] images;

  // the target
  private Texture2D texture;
  private Appearance appearance;

  // the wakeup criterion
  private WakeupCriterion wakeupC;

  // are the images flipped?
  private boolean flip;

  // need the current type because by copy changes all images
  // to TYPE_INT_ARGB
  private int currentType;

  // for custom types
  public static final int TYPE_CUSTOM_RGBA = 0x01;
  public static final int TYPE_CUSTOM_RGB = 0x02;

  private int customType;

  // create a new AnimateTextureBehavior
  // initialize the images
  public AnimateTexturesBehavior(Texture2D texP, 
     				 java.net.URL[] fnames,
				 Appearance appP,
				 TextureByReference applet) {
    int size = fnames.length;
    images = new ImageComponent2D[size];
    BufferedImage bImage;
    TextureLoader loader;
    for (int i = 0; i < size; i++) {
      loader = new TextureLoader(fnames[i],
 				 TextureLoader.BY_REFERENCE |
 				 TextureLoader.Y_UP, applet);
      images[i] = loader.getImage();
      bImage = images[i].getImage();
      
      // convert the image to TYPE_4BYTE_ABGR
      currentType = BufferedImage.TYPE_4BYTE_ABGR;
      bImage = ImageOps.convertImage(bImage, currentType);
      // flip the image
      flip = true;
      ImageOps.flipImage(bImage);
      
      // set the image on the ImageComponent to the new one
      images[i].set(bImage);

      images[i].setCapability(ImageComponent.ALLOW_IMAGE_READ);
      images[i].setCapability(ImageComponent.ALLOW_FORMAT_READ);
    }
    texture = texP;
    current = 0;
    max = size;
    wakeupC = new WakeupOnElapsedFrames(20);
    appearance = appP;
  }

  // initialize to the first image
  public void initialize() {
    texture.setImage(0, images[current]);
    if (current < max-1) current++;
    else current = 0;
    wakeupOn(wakeupC);
  }

  // procesStimulus changes the ImageComponent of the texture
  public void processStimulus(Enumeration criteria) {
    //    ImageOps.printType(images[current].getImage());
    texture.setImage(0, images[current]);
    appearance.setTexture(texture);
    if (current < max-1) current++; 
    else current = 0;
    wakeupOn(wakeupC);
  }

  // flip the image -- useful depending on yUp
  public void setFlipImages(boolean b) {
    // double check that flipping is necessary
    if (b != flip) {
      BufferedImage bImage;

      // these are the same for all images so get info once
      int format = images[0].getFormat();
      boolean byRef = images[0].isByReference();
      boolean yUp = images[0].isYUp();
      
      // flip all the images
      // have to new ImageComponents because can't set the image at runtime
      for (int i = 0; i < images.length; i++) {
	bImage = images[i].getImage();
	ImageOps.flipImage(bImage);
	// if we are byRef and the bImage type does not match currentType
	// we need to convert it.  If we are not byRef we will 
	// save converting until it is changed to byRef
	if (byRef && bImage.getType() != currentType) {
	  if (currentType != BufferedImage.TYPE_CUSTOM) {
	    bImage = ImageOps.convertImage(bImage, currentType);
	  }
	  else if (customType == this.TYPE_CUSTOM_RGBA) {
	    bImage = ImageOps.convertToCustomRGBA(bImage);
	  }
	  else {
	    bImage = ImageOps.convertToCustomRGB(bImage);
	  }
	}
	images[i] = new ImageComponent2D(format, bImage, byRef, yUp);
	images[i].setCapability(ImageComponent.ALLOW_IMAGE_READ);
	images[i].setCapability(ImageComponent.ALLOW_FORMAT_READ);
      }
      
      // set flip to new value
      flip = b;
    }
  }

  // create new ImageComponents with yUp set to the parameter.  yUp on
  // an ImageComponent cannot be changed at runtim
  public void setYUp(boolean b) {
    // double check that changing yUp is necessary
    if (b != images[0].isYUp()) {

      // these are the same for all images so get info once
      int format = images[0].getFormat();
      boolean byRef = images[0].isByReference();
      
      // reset yUp on all the images -- have to new ImageComponents because
      // cannot change the value at runtime
      for (int i = 0; i < images.length; i++) {
	// if we are byRef and the bImage type does not match currentType
	// we need to convert it.  If we are not byRef we will 
	// save converting until it is changed to byRef
	BufferedImage bImage = images[i].getImage();
	if (byRef && bImage.getType() != currentType) {
	  //	  bImage = ImageOps.convertImage(bImage, currentType);
	  if (currentType != BufferedImage.TYPE_CUSTOM) {
	    bImage = ImageOps.convertImage(bImage, currentType);
	  }
	  else if (customType == this.TYPE_CUSTOM_RGBA) {
	    bImage = ImageOps.convertToCustomRGBA(bImage);
	  }
	  else {
	    bImage = ImageOps.convertToCustomRGB(bImage);
	  }
	}
	images[i] = new ImageComponent2D(format, bImage, 
					 byRef, b);
	images[i].setCapability(ImageComponent.ALLOW_IMAGE_READ);
	images[i].setCapability(ImageComponent.ALLOW_FORMAT_READ);
      }
    }
  }

  // create new ImageComponents with ByReference set by parameter.  
  // by reference cannot be changed on an image component at runtime 
  public void setByReference(boolean b) {
    // double check that changing is necessary
    if (b != images[0].isByReference()) {

      // these are the same for all images so get info once
      int format = images[0].getFormat();
      boolean yUp = images[0].isYUp();

      // reset yUp on all the images
      // have to new ImageComponents because cannot set value
      for (int i = 0; i < images.length; i++) {
	// if the bImage type does not match currentType and we are setting
	// to byRef we need to convert it
	BufferedImage bImage = images[i].getImage();
	if (bImage.getType() != currentType && b) {
	  //	  bImage = ImageOps.convertImage(bImage, currentType);
	  if (currentType != BufferedImage.TYPE_CUSTOM) {
	    bImage = ImageOps.convertImage(bImage, currentType);
	  }
	  else if (customType == this.TYPE_CUSTOM_RGBA) {
	    bImage = ImageOps.convertToCustomRGBA(bImage);
	  }
	  else {
	    bImage = ImageOps.convertToCustomRGB(bImage);
	  }
	}
	images[i] = new ImageComponent2D(format, bImage, b, yUp);
	images[i].setCapability(ImageComponent.ALLOW_IMAGE_READ);
	images[i].setCapability(ImageComponent.ALLOW_FORMAT_READ);
      }
    }
  }

  // make a new wakeup criterion object based on the new delay time
  public void setFrameDelay(int delay) {
    wakeupC = new WakeupOnElapsedFrames(delay);
  }

  //change the type of image
  public void setImageType(int newType) {
    currentType = newType;

    // only need to change the images if we are byRef otherwise will change
    // them when we chnage to byRef
    if (images[0].isByReference() == true) {
      // this information is the same for all
      int format = images[0].getFormat();
      boolean yUp = images[0].isYUp();
      boolean byRef = true;
      for (int i = 0; i < images.length; i++) {
	BufferedImage bImage = images[i].getImage();
	bImage = ImageOps.convertImage(bImage, currentType);
	images[i] = new ImageComponent2D(format, bImage, byRef, yUp);
	images[i].setCapability(ImageComponent.ALLOW_IMAGE_READ);
	images[i].setCapability(ImageComponent.ALLOW_FORMAT_READ);
      }
    }
  }

  public void setImageTypeCustomRGBA() {
    currentType = BufferedImage.TYPE_CUSTOM;
    customType = this.TYPE_CUSTOM_RGBA;

    // only need to change images if we are byRef otherwise will change
    // them when we change to byRef
    if (images[0].isByReference()) {
      // this information is the same for all
      int format = images[0].getFormat();
      boolean yUp = images[0].isYUp();
      boolean byRef = true;
      for (int i = 0; i < images.length; i++) {
	BufferedImage bImage = images[i].getImage();
	bImage = ImageOps.convertToCustomRGBA(bImage);
	images[i] = new ImageComponent2D(format, bImage, byRef, yUp);
	images[i].setCapability(ImageComponent.ALLOW_IMAGE_READ);
	images[i].setCapability(ImageComponent.ALLOW_FORMAT_READ);
      }
    }
  }

  public void setImageTypeCustomRGB() {
    currentType = BufferedImage.TYPE_CUSTOM;
    customType = this.TYPE_CUSTOM_RGB;

    // only need to change images if we are byRef otherwise will change
    // them when we change to byRef
    if (images[0].isByReference()) {
      // this information is the same for all
      int format = images[0].getFormat();
      boolean yUp = images[0].isYUp();
      boolean byRef = true;
      for (int i = 0; i < images.length; i++) {
	BufferedImage bImage = images[i].getImage();
	bImage = ImageOps.convertToCustomRGB(bImage);
	images[i] = new ImageComponent2D(format, bImage, byRef, yUp);
	images[i].setCapability(ImageComponent.ALLOW_IMAGE_READ);
	images[i].setCapability(ImageComponent.ALLOW_FORMAT_READ);
      }
    }
  }
}
