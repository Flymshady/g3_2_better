#version 150
in vec2 inPosition;

uniform mat4 view;
uniform mat4 projection;
uniform float time;
uniform float type;
uniform mat4 model;

float getZ(vec2 vec) {
    return sin(time + vec.y * 3.14 *2);
}

vec3 getDesk(vec2 vec){
    float x= vec.x;
    float y=vec.y;
    float z=1;
    return vec3(x,y,z);
}


vec3 getPVObject(vec2 vec){
    float az = vec.x * 3.14;
    float ze = vec.y * 3.14/2;
    float r = 1;

    float x = r*cos(az)*cos(ze);
    float y = 2*r*sin(az)*cos(ze);
    float z = 0.5*r*sin(ze);
    return vec3(x,y,z);
}
vec3 getElephant(vec2 vec) {
    float az = vec.x * 3.14;
    float ze = vec.y * 3.14 / 2;
    float r = 1+cos(4*az);

    float x = r*cos(az)*cos(ze);
    float y = r*sin(az)*cos(ze);
    float z = r*sin(ze);
    return vec3(x,y,z);
}


vec3 getMySpheric(vec2 vec) {
    float az = vec.x * 3.14;
    float ze = vec.y * 3.14 / 2;
    float r = 1+sin(ze)+cos(az);

    float x = r*cos(az)*cos(ze);
    float y = r*sin(az)*cos(ze);
    float z = r*sin(ze);
    return vec3(x,y,z);
}

vec3 getMySombrero(vec2 vec) {

    float az = vec.x*3.14;
    float r = vec.y*3.14;
    float v = cos(2*r);

    float x = r*cos(az);
    float y = r*sin(az);
    float z = v;
    return vec3(x, y, z);
}

vec3 getMyCylindric(vec2 vec) {
    float az = vec.x*3.14;
    float r = vec.y*3.14;
    float v = r;

    float x = r*cos(az);
    float y = r*sin(az);
    float z = v;
    return vec3(x, y, z);
}

vec3 getKart(vec2 vec){
    float x= 1.5*cos(vec.x*3.14)+cos(vec.y*2*3.14/2)*cos(vec.x*3.14);
    float y=2*sin(vec.x*3.14/2)+cos(vec.y*3.14/2)*sin(vec.x*3.14/2);
    float z =sin(vec.y*3.14);
    return vec3(x,y,z);
}

void main() {
    if(type==0){
        vec2 position;
        position = inPosition * 2 - 1;
        vec4 pos4 = vec4(getMySpheric(position), 1.0);
        gl_Position = projection * view *model* pos4;
    }
    if(type==1){
        vec2 position;
        position = inPosition * 2 - 1;
        vec4 pos4 = vec4(getElephant(position), 1.0);
        gl_Position = projection * view *model* pos4;
    }
    if(type==2){
        vec2 position = inPosition * 2 - 1;
        vec4 pos4 = vec4(position, getZ(position), 1.0);
        gl_Position = projection * view *model* pos4;
    }
    if(type==3){
        vec2 position = inPosition * 2 - 1;
        vec4 pos4 = vec4(getDesk(position), 1.0);
        gl_Position = projection * view *model* pos4;
    }
    if(type==4){
        vec2 position;
        position = inPosition * 2-1;
        vec4 pos4 = vec4(getMySombrero(position), 1.0);
        gl_Position = projection * view *model* pos4;
    }
    if(type==5){
        vec2 position;
        position = inPosition * 2 - 1;
        vec4 pos4 = vec4(getMyCylindric(position), 1.0);
        gl_Position = projection * view *model* pos4;
    }
    if(type==6){
        vec2 position;
        position = inPosition * 2 - 1;
        vec4 pos4 = vec4(getKart(position), 1.0);
        gl_Position = projection * view *model* pos4;
    }
    if(type==8){
        vec2 position = inPosition*2-1;
        vec4 pos4 = model*vec4(getPVObject(position), 1.0);
        gl_Position = projection * view * pos4;
    }
} 
