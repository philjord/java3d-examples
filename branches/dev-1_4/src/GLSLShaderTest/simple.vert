/*
 * $RCSfile$
 *
 * Copyright (c) 2004 Sun Microsystems, Inc. All rights reserved.
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

void directionalLight(
    in    int  i,
    in    vec3 normal,
    inout vec4 ambient,
    inout vec4 diffuse,
    inout vec4 specular)
{
    // Normalized light direction and half vector
    // (shouldn't they be pre-normalized?!)
    vec3 lightDirection = normalize(vec3(gl_LightSource[i].position));
    vec3 halfVector = normalize(vec3(gl_LightSource[i].halfVector));

    float nDotVP; // normal . light_direction
    float nDotHV; // normal . light_half_vector
    float pf; // power factor

    nDotVP = max(0.0, dot(normal, lightDirection));
    nDotHV = max(0.0, dot(normal, halfVector));

    if (nDotVP == 0.0) {
	pf = 0.0;
    }
    else {
	pf = pow(nDotHV, gl_FrontMaterial.shininess);
    }

    ambient += gl_LightSource[i].ambient;
    diffuse += gl_LightSource[i].diffuse * nDotVP;
    specular += gl_LightSource[i].specular * pf;
}


const int numEnabledLights = 2; // TODO: this should be a built-in parameter!

void main()
{
    //vec4 ecPosition = gl_ModelViewMatrix * gl_Vertex;
    //vec3 ecPosition3 = ecPosition.xyz / ecPosition.w;
    vec3 tnorm = normalize(vec3(gl_NormalMatrix * gl_Normal));
    vec4 amb = vec4(0.0);
    vec4 diff = vec4(0.0);
    vec4 spec = vec4(0.0);
    int i;

    // Transform the vertex
    vec4 outPosition = gl_ModelViewProjectionMatrix * gl_Vertex;

    for (i = 0; i < numEnabledLights; i++) {
	directionalLight(i, tnorm, amb, diff, spec);
    }

    // Apply the result of the lighting equation
    vec4 outSecondaryColor = vec4(vec3(spec * gl_FrontMaterial.specular), 1.0);
    vec3 color0 = vec3(gl_FrontLightModelProduct.sceneColor +
		       amb * gl_FrontMaterial.ambient +
		       diff * gl_FrontMaterial.diffuse);

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

    gl_FrontColor = outColor;
    gl_FrontSecondaryColor = outSecondaryColor;
    gl_Position = outPosition;
}
