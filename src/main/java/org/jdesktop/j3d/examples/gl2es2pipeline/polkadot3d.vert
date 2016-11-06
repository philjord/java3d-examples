// This is the Vertex Shader for three dimensional polka dots.
//
// author(s):  Joshua Doss
//
// Copyright (C) 2002-2004  3Dlabs Inc. Ltd.

// GL2ES2: Java3D built-in attributes, these are calculated and passsed in if declared here
attribute vec4 glVertex;
attribute vec3 glNormal;  

// GL2ES2: Java3D built-in uniforms, these are calculated and passsed in if declared here
uniform mat4 glModelViewMatrix;
uniform mat4 glModelViewProjectionMatrix;
uniform mat3 glNormalMatrix;

//Create uniform variables for lighting to allow user interaction
//uniform float SpecularContribution;
//uniform vec3 LightPosition;

const float SpecularContribution = 0.36;
const vec3 LightPosition = vec3 (0, 4, 5);

varying vec3 MCPosition;
varying float LightIntensity;

void main(void)
{
    float diffusecontribution  = 1.0 - SpecularContribution;
    
    // compute the vertex position in eye coordinates
    // GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)
    //vec3  ecPosition           = vec3(gl_ModelViewMatrix * gl_Vertex);
    vec3  ecPosition           = vec3(glModelViewMatrix * glVertex);
    
    // compute the transformed normal
    // GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)
    //vec3  tnorm                = normalize(gl_NormalMatrix * gl_Normal);
    vec3  tnorm                = normalize(glNormalMatrix * glNormal);
    
    // compute a vector from the model to the light position
    vec3  lightVec             = normalize(LightPosition - ecPosition);
    
    // compute the reflection vector
    vec3  reflectVec           = reflect(-lightVec, tnorm);
    
    // compute a unit vector in direction of viewing position
    vec3  viewVec              = normalize(-ecPosition);
    
    // calculate amount of diffuse light based on normal and light angle
    float diffuse              = max(dot(lightVec, tnorm), 0.0);
    float spec                 = 0.0;
    
    // if there is diffuse lighting, calculate specular
    if(diffuse > 0.0)
       {
          spec = max(dot(reflectVec, viewVec), 0.0);
          spec = pow(spec, 16.0);
       }
    
    // add up the light sources, since this is a varying (global) it will pass to frag shader     
    LightIntensity  = diffusecontribution * diffuse * 1.5 +
                          SpecularContribution * spec;
    
    // the varying variable MCPosition will be used by the fragment shader to determine where
    //    in model space the current pixel is           
    // GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)           
    //MCPosition      = vec3 (gl_Vertex);
    MCPosition      = vec3 (glVertex);
    
    // send vertex information
    // GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)
	//gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	gl_Position = glModelViewProjectionMatrix * glVertex;
}

