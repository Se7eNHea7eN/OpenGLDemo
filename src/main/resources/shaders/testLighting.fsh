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

uniform vec3 eyePos;
uniform float shininess;
uniform float specularStrength;

void main()
{
    vec3 ambient = ambientStrength * ambientColor;
    vec3 diffuse = vec3(0.);
    vec3 specular = vec3(0.);
    vec3 normal = normalize(oNormal);
    for(int i = 0;i<pointLightCount;i++){
        PointLight pointLight = pointLights[i];
        vec3 lightDir = normalize(pointLight.position - oPosition);
        float cosTheta = dot(lightDir,normal);
        diffuse += pointLight.color * pointLight.intensive * cosTheta;

        //视向量单位化
        vec3 viewDir = normalize(eyePos - oPosition);

        //计算出射向量
        vec3 reflectDir = normalize(reflect(-lightDir, normal));
        //计算高光值 视向量和出射向量夹角余弦值的shininess次幂。
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
        //优化为blinn-phong
//        vec3 halfwayDir = normalize(lightDir + viewDir);
//        float spec = pow(max(dot(normal, halfwayDir), 0.0), shininess);

        specular += specularStrength * spec * pointLight.color* pointLight.intensive;
    }
    vec3 color = (ambient + diffuse + specular) * objColor;
    FragColor = vec4(color,1.0);
}