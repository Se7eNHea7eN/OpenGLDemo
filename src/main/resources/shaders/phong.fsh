precision mediump float;

struct PointLight
{
    int enable;
    vec3 position;
    vec3 color;
    float intensive;
};

uniform PointLight pointlights[8];

varying vec3 vPosition;
varying vec3 vNormal;

uniform vec3 objColor;

uniform float ambientStrength;
uniform vec3 ambientColor;

uniform vec3 eyePos;
uniform float specularStrength;
uniform float shininess;
void main()
{
    //计算环境光
    vec3 ambient = ambientStrength * ambientColor;
    vec3 diffuse;
    vec3 specular;

    for(int i = 0 ; i < 8 ; i++ ){
        PointLight pointLight = pointlights[i];
        if(pointLight.enable !=0){
            //法线向量单位化
            vec3 norm = normalize(vNormal);
            //入射光向量单位化
            vec3 lightDir = normalize(pointLight.position - vPosition);

            //计算法线和入射光向量的点积。两个向量都是单位向量，实际结果为夹角的余弦值。如果是钝角余弦值可能出现负值，这里最小值限制为0。
            float diff = max(dot(norm, lightDir), 0.0);
            //夹角的余弦值和光照颜色相乘得到漫反射色值
            diffuse += diff * pointLight.color;

            //视向量单位化
            vec3 viewDir = normalize(eyePos - vPosition);
            //计算出射向量
            vec3 reflectDir = reflect(-lightDir, norm);
            //计算高光值 视向量和出射向量夹角余弦值的shininess次幂。
            float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
            specular += specularStrength * spec * pointLight.color;
        }
    }

    vec3 color =  (ambient + diffuse + specular)  * objColor;
    gl_FragColor = vec4(color,1.0);
}

