#version 140

precision mediump float;


uniform int alphaTestEnabled;
uniform int alphaTestFunction;
uniform float alphaTestValue;

struct fogData
{
	int fogEnabled = -1;
	vec3 expColor = new Vector3f();
	float expDensity;
	vec3 linearColor = new Vector3f();
	float linearStart;
	float linearEnd;
};
uniform fogData fogData;

//End of FFP inputs
in vec2 glTexCoord0;

uniform sampler2D BaseMap;

in vec3 LightDir;
in vec3 ViewDir;

in vec3 N;

in vec4 A;
in vec4 C;
in vec4 D;


in vec3 emissive;
in vec3 specular;
in float shininess;



out vec4 glFragColor;

void main( void )
{
	vec4 baseMap = texture( BaseMap, glTexCoord0.st );
	
	//web says the keyword discard in a shader is bad
	//I could just gl_FragColor=vec(0,0,0,0); return;
	if(alphaTestEnabled != 0)
	{				
	 	if(alphaTestFunction==516)//>
			if(baseMap.a<=alphaTestValue)discard;			
		else if(alphaTestFunction==518)//>=
			if(baseMap.a<alphaTestValue)discard;		
		else if(alphaTestFunction==514)//==
			if(baseMap.a!=alphaTestValue)discard;
		else if(alphaTestFunction==517)//!=
			if(baseMap.a==alphaTestValue)discard;
		else if(alphaTestFunction==513)//<
			if(baseMap.a>=alphaTestValue)discard;
		else if(alphaTestFunction==515)//<=
			if(baseMap.a>alphaTestValue)discard;		
		else if(alphaTestFunction==512)//never	
			discard;			
	}

	vec3 normal = N;
	
	vec3 L = normalize(LightDir);
	vec3 E = normalize(ViewDir);
	vec3 R = reflect(-L, normal);
	vec3 H = normalize( L + E );
	
	float NdotL = max( dot(normal, L), 0.0 );
	float NdotH = max( dot(normal, H), 0.0 );
	float EdotN = max( dot(normal, E), 0.0 );
	float NdotNegL = max( dot(normal, -L), 0.0 );

	vec4 color;
	vec3 albedo = baseMap.rgb * C.rgb;
	vec3 diffuse = A.rgb + (D.rgb * NdotL);


	// Specular
	vec3 spec = specular * pow(NdotH, 0.3*shininess);
	spec *= D.rgb;
	
	color.rgb = albedo * (diffuse + emissive) + spec;
	color.a = C.a * baseMap.a;
	
	if(fogData.fogEnabled == 1)
	{
		//distance
		float dist = 0.0;
		float fogFactor = 0.0;
		 
		//compute distance used in fog equations
		dist = length(ViewDir);		 
		 
		if(fogData.linearEnd > 0.0)//linear fog
		{
		   fogFactor = (fogData.linearEnd - dist)/(fogData.linearEnd - fogData.linearStart);
		   fogFactor = clamp( fogFactor, 0.0, 1.0 );
		 
		   //if you inverse color in glsl mix function you have to put 1.0 - fogFactor
		   color = mix(fogData.linearColor, color, fogFactor);
		}
		else if( fogData.expDensity > 0.0)// exponential fog
		{
		    fogFactor = 1.0 /exp(dist * fogData.expDensity);
		    fogFactor = clamp( fogFactor, 0.0, 1.0 );
		 
		    // mix function fogColor-(1-fogFactor) + lightColor-fogFactor
		    color = mix(fogData.expColor, color, fogFactor);
		}
	}

	glFragColor = color;
}
