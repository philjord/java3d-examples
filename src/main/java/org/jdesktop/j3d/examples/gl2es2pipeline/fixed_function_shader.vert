#version 150 

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

uniform int ignoreVertexColors;

uniform vec4 glLightModelambient;

struct material
{
	int lightEnabled;
 	vec4 ambient;
 	vec4 diffuse;
 	vec4 emission;
 	vec3 specular;
 	float shininess;
};
uniform material glFrontMaterial;

struct lightSource
{
	 vec4 position;
	 vec4 diffuse;
	 vec4 specular;
	 float constantAttenuation, linearAttenuation, quadraticAttenuation;
	 float spotCutoff, spotExponent;
	 vec3 spotDirection;
};

uniform int numberOfLights;// numberOfLights will be set to how many the pipeline can send
const int maxLights = 1;// this is for the shader, it will process no more than this, must be a const
uniform lightSource glLightSource[maxLights];

uniform mat4 textureTransform;

// alpha testing is normally done in frag versus the texture alpha  
//uniform int alphaTestEnabled;
//uniform int alphaTestFunction;
//uniform float alphaTestValue;


// struct fogData
// {
// int fogEnabled = -1;
// vec4 expColor;
// float expDensity;
// vec4 linearColor;
// float linearStart;
// float linearEnd;
// };
// uniform fogData fogData;


// Fixed function pipeline pre-calculated values not available:
// Note: 
// A,D,S = Ambient,Diffuse,Specular 
// cm, cli = glFrontMaterial, glLightSource


// gl_LightSource[i].halfVector
// http://stackoverflow.com/questions/3744038/what-is-half-vector-in-modern-glsl
// vec3 ecPos = vec3(glModelViewMatrix * glVertex);	
// vec3 ecL;
// if(	glLightSource[i].position.w == 0.0)
// 	ecL = vec3(glLightSource0position.xyz);// no -ecPos in case of dir lights?
//	else
//	ecL = vec3(glLightSource0position.xyz - ecPos);
//  vec3 L = normalize(ecL.xyz); 
//	vec3 V = -ecPos.xyz; 
//	vec3 halfVector = normalize(L + V);

//gl_LightSource[i].ambient
//use glLightModelambient

// gl_FrontLightModelProduct.sceneColor  
// Derived. Ecm + Acm * Acs (Acs is normal glLightModelambient)
// use vec4 sceneColor = glFrontMaterial.emission + glFrontMaterial.ambient * glLightModelambient;


//gl_FrontLightProduct[i]
//vec4 ambient;    // Acm * Acli (Acli does not exist use glLightModelambient)
//vec4 diffuse;    // Dcm * Dcli
//vec4 specular;   // Scm * Scli
// calculate yourself


out vec2 glTexCoord0;

out vec3 LightDir;
out vec3 ViewVec;

out vec3 N;

out vec4 A;
out vec4 C;
out vec4 D;
out vec3 S;
out vec3 E;

out float shininess;

void main( void )
{
	mat4 glModelViewMatrix = glViewMatrix * glModelMatrix;
	gl_Position = glProjectionMatrix * glModelViewMatrix * glVertex;//glModelViewProjectionMatrix * glVertex;
	
	glTexCoord0 = (textureTransform * vec4(glMultiTexCoord0,0,1)).st;		

	mat3 glNormalMatrix =  mat3(transpose(inverse(glModelViewMatrix)));
	N = normalize(glNormalMatrix * glNormal);
		
	vec3 v = vec3(glModelViewMatrix * glVertex);

	ViewVec = -v.xyz;
	LightDir = glLightSource[0].position.xyz;

	A = glLightModelambient;
	if( ignoreVertexColors != 0) 
		C = vec4(1,1,1,1);//glFrontMaterialdiffuse; // objectColor should be used if it is no lighting
	else 
		C = glColor; 
	D = glLightSource[0].diffuse * glFrontMaterial.diffuse;	
	S = glLightSource[0].specular.rgb * glFrontMaterial.specular;		
	E = glFrontMaterial.emission.rgb;
	
	shininess = glFrontMaterial.shininess;
		 
}
