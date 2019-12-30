#version 430 core

layout (vertices = 3) out;
layout(location = 1) in vec3 inColor[];
layout(location = 1) out vec3 outColor[];

uniform float inner_nmb;
uniform float outer_nmb;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 model;

void main(void){

    if (gl_InvocationID == 0){
        gl_TessLevelInner[0] = inner_nmb + 10;
        gl_TessLevelInner[1] = inner_nmb * 5;
        gl_TessLevelOuter[0] = 2;
        gl_TessLevelOuter[1] = outer_nmb * 5;
        gl_TessLevelOuter[2] = 2;
       gl_TessLevelOuter[3] = outer_nmb * 5;
    }

    gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;
    outColor[gl_InvocationID] = inColor[gl_InvocationID];
}