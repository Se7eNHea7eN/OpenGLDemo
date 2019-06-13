#version 330 core

in vec2 textureCoordinate;

uniform sampler2D inputImageTexture;

out vec4 FragColor;
// 'colorImage' is a sampler2D with the depth image
// read from the current depth buffer bound to it.
//
float LinearizeDepth(in vec2 uv)
{
    float zNear = 0.01;    // TODO: Replace by the zNear of your perspective projection
    float zFar  = 1000.0; // TODO: Replace by the zFar  of your perspective projection
    //float depth = texture2D(inputImageTexture, uv).x;

    float closestDepth = texture(inputImageTexture, uv).r;

    return closestDepth;
}

void main()
{
    float d = LinearizeDepth(textureCoordinate);
    FragColor =  vec4(d,d,d,1.0);
}