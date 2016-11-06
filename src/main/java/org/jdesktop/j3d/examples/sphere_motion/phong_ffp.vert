

// A GLSL vertex program for doing Phone shading (per-fragment lighting)

// Per-pixel normal (output to fragment shader)
varying vec3 normalDirection;
varying vec3 viewDirection;
varying vec4 position;

void main()
{
    normalDirection = normalize(vec3(gl_NormalMatrix * gl_Normal));
 	vec3 v = vec3(gl_ModelViewMatrix * gl_Vertex);
	viewDirection = normalize(-v.xyz);
	position = vec4(v,1);
	
    // Transform the vertex
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;    
}
