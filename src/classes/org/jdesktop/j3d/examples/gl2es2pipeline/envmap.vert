//
// Vertex shader for environment mapping with an
// equirectangular 2D texture
//
// Authors: John Kessenich, Randi Rost
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


varying vec3  Normal;
varying vec3  EyeDir;
varying float LightIntensity;

uniform vec3  LightPos;

void main(void) 
{
    // GL2ES2: ftransform() no longer exists, but it is simple (note use of Java3D built-in uniforms and attributes)
    // GL2ES2: gl_Position is unchanged
    //gl_Position    = ftransform();  
    gl_Position    = glModelViewProjectionMatrix * glVertex;
    
    // compute the transformed normal
    // GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)
    //vec3  Normal                = normalize(gl_NormalMatrix * gl_Normal);
    vec3  Normal                = normalize(glNormalMatrix * glNormal);
    
    // GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)
    //vec4 pos       = gl_ModelViewMatrix * gl_Vertex;
    vec4 pos       = glModelViewMatrix * glVertex;
    
    EyeDir         = pos.xyz;
    LightIntensity = max(dot(normalize(LightPos - EyeDir), Normal), 0.0);
}
