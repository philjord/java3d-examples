#version 120
//
// dimple.vert: Vertex shader for bump mapping dimples (bumps)
//
// author: John Kessenich
//
// Copyright (c) 2002: 3Dlabs, Inc.
//


// GL2ES2: Java3D built-in attributes, these are calculated and passsed in if declared here
attribute vec4 glVertex;
attribute vec3 glNormal;  
attribute vec4 glColor;  

// GL2ES2: Java3D built-in uniforms, these are calculated and passsed in if declared here
uniform mat4 glModelViewMatrix;
uniform mat4 glModelViewProjectionMatrix;
uniform mat3 glNormalMatrix;

uniform int ignoreVertexColors;

// GL2ES2: new output varyings, these replace gl_TexCoord[] and gl_FrontColor (along with A and D)
varying vec2 glTexCoord0;
varying vec4 C;

varying vec3 LightDir;
varying vec3 EyeDir;
varying vec3 Normal;

uniform vec3 LightPosition;
// uniform float Scale;
// vec3 LightPosition = vec3(0.0, 0.0, 5.0);
float Scale = 1.0;



void main(void) 
{
	// GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)
	vec4 pos = glModelViewMatrix * glVertex;
	// GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)
    gl_Position = glModelViewProjectionMatrix * glVertex;
    vec3 eyeDir = vec3(pos);
    // GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)
    // GL2ES2: swap built-in varying for declared varying
	//gl_TexCoord[0] = gl_MultiTexCoord0;
    glTexCoord0 = glVertex.xy;
    // GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)
    // GL2ES2: swap built-in varying for declared varying
    //gl_FrontColor = gl_Color;
    
	if( ignoreVertexColors != 0) 
		C = vec4(1,1,1,1); 
	else
		C = glColor;
	
    vec3 n = normalize(glNormalMatrix * glNormal);
    vec3 t = normalize(cross(vec3(1.141, 2.78, 3.14), n));
    vec3 b = cross(n, t);

    vec3 v;
    v.x = dot(LightPosition, t);
    v.y = dot(LightPosition, b);
    v.z = dot(LightPosition, n);
    LightDir = normalize(v);

    v.x = dot(eyeDir, t);
    v.y = dot(eyeDir, b);
    v.z = dot(eyeDir, n);
    EyeDir = normalize(v);
    
    
  
}
