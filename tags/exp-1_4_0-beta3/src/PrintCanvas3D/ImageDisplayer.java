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

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.event.*;

class ImageDisplayer extends JFrame implements ActionListener {
    BufferedImage bImage;

    private class ImagePanel extends JPanel {
	public void paint(Graphics g) {
	    g.setColor(Color.black);
	    g.fillRect(0, 0, getSize().width, getSize().height);
	    g.drawImage(bImage, 0, 0, this);
	}

	private ImagePanel() {
	    setPreferredSize(new Dimension(bImage.getWidth(),
					   bImage.getHeight()));
	}
    }

    private JMenuItem printItem;
    private JMenuItem closeItem;

    private void freeResources() {
	this.removeAll();
	this.setVisible(false);
	bImage = null;
    }

    public void actionPerformed (ActionEvent event) {
	Object target = event.getSource();

	if (target == printItem) {
	    new ImagePrinter(bImage).print();
	}
	else if (target == closeItem) {
	    freeResources();
	}
    }

    private JMenuBar createMenuBar() {
	JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu("File");
	printItem = new JMenuItem("Print...");
	printItem.addActionListener(this);
	closeItem = new JMenuItem("Close");
	closeItem.addActionListener(this);
	fileMenu.add(printItem);
	fileMenu.add(new JSeparator());
	fileMenu.add(closeItem);
	menuBar.add(fileMenu);
	return menuBar;
    }

    ImageDisplayer(BufferedImage bImage) {
	this.bImage = bImage;
	this.setTitle("Off-screen Canvas3D Snapshot");

	// Create and initialize menu bar
	this.setJMenuBar(createMenuBar());

	// Create scroll pane, and embedded image panel
	ImagePanel imagePanel = new ImagePanel();
	JScrollPane scrollPane = new JScrollPane(imagePanel);
	scrollPane.getViewport().setPreferredSize(new Dimension(700, 700));

	// Handle the close event
	this.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent winEvent) {
		freeResources();
	    }
	});

	// Add scroll pane to the frame and make it visible
	this.getContentPane().add(scrollPane);
	this.pack();
	this.setVisible(true);
    }
}
