/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
#version 330
#extension GL_NV_shadow_samplers_cube : enable

uniform samplerCube tex;

in vec3 dir;

out vec4 FragColor;
void main(void) {
    vec4 color = textureCube(tex, dir);
    FragColor = vec4(color.rgb * 1.5, 1.0);
    gl_FragDepth = 0.9999999;
}
