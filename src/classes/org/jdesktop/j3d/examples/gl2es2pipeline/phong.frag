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

uniform vec4 glLightSource0position;
uniform vec4 glLightSource0diffuse;

uniform vec4 glLightModelambient;

uniform vec4 glFrontMaterialdiffuse;
uniform float glFrontMaterialshininess;
uniform vec3 glFrontMaterialspecular;

varying vec3 worldPos;

void directionalLight0(in vec3 normal, inout vec4 ambient,  inout vec4 diffuse,  inout vec3 specular)
{
    // Normalized light direction and half vector
    vec3 lightDirection = normalize(vec3(glLightSource0position));
    
    
    // half vector requires a few calcs 
    //vec3 halfVector = normalize(vec3(gl_LightSource[0].halfVector));    
   
 	vec3 L = normalize(glLightSource0position.xyz - worldPos);
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
	pf = pow(nDotHV, glFrontMaterialshininess);
    }

	// GL2ES2: ambient is part of light model
    //ambient += gl_LightSource[0].ambient;
    ambient += glLightModelambient;
    diffuse += glLightSource0diffuse * nDotVP;
    
    // GL2ES2: specular is part of material
    //specular += gl_LightSource[0].specular * pf;
    specular += glFrontMaterialspecular * pf;
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
    vec4 secondaryColor = vec4(spec * glFrontMaterialspecular, 1.0);
    // GL2ES2: need to look up the calculations on sceneColor
    //vec4 color = vec4(vec3(gl_FrontLightModelProduct.sceneColor +
		      // amb * glFrontMaterialambient +
	vec4 color = vec4(vec3(glFrontMaterialdiffuse +      
		      	amb * glLightModelambient +
		       diff * glFrontMaterialdiffuse), 1.0);

    gl_FragColor = color + secondaryColor;
}
