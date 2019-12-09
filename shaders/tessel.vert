#version 440
layout(location = 2) in vec3 inColor; // vstup z vertex bufferu
layout(location = 1) in vec2 inPosition; // vstup z vertex bufferu

layout(location = 1) out vec3 vsColor; // vystup do dalsich casti retezce

uniform mat4 view;
uniform mat4 projection;
uniform float time;
uniform mat4 lightViewProjection;
uniform float type;
uniform mat4 model;
uniform vec3 lightPos;
uniform int mode;


out vec3 vertColor;
out vec3 normal;
out vec3 light;
out vec3 viewDirection;
out vec4 depthTextureCoord;
out vec2 texCoord;
out vec3 depthColor;
out vec4 pos4;
out float intensity;
out float dist;

vec3 getSun(vec2 vec){
	float az = vec.x * 3.14;
	float ze = vec.y * 3.14 / 2;
	float r = 1;

	float x = r*cos(az)*cos(ze);
	float y = r*sin(az)*cos(ze);
	float z = r*sin(ze);
	return vec3(x, y, z);
}

vec3 getSunNormal(vec2 vec){
	vec3 u = getSun(vec+vec2(0.001, 0))
	- getSun(vec-vec2(0.001,0));
	vec3 v = getSun(vec+vec2(0, 0.001))
	- getSun(vec-vec2(0, 0.001));
	return cross(u,v);
}



void main() {



	vec2 position = inPosition;
	position = inPosition * 2 - 1;
	pos4 = model*vec4(getSun(position), 1.0);
	gl_Position = projection * view * pos4;
	normal= normalize(getSunNormal(position));
	normal=inverse(transpose(mat3(view)* mat3(model)))*normal;

	vertColor=vec3(normal.xyz);
	vsColor = vertColor;
	//vsColor = inColor*(gl_VertexID);
} 
