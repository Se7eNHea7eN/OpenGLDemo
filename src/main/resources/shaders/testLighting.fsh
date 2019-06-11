#version 330 core

in vec3 oPosition;
in vec3 oNormal;
in vec2 oTexCoord;

uniform vec3 objColor;
uniform float ambientStrength;
uniform vec3 ambientColor;

out vec4 FragColor;

void main()
{
    vec3 ambient = ambientStrength * ambientColor;
    FragColor = vec4(objColor * ambient,1.0);
}