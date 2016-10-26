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

import java.awt.GraphicsConfiguration;

import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.J3DGraphics2D;

/**
 * This is an extension to the Canvas3D with the postRender method overridden to draw some things on the 
 * 2DGraphics of the Canvas3D 
 */
public class Canvas3D2D extends Canvas3D
{

	public Canvas3D2D(GraphicsConfiguration gc)
	{
		super(gc);
	}

	@Override
	public void postRender()
	{
		J3DGraphics2D g = getGraphics2D();

		// draw a cross hair
		g.drawLine((this.getWidth() / 2) - 5, (this.getHeight() / 2), (this.getWidth() / 2) + 5, (this.getHeight() / 2));
		g.drawLine((this.getWidth() / 2), (this.getHeight() / 2) - 5, (this.getWidth() / 2), (this.getHeight() / 2) + 5);

		g.drawString("This is an example String", 50, 20);

		// etc e.g.
		//g.drawImage(getBufferedImage(), 10, 50, null);

		g.flush(false);

	}

}