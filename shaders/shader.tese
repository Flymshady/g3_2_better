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



vec3 getSphere(){

    float phi =  PI * (gl_TessCoord.x * 2 - 1);
    float theta =  2*(PI) * (gl_TessCoord.y * 2 - 1);
    float r = 1;

    float x = r*cos(phi)*sin(theta);
    float y = r*sin(phi)*sin(theta);
    float z = r*cos(theta);
    return vec3(x, y, z);


    /*
    float phi = 2 * PI * (gl_TessCoord.x - 0.5);
    float theta = 4 * PI * (gl_TessCoord.y - 0.5);
    float r = 1;

    float x = r*cos(phi)*cos(theta);
    float y = r*sin(phi);
    float z = r*cos(phi)*sin(theta);
    return vec3(x, y, z);
    */
}


vec3 sphere(vec2 vec){
    float az = vec.x * 3.14;
    float ze = vec.y * 3.14 / 2;
    float r = 1;

    float x = r*cos(az)*cos(ze);
    float y = r*sin(az)*cos(ze);
    float z = r*sin(ze);
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



    vec3 xyz = getSphere();

    gl_Position =  projection * view * model * vec4( xyz,1.0 );

    outColor = inColor[0];

    vec2 pos = vec2(gl_in[0].gl_Position.xy);
    vec3 normal= normalize(getSphereNormal(pos));
    normal=inverse(transpose(mat3(view)* mat3(model)))*normal;


    outColor = gl_TessCoord.x * vec3(1.0, 0.0, 0.0) + gl_TessCoord.y * vec3(0.0,1.0,0.0) +
    gl_TessCoord.z * vec3(1.0,1.0,0.0);


 //   outColor=vec3(normal.xyz);
    /*
        vec3 position =  vec3(gl_in[0].gl_Position.xyz*2-1);
        vec4 pos4 = vec4(getSphere(position), 1.0);
        gl_Position = projection * view * model* pos4;


        outColor = gl_TessCoord.x * vec3(1.0, 0.0, 0.0) + gl_TessCoord.y * vec3(0.0,1.0,0.0) +
        gl_TessCoord.z * vec3(1.0,1.0,0.0);

        outColor = inColor[0];

        outColor = gl_TessCoord.x * inColor[0] +
        gl_TessCoord.y * inColor[1] +
        gl_TessCoord.z * inColor[2];

        */


    /*
    gl_Position = (gl_TessCoord.x * gl_in[0].gl_Position +
    gl_TessCoord.y * gl_in[1].gl_Position +
    gl_TessCoord.z * gl_in[2].gl_Position);

    outColor = gl_TessCoord.x * vec3(1.0, 0.0, 0.0) + gl_TessCoord.y * vec3(0.0,1.0,0.0) +
    gl_TessCoord.z * vec3(1.0,1.0,0.0);

    outColor = inColor[0];

    outColor = gl_TessCoord.x * inColor[0] +
    gl_TessCoord.y * inColor[1] +
    gl_TessCoord.z * inColor[2];
*/
//
/*
    vec3 p = gl_in[0].gl_Position.xyz;
    float u = gl_TessCoord.x;
    float v = gl_TessCoord.y;
    float w = gl_TessCoord.z;
    float phi = PI * ( u - .5 );
    float theta = 2. * PI * ( v - .5 );
    float cosphi = cos(phi);
    vec3 xyz = vec3( cosphi*cos(theta), sin(phi), cosphi*sin(theta) );
    normal = xyz;
    vertColor = vec3(normal.xyz);

    xyz *= (inner_nmb );
    xyz += inner_nmb;
    gl_Position = projection * view * model * vec4( xyz,1. );


*/


//
  /*
    pos4 = model*vec4(getSphere(position), 1.0);
    gl_Position = projection * view * pos4;
    normal= normalize(getSphereNormal(position));
    normal=inverse(transpose(mat3(view)* mat3(model)))*normal;

    vertColor=vec3(normal.xyz);
    vsColor = vertColor;

    gl_Position = (gl_TessCoord.x * gl_in[0].gl_Position +
    gl_TessCoord.y * gl_in[1].gl_Position +
    gl_TessCoord.z * gl_in[2].gl_Position);
    */

   /* outColor = gl_TessCoord.x * vec3(1.0, 0.0, 0.0) + gl_TessCoord.y * vec3(0.0,1.0,0.0) +
    gl_TessCoord.z * vec3(1.0,1.0,0.0);

    outColor = inColor[0];

    outColor = gl_TessCoord.x * inColor[0] +
    gl_TessCoord.y * inColor[1] +
    gl_TessCoord.z * inColor[2];
    */


}

