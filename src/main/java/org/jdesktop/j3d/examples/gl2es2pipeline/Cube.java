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
package org.jdesktop.j3d.examples.gl2es2pipeline;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.jogamp.java3d.GeometryArray;
import org.jogamp.java3d.J3DBuffer;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.TriangleArray;
import org.jogamp.java3d.utils.shader.SimpleShaderAppearance;

/**
 * @author Administrator
 *
 */
public class Cube extends Shape3D
{

	private static final float[] verts = {
			// front face
			1.0f, -1.0f, 1.0f, //1
			1.0f, 1.0f, 1.0f, //2
			-1.0f, 1.0f, 1.0f, //3
			1.0f, -1.0f, 1.0f, //1
			-1.0f, 1.0f, 1.0f, //3
			-1.0f, -1.0f, 1.0f, //4
			// back face
			-1.0f, -1.0f, -1.0f, //1
			-1.0f, 1.0f, -1.0f, //2
			1.0f, 1.0f, -1.0f, //3
			-1.0f, -1.0f, -1.0f, //1
			1.0f, 1.0f, -1.0f, //3
			1.0f, -1.0f, -1.0f, //4
			// right face
			1.0f, -1.0f, -1.0f, //1
			1.0f, 1.0f, -1.0f, //2
			1.0f, 1.0f, 1.0f, //3
			1.0f, -1.0f, -1.0f, //1
			1.0f, 1.0f, 1.0f, //3
			1.0f, -1.0f, 1.0f, //4
			// left face
			-1.0f, -1.0f, 1.0f, //1
			-1.0f, 1.0f, 1.0f, //2
			-1.0f, 1.0f, -1.0f, //3
			-1.0f, -1.0f, 1.0f, //1
			-1.0f, 1.0f, -1.0f, //3
			-1.0f, -1.0f, -1.0f, //4
			// top face
			1.0f, 1.0f, 1.0f, //1
			1.0f, 1.0f, -1.0f, //2
			-1.0f, 1.0f, -1.0f, //3
			1.0f, 1.0f, 1.0f, //1
			-1.0f, 1.0f, -1.0f, //3
			-1.0f, 1.0f, 1.0f, //4			
			// bottom face
			-1.0f, -1.0f, 1.0f, //1
			-1.0f, -1.0f, -1.0f, //2
			1.0f, -1.0f, -1.0f, //3
			-1.0f, -1.0f, 1.0f, //1
			1.0f, -1.0f, -1.0f, //3
			1.0f, -1.0f, 1.0f, };//4

	private static final float[] colors = {
			// front face (red)
			1.0f, 0.0f, 0.0f, //1
			1.0f, 0.0f, 0.0f, //2
			1.0f, 0.0f, 0.0f, //3
			1.0f, 0.0f, 0.0f, //1
			1.0f, 0.0f, 0.0f, //3
			1.0f, 0.0f, 0.0f, //4
			// back face (green)
			0.0f, 1.0f, 0.0f, //1
			0.0f, 1.0f, 0.0f, //2
			0.0f, 1.0f, 0.0f, //3
			0.0f, 1.0f, 0.0f, //1
			0.0f, 1.0f, 0.0f, //3
			0.0f, 1.0f, 0.0f, //4			
			// right face (blue)
			0.0f, 0.0f, 1.0f, //1
			0.0f, 0.0f, 1.0f, //2
			0.0f, 0.0f, 1.0f, //3
			0.0f, 0.0f, 1.0f, //1
			0.0f, 0.0f, 1.0f, //3
			0.0f, 0.0f, 1.0f, //4
			// left face (yellow)
			1.0f, 1.0f, 0.0f, //1
			1.0f, 1.0f, 0.0f, //2
			1.0f, 1.0f, 0.0f, //3
			1.0f, 1.0f, 0.0f, //1
			1.0f, 1.0f, 0.0f, //3
			1.0f, 1.0f, 0.0f, //4
			// top face (magenta)
			1.0f, 0.0f, 1.0f, //1
			1.0f, 0.0f, 1.0f, //2
			1.0f, 0.0f, 1.0f, //3
			1.0f, 0.0f, 1.0f, //1
			1.0f, 0.0f, 1.0f, //3
			1.0f, 0.0f, 1.0f, //4
			// bottom face (cyan)
			0.0f, 1.0f, 1.0f, //1
			0.0f, 1.0f, 1.0f, //2
			0.0f, 1.0f, 1.0f, //3
			0.0f, 1.0f, 1.0f, //1
			0.0f, 1.0f, 1.0f, //3
			0.0f, 1.0f, 1.0f, };//4

	/**
	 * Constructs a color cube with unit scale.  The corners of the
	 * color cube are [-1,-1,-1] and [1,1,1].
	 */
	public Cube()
	{
		TriangleArray cube = new TriangleArray(36,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);

		cube.setCoordRefBuffer(new J3DBuffer(makeFloatBuffer(verts)));
		cube.setColorRefBuffer(new J3DBuffer(makeFloatBuffer(colors)));

		this.setGeometry(cube);
		this.setAppearance(new SimpleShaderAppearance());
	}

	/**
	 * Constructs a color cube with the specified scale.  The corners of the
	 * color cube are [-scale,-scale,-scale] and [scale,scale,scale].
	 * @param scale the scale of the cube
	 */
	public Cube(double scale)
	{
		TriangleArray cube = new TriangleArray(36,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);

		float scaledVerts[] = new float[verts.length];
		for (int i = 0; i < verts.length; i++)
			scaledVerts[i] = verts[i] * (float) scale;

		cube.setCoordRefBuffer(new J3DBuffer(makeFloatBuffer(scaledVerts)));
		cube.setColorRefBuffer(new J3DBuffer(makeFloatBuffer(colors)));

		this.setGeometry(cube);

		this.setAppearance(new SimpleShaderAppearance());
	}

	public Cube(double scale, float r, float g, float b)
	{
		TriangleArray cube = new TriangleArray(36,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);

		float scaledVerts[] = new float[verts.length];
		for (int i = 0; i < verts.length; i++)
			scaledVerts[i] = verts[i] * (float) scale;

		cube.setCoordRefBuffer(new J3DBuffer(makeFloatBuffer(scaledVerts)));

		float colorsSet[] = new float[36 * 3];
		for (int i = 0; i < 36; i++)
		{
			colorsSet[i * 3 + 0] = r;
			colorsSet[i * 3 + 1] = g;
			colorsSet[i * 3 + 2] = b;
		}

		cube.setColorRefBuffer(new J3DBuffer(makeFloatBuffer(colorsSet)));

		this.setGeometry(cube);
		this.setAppearance(new SimpleShaderAppearance());
	}

	/**
		 * Constructs a color cube with the specified scale.  The corners of the
		 * color cube are [-scale,-scale,-scale] and [scale,scale,scale].
		 * @param scale the scale of the cube
		 */
	public Cube(double xScale, double yScale, double zScale)
	{
		TriangleArray cube = new TriangleArray(36,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);

		float scaledVerts[] = new float[verts.length];
		for (int i = 0; i < verts.length; i += 3)
		{
			scaledVerts[i + 0] = verts[i + 0] * (float) xScale;
			scaledVerts[i + 1] = verts[i + 1] * (float) yScale;
			scaledVerts[i + 2] = verts[i + 2] * (float) zScale;
		}

		cube.setCoordRefBuffer(new J3DBuffer(makeFloatBuffer(scaledVerts)));
		cube.setColorRefBuffer(new J3DBuffer(makeFloatBuffer(colors)));

		this.setGeometry(cube);
		this.setAppearance(new SimpleShaderAppearance());
	}

	public Cube(double xScale, double yScale, double zScale, float r, float g, float b)
	{
		TriangleArray cube = new TriangleArray(36,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);

		float scaledVerts[] = new float[verts.length];
		for (int i = 0; i < verts.length; i += 3)
		{
			scaledVerts[i + 0] = verts[i + 0] * (float) xScale;
			scaledVerts[i + 1] = verts[i + 1] * (float) yScale;
			scaledVerts[i + 2] = verts[i + 2] * (float) zScale;
		}

		cube.setCoordRefBuffer(new J3DBuffer(makeFloatBuffer(scaledVerts)));

		float colorsSet[] = new float[36 * 3];
		for (int i = 0; i < 36; i++)
		{
			colorsSet[i * 3 + 0] = r;
			colorsSet[i * 3 + 1] = g;
			colorsSet[i * 3 + 2] = b;
		}

		cube.setColorRefBuffer(new J3DBuffer(makeFloatBuffer(colorsSet)));

		this.setGeometry(cube);
		this.setAppearance(new SimpleShaderAppearance());
	}

	public Cube(float xMin, float yMin, float zMin, float xMax, float yMax, float zMax)
	{
		TriangleArray cube = new TriangleArray(36,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);

		float scaledVerts[] = new float[] {
				// front face
				xMax, yMin, zMax, //1
				xMax, yMax, zMax, //2
				xMin, yMax, zMax, //3
				xMax, yMin, zMax, //1
				xMin, yMax, zMax, //3
				xMin, yMin, zMax, //4
				// back face
				xMin, yMin, zMin, //1
				xMin, yMax, zMin, //2
				xMax, yMax, zMin, //3				
				xMin, yMin, zMin, //1
				xMax, yMax, zMin, //3
				xMax, yMin, zMin, //4
				// right face
				xMax, yMin, zMin, //1
				xMax, yMax, zMin, //2
				xMax, yMax, zMax, //3
				xMax, yMin, zMin, //1
				xMax, yMax, zMax, //3
				xMax, yMin, zMax, //4
				// left face
				xMin, yMin, zMax, //1
				xMin, yMax, zMax, //2
				xMin, yMax, zMin, //3
				xMin, yMin, zMax, //1
				xMin, yMax, zMin, //3
				xMin, yMin, zMin, //4				
				// top face
				xMax, yMax, zMax, //1
				xMax, yMax, zMin, //2
				xMin, yMax, zMin, //3
				xMax, yMax, zMax, //1
				xMin, yMax, zMin, //3
				xMin, yMax, zMax, //4
				// bottom face
				xMin, yMin, zMax, //1
				xMin, yMin, zMin, //2
				xMax, yMin, zMin, //3
				xMin, yMin, zMax, //1
				xMax, yMin, zMin, //3
				xMax, yMin, zMax, };//4

		cube.setCoordRefBuffer(new J3DBuffer(makeFloatBuffer(scaledVerts)));
		cube.setColorRefBuffer(new J3DBuffer(makeFloatBuffer(colors)));

		this.setGeometry(cube);
		this.setAppearance(new SimpleShaderAppearance());
	}

	public static FloatBuffer makeFloatBuffer(float[] arr)
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}

}
