struct PointLight
{
    vec3 position;
    vec3 color;
    float intensive;
};

uniform PointLight pointLights[8];
uniform int pointLightCount;


struct DirectionLight
{
    vec3 direction;
    vec3 color;
    float intensive;
};

uniform DirectionLight directionLights[8];
uniform int directionLightCount;



varying vec3 vPosition;
varying vec3 vNormal;

uniform vec3 objColor;

uniform float ambientStrength;
uniform vec3 ambientColor;

void main()
{
    //计算环境光
    vec3 ambient = ambientStrength * ambientColor;
    vec3 diffuse = vec3(0.);

    for(int i = 0 ; i < pointLightCount ; i++ ){
        PointLight pointLight = pointLights[i];
        //法线向量单位化
        vec3 norm = normalize(vNormal);
        //入射光向量单位化
        vec3 lightDir = normalize(pointLight.position - vPosition);

        //计算法线和入射光向量的点积。两个向量都是单位向量，实际结果为夹角的余弦值。如果是钝角余弦值可能出现负值，这里最小值限制为0。
        float diff = max(dot(norm, lightDir), 0.0);
        //夹角的余弦值和光照颜色相乘得到漫反射色值
        diffuse += diff * pointLight.color * pointLight.intensive;
    }

   for(int i = 0 ; i < directionLightCount ; i++ ){
        DirectionLight pointLight = directionLights[i];
        //法线向量单位化
        vec3 norm = normalize(vNormal);
        //入射光向量单位化
        vec3 lightDir = pointLight.direction;

        //计算法线和入射光向量的点积。两个向量都是单位向量，实际结果为夹角的余弦值。如果是钝角余弦值可能出现负值，这里最小值限制为0。
        float diff = max(dot(norm, lightDir), 0.0);
        //夹角的余弦值和光照颜色相乘得到漫反射色值
        diffuse += diff * pointLight.color * pointLight.intensive;
    }

    vec3 color =  (ambient + diffuse)  * objColor;
    gl_FragColor = vec4(color,1.0);
}
