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

package org.jdesktop.j3d.examples.alternate_appearance;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.jdesktop.j3d.examples.gl2es2pipeline.SimpleShaderAppearance;
import org.jogamp.java3d.AlternateAppearance;
import org.jogamp.java3d.AmbientLight;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BoundingLeaf;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.Bounds;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.DirectionalLight;
import org.jogamp.java3d.Group;
import org.jogamp.java3d.Material;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.utils.applet.MainFrame;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3f;

public class AlternateAppearanceBoundsTestGL2ES2 extends JApplet implements ActionListener
{

	Material mat1, altMat;
	Appearance app, otherApp;
	JComboBox<?> altAppMaterialColor;
	JComboBox<?> appMaterialColor;
	JCheckBox useBoundingLeaf;
	JCheckBox override;
	JComboBox<?> boundsType;
	private Group content1 = null;
	AlternateAppearance altApp;
	Shape3D[] shapes1;
	boolean boundingLeafOn = false;
	// Globally used colors
	Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
	Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
	Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
	Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);
	Color3f[] colors = { white, red, green, blue };

	private Bounds worldBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), // Center
			1000.0); // Extent
	private Bounds smallBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), // Center
			0.25); // Extent
	private Bounds tinyBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), // Center
			0.05); // Extent
	private BoundingLeaf leafBounds = null;
	private int currentBounds = 2;

	private Bounds[] allBounds = { tinyBounds, smallBounds, worldBounds };

	DirectionalLight light1 = null;

	// Get the current bounding leaf position
	//private int currentPosition = 0;
	//    Point3f pos = (Point3f)positions[currentPosition].value;

	private SimpleUniverse u = null;

	public AlternateAppearanceBoundsTestGL2ES2()
	{
	}

	@Override
	public void init()
	{
		System.setProperty("sun.awt.noerasebackground", "true");
		Container contentPane = getContentPane();

		Canvas3D c = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		contentPane.add("Center", c);

		BranchGroup scene = createSceneGraph();
		// SimpleUniverse is a Convenience Utility class
		u = new SimpleUniverse(c);

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		u.getViewingPlatform().setNominalViewingTransform();
		u.addBranchGraph(scene);

		// Create GUI
		JPanel p = new JPanel();
		BoxLayout boxlayout = new BoxLayout(p, BoxLayout.Y_AXIS);
		p.add(createBoundsPanel());
		p.add(createMaterialPanel());
		p.setLayout(boxlayout);

		contentPane.add("South", p);
	}

	@Override
	public void destroy()
	{
		u.cleanup();
	}

	BranchGroup createSceneGraph()
	{
		BranchGroup objRoot = new BranchGroup();

		// Create an alternate appearance
		otherApp = new SimpleShaderAppearance(true, false);
		//otherApp = new Appearance();
		altMat = new Material();
		altMat.setCapability(Material.ALLOW_COMPONENT_WRITE);
		altMat.setDiffuseColor(new Color3f(0.0f, 1.0f, 0.0f));
		otherApp.setMaterial(altMat);

		altApp = new AlternateAppearance();
		altApp.setAppearance(otherApp);
		altApp.setCapability(AlternateAppearance.ALLOW_BOUNDS_WRITE);
		altApp.setCapability(AlternateAppearance.ALLOW_INFLUENCING_BOUNDS_WRITE);
		altApp.setInfluencingBounds(worldBounds);
		objRoot.addChild(altApp);

		// Build foreground geometry
		Appearance app1 = new SimpleShaderAppearance(true, false);
		//Appearance app1 = new Appearance();
		mat1 = new Material();
		mat1.setCapability(Material.ALLOW_COMPONENT_WRITE);
		mat1.setDiffuseColor(new Color3f(1.0f, 0.0f, 0.0f));
		app1.setMaterial(mat1);
		content1 = new SphereGroup(0.05f, // radius of spheres
				0.15f, // x spacing
				0.15f, // y spacing
				5, // number of spheres in X
				5, // number of spheres in Y
				app1, // appearance
				true); // alt app override = true
		objRoot.addChild(content1);
		shapes1 = ((SphereGroup) content1).getShapes();

		// Add lights
		light1 = new DirectionalLight();
		light1.setEnable(true);
		light1.setColor(new Color3f(0.2f, 0.2f, 0.2f));
		light1.setDirection(new Vector3f(1.0f, 0.0f, -1.0f));
		light1.setInfluencingBounds(worldBounds);
		light1.setCapability(DirectionalLight.ALLOW_INFLUENCING_BOUNDS_WRITE);
		light1.setCapability(DirectionalLight.ALLOW_BOUNDS_WRITE);
		objRoot.addChild(light1);

		// Add an ambient light to dimly illuminate the rest of
		// the shapes in the scene to help illustrate that the
		// directional lights are being scoped... otherwise it looks
		// like we're just removing shapes from the scene
		AmbientLight ambient = new AmbientLight();
		ambient.setEnable(true);
		ambient.setColor(new Color3f(1.0f, 1.0f, 1.0f));
		ambient.setInfluencingBounds(worldBounds);
		objRoot.addChild(ambient);

		// Define a bounding leaf
		leafBounds = new BoundingLeaf(allBounds[currentBounds]);
		leafBounds.setCapability(BoundingLeaf.ALLOW_REGION_WRITE);
		objRoot.addChild(leafBounds);
		if (boundingLeafOn)
		{
			altApp.setInfluencingBoundingLeaf(leafBounds);
		}
		else
		{
			altApp.setInfluencingBounds(allBounds[currentBounds]);
		}

		return objRoot;
	}

	JPanel createBoundsPanel()
	{
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder("Scopes"));

		String boundsValues[] = { "Tiny Bounds", "Small Bounds", "Big Bounds" };

		boundsType = new JComboBox<Object>(boundsValues);
		boundsType.addActionListener(this);
		boundsType.setSelectedIndex(2);
		panel.add(new JLabel("Bounds"));
		panel.add(boundsType);

		useBoundingLeaf = new JCheckBox("Enable BoundingLeaf", boundingLeafOn);
		useBoundingLeaf.addActionListener(this);
		panel.add(useBoundingLeaf);

		override = new JCheckBox("Enable App Override", false);
		override.addActionListener(this);
		panel.add(override);

		return panel;

	}

	JPanel createMaterialPanel()
	{
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder("Appearance Attributes"));

		String colorVals[] = { "WHITE", "RED", "GREEN", "BLUE" };

		altAppMaterialColor = new JComboBox<Object>(colorVals);
		altAppMaterialColor.addActionListener(this);
		altAppMaterialColor.setSelectedIndex(2);
		panel.add(new JLabel("Alternate Appearance MaterialColor"));
		panel.add(altAppMaterialColor);

		appMaterialColor = new JComboBox<Object>(colorVals);
		appMaterialColor.addActionListener(this);
		appMaterialColor.setSelectedIndex(1);
		panel.add(new JLabel("Normal Appearance MaterialColor"));
		panel.add(appMaterialColor);

		return panel;

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		int i;

		Object target = e.getSource();
		if (target == altAppMaterialColor)
		{
			altMat.setDiffuseColor(colors[altAppMaterialColor.getSelectedIndex()]);
		}
		else if (target == useBoundingLeaf)
		{
			boundingLeafOn = useBoundingLeaf.isSelected();
			if (boundingLeafOn)
			{
				leafBounds.setRegion(allBounds[currentBounds]);
				altApp.setInfluencingBoundingLeaf(leafBounds);
			}
			else
			{
				altApp.setInfluencingBoundingLeaf(null);
				altApp.setInfluencingBounds(allBounds[currentBounds]);
			}

		}
		else if (target == boundsType)
		{
			currentBounds = boundsType.getSelectedIndex();
			if (boundingLeafOn)
			{
				leafBounds.setRegion(allBounds[currentBounds]);
				altApp.setInfluencingBoundingLeaf(leafBounds);
			}
			else
			{
				altApp.setInfluencingBoundingLeaf(null);
				altApp.setInfluencingBounds(allBounds[currentBounds]);
			}

		}
		else if (target == override)
		{
			for (i = 0; i < shapes1.length; i++)
				shapes1[i].setAppearanceOverrideEnable(override.isSelected());
		}
		else if (target == appMaterialColor)
		{
			mat1.setDiffuseColor(colors[appMaterialColor.getSelectedIndex()]);
		}

	}

	public static void main(String[] args)
	{
		System.setProperty("sun.awt.noerasebackground", "true");
		System.setProperty("j3d.rend", "jogl2es2");
		System.setProperty("j3d.displaylist", "false");
		new MainFrame(new AlternateAppearanceBoundsTestGL2ES2(), 800, 800);
	}

}
