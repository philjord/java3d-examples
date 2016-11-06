/*
 * $RCSfile$
 *
 * Copyright (c) 2007 Sun Microsystems, Inc. All rights reserved.
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

package org.jdesktop.j3d.examples.package_info;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JTextArea;

import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.GraphicsConfigTemplate3D;
import org.jogamp.java3d.VirtualUniverse;

public class QueryProperties extends javax.swing.JFrame {

    public static void printProps(JTextArea textArea, Map map, String[] propList) {
	// Create an alphabetical list of keys
	List  keyList = new ArrayList(map.keySet());
	Collections.sort(keyList);
	Iterator it;

	// Collection used to remember the properties we've already
	// printed, so we don't print them twice
	HashSet hs = new HashSet();

	// Print out the values for the caller-specified properties
	String key;
	for (int i = 0; i < propList.length; i++) {
	    int len = propList[i].length();
	    int idxWild = propList[i].indexOf('*');
	    if (idxWild < 0) {
		key = propList[i];
		if (!hs.contains(key)) {
		    textArea.append(key + " = " + map.get(key) + "\n");
		    hs.add(key);
		}
	    }
	    else if (idxWild == len-1) {
		String pattern = propList[i].substring(0, len-1);
		it = keyList.iterator();
		while (it.hasNext()) {
		    key = (String)it.next();
		    if (key.startsWith(pattern) && !hs.contains(key)) {
			textArea.append(key + " = " + map.get(key) + "\n");
			hs.add(key);
		    }
		}
	    }
	    else {
		textArea.append(propList[i] +
				   " = ERROR: KEY WITH EMBEDDED WILD CARD IGNORED\n");
	    }
	}

	// Print out the values for those properties not already printed
	it = keyList.iterator();
	while (it.hasNext()) {
	    key = (String)it.next();
	    if (!hs.contains(key)) {
		textArea.append(key + " = " + map.get(key) + "\n");
	    }
	}

    }

    /** Creates new form QueryProperties */
    public QueryProperties() {
        initComponents();

        VirtualUniverse vu = new VirtualUniverse();
	Map vuMap = vu.getProperties();
	final String[] vuPropList = {
	    "j3d.version",
 	    "j3d.vendor",
 	    "j3d.specification.version",
 	    "j3d.specification.vendor",
	    "j3d.*"
	    // Just print all other properties in alphabetical order
	};

	printProps(myTextArea, vuMap, vuPropList);
	myTextArea.append("\n");

	GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();

	/* We need to set this to force choosing a pixel format
	   that support the canvas.
	*/
	template.setStereo(template.PREFERRED);
	template.setSceneAntialiasing(template.PREFERRED);

        GraphicsConfiguration config =
	    GraphicsEnvironment.getLocalGraphicsEnvironment().
                getDefaultScreenDevice().getBestConfiguration(template);

	Map c3dMap = new Canvas3D(config).queryProperties();
	final String[] c3dPropList = {
	    "native.*",
	    "doubleBufferAvailable",
	    "stereoAvailable",
	    "sceneAntialiasing*",
	    "compressedGeometry.majorVersionNumber",
	    "compressedGeometry.minorVersionNumber",
	    "compressedGeometry.*",
	    "textureUnitStateMax",
	    "textureWidthMax",
	    "textureHeightMax",
	    // Just print all other properties in alphabetical order
	};

	printProps(myTextArea, c3dMap, c3dPropList);
    }

    // ----------------------------------------------------------------
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        myTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("QueryProperties");
        jScrollPane1.setPreferredSize(new java.awt.Dimension(400, 500));
        myTextArea.setColumns(20);
        myTextArea.setEditable(false);
        myTextArea.setRows(5);
        jScrollPane1.setViewportView(myTextArea);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {System.setProperty("sun.awt.noerasebackground", "true"); 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new QueryProperties().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea myTextArea;
    // End of variables declaration//GEN-END:variables
    
}
