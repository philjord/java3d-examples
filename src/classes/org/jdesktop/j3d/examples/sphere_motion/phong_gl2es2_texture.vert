// GL2ES2: Java3D built-in attributes, these are calculated and passsed in if declared here
attribute vec4 glVertex;
attribute vec3 glNormal;  
attribute vec2 glMultiTexCoord0;

// GL2ES2: Java3D built-in uniforms, these are calculated and passsed in if declared here
uniform mat4 glModelViewProjectionMatrix;
uniform mat4 glModelViewMatrix;
uniform mat3 glNormalMatrix;


// Per-pixel normal (output to fragment shader)
varying vec3 normalDirection;
varying vec3 viewDirection;
varying vec4 position;

varying vec2 glTexCoord0;

void main()
{
    normalDirection = normalize(vec3(glNormalMatrix * glNormal));
 	vec3 v = vec3(glModelViewMatrix * glVertex);
	viewDirection = normalize(-v.xyz);
	position = vec4(v,1);
	glTexCoord0 = glMultiTexCoord0.st;
	
    // Transform the vertex
    gl_Position = glModelViewProjectionMatrix * glVertex;    
}






