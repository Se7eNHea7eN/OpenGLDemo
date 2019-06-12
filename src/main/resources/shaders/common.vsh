#version 330 core

layout(location = 0) in vec4 iPosition;
layout(location = 1) in vec3 iNormal;
layout(location = 2) in vec2 iTexCoord;


uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;


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