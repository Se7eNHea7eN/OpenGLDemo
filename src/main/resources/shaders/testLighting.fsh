#version 330 core

in vec3 oPosition;
in vec3 oNormal;
in vec2 oTexCoord;

uniform vec3 objColor;
uniform float ambientStrength;
uniform vec3 ambientColor;

out vec4 FragColor;

struct PointLight
{
    vec3 position;
    vec3 color;
    float intensive;
    float farPlane;
};

uniform PointLight pointLights[8];
uniform int pointLightCount;


void main()
{
    vec3 ambient = ambientStrength * ambientColor;
    vec3 diffuse = vec3(0.);
    vec3 normal = normalize(oNormal);
    for(int i = 0;i<pointLightCount;i++){
        PointLight pointLight = pointLights[i];
        vec3 lightDir = normalize(pointLight.position - oPosition);
        float cosTheta = dot(lightDir,normal);
        diffuse += pointLight.color * pointLight.intensive * cosTheta;
    }
    vec3 color = (ambient + diffuse) * objColor;
    FragColor = vec4(color,1.0);
}