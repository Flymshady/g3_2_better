#version 430 core
layout (triangles, equal_spacing, ccw) in;
layout(location = 1) in vec3 inColor[];
layout(location = 1) out vec3 outColor;

uniform mat4 view;
uniform mat4 projection;
uniform mat4 model;
uniform float inner_nmb;
uniform float outer_nmb;

const float PI = 3.14159265;

vec3 sphere(vec2 vec){
    vec=vec*2-1;
    float phi =  PI * (vec.x);
    float theta =  2*PI* (vec.y);
    float r = 1;
    float x = r*sin(phi)*cos(theta);
    float y = r*sin(phi)*sin(theta);
    float z = r*cos(phi);
    return vec3(x, y, z);
}
vec3 getSphereNormal(vec2 vec){
    vec3 u = sphere(vec+vec2(0.001, 0))
    - sphere(vec-vec2(0.001,0));
    vec3 v = sphere(vec+vec2(0, 0.001))
    - sphere(vec-vec2(0, 0.001));
    return cross(u,v);
}

void main(void){
    vec2 pos = vec2(gl_TessCoord);
    gl_Position =  projection * view * model * vec4( sphere(pos),1.0 );
    outColor = inColor[0];
    vec3 normal= normalize(getSphereNormal(pos));
    normal=inverse(transpose(mat3(view)* mat3(model)))*normal;
    outColor=vec3(normal.xyz);
}

