#version 440
layout(location = 1) in vec3 inColor;
out vec4 outColor;
void main() {
	outColor = vec4(inColor, 1.0);
} 