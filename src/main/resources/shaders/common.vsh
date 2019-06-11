#version 430 core

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;

in vec4 iPosition;
in vec3 iNormal;
in vec2 iTexCoord;

out vec3 oPosition;
out vec3 oNormal;
out vec2 oTexCoord;

void main()
{
	oPosition = vec3(modelMatrix  * iPosition);
    oNormal = vec3(modelMatrix  * vec4(iNormal, 0.0));
    oTexCoord = iTexCoord;
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * iPosition;
}