#version 150
in vec2 inPosition;

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

vec3 getPVObjectNormal(vec2 vec){
	vec3 u = getPVObject(vec+vec2(0.001, 0))
	- getPVObject(vec-vec2(0.001,0));
	vec3 v = getPVObject(vec+vec2(0, 0.001))
	- getPVObject(vec-vec2(0, 0.001));
	return cross(u,v);
}

vec3 getSunNormal(vec2 vec){
	vec3 u = getSun(vec+vec2(0.001, 0))
	- getSun(vec-vec2(0.001,0));
	vec3 v = getSun(vec+vec2(0, 0.001))
	- getSun(vec-vec2(0, 0.001));
	return cross(u,v);
}

vec3 getKart(vec2 vec){
	float x= 1.5*cos(vec.x*3.14)+cos(vec.y*2*3.14/2)*cos(vec.x*3.14);
	float y=2*sin(vec.x*3.14/2)+cos(vec.y*3.14/2)*sin(vec.x*3.14/2);
	float z =sin(vec.y*3.14);
	return vec3(x,y,z);
}

vec3 getKartNormal(vec2 vec){
	vec3 u = getKart(vec+vec2(0.001, 0))
	- getKart(vec-vec2(0.001,0));
	vec3 v = getKart(vec+vec2(0, 0.001))
	- getKart(vec-vec2(0, 0.001));
	return cross(u,v);
}


float getZ(vec2 vec) {
	return sin(time + vec.y * 3.14 *2);
}

vec3 getWave(vec2 vec){
	float x= vec.x;
	float y=vec.y;
	float z = getZ(vec);
	return vec3(x,y,z);
}

vec3 getDesk(vec2 vec){
	float x= vec.x;
	float y=vec.y;
	float z=1;
	return vec3(x,y,z);
}
vec3 getDeskNormal(vec2 vec){
	vec3 u = getDesk(vec+vec2(0.001, 0))
	- getDesk(vec-vec2(0.001, 0));
	vec3 v = getDesk(vec+vec2(0, 0.001))
	- getDesk(vec-vec2(0, 0.001));
	return cross(u, v);
}

vec3 getElephant(vec2 vec) {
	float az = vec.x * 3.14;
	float ze = vec.y * 3.14/2;
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

vec3 getElephantNormal(vec2 vec){
	vec3 u = getElephant(vec+vec2(0.001, 0))
	- getElephant(vec-vec2(0.001,0));

	vec3 v = getElephant(vec+vec2(0, 0.001))
	- getElephant(vec-vec2(0, 0.001));
	return cross(u,v);
}

vec3 getMySphericNormal(vec2 vec){
	vec3 u = getMySpheric(vec+vec2(0.001, 0))
	- getMySpheric(vec-vec2(0.001,0));

	vec3 v = getMySpheric(vec+vec2(0, 0.001))
	- getMySpheric(vec-vec2(0, 0.001));
	return cross(u,v);
}

vec3 getWaveNormal(vec2 vec){
	vec3 u = getWave(vec+vec2(0.001, 0))
	- getWave(vec-vec2(0.001,0));

	vec3 v = getWave(vec+vec2(0, 0.001))
	- getWave(vec-vec2(0, 0.001));
	return cross(u,v);
}

vec3 getMySombrero(vec2 vec) {
	float az = vec.x*3.14*2;
	float r = vec.y*3.14*2;
	float v = cos(2*r);

	float x = r*cos(az);
	float y = r*sin(az);
	float z = v;
	return vec3(x, y, z);

}
vec3 getSombreroNormal(vec2 vec){
	vec3 u = getMySombrero(vec+vec2(0.001, 0))
	- getMySombrero(vec-vec2(0.001,0));

	vec3 v = getMySombrero(vec+vec2(0, 0.001))
	- getMySombrero(vec-vec2(0, 0.001));
	return cross(u,v);
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
vec3 getCylindricNormal(vec2 vec){
	vec3 u = getMyCylindric(vec+vec2(0.001, 0))
	- getMyCylindric(vec-vec2(0.001,0));

	vec3 v = getMyCylindric(vec+vec2(0, 0.001))
	- getMyCylindric(vec-vec2(0, 0.001));
	return cross(u,v);
}

void main() {
	vec2 position;
	if(mode==7){ //per vertex
		if (type==8){
			position = inPosition*2-1;
			pos4 = model*vec4(getPVObject(position), 1.0);
			gl_Position = projection * view * pos4;
			normal= normalize(getPVObjectNormal(position));
			normal=inverse(transpose(mat3(view)* mat3(model)))*normal;
			light = normalize(mat3(view)*lightPos - (view * pos4).xyz);
			intensity = dot(light, normal);
			vertColor=vec3(normal.xyz);
		}
		if (type==0){
			position = inPosition * 2 - 1;
			pos4 = model*vec4(getMySpheric(position), 1.0);
			gl_Position = projection * view * pos4;
			normal= normalize(getMySphericNormal(position));
			normal=inverse(transpose(mat3(view)* mat3(model)))*normal;
			light = normalize(mat3(view)*lightPos - (view * pos4).xyz);
			intensity = dot(light, normal);
			vertColor=vec3(normal.xyz);
		}
	}else if(mode==8){ //per pixel
		if (type==8){
			position = inPosition*2-1;
			pos4 = model*vec4(getPVObject(position), 1.0);
			gl_Position = projection * view * pos4;
			normal= normalize(getPVObjectNormal(position));
			normal=inverse(transpose(mat3(view)* mat3(model)))*normal;
			light = normalize(mat3(view)*lightPos - (view * pos4).xyz);
		}
		if (type==0){
			position = inPosition * 2 - 1;
			pos4 = model*vec4(getMySpheric(position), 1.0);
			gl_Position = projection * view * pos4;
			normal= normalize(getMySphericNormal(position));
			normal=inverse(transpose(mat3(view)* mat3(model)))*normal;
			light = normalize(mat3(view)*lightPos - (view * pos4).xyz);

		}
	}else {
		if (type==0){
			position = inPosition * 2 - 1;
			pos4 = model*vec4(getMySpheric(position), 1.0);
			gl_Position = projection * view * pos4;
			normal= normalize(getMySphericNormal(position));
			normal=inverse(transpose(mat3(view)* mat3(model)))*normal;
		}
		if (type==1){
			position = inPosition * 2 - 1;
			pos4 = model*vec4(getElephant(position), 1.0);
			gl_Position = projection * view * pos4;
			normal= normalize(getElephantNormal(position));
			normal=inverse(transpose(mat3(view)* mat3(model)))*normal;
		}
		if (type==2){
			position = inPosition * 2 - 1;
			pos4 = model*vec4(position, getZ(position), 1.0);
			gl_Position = projection * view * pos4;
			normal= normalize(getWaveNormal(position));
			normal=inverse(transpose(mat3(view)* mat3(model)))*normal;
		}

		if (type==3){
			position = inPosition*2 - 1;
			pos4 = model*vec4(getDesk(position), 1.0);
			gl_Position = projection * view * pos4;
			normal= normalize(getDeskNormal(position));
			normal=inverse(transpose(mat3(view)* mat3(model)))*normal;
		}
		if (type==4){
			position = inPosition*2-1;
			pos4 = model*vec4(getMySombrero(position), 1.0);
			gl_Position = projection * view * pos4;
			normal= normalize(getSombreroNormal(position));
			normal=inverse(transpose(mat3(view)* mat3(model)))*normal;
		}
		if (type==5){
			position = inPosition*2-1;
			pos4 = model*vec4(getMyCylindric(position), 1.0);
			gl_Position = projection * view * pos4;
			normal= normalize(getCylindricNormal(position));
			normal=inverse(transpose(mat3(view)* mat3(model)))*normal;
		}
		if (type==6){
			position = inPosition*2-1;
			pos4 = model*vec4(getKart(position), 1.0);
			gl_Position = projection * view * pos4;
			normal= normalize(getKartNormal(position));
			normal=inverse(transpose(mat3(view)* mat3(model)))*normal;
		}
		if (type==7){
			position = inPosition*2-1;
			pos4 = model*vec4(getSun(position), 1.0);
			gl_Position = projection * view * pos4;
			normal= normalize(getSunNormal(position));
			normal=inverse(transpose(mat3(view)* mat3(model)))*normal;
		}
		vertColor = pos4.xyz;
		depthColor = pos4.zzz;
		light = normalize(mat3(view)*lightPos - (view * pos4).xyz);
		texCoord = inPosition;
		viewDirection = -(view* pos4).xyz;
		depthTextureCoord = lightViewProjection *pos4;
		depthTextureCoord.xyz = depthTextureCoord.xyz/depthTextureCoord.w;
		depthTextureCoord.xyz = (depthTextureCoord.xyz + 1) / 2;
		dist=length(light);
	}
	if (type==7){ //sun object
		position = inPosition*2-1;
		pos4 = model*vec4(getSun(position), 1.0);
		gl_Position = projection * view * pos4;
		normal= normalize(getSunNormal(position));
		normal=inverse(transpose(mat3(view)* mat3(model)))*normal;
	}
} 
