/*
 *	@(#)README.txt 1.5 01/06/20 16:18:15
 *
 * Copyright (c) 1996-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
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
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN
 * OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 * FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
 * PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF
 * LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed,licensed or intended
 * for use in the design, construction, operation or maintenance of
 * any nuclear facility.
 */

	Release Notes for the Lightwave 3D Java3D Loader
	------------------------------------------------
			Updated May 13, 1998



These release notes are intended to document the known working and
non-working features of the loader.  This is important because the loader
implements an important subset of Lightwave functionality, but it definitely
skips many major features of Lightwave files.  Please read these notes
to make sure that the features you need are actually implemented.  Or
if you see differences between Lightwave display of your file and
the Java3D version of that file, take a look at these notes to see
what might not be working properly.


Testing the Loader
------------------
The application in this directory (Viewer) is intended to be a very
basic test application for loading/viewing Lightwave 3D models.  To
use the program, type:
	java Viewer <filename>
where <filename> is the name of a valid Lightwave 3D scene file that is
reachable from the current directory.  There is a very basic test file
included in this directory called ballcone.lws.  To load/view that file,
type:
	java Viewer ballcone.lws
Note that Lightwave scene files (*.lws) embed the pathnames to object
files (*.lwo) within them, and that object files have pathnames
to image files (for textures) embedded in them.  Whatever those
pathnames are in those files must be valid for the directory in which
you are running the application that loads the scene file.  For example,
if I was loading in a scene file that referred to an object file 
called "data/object.lwo", then the file "object.lwo" should be located in
a subdirectory of this current directory called "data".


Summary of Loader
-----------------
The Lw3d loader was intended to implement a major subset of the features in
Lightwave 3D that would be used for realtime 3D animations.  That is, any
features (such as Bones and other high-end rendering
options) which would require significant rendering time were simply
not doable.  These more advanced features are intended to be rendered
off-line, saving each frame separately and later compositing them
together into an animation.  But Java3D is a realtime 3D rendering
system, so these type of animations just do not map into a Java3D viewer
very well.

Another category of non-implemented items are those that we simply have not
yet gotten to.  There are a few known features of Lightwave files that
would work well through Java3D but just haven't been implemented in the
loader yet.

Although there are a lot of features that are not yet implemented,
the basics of Lightwave 3D functionality (scene creation, animating
objects/lights/cameras, different surface properties) all work pretty
much as expected.  So try it out and let us know non-documented items
that didn't work properly.


Details of Non-Implemented Features
-----------------------------------
This list is probably not comprehensive, but hopefully points out most of
the features or areas where the implementation is incomplete (or not
there at all).


Limitations of Scene Files (*.lws)
----------------------------------
1) Bones/Skeleton
Bones functionality is not implemented at all.  Unfortunately, this
great feature of Lightwave 3D is not currently implementable in the
loader because the processing time that it would take to compute
frames based on Bones data would be far more than a real-time rendering
system can afford.

The loader may, at some future point, provide a mechanism to read
in frames of geometry that were saved from Lightwave Bones descriptions.
That is, there are plug-ins available for Lightwave 3D that allow you
to save out files with Bones information as a series of files with
pre-calculated geometry for each frame; eventually we would like the
Lightwave 3D loader to support those files.

Workaround: None; the best and only workaround is to find a different
method of animating your objects.


2) Spline paths
Spline paths will be interpreted as linear piecewise paths instead,
traveling between each control point specified for the spline.

Workaround: Specify linear paths.  If your path looks too hard-jointed 
through the loader, specify more keyframes for the path to smooth it out.


3) Object Scaling
Scaling objects in the scene (versus the object files) is currently
ignored.

Workaround: scale the objects in their individual object files.


4) Shadows
Shadows options are ignored in the loader.

Workaround: None.


5) Envelopes
Most envelopes are ignored.  There are a couple of exceptions to this,
such as light intensity envelopes, but even those features have not been 
completely implemented and tested.

Workaround: None.


6) Camera effects
All advanced-rendering camera effects are ignored in the loader.  This
includes the following items in Lightwave 3D files:
	- Lens Flare
	- F-stop
	- Focal Distance
	- Blur Length
	- Dissolves
	- Glow
	- Zoom
	- Intensity Falloff
	- Antialiasing
	
Workaround: None.


7) Inverse Kinematics
IK options such as Goal Objects and Anchors are ignored.

Workaround: Animate objects directly instead of indirectly via IK.


8) Morphs
All morph options are ignored.

Workaround: None.


9) Display properties
Lightwave allows you to specify surface properties for different rendering
modes (e.g., wireframe color).  All of these parameters are ignored and
the full properties of any item are used at all times.

Workaround: None.


10) Various Surface Properties
Various minor surface properties are currently ignored, including:
	- Polygon size
	- Dissolves
	- Clip map
	- Unaffected by fog
	- Edge parameters

Workaround: None.


11) Lights
The following items are currently ignored for Light objects:
	- Target objects
	- Flare parameters
	- Shadow options

Workaround: None for flares or shadows.  For targeting problems, animate the
light directly (versus indirectly through using Target).


12) Camera Targeting
The Target option for Camera objects is currently ignored by the loader.

Workaround: Animate the camera directly (versus indirectly through using
Target).

	
13) Effects
Most effects (from the Effects dialog box in the layout program) are
ignored, save for fog (which should accept all parameters) and 
backdrop colors (solid backdrops only - gradient backdrops are
ignored).

Workaround: None.


14) Render Options
Most options from the Render dialog box are ignored - most of these pertain 
to saving the animation in any case (something that doesn't happen through
the Loader).

Workaround: None.



Limitations of Object Files (*.lwo)
-----------------------------------
1) MetaNURBS
Geometry stored in MetaNURBS format will be ignored by the loader.

Workaround: pre-tessellate your MetaNURBS surfaces and save your
geometry (object) files in that format.


2) Layered Object Files
There is currently no support for the "Layered Object File Format" 
of Lightwave 3D.

Workaround: None.


3) Reflectivity
There is no way to reproduce the reflective properties of surfaces
through Java3D, so any reflectivity settings in Lightwave object files 
will be ignored.

Workaround: None.


4) Refraction
Refractive properties of surfaces are ignored.

Workaround: None.


5) Edge Transparency
Edge transparency properties are ignored.

Workaround.  None.


6) Texture types
Texture mapping is currently somewhat limited in the loader.  The following
types of texture mapping effects should work:
	- Diffuse (the texture modifies the Diffuse aspects of the surface)
	- Color (the texture modifies the Color properties of the surface).
Textures that attempt to modify other parameters of the surface will
be ignored.

Also, the following texture types should work:
	- Planar Image Map
	- Spherical Image Map
	- Cylindrical Image Map
Other kinds of mappings will not work (including Marble, Grid, Dots, etc.)

Some Texture parameters will not work.  The following should work correctly:
	- size
	- center
Advanced texture parameters such as falloff and velocity will be ignored.

Summary: There are so many texture mapping parameters in Lightwave 3D that 
it's difficult to produce a list of all of the items that won't work
properly.  In a nutshell, basic decal-type (color modifying) or brightness
(diffuse modifying) textures that are mapped as planes, spheres, or
cylinders should work correctly.  Anything else will probably not work.

Workaround: Use the basics.


7) Plug-ins
There is currently no support for any plug-in capabilities.  For example,
if there are plug-in shaders specified for your file, those shaders will be
ignored.

Workaround: None.


8) Image Sequences
There is no support for image sequences - textures must be static files.

Workaround: None.


