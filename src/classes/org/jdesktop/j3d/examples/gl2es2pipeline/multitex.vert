// tex coord gen sample from http://www.ogre3d.org/forums/viewtopic.php?f=4&t=59737




// GL2ES2: Java3D built-in attributes, these are calculated and passsed in if declared here
attribute vec4 glVertex;
attribute vec3 glNormal;  

// GL2ES2: Java3D built-in uniforms, these are calculated and passsed in if declared here
uniform mat4 glModelViewProjectionMatrix;
uniform mat4 glModelViewMatrix;
uniform mat3 glNormalMatrix;

// Per-pixel normal (output to fragment shader)
varying vec3  Normal;

varying vec2 texCoord0;
varying vec2 texCoord1;

	//Example 8-4   Sphere Map Texture Coordinate Generation
    // position is the normalized position coordinate in eye space 
    // normal is the normalized normal coordinate in eye space 
    // returns a vec2 texture coordinate
vec2 sphere_map(vec3 position, vec3 normal)
{
    vec3 reflection = reflect(position, normal);
    float m = 2.0 * sqrt(reflection.x * reflection.x + reflection.y * reflection.y + (reflection.z + 1.0) * (reflection.z + 1.0)); 
    return vec2((reflection.x / m + 0.5), (reflection.y / m + 0.5));
}

    //Example 8-5   Cube Map Texture Coordinate Generation

    // position is the normalized position coordinate in eye space 
    // normal is the normalized normal coordinate in eye space 
    // returns the reflection vector as a vec3 texture coordinate 
vec3 cube_map(vec3 position, vec3 normal) 
{
    return reflect(position, normal);
}

//Object Linear Mapping
//When the texture generation mode is set to GL_OBJECT_LINEAR, texture coordinates are generated using the following function:
//coord = P1*X + P2*Y + P3*Z + P4*W
// The X, Y, Z, and W values are the vertex coordinates from the object being textured, and the P1–P4 values are the coefficients for a plane equation.

//For this shader in my code I have:
//Vector4f plane0S = new Vector4f(3.0f, 1.5f, 0.3f, 0.0f); //to calc coord S
//Vector4f plane0T = new Vector4f(1.0f, 2.5f, 0.24f, 0.0f); //to calc coord T
//I could hand them is as uniforms, but I choose to hard code them


vec2 object_linear(vec4 pos, vec4 planeOS, vec4 planeOT)
{
	return vec2(pos.x*planeOS.x+pos.y*planeOS.y+pos.z*planeOS.z+ pos.w*planeOS.w,pos.x*planeOT.x+pos.y*planeOT.y+pos.z*planeOT.z+pos.w*planeOT.w);
}
    
    
void main()
{
    Normal = normalize(vec3(glNormalMatrix * glNormal));

    // Transform the vertex
    gl_Position = glModelViewProjectionMatrix * glVertex;
    
    texCoord0 = object_linear(gl_Position, vec4(3.0, 1.5, 0.3, 0.0),vec4(1.0, 2.5, 0.24, 0.0)); 
	texCoord1 = sphere_map(gl_Position.xyz, Normal);
    
}
