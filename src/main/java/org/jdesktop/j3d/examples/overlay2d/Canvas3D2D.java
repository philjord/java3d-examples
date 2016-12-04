/*
 * Copyright (c) 2016 JogAmp Community. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the JogAmp Community.
 *
 */

package org.jdesktop.j3d.examples.overlay2d;

import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.jdesktop.j3d.examples.Resources;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.J3DGraphics2D;
import org.jogamp.java3d.utils.image.ImageException;

/**
 * This is an extension to the Canvas3D with the postRender method overridden to draw some things on the 
 * 2DGraphics of the Canvas3D 
 */
public class Canvas3D2D extends Canvas3D
{
	private URL bgImage = null;
	private BufferedImage bufferedImage;

	public Canvas3D2D(GraphicsConfiguration gc)
	{
		super(gc);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void postRender()
	{
		J3DGraphics2D g = getGraphics2D();

		g.setColor(new Color(1.0f, 1.0f, 1.0f));
		g.drawString("This is an example String", 50, 20);

		if (bufferedImage == null)
		{
			// the path to the image for an applet
			bgImage = Resources.getResource("main/resources/images/bg.jpg");
			if (bgImage == null)
			{
				System.err.println("main/resources/images/bg.jpg not found");
				System.exit(1);
			}

			bufferedImage = (BufferedImage) java.security.AccessController.doPrivileged(new java.security.PrivilegedAction() {
				@Override
				public Object run()
				{
					try
					{
						return ImageIO.read(bgImage);
					}
					catch (IOException e)
					{
						throw new ImageException(e);
					}
				}
			});
		}
		g.drawImage(bufferedImage, 10, 50, null);

		g.setColor(new Color(1.0f, 0f, 0f));
		// draw a cross hair
		g.drawLine((this.getWidth() / 2) - 5, (this.getHeight() / 2), (this.getWidth() / 2) + 5, (this.getHeight() / 2));
		g.drawLine((this.getWidth() / 2), (this.getHeight() / 2) - 5, (this.getWidth() / 2), (this.getHeight() / 2) + 5);

		g.flush(false);

	}

}