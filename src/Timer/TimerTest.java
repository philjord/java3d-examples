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

import java.applet.Applet;
import java.awt.Panel;
import java.awt.TextArea;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.timer.J3DTimer;

public class TimerTest extends Applet {

    long[] ticks = new long[10];
    long[] sysTime = new long[ticks.length];

    public TimerTest() {
    }

    public void init() {
	Panel panel = new Panel();
	String header = new String("              J3D Timer                System Timer\n");
	TextArea textArea = new TextArea(header, 12, 35, TextArea.SCROLLBARS_NONE );	
	panel.add(textArea);
	this.add(panel);
	

        for(int i=0; i<ticks.length; i++) {
            ticks[i] = J3DTimer.getValue();
            sysTime[i] = System.currentTimeMillis();
        }
	
        for(int i=0; i<ticks.length; i++)
            //System.out.println("tick "+ticks[i]+"    "+sysTime[i] );
	    textArea.append("tick "+ticks[i]+"    "+sysTime[i] + "\n" );
        //System.out.println("Resolution "+J3DTimer.getResolution() );
	textArea.append("Resolution "+J3DTimer.getResolution() + "\n" );

    }

    public static void main( String args[] ) {
	new MainFrame(new TimerTest(), 380, 256);
    }

}
