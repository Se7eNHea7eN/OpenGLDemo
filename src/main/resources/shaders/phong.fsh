struct PointLight
{
    vec3 position;
    vec3 color;
    float intensive;
    mat4 matrix;
    sampler2D depthTexture;
};


uniform PointLight pointLights[8];
uniform int pointLightCount;


struct DirectionLight
{
    vec3 direction;
    vec3 color;
    float intensive;
    mat4 matrix;
    sampler2D depthTexture;
};

uniform DirectionLight directionLights[8];
uniform int directionLightCount;

varying vec3 vPosition;
varying vec3 vNormal;

uniform vec3 objColor;

uniform float ambientStrength;
uniform vec3 ambientColor;

uniform vec3 eyePos;
uniform float specularStrength;
uniform float shininess;

float ShadowCalculation(vec4 fragPosLightSpace,vec3 lightDir,sampler2D shadowMap)
{
    // 执行透视除法
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    // 变换到[0,1]的范围
    projCoords = projCoords * 0.5 + 0.5;

    if (projCoords.x >= 0.0 && projCoords.x <=1.0 && projCoords.y >= 0.0 && projCoords.y <=1.0){

        // 取得最近点的深度(使用[0,1]范围下的fragPosLight当坐标)
        float closestDepth = texture(shadowMap, projCoords.xy).r;
        // 取得当前片元在光源视角下的深度
        float currentDepth = projCoords.z;
        // 检查当前片元是否在阴影中
        float bias = max(0.0001 * (1.0 - dot(vNormal, lightDir)), 0.00001);
        float shadow = currentDepth - bias > closestDepth  ? 1.0 : 0.0;
        //float shadow = currentDepth > closestDepth  ? 1.0 : 0.0;
        return shadow;
    }
    return 0.;

}

void main()
{
    //计算环境光
    vec3 ambient = ambientStrength * ambientColor;
    vec3 diffuse = vec3(0.);
    vec3 specular= vec3(0.);

    for(int i = 0 ; i < pointLightCount ; i++ ){
        PointLight pointLight = pointLights[i];
        //法线向量单位化
        vec3 norm = normalize(vNormal);
        //入射光向量单位化
        vec3 lightDir = normalize(pointLight.position - vPosition);

     // 计算阴影
        float shadow = ShadowCalculation(pointLight.matrix * vec4(vPosition,1.),lightDir,pointLight.depthTexture);

        //计算法线和入射光向量的点积。两个向量都是单位向量，实际结果为夹角的余弦值。如果是钝角余弦值可能出现负值，这里最小值限制为0。
        float diff = max(dot(norm, lightDir), 0.0);
        //夹角的余弦值和光照颜色相乘得到漫反射色值
        diffuse += (1-shadow) *diff * pointLight.color * pointLight.intensive;

        //视向量单位化
        vec3 viewDir = normalize(eyePos - vPosition);
        //计算出射向量
        vec3 reflectDir = reflect(-lightDir, norm);
        //计算高光值 视向量和出射向量夹角余弦值的shininess次幂。
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
        specular += (1-shadow) * specularStrength * spec * pointLight.color* pointLight.intensive;
    }

    for(int i = 0 ; i < directionLightCount ; i++ ){
        DirectionLight pointLight = directionLights[i];
        //法线向量单位化
        vec3 norm = normalize(vNormal);
        //入射光向量单位化
        vec3 lightDir = pointLight.direction;

     // 计算阴影
        float shadow = ShadowCalculation(pointLight.matrix * vec4(vPosition,1.),lightDir,pointLight.depthTexture);

        //计算法线和入射光向量的点积。两个向量都是单位向量，实际结果为夹角的余弦值。如果是钝角余弦值可能出现负值，这里最小值限制为0。
        float diff = max(dot(norm, lightDir), 0.0);
        //夹角的余弦值和光照颜色相乘得到漫反射色值
        diffuse += (1-shadow) * diff * pointLight.color * pointLight.intensive;

        //视向量单位化
        vec3 viewDir = normalize(eyePos - vPosition);
        //计算出射向量
        vec3 reflectDir = reflect(-lightDir, norm);
        //计算高光值 视向量和出射向量夹角余弦值的shininess次幂。
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);



        specular += (1-shadow) * specularStrength * spec * pointLight.color* pointLight.intensive;
    }

    vec3 color =  (ambient + diffuse + specular)  * objColor;
    gl_FragColor = vec4(color,1.0);
}

