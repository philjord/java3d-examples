//
// Simple vertex shader for wood
//
// Author: John Kessenich
//
// Copyright (c) 2002-2004 3Dlabs Inc. Ltd.
//
// See 3Dlabs-License.txt for license information
//

// GL2ES2: Java3D built-in attributes, these are calculated and passsed in if declared here
attribute vec4 glVertex;
attribute vec3 glNormal;  

// GL2ES2: Java3D built-in uniforms, these are calculated and passsed in if declared here
uniform mat4 glModelViewMatrix;
uniform mat4 glModelViewProjectionMatrix;
uniform mat3 glNormalMatrix;

varying float lightIntensity;
varying vec3 Position;
//uniform vec3 LightPosition;
//uniform float Scale;
const vec3 LightPosition = vec3 (0.0,0.0,0.4);
const float Scale = 1.0;

void main(void)
{
	// GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)
	//vec4 pos = glModelViewMatrix * glVertex;
	vec4 pos = glModelViewMatrix * glVertex;
	// GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)
	//Position = vec3(glVertex) * Scale;
	Position = vec3(glVertex) * Scale;
	// GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)
	//vec3 tnorm = normalize(gl_NormalMatrix * gl_Normal);
	vec3 tnorm = normalize(glNormalMatrix * glNormal);
	lightIntensity = max(dot(normalize(LightPosition - vec3(pos)), tnorm), 0.0) * 1.5;
	
	// GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)
	//gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	gl_Position = glModelViewProjectionMatrix * glVertex;
}
