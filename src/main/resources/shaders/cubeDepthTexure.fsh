uniform samplerCube tex;

varying vec3 dir;

float LinearizeDepth(in float depth)
{
    float zNear = 0.01;    // TODO: Replace by the zNear of your perspective projection
    float zFar  = 1000.0; // TODO: Replace by the zFar  of your perspective projection
    return (2.0 * zNear) / (zFar + zNear - depth * (zFar - zNear));
}

void main(void) {
    float d = textureCube(tex, dir).x;
    gl_FragColor = vec4(d,d,d,1.0);
    gl_FragDepth = 0.9999999;
}
