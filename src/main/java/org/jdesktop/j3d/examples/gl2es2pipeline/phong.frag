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

// A GLSL fragment program for handling 1 directional light with specular.
// This implements per-pixel lighting (Phong shading)

// GL2ES2: Java3D built-in uniforms, these are calculated and passsed in if declared here
uniform mat4 glModelViewMatrix;

 

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

varying vec3 worldPos;

varying vec4 sceneColor;

void directionalLight0(in vec3 normal, inout vec4 ambient,  inout vec4 diffuse,  inout vec3 specular)
{
    // Normalized light direction and half vector
    vec3 lightDirection = normalize(vec3(glLightSource[0].position));
    
   
 	//GL2ES2: half vector must be calculated
    //vec3 halfVector = normalize(vec3(gl_LightSource[0].halfVector));
    //http://stackoverflow.com/questions/3744038/what-is-half-vector-in-modern-glsl
    vec3 ecPos = vec3(glModelViewMatrix * vec4(worldPos,1.0));	
    vec3 ecL;
    if(	glLightSource[0].position.w == 0.0)
    	ecL = vec3(glLightSource[0].position.xyz);// no -ecPos in case of dir lights?
  	else
		ecL = vec3(glLightSource[0].position.xyz - ecPos);
    vec3 L = normalize(ecL.xyz);
 	vec3 V = -ecPos.xyz; 
 	vec3 halfVector = normalize(L + V);
    

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

	// GL2ES2: ambient is part of light model
    //ambient += gl_LightSource[0].ambient;
    ambient += glLightModelambient;
    diffuse += glLightSource[0].diffuse * nDotVP;
    
    // GL2ES2: specular is part of material
    //specular += gl_LightSource[0].specular * pf;
    specular += glFrontMaterial.specular * pf;
}


// Per-pixel normal (input from vertex shader)
varying vec3  Normal;

void main()
{
    vec3 unitNorm = normalize(Normal);
    vec4 amb = vec4(0.0);
    vec4 diff = vec4(0.0);
    vec3 spec = vec3(0.0);
    int i;


    directionalLight0(unitNorm, amb, diff, spec);
    

    // Apply the result of the lighting equation
    vec4 secondaryColor = vec4(spec * glFrontMaterial.specular, 1.0);
    // GL2ES2: change to calc'ed sceneColor
    //vec4 color = vec4(vec3(gl_FrontLightModelProduct.sceneColor +
	 vec4 color = vec4(vec3(sceneColor + 
		      	amb * glLightModelambient +
		       diff * glFrontMaterial.diffuse), 1.0);

    gl_FragColor = color + secondaryColor;
}
