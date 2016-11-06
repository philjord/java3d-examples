//
// Vertex shader for antialiased procedural bricks
//
// Authors: Dave Baldwin, Steve Koren, Randi Rost
//          based on a shader by Darwyn Peachey
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


uniform vec3 LightPosition;
//const vec3 LightPosition = vec3 (0, 4, 4);

const float SpecularContribution = 0.3;
const float DiffuseContribution  = 1.0 - SpecularContribution;

varying float LightIntensity;
varying vec2  MCposition;

void main(void)
{
	// GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)
	//vec3 ecPosition = vec3 (gl_ModelViewMatrix * gl_Vertex);
    vec3 ecPosition = vec3 (glModelViewMatrix * glVertex);
    // GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)
    //vec3 tnorm      = normalize(gl_NormalMatrix * glNormal);
    vec3 tnorm      = normalize(glNormalMatrix * glNormal);
    
    
    vec3 lightVec   = normalize(LightPosition - ecPosition);
    vec3 reflectVec = reflect(-lightVec, tnorm);
    vec3 viewVec    = normalize(-ecPosition);
    float diffuse   = max(dot(lightVec, tnorm), 0.0);
    float spec      = 0.0;

    if (diffuse > 0.0)
    {
        spec = max(dot(reflectVec, viewVec), 0.0);
        spec = pow(spec, 16.0);
    }

    LightIntensity  = DiffuseContribution * diffuse +
                      SpecularContribution * spec;

	// GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)
	//MCposition      = gl_Vertex.xy;
    MCposition      = glVertex.xy;
    
    // GL2ES2: ftransform() no longer exists, but it is simple (note use of Java3D built-in uniforms and attributes)
    // GL2ES2: gl_Position is unchanged
    //gl_Position    = ftransform();  
    gl_Position    = glModelViewProjectionMatrix * glVertex;
}
