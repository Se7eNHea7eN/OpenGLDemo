#version 430 core

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;

in vec4 aPosition;
in vec3 aNormal;
in vec2 aTexCoord;

out vec3 vPosition;
out vec3 vNormal;
out vec2 vTexCoord;

void main()
{
	vPosition = vec3(modelMatrix  * aPosition);
    vNormal = vec3(modelMatrix  * vec4(aNormal, 0.0));
    vTexCoord = aTexCoord;
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * aPosition;
}