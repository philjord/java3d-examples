#version 120

// Per-pixel normal (input from vertex shader)
varying vec3 normalDirection;
varying vec3 viewDirection;
varying vec4 position;

const int numberOfLights = 2;

void main()
{
	vec3 lightDirection;
	float attenuation;
  
	// initialize total lighting with ambient lighting
	vec3 totalLighting = vec3(gl_FrontMaterial.emission) + (vec3(gl_LightModel.ambient) * vec3(gl_FrontMaterial.ambient)); 
  
	for (int index = 0; index < numberOfLights; index++) // for all light sources
	{
      	if (0.0 == gl_LightSource[index].position.w) // directional light?
		{
	 		attenuation = 1.0; // no attenuation
	  		lightDirection = normalize(vec3(gl_LightSource[index].position));
		} 
      	else // point light or spotlight (or other kind of light) 
		{
		  	vec3 positionToLightSource = vec3(gl_LightSource[index].position - position);
		  	float distance = length(positionToLightSource);
		  	lightDirection = normalize(positionToLightSource);
		  	attenuation = 1.0 / (gl_LightSource[index].constantAttenuation
				       + gl_LightSource[index].linearAttenuation * distance
				       + gl_LightSource[index].quadraticAttenuation * distance * distance);
		       
				       
  			if (gl_LightSource[index].spotCutoff <= 90.0) // spotlight?
		    {
		    	float clampedCosine = max(0.0, dot(-lightDirection, normalize(gl_LightSource[index].spotDirection)));
			    if (clampedCosine < cos(radians(gl_LightSource[index].spotCutoff))) // outside of spotlight cone?
				{
					attenuation = 0.0;
				}
		    		else
				{
					attenuation = attenuation * pow(clampedCosine, gl_LightSource[index].spotExponent);   
				}
	    	}
		}
  
      	vec3 diffuseReflection = attenuation * vec3(gl_LightSource[index].diffuse) * vec3(gl_FrontMaterial.diffuse)* max(0.0, dot(normalDirection, lightDirection));
      
      	vec3 specularReflection;
     	if (dot(normalDirection, lightDirection) < 0.0) // light source on the wrong side?
	  	{
	  		specularReflection = vec3(0.0, 0.0, 0.0); // no specular reflection
		}
      	else // light source on the right side
		{
			specularReflection = attenuation * vec3(gl_LightSource[index].specular) * vec3(gl_FrontMaterial.specular) 
		    * pow(max(0.0, dot(reflect(-lightDirection, normalDirection), viewDirection)), gl_FrontMaterial.shininess);
		}

    	totalLighting = totalLighting + diffuseReflection + specularReflection;
    }
 
  	gl_FragColor = vec4(totalLighting, 1.0);
}
