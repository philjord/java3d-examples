//
// Fragment shader for cartoon-style shading
//
// Author: Philip Rideout
//
// Copyright (c) 2004 3Dlabs Inc. Ltd.
//
// See 3Dlabs-License.txt for license information
//

//uniform vec3 DiffuseColor;
//uniform vec3 PhongColor;
//uniform float Edge;
//uniform float Phong;

vec3 DiffuseColor = vec3(0.5,0.5,1.0);
vec3 PhongColor = vec3(0.75,0.75,1.0);
float Edge = 0.64;
float Phong = 0.90;

varying vec3 Normal;
varying vec3 LightDir;

void main (void)
{
	vec3 color = DiffuseColor;
	float f = max( 0.0, dot(LightDir,Normal));
	if (abs(f) < Edge)
		color = DiffuseColor * 0.2;
	if (f > Phong)
		color = PhongColor;

	gl_FragColor = vec4(color, 1);
}
