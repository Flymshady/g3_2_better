#version 440
layout(location = 2) in vec3 inColor;
layout(location = 1) in vec3 inPosition;
layout(location = 1) out vec3 vsColor;

void main() {

	vec3 position = inPosition;
	gl_Position = vec4(position, 1.0);
	vsColor = inColor;


}