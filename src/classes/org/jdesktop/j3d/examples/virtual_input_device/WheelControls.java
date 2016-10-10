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

package org.jdesktop.j3d.examples.virtual_input_device;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;

public class WheelControls extends Canvas implements RotationControls, MouseMotionListener, MouseListener {

    private final static int NONE=0;
    private final static int SLIDE_Y=1;
    private final static int SLIDE_X=2;
    private final static int SLIDE_Z=3;

    private int mode = NONE;

    private Dimension size;
    private int thickness;
    private int diameter;
    private int space;
    private int pipSize;
    private int pipOffset;	// Amount pip is below wheel
    private int margin;		// Margin between edge of Canvas and
				// controls

    private Polygon yPip;
    private Rectangle yBackClip;

    private Polygon xPip;
    private Rectangle xBackClip;

    private Polygon zPip;

    private Rectangle yArea;
    private Rectangle xArea;
    private Rectangle zArea;

    private Point oldMousePos = new Point();

    float yAngle = 0.0f;
    float xAngle = 0.0f;
    float zAngle = 0.0f;

    float yOrigAngle;
    float xOrigAngle;
    float zOrigAngle;

    float angleStep = (float)Math.PI/30.0f;

    public WheelControls() {
	this(0.0f, 0.0f, 0.0f);
    }

    public WheelControls( float rotX, float rotY, float rotZ ) {
	size = new Dimension( 200, 200 );

	xAngle = constrainAngle(rotX);
	yAngle = constrainAngle(rotY);
	zAngle = constrainAngle(rotZ);

        yOrigAngle = yAngle;
        xOrigAngle = xAngle;
        zOrigAngle = zAngle;

	setSizes();

	yPip = new Polygon();
	yPip.addPoint( 0, 0 );
	yPip.addPoint( -pipSize/2, pipSize );
	yPip.addPoint( pipSize/2, pipSize );

	xPip = new Polygon();
	xPip.addPoint(0,0);
	xPip.addPoint( pipSize, -pipSize/2 );
	xPip.addPoint( pipSize, pipSize/2 );

	zPip = new Polygon();
	zPip.addPoint( diameter/2, pipOffset );
	zPip.addPoint( diameter/2-pipSize/2, pipOffset-pipSize );
	zPip.addPoint( diameter/2+pipSize/2, pipOffset-pipSize );

	addMouseListener( this );
	addMouseMotionListener( this );
    }

    private void setSizes() {
	margin = 10;
	int width = size.width - margin*2;
	thickness = width * 7 / 100;
	diameter = width * 70 / 100;
	space = width * 10 / 100;
	pipSize = width * 7 / 100;

	pipOffset = thickness/2;

    }

    public void paint( Graphics g ) {
	Graphics2D g2 = (Graphics2D)g;

	g.drawOval( margin,margin, diameter, diameter );
	zArea = new Rectangle( margin, margin, diameter, diameter );
	drawZPip( g2, zAngle );

	g.drawRect( margin, margin+diameter+space, 
		    diameter, thickness ); // Y Wheel
	yArea = new Rectangle( margin, margin+diameter+space, margin+diameter, 
			       thickness+pipOffset );
	yBackClip = new Rectangle( margin-thickness, 
				   margin+diameter+space+thickness, 
				   margin+diameter+thickness*2, thickness );
	drawYPip( g2, yAngle );

	g.drawRect( margin+diameter+space, margin, 
		    thickness, diameter ); // X Wheel
	xArea = new Rectangle( margin+diameter+space, margin, 
			       thickness+pipOffset, margin+diameter );
	xBackClip = new Rectangle( margin+diameter+space+thickness, 
				   margin-thickness, 
				   thickness, margin+diameter+thickness*2 );
	drawXPip( g2, xAngle );


    }

    public float getXAngle() {
	return xAngle;
    }

    public float getYAngle() {
	return yAngle;
    }

    public float getZAngle() {
	return zAngle;
    }


    public void reset() {
                // Overwrite the old pip
                drawYPip( (Graphics2D)(this.getGraphics()),
                          yAngle );
                yAngle = yOrigAngle;
                // Draw the new Pip
                drawYPip( (Graphics2D)(this.getGraphics()),
                          yAngle );

                // Overwrite the old pip
                drawXPip( (Graphics2D)(this.getGraphics()),
                          xAngle );
                xAngle = xOrigAngle;
                // Draw the new Pip
                drawXPip( (Graphics2D)(this.getGraphics()),
                          xAngle );

                drawZPip( (Graphics2D)(this.getGraphics()),
                          zAngle );
 
                zAngle =  zOrigAngle;

                drawZPip( (Graphics2D)(this.getGraphics()),
                          zAngle );
                oldMousePos.setLocation(0,0);
    }


    private void drawXPip( Graphics2D g2, float angle ) {
	AffineTransform trans = new AffineTransform();
	int y;
	int xOrig = margin+diameter+space;
	int yOrig = margin;
	Color origColor = g2.getColor();

	if (angle <= Math.PI) {
	    y = yOrig + diameter - (int)((Math.abs( angle-Math.PI/2 )/(Math.PI/2)) * diameter/2);
	} else
	    y = yOrig + (int)((Math.abs( (angle-Math.PI*1.5) )/(Math.PI/2)) * diameter/2);

	if (angle<Math.PI/2 || angle > Math.PI*1.5)
	    g2.setColor( Color.red );		// Infront of wheel
	else {
	    g2.setColor( Color.black );		// Behind Wheel
	    g2.setClip( xBackClip );
	}

	g2.setXORMode( getBackground() );
	trans.setToTranslation( xOrig+pipOffset, y );
	g2.setTransform( trans );
	g2.fillPolygon( xPip );

	// Reset graphics context
	trans.setToIdentity();
	g2.setTransform( trans );
	g2.setColor(origColor);
	g2.setPaintMode();
    }

    private void drawYPip( Graphics2D g2, float angle ) {
	AffineTransform trans = new AffineTransform();
	int x;
	int xOrig = margin;
	int yOrig = margin+diameter+space;
	Color origColor = g2.getColor();

	if (angle <= Math.PI) {
	    x = xOrig + diameter - (int)((Math.abs( angle-Math.PI/2 )/(Math.PI/2)) * diameter/2);
	} else
	    x = xOrig + (int)((Math.abs( (angle-Math.PI*1.5) )/(Math.PI/2)) * diameter/2);

	if (angle<Math.PI/2 || angle > Math.PI*1.5)
	    g2.setColor( Color.red );		// Infront on wheel
	else {
	    g2.setColor( Color.black );		// Behind Wheel
	    g2.setClip( yBackClip );
	}

	g2.setXORMode( getBackground() );
	trans.setToTranslation( x, yOrig+pipOffset );
	g2.setTransform( trans );
	g2.fillPolygon( yPip );

	// Reset graphics context
	trans.setToIdentity();
	g2.setTransform( trans );
	g2.setColor(origColor);
	g2.setPaintMode();
    }

    private void drawZPip( Graphics2D g2, float zAngle ) {
	AffineTransform trans = new AffineTransform();
	Color origColor = g2.getColor();

	trans.translate( margin, margin );
	trans.rotate(zAngle, diameter/2, diameter/2 );

	g2.setXORMode( getBackground() );
	g2.setTransform(trans);
	g2.setColor( Color.red );
	g2.fillPolygon( zPip );

	// Reset graphics context
	trans.setToIdentity();
	g2.setTransform( trans );
	g2.setColor( origColor );
	g2.setPaintMode();
    }

    public Dimension getPreferredSize() {
	return size;
    }

    public void setSize( Dimension d ) {
	// Set size to smallest dimension
	if (d.width<d.height)
	    size.width = size.height = d.width;
	else
	    size.width = size.height = d.height;
	setSizes();
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
	if ( yArea.contains( e.getPoint() )) {
	    mode = SLIDE_Y;
	    oldMousePos = e.getPoint();
	} else if (xArea.contains( e.getPoint() )) {
	    mode = SLIDE_X;
	    oldMousePos = e.getPoint();
	} else if (zArea.contains( e.getPoint() )) {
	    mode = SLIDE_Z;
	    oldMousePos = e.getPoint();
	}
    }

    public void mouseReleased( MouseEvent e ) {
	mode = NONE;
    }

    public void mouseDragged( MouseEvent e ) {
	Point pos = e.getPoint();

	int diffX = pos.x - oldMousePos.x;
	int diffY = pos.y - oldMousePos.y;

	switch(mode) {
	    case NONE:
		break;
	    case SLIDE_Y:
		// Overwrite the old pip
		drawYPip( (Graphics2D)((Canvas)e.getSource()).getGraphics(),
			  yAngle );
		if (diffX<0)
		    yAngle -= angleStep;
		else if (diffX>0)
		    yAngle += angleStep;

		yAngle = constrainAngle(yAngle);

		// Draw the new Pip
		drawYPip( (Graphics2D)((Canvas)e.getSource()).getGraphics(),
			  yAngle );
	        oldMousePos = pos;
		break;
	    case SLIDE_X:
		// Overwrite the old pip
		drawXPip( (Graphics2D)((Canvas)e.getSource()).getGraphics(),
			  xAngle );
		if (diffY<0)
		    xAngle -= angleStep;
		else if (diffY>0)
		    xAngle += angleStep;

		xAngle = constrainAngle(xAngle);

		// Draw the new Pip
		drawXPip( (Graphics2D)((Canvas)e.getSource()).getGraphics(),
			  xAngle );
	        oldMousePos = pos;
		break;
	    case SLIDE_Z:
		drawZPip( (Graphics2D)((Canvas)e.getSource()).getGraphics(),
			  zAngle );

		if (diffX<0)
		    zAngle -= angleStep;
		else if (diffX>0)
		    zAngle += angleStep;

		zAngle = constrainAngle( zAngle );
		drawZPip( (Graphics2D)((Canvas)e.getSource()).getGraphics(),
			  zAngle );
	        oldMousePos = pos;
		break;
	    default:
		throw( new RuntimeException("Internal Error"));
	}
    }

    public void mouseMoved( MouseEvent e ) {
    }

    /**
      * Constrain angle to be 0<angle<2PI
      */
    private float constrainAngle( float angle ) {
        if ( angle > (float)Math.PI*2 ) return angle-(float)Math.PI*2;
        if ( angle < 0.0f) return angle+(float)Math.PI*2;
	return angle;
    }
}
