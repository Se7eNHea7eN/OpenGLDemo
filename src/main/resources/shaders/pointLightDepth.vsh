#version 330 core
in vec3 iPosition;

uniform mat4 modelMatrix;

void main()
{
    gl_Position = modelMatrix * vec4(iPosition, 1.0);
}