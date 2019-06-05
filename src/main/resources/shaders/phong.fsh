#version 430 core

struct PointLight
{
    vec3 position;
    vec3 color;
    float intensive;
    //samplerCubeShadow depthTexture;
    float farPlane;
};

uniform PointLight pointLights[8];
uniform samplerCube pointLightCubeShadows[8];

uniform int pointLightCount;

struct DirectionLight
{
    vec3 direction;
    vec3 color;
    float intensive;
    mat4 matrix;
    //sampler2D depthTexture;
};
uniform DirectionLight directionLights[8];
uniform sampler2D directionLightShadows[8];

uniform int directionLightCount;

in vec3 vPosition;
in vec3 vNormal;
in vec2 vTexCoord;

uniform vec3 objColor;
uniform sampler2D objTexture;
uniform int useTexture;

uniform float ambientStrength;
uniform vec3 ambientColor;

uniform vec3 eyePos;
uniform float specularStrength;
uniform float shininess;

out vec4 FragColor;

float PointShadowCalculation(vec3 fragPos,PointLight pointLight,samplerCube shadowMap)
{
    vec3 fragToLight = fragPos - pointLight.position;
    float closestDepth = texture(shadowMap, fragToLight).r;
    if(closestDepth < 0){
       closestDepth = 1.0;
    }

    closestDepth *= pointLight.farPlane;


    // Now get current linear depth as the length between the fragment and light position
    float currentDepth = length(fragToLight);
    // Now test for shadows
    float bias = 0.05;

    float shadow = currentDepth -  bias > closestDepth ? 1.0 : 0.0;
    //shadow = 0.;
    return shadow;
}

float ShadowCalculation(vec4 fragPosLightSpace,vec3 lightDir,sampler2D shadowMap)
{
    // 执行透视除法
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    // 变换到[0,1]的范围
    projCoords = projCoords * 0.5 + 0.5;
    if(projCoords.z > 1.0)
        return 0.0;

    // 取得最近点的深度(使用[0,1]范围下的fragPosLight当坐标)
    float closestDepth = texture(shadowMap, projCoords.xy).r;
    // 取得当前片元在光源视角下的深度
    float currentDepth = projCoords.z;
    // 检查当前片元是否在阴影中
    float bias = max(0.0001 * (1.0 - dot(vNormal, lightDir)), 0.00001);
    float shadow = currentDepth - bias > closestDepth  ? 1.0 : 0.0;

    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
    for(int x = -1; x <= 1; ++x)
    {
        for(int y = -1; y <= 1; ++y)
        {
            float pcfDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
            shadow += currentDepth - bias > pcfDepth ? 1.0 : 0.0;
        }
    }
    shadow /= 9.0;

    return shadow;
}


void main()
{
    //计算环境光
    vec3 ambient = ambientStrength * ambientColor;
    vec3 diffuse = vec3(0.);
    vec3 specular= vec3(0.);

    //遍历点光源
    for(int i = 0 ; i < pointLightCount ; i++ ){
        PointLight pointLight = pointLights[i];
        //法线向量单位化
        vec3 norm = normalize(vNormal);
        //入射光向量单位化
        vec3 lightDir = normalize(pointLight.position - vPosition);

     // 计算阴影
        float shadow = PointShadowCalculation(vPosition,pointLight,pointLightCubeShadows[i]);

        //计算法线和入射光向量的点积。两个向量都是单位向量，实际结果为夹角的余弦值。如果是钝角余弦值可能出现负值，这里最小值限制为0。
        float diff = max(dot(norm, lightDir), 0.0);
        //夹角的余弦值和光照颜色相乘得到漫反射色值
        diffuse += (1-shadow) *diff * pointLight.color * pointLight.intensive;

        //视向量单位化
        vec3 viewDir = normalize(eyePos - vPosition);

                //计算出射向量
                //vec3 reflectDir = reflect(-lightDir, norm);
                //计算高光值 视向量和出射向量夹角余弦值的shininess次幂。
                //float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
        //优化为blinn-phong
        vec3 halfwayDir = normalize(lightDir + viewDir);
        float spec = pow(max(dot(norm, halfwayDir), 0.0), shininess);

        specular += (1-shadow) * specularStrength * spec * pointLight.color* pointLight.intensive;
    }
    //遍历定向光源
    for(int i = 0 ; i < directionLightCount ; i++ ){
        DirectionLight directionLight = directionLights[i];
        //法线向量单位化
        vec3 norm = normalize(vNormal);
        //入射光向量单位化
        vec3 lightDir = normalize(directionLight.direction);

     // 计算阴影
        float shadow = ShadowCalculation(directionLight.matrix * vec4(vPosition,1.),lightDir,directionLightShadows[i]);

        //计算法线和入射光向量的点积。两个向量都是单位向量，实际结果为夹角的余弦值。如果是钝角余弦值可能出现负值，这里最小值限制为0。
        float diff = max(dot(norm, -lightDir), 0.0);
        //夹角的余弦值和光照颜色相乘得到漫反射色值
        diffuse += (1-shadow) * diff * directionLight.color * directionLight.intensive;

        //视向量单位化
        vec3 viewDir = normalize(eyePos - vPosition);
                //计算出射向量
                //vec3 reflectDir = reflect(-lightDir, norm);
                //计算高光值 视向量和出射向量夹角余弦值的shininess次幂。
                //float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);

        //优化为blinn-phong
        vec3 halfwayDir = normalize(lightDir + viewDir);
        float spec = pow(max(dot(norm, halfwayDir), 0.0), shininess);

        specular += (1-shadow) * specularStrength * spec * directionLight.color* directionLight.intensive;
    }
    vec4 baseColor = vec4(objColor,1.0);
    if(useTexture != 0)
        baseColor = texture2D(objTexture,vTexCoord);

    vec3 color =  (ambient + diffuse + specular)  * baseColor.rgb;
    FragColor = vec4(color,1.0);
}

