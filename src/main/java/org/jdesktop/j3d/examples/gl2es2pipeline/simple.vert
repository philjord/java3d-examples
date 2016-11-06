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

// A simple GLSL vertex program for handling 2 directional lights with
// separate specular

// GL2ES2: Java3D built-in attributes, these are calculated and passsed in if declared here
attribute vec4 glVertex;
attribute vec3 glNormal;  
attribute vec4 glColor;  

// GL2ES2: Java3D built-in uniforms, these are calculated and passsed in if declared here
uniform mat4 glModelViewMatrix;
uniform mat4 glModelViewProjectionMatrix;
uniform mat3 glNormalMatrix;

uniform vec4 glLightModelambient;

 
struct material
{
	int lightEnabled;
 	vec4 ambient;
 	vec4 diffuse;
 	vec4 emission;
 	vec3 specular;
 	float shininess;
};
uniform material glFrontMaterial;

struct lightSource
{
	 vec4 position;
	 vec4 diffuse;
	 vec4 specular;
	 float constantAttenuation, linearAttenuation, quadraticAttenuation;
	 float spotCutoff, spotExponent;
	 vec3 spotDirection;
};

uniform int numberOfLights;
const int maxLights = 1;
uniform lightSource glLightSource[maxLights];

//GL2ES2: varying color data needs to be defined
varying vec4 glFrontColor;
varying vec4 glFrontSecondaryColor;
    
void directionalLight(
    in    int  i,
    in    vec3 normal,
    inout vec4 ambient,
    inout vec4 diffuse,
    inout vec3 specular)
{
    // Normalized light direction and half vector
    // (shouldn't they be pre-normalized?!)
    
    //GL2ES2 notice not using the i parameter but hard coded to 0
    vec3 lightDirection = normalize(vec3(glLightSource[0].position));
    
    //GL2ES2: half vector must be calculated
    //vec3 halfVector = normalize(vec3(gl_LightSource[0].halfVector));
    vec3 worldPos = vec3(glModelViewMatrix * glVertex);		
    vec3 L = normalize(glLightSource[0].position.xyz - worldPos);
 	vec3 V = vec3(0,0,1);//eye position
 	vec3 halfVector = (L + V);
 	
  

    float nDotVP; // normal . light_direction
    float nDotHV; // normal . light_half_vector
    float pf; // power factor

    nDotVP = max(0.0, dot(normal, lightDirection));
    nDotHV = max(0.0, dot(normal, halfVector));

    if (nDotVP == 0.0) {
	pf = 0.0;
    }
    else {
	pf = pow(nDotHV, glFrontMaterial.shininess);
    }

    ambient += glLightModelambient;
    diffuse += glLightSource[0].diffuse * nDotVP;
    specular += glFrontMaterial.specular * pf;
}

//GL2ES2: only a single light for now
const int numEnabledLights = 1; // TODO: this should be a built-in parameter!

void main()
{
    //vec4 ecPosition = gl_ModelViewMatrix * gl_Vertex;
    //vec3 ecPosition3 = ecPosition.xyz / ecPosition.w;
    // GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)
    //vec3 tnorm = normalize(vec3(gl_NormalMatrix * gl_Normal));
    vec3 tnorm = normalize(vec3(glNormalMatrix * glNormal));
    vec4 amb = vec4(0.0);
    vec4 diff = vec4(0.0);
    vec3 spec = vec3(0.0);
    int i;

    // Transform the vertex
    // GL2ES2: swap built-in variable for Java3d built-in uniforms and attributes gl_* = gl* + declaration (at top)
    //vec4 outPosition = gl_ModelViewProjectionMatrix * gl_Vertex;
    vec4 outPosition = glModelViewProjectionMatrix * glVertex;

    for (i = 0; i < numEnabledLights; i++) {
	directionalLight(i, tnorm, amb, diff, spec);
    }

	//GL2ES2: sceneColor Derived. Ecm + Acm * Acs (Acs is normal glLightModelambient)
 	vec4 sceneColor = glFrontMaterial.emission + glFrontMaterial.ambient * glLightModelambient;

    // Apply the result of the lighting equation
    vec4 outSecondaryColor = vec4(vec3(spec * glFrontMaterial.specular), 1.0);
    vec3 color0 = vec3(sceneColor +
		       amb * glLightModelambient +
		       diff * glFrontMaterial.diffuse);

    // Generate a pseudo-random noise pattern
    vec3 xyz = clamp((outPosition.xyz + 1.0) * 0.5, 0.0, 1.0);

    xyz = fract(xyz * 262144.0);
    float randSeed = fract(3.0 * xyz.x + 5.0 * xyz.y + 7.0 * xyz.z);

    vec3 altColor;

    randSeed = fract(37.0 * randSeed);
    altColor.x = randSeed * 0.5 + 0.5;
    randSeed = fract(37.0 * randSeed);
    altColor.y = randSeed * 0.5 + 0.5;
    randSeed = fract(37.0 * randSeed);
    altColor.z = randSeed * 0.5 + 0.5;
    randSeed = fract(37.0 * randSeed);
    float altAlpha = randSeed * 0.5;

    // Apply noise and output final vertex color
    vec4 outColor;
    outColor = vec4(mix(color0, altColor, altAlpha), 1.0);

    glFrontColor = outColor;
    glFrontSecondaryColor = outSecondaryColor;
    gl_Position = outPosition;
}
