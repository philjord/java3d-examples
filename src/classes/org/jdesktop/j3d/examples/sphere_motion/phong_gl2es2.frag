#version 120

// Per-pixel normal (input from vertex shader)
varying vec3 normalDirection;
varying vec3 viewDirection;
varying vec4 position;

uniform vec4 glLightModelambient;

 
struct material
{
	int lightEnabled;
 	vec4 ambient;
 	vec4 diffuse;
 	vec4 emission;// note vec4 extra 1.0 sent through for ease
 	vec3 specular;
 	float shininess;
};
uniform material glFrontMaterial;

struct lightSource
{
	 int enabled;
	 vec4 position;
	 vec4 diffuse;
	 vec4 specular;
	 float constantAttenuation, linearAttenuation, quadraticAttenuation;
	 float spotCutoff, spotExponent;
	 vec3 spotDirection;
};

uniform int numberOfLights;
const int maxLights = 2;
uniform lightSource glLightSource[maxLights];

void main()
{
	vec3 lightDirection;
	float attenuation;
  
	// initialize total lighting with ambient lighting
	vec3 totalLighting = vec3(glFrontMaterial.emission) + (vec3(glLightModelambient) * vec3(glFrontMaterial.ambient)); 
  
	for (int index = 0; index < numberOfLights; index++) // for all light sources
	{
		if(glLightSource[index].enabled == 1)
		{
	      	if (0.0 == glLightSource[index].position.w) // directional light?
			{
		 		attenuation = 1.0; // no attenuation
		  		lightDirection = normalize(vec3(glLightSource[index].position));
		  		
			} 
	      	else // point light or spotlight (or other kind of light) 
			{
			  	vec3 positionToLightSource = vec3(glLightSource[index].position - position);
			  	float distance = length(positionToLightSource);
			  	lightDirection = normalize(positionToLightSource);
			  	attenuation = 1.0 / (glLightSource[index].constantAttenuation
					       + glLightSource[index].linearAttenuation * distance
					       + glLightSource[index].quadraticAttenuation * distance * distance);					       
					       
	  			if (glLightSource[index].spotCutoff <= 90.0) // spotlight?
			    {
			    	float clampedCosine = max(0.0, dot(-lightDirection, normalize(glLightSource[index].spotDirection)));
				    if (clampedCosine < cos(radians(glLightSource[index].spotCutoff))) // outside of spotlight cone?
					{
						attenuation = 0.0;
					}
			    		else
					{
						attenuation = attenuation * pow(clampedCosine, glLightSource[index].spotExponent);   
					}
		    	} 
			}
			
	      	vec3 diffuseReflection = attenuation * vec3(glLightSource[index].diffuse) * vec3(glFrontMaterial.diffuse)* max(0.0, dot(normalDirection, lightDirection));
	      
	      	vec3 specularReflection;
	     	if (dot(normalDirection, lightDirection) < 0.0) // light source on the wrong side?
		  	{
		  		specularReflection = vec3(0.0, 0.0, 0.0); // no specular reflection
			}
	      	else // light source on the right side
			{
				specularReflection = attenuation * vec3(glLightSource[index].specular) * vec3(glFrontMaterial.specular) 
			    * pow(max(0.0, dot(reflect(-lightDirection, normalDirection), viewDirection)), glFrontMaterial.shininess);
			}
	
	      	totalLighting = totalLighting + diffuseReflection + specularReflection;
      	}
    }
 
  	gl_FragColor = vec4(totalLighting, 1.0);
}
