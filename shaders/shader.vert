#version 440
layout(location = 2) in vec3 inColor;
layout(location = 1) in vec2 inPosition;

layout(location = 1) out vec3 vsColor;

uniform mat4 view;
uniform mat4 projection;
uniform mat4 model;
uniform int mode;

out vec3 vertColor;
out vec3 normal;
out vec4 pos4;

vec3 getSphere(vec2 vec){
	float az = vec.x * 3.14;
	float ze = vec.y * 3.14 / 2;
	float r = 1;

	float x = r*cos(az)*cos(ze);
	float y = r*sin(az)*cos(ze);
	float z = r*sin(ze);
	return vec3(x, y, z);
}

vec3 getSphereNormal(vec2 vec){
	vec3 u = getSphere(vec+vec2(0.001, 0))
	- getSphere(vec-vec2(0.001,0));
	vec3 v = getSphere(vec+vec2(0, 0.001))
	- getSphere(vec-vec2(0, 0.001));
	return cross(u,v);
}



void main() {

	vec2 position = inPosition;
	position = inPosition * 2 - 1;
	pos4 = model*vec4(getSphere(position), 1.0);
	gl_Position = projection * view * pos4;
	normal= normalize(getSphereNormal(position));
	normal=inverse(transpose(mat3(view)* mat3(model)))*normal;
	vertColor=vec3(normal.xyz);
	vsColor = vertColor;
}