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


uniform int ignoreVertexColors;

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
const int maxLights = 1;
uniform lightSource glLightSource[maxLights];

uniform mat4 textureTransform;
 
//uniform int alphaTestEnabled;
//uniform int alphaTestFunction;
//uniform float alphaTestValue;


	// struct fogData
	// {
	// int fogEnabled = -1;
	// vec3 expColor = new Vector3f();
	// float expDensity;
	// vec3 linearColor = new Vector3f();
	// float linearStart;
	// float linearEnd;
	// };
	// uniform fogData fogData;


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
	mat4 glModelViewMatrix = glViewMatrix * glModelMatrix;
	gl_Position = glProjectionMatrix * glModelViewMatrix * glVertex;//glModelViewProjectionMatrix * glVertex;
	
	glTexCoord0 = (textureTransform * vec4(glMultiTexCoord0,0,1)).st;		

	mat3 glNormalMatrix =  mat3(transpose(inverse(glModelViewMatrix)));
	N = normalize(glNormalMatrix * glNormal);
		
	vec3 v = vec3(glModelViewMatrix * glVertex);

	ViewDir = -v.xyz;
	LightDir = glLightSource[0].position.xyz;

	A = glLightModelambient;
	if( ignoreVertexColors != 0) 
		C = vec4(1,1,1,1);//glFrontMaterialdiffuse; // objectColor should be used if it is no lighting
	else 
		C = glColor; 
	D = glLightSource[0].diffuse * glFrontMaterial.diffuse;		
	
	emissive = glFrontMaterial.emission.rgb;
	specular = glFrontMaterial.specular;
	shininess = glFrontMaterial.shininess;
		 
}
