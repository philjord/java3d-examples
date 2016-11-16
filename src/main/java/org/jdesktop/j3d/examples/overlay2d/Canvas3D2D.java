/*
 * Copyright (c) 2016 JogAmp Community. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
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