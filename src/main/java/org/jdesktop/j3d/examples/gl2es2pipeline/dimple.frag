
//
// dimple.frag: Fragment shader for bump mapping dimples (bumps)
//
// author: John Kessenich
//
// Copyright (c) 2002: 3Dlabs, Inc.
//
//

// GL2ES2: non buit-in varyings
varying vec2 glTexCoord0;
varying vec4 C;

varying vec3 LightDir;
varying vec3 EyeDir;
varying vec3 Normal;

//const vec3 Color = vec3(0.7, 0.6, 0.18);

//const float Density = 16.0;
//const float Size = 0.25;

uniform vec3 Color;
uniform float Density;
uniform float Size;
// uniform float SpecularFactor;

//float Density = 27.6;
//float Size = 0.13025;


//uniform float Scale;

const float SpecularFactor = 0.4;

void main (void)
{
    vec3 litColor;

	// GL2ES2: non buit-in varying
    vec2 c = Density * (glTexCoord0.xy);
    vec2 p = fract(c) - vec2(0.5);
    float d = (p.x * p.x) + (p.y * p.y);
    if (d >= Size)
        p = vec2(0.0);

    vec3 normDelta = vec3(-p.x, -p.y, 1.0);
      
    litColor = Color * max(0.0, dot(normDelta, LightDir));
      
    float t = 2.0 * dot(LightDir, normDelta);
    vec3 reflectDir = t * normDelta;
    reflectDir = LightDir - reflectDir;
    
//    vec3 reflectDir = LightDir - 2.0 * dot(LightDir, normDelta) * normDelta;
    
    float spec = max(dot(EyeDir, reflectDir), 0.0);
    spec = spec * spec;
    spec = spec * spec;
    spec *= SpecularFactor;

    litColor = min(litColor + spec, vec3(1.0));
    // GL2ES2: gl_FragColor is unchanged, C is a non built-in varying
    gl_FragColor = vec4(litColor, C.a);
//    gl_FragColor = vec4(litColor, 1.0);
//    gl_FragColor = vec4(Scale);
}
