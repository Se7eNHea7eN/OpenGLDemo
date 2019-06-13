#version 330 core
out vec4 FragColor;
in vec2 oTexCoord;
in vec3 oPosition;
in vec3 oNormal;

uniform sampler2D  normalMap;
uniform sampler2D  albedoMap;
uniform sampler2D  metallicMap;
uniform int useMetallicMap;
uniform float metallicValue;

uniform sampler2D  roughnessMap;
uniform sampler2D  aoMap;
uniform int useAoMap;
uniform float aoValue;
struct PointLight
{
    vec3 position;
    vec3 color;
};

uniform PointLight pointLights[8];
uniform int pointLightCount;

struct DirectionLight
{
    vec3 direction;
    vec3 color;
};
uniform DirectionLight directionLights[8];
uniform int directionLightCount;


uniform vec3 eyePos;

const float PI = 3.14159265359;

vec3 getNormalFromMap()
{
    vec3 tangentNormal = texture(normalMap, oTexCoord).xyz * 2.0 - 1.0;

    vec3 Q1  = dFdx(oPosition);
    vec3 Q2  = dFdy(oPosition);
    vec2 st1 = dFdx(oTexCoord);
    vec2 st2 = dFdy(oTexCoord);

    vec3 N   = normalize(oNormal);
    vec3 T  = normalize(Q1*st2.t - Q2*st1.t);
    vec3 B  = -normalize(cross(N, T));
    mat3 TBN = mat3(T, B, N);

    return normalize(TBN * tangentNormal);
}

float DistributionGGX(vec3 N, vec3 H, float roughness)
{
    float a = roughness*roughness;
    float a2 = a*a;
    float NdotH = max(dot(N, H), 0.0);
    float NdotH2 = NdotH*NdotH;

    float nom   = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return nom / max(denom, 0.001); // prevent divide by zero for roughness=0.0 and NdotH=1.0
}

float GeometrySchlickGGX(float NdotV, float roughness)
{
    float r = (roughness + 1.0);
    float k = (r*r) / 8.0;

    float nom   = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return nom / denom;
}

float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness)
{
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2 = GeometrySchlickGGX(NdotV, roughness);
    float ggx1 = GeometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}

vec3 fresnelSchlick(float cosTheta, vec3 F0)
{
    return F0 + (1.0 - F0) * pow(1.0 - cosTheta, 5.0);
}

void main()
{
//    vec3 albedo = pow(texture(albedoMap, oTexCoord).rgb,vec3(2.2));
    vec3 albedo = texture(albedoMap, oTexCoord).rgb;
    float metallic;
    if(useMetallicMap > 0){
        metallic = texture(metallicMap, oTexCoord).r;
    }else{
        metallic = metallicValue;
    }

    float roughness = texture(roughnessMap, oTexCoord).r;
    float ao;
    if(useAoMap > 0)
      ao = texture(aoMap, oTexCoord).r;
    else
        ao = aoValue;
    vec3 N = getNormalFromMap();
    vec3 V = normalize(eyePos - oPosition);

    vec3 F0 = vec3(0.04);
    F0 = mix(F0, albedo, metallic);

    vec3 Lo = vec3(0.0);
    for(int i = 0; i < pointLightCount; ++i)
    {
        PointLight pointLight = pointLights[i];

        vec3 L = normalize(pointLight.position - oPosition);
        vec3 H = normalize(V + L);
        float distance = length(pointLight.position - oPosition);
        float attenuation = 1.0 / (distance * distance);
        vec3 radiance = pointLight.color * attenuation;

        float NDF = DistributionGGX(N, H, roughness);
        float G   = GeometrySmith(N, V, L, roughness);
        vec3 F    = fresnelSchlick(clamp(dot(H, V), 0.0, 1.0), F0);

        vec3 nominator    = NDF * G * F;
        float denominator = 4 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0);
        vec3 specular = nominator / max(denominator, 0.001); // prevent divide by zero for NdotV=0.0 or NdotL=0.0

        vec3 kS = F;

        vec3 kD = vec3(1.0) - kS;

        kD *= 1.0 - metallic;

        float NdotL = max(dot(N, L), 0.0);

        Lo += (kD * albedo / PI + specular) * radiance * NdotL;
    }

    for(int i = 0; i < directionLightCount; ++i)
    {
        DirectionLight directionLight = directionLights[i];

        vec3 L = normalize(directionLight.direction);
        vec3 H = normalize(V + L);

        vec3 radiance = directionLight.color;

        float NDF = DistributionGGX(N, H, roughness);
        float G   = GeometrySmith(N, V, L, roughness);
        vec3 F    = fresnelSchlick(clamp(dot(H, V), 0.0, 1.0), F0);

        vec3 nominator    = NDF * G * F;
        float denominator = 4 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0);
        vec3 specular = nominator / max(denominator, 0.001); // prevent divide by zero for NdotV=0.0 or NdotL=0.0

        vec3 kS = F;

        vec3 kD = vec3(1.0) - kS;

        kD *= 1.0 - metallic;

        float NdotL = max(dot(N, L), 0.0);

        Lo += (kD * albedo / PI + specular) * radiance * NdotL;
    }

    vec3 ambient = vec3(0.03) * albedo * ao;

    vec3 color = ambient + Lo;

    color = color / (color + vec3(1.0));
    color = pow(color, vec3(1.0/2.2));

    FragColor = vec4(color, 1.0);
}