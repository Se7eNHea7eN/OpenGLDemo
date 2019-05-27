#version 330 core
layout (location = 0) in vec3 aPosition;

uniform mat4 modelMatrix;

void main()
{
    gl_Position = modelMatrix * vec4(aPosition, 1.0);
}