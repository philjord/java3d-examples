#version 150 
//#version 120 is not optional, trouble otherwise

//Note don't put if else constructs on one line or trouble

in vec4 glVertex;         
in vec4 glColor;       
in vec3 glNormal;     
in vec2 glMultiTexCoord0; 

uniform mat4 glProjectionMatrix;
//uniform mat4 glProjectionMatrixInverse;
uniform mat4 glViewMatrix;
uniform mat4 glModelMatrix;
//uniform mat4 glModelViewMatrix;
//uniform mat4 glModelViewMatrixInverse;
//uniform mat4 glModelViewProjectionMatrix;
				
//uniform mat3 glNormalMatrix;

//uniform vec4 glFrontMaterialambient;
uniform vec4 glFrontMaterialdiffuse;
uniform vec4 glFrontMaterialemission;
uniform vec3 glFrontMaterialspecular;
uniform float glFrontMaterialshininess;
uniform int ignoreVertexColors;

uniform vec4 glLightModelambient;

uniform vec4 glLightSource0position;
uniform vec4 glLightSource0diffuse;

uniform mat4 textureTransform;
 
//uniform int alphaTestEnabled;
//uniform int alphaTestFunction;
//uniform float alphaTestValue;


//uniform int fogEnabled;
//uniform vec4 expColor;
//uniform float expDensity;
//uniform vec4 linearColor;
//uniform float linearStart;
//uniform float linearEnd;

//End of FFP inputs
//The line above in not optional for parsing reasons

//Fixed function pipeline pre-calculated values not available
//vec3 halfVector = normalize(vec3(gl_LightSource[0].halfVector));
//http://stackoverflow.com/questions/3744038/what-is-half-vector-in-modern-glsl
// vec3 ecPos = vec3(glModelViewMatrix * glVertex);	
// vec3 ecL;
// if(	glLightSource0position.w == 0.0)
// 	ecL = vec3(glLightSource0position.xyz);// no -ecPos in case of dir lights?
//	else
//	ecL = vec3(glLightSource0position.xyz - ecPos);
//  vec3 L = normalize(ecL.xyz); 
//	vec3 V = -ecPos.xyz; 
//	vec3 halfVector = normalize(L + V);

// gl_FrontLightModelProduct.sceneColor  // Derived. Ecm + Acm * Acs (Acs is normal glLightModelambient)
// use vec4 sceneColor = glFrontMaterialemission + glFrontMaterialambient * glLightModelambient;


//gl_LightSource[i].specular
//use glFrontMaterialspecular

//gl_LightSource[i].ambient
//use glLightModelambient

//gl_FrontLightProduct[i]
//vec4 ambient;    // Acm * Acli (Acli does not exist)
//vec4 diffuse;    // Dcm * Dcli
//vec4 specular;   // Scm * Scli (Scli does not exist)
// calculate yourself




out vec2 glTexCoord0;

out vec3 LightDir;
out vec3 ViewDir;

out vec3 N;

out vec4 A;
out vec4 C;
out vec4 D;

out vec3 emissive;
out vec3 specular;
out float shininess;

void main( void )
{
	mat4 glModelViewMatrix = glViewMatrix*glModelMatrix;
	gl_Position = glProjectionMatrix*glModelViewMatrix * glVertex;//glModelViewProjectionMatrix * glVertex;
	
	glTexCoord0 = (textureTransform * vec4(glMultiTexCoord0,0,1)).st;		

	mat3 glNormalMatrix =  mat3(transpose(inverse(glModelViewMatrix)));
	N = normalize(glNormalMatrix * glNormal);
		
	vec3 v = vec3(glModelViewMatrix * glVertex);

	ViewDir = -v.xyz;
	LightDir = glLightSource0position.xyz;

	A = glLightModelambient;
	if( ignoreVertexColors != 0) 
		C = glFrontMaterialdiffuse; // objectColor should be used if it is no lighting
	else 
		C = glColor; 
	D = glLightSource0diffuse * glFrontMaterialdiffuse;		
	
	emissive = glFrontMaterialemission.rgb;
	specular = glFrontMaterialspecular;
	shininess = glFrontMaterialshininess;
		 
}
