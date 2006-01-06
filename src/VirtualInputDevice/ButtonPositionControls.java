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

import java.awt.*;
import java.awt.event.*;
import javax.vecmath.*;
import javax.media.j3d.*;

public class ButtonPositionControls extends Panel implements PositionControls, MouseListener {
    private final static int STILL=0;
    private final static int MOVING_UP=1;
    private final static int MOVING_DOWN=2;
    private final static int MOVING_LEFT=3;
    private final static int MOVING_RIGHT=4;
    private final static int MOVING_FORWARD=5;
    private final static int MOVING_BACK=6;

    // initial mode
    private int mode = STILL;

    Vector3f position = new Vector3f();
    Vector3f orig_position = new Vector3f();

    private Button leftB = new Button("Move Left");
    private Button rightB = new Button("Move Right");
    private Button upB = new Button("Move Up");
    private Button downB = new Button("Move Down");

    private Button forwardB = new Button("Move Forward");
    private Button backwardB = new Button("Move Back");

    private Button reset = new Button("Reset");
    private InputDevice device;

    private float step_rate = 0.0023f;   // movement rate per millisecond
    private long time_last_state_change = System.currentTimeMillis();

    // the constructor arguments are the intitial X, Y, and Z positions
    public ButtonPositionControls( float x, float y, float z ) {

        // up, down, right, and left movement buttons
        Panel panPanel = new Panel();
        panPanel.setLayout( new BorderLayout() );
        panPanel.add("North", upB);
        panPanel.add("East", rightB);
        panPanel.add("South", downB);
        panPanel.add("West", leftB);

        // forward, backward, and reset buttons 
        Panel p = new Panel();
        p.setLayout( new GridLayout(0,1,0,0) );
        p.add(forwardB);
        p.add(backwardB);
        p.add(reset);

        // set the initial position
        position.x = x;
        position.y = y;
        position.z = z;
        orig_position.set(position);

        // add a mouse listener to each button
        upB.addMouseListener(this);
        downB.addMouseListener(this);
        leftB.addMouseListener(this);
        rightB.addMouseListener(this);
        forwardB.addMouseListener(this);
        backwardB.addMouseListener(this);
        reset.addMouseListener(this);

	this.setLayout( new BorderLayout() );
        add("East", p );
	add("West", panPanel );
    }

    public void setDevice ( InputDevice device) {
        this.device = device;
    }

    public void getPosition(Vector3f pos ) {
	calculateMotion();
	pos.set(position);
    }

    public void setPosition(Vector3f pos ) {
	position.set(pos);
    }

    public void setStepRate( float stepRate ) {
	step_rate = stepRate;
    }

    private void calculateMotion() {

        long current_time = System.currentTimeMillis();
        long elapsed_time = current_time - time_last_state_change;

        switch(mode) {
            case STILL:
                break;
            case MOVING_LEFT:
                position.x = orig_position.x - step_rate*elapsed_time;
                break;
            case MOVING_RIGHT:
                position.x = orig_position.x + step_rate*elapsed_time;
                break;
            case MOVING_UP:
                position.y = orig_position.y + step_rate*elapsed_time;
                break;
            case MOVING_DOWN:
                position.y = orig_position.y - step_rate*elapsed_time;
                break;
            case MOVING_FORWARD:
                position.z = orig_position.z - step_rate*elapsed_time;
                break;
            case MOVING_BACK:
                position.z = orig_position.z + step_rate*elapsed_time;
                break;
            default:
                throw( new RuntimeException("Unknown motion"));
        }
    }

    public void mouseClicked( MouseEvent e ) {
    }
 
    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
        if (e.getSource()==leftB && mode != MOVING_LEFT) {
              time_last_state_change =  System.currentTimeMillis();
              mode = MOVING_LEFT;
              orig_position.set(position);
        } else if (e.getSource()==rightB && mode != MOVING_RIGHT) {
              time_last_state_change =  System.currentTimeMillis();
              mode = MOVING_RIGHT;
              orig_position.set(position);
        } else if (e.getSource()==upB && mode != MOVING_UP) {
              time_last_state_change =  System.currentTimeMillis();
              mode = MOVING_UP;
              orig_position.set(position);
        } else if (e.getSource()==downB && mode != MOVING_DOWN) {
              time_last_state_change =  System.currentTimeMillis();
              mode = MOVING_DOWN;
              orig_position.set(position);
        } else if (e.getSource()==forwardB && mode != MOVING_FORWARD) {
              time_last_state_change =  System.currentTimeMillis();
              mode = MOVING_FORWARD;
              orig_position.set(position);
        } else if (e.getSource()==backwardB && mode != MOVING_BACK) {
              time_last_state_change =  System.currentTimeMillis();
              mode = MOVING_BACK;
              orig_position.set(position);
        } else if (e.getSource()==reset) {
              device.setNominalPositionAndOrientation();
        }
    }

    public void mouseReleased( MouseEvent e ) {
        mode = STILL;
    }
}
