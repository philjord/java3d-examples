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

import java.awt.*;
import java.awt.image.*;
import java.awt.color.*;

// some useful, static image operations

public class ImageOps {

  // flip the image
  public static void flipImage(BufferedImage bImage) {
    int width = bImage.getWidth();
    int height = bImage.getHeight();
    int[] rgbArray = new int[width*height];
    bImage.getRGB(0, 0, width, height, rgbArray, 0, width);
    int[] tempArray = new int[width*height];
    int y2 = 0;
    for (int y = height-1; y >= 0; y--) {
      for (int x = 0; x < width; x++) {
	tempArray[y2*width+x] = rgbArray[y*width+x];
      }
      y2++;
    }
    bImage.setRGB(0, 0, width, height, tempArray, 0, width);
  }


  // convert the image to a specified BufferedImage type and return it
  public static BufferedImage convertImage(BufferedImage bImage, int type) {
    int width = bImage.getWidth();
    int height = bImage.getHeight();
    BufferedImage newImage = new BufferedImage(width, height, type);
    int[] rgbArray = new int[width*height];
    bImage.getRGB(0, 0, width, height, rgbArray, 0, width);
    newImage.setRGB(0, 0, width, height, rgbArray, 0, width);
    return newImage;
  }

  // print out some of the types of BufferedImages
  static void printType(BufferedImage bImage) {
    int type = bImage.getType();
    if (type == BufferedImage.TYPE_4BYTE_ABGR) {
      System.out.println("TYPE_4BYTE_ABGR");
    }
    else if (type == BufferedImage.TYPE_INT_ARGB) {
      System.out.println("TYPE_INT_ARGB");
    }
    else if (type == BufferedImage.TYPE_3BYTE_BGR) {
      System.out.println("TYPE_3BYTE_BGR");
    }
    else if (type == BufferedImage.TYPE_CUSTOM) {
      System.out.println("TYPE_CUSTOM");
    }
    else System.out.println(type);
  }

  public static BufferedImage convertToCustomRGBA(BufferedImage bImage) {
    if (bImage.getType() != BufferedImage.TYPE_INT_ARGB) {
      ImageOps.convertImage(bImage, BufferedImage.TYPE_INT_ARGB);
    }

    int width = bImage.getWidth();
    int height = bImage.getHeight();

    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
    int[] nBits = {8, 8, 8, 8};
    ColorModel cm = new ComponentColorModel(cs, nBits, true, false,
					    Transparency.OPAQUE, 0);
    int[] bandOffset = {0, 1, 2, 3};

    WritableRaster newRaster =
      Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height,
				     width*4, 4, bandOffset, null);
    byte[] byteData = ((DataBufferByte)newRaster.getDataBuffer()).getData();
    Raster origRaster = bImage.getData();
    int[] pixel = new int[4];
    int k = 0;
    for (int j = 0; j < height; j++) {
      for (int i = 0; i < width; i++) {
	pixel = origRaster.getPixel(i, j, pixel);
	byteData[k++] = (byte)(pixel[0]);
	byteData[k++] = (byte)(pixel[1]);
	byteData[k++] = (byte)(pixel[2]);
	byteData[k++] = (byte)(pixel[3]);
      }
    }
    BufferedImage newImage = new BufferedImage(cm, newRaster, false, null);
//     if (newImage.getType() == BufferedImage.TYPE_CUSTOM) {
//       System.out.println("Type is custom");
//     }
    return newImage;
  }

  public static BufferedImage convertToCustomRGB(BufferedImage bImage) {
    if (bImage.getType() != BufferedImage.TYPE_INT_ARGB) {
      ImageOps.convertImage(bImage, BufferedImage.TYPE_INT_ARGB);
    }

    int width = bImage.getWidth();
    int height = bImage.getHeight();

    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
    int[] nBits = {8, 8, 8};
    ColorModel cm = new ComponentColorModel(cs, nBits, false, false, 
				       Transparency.OPAQUE, 0);
    int[] bandOffset = {0, 1, 2};

    WritableRaster newRaster =
      Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, 
				     width*3, 3, bandOffset, null);
    byte[] byteData = ((DataBufferByte)newRaster.getDataBuffer()).getData();
    Raster origRaster = bImage.getData();
    int[] pixel = new int[4];
    int k = 0;
    for (int j = 0; j < height; j++) {
      for (int i = 0; i < width; i++) {
	pixel = origRaster.getPixel(i, j, pixel);
	byteData[k++] = (byte)(pixel[0]);
	byteData[k++] = (byte)(pixel[1]);
	byteData[k++] = (byte)(pixel[2]);
      }
    }
    BufferedImage newImage = new BufferedImage(cm, newRaster, false, null);
//     if (newImage.getType() == BufferedImage.TYPE_CUSTOM) {
//       System.out.println("Type is custom");
//     }
    return newImage;
  }
}

