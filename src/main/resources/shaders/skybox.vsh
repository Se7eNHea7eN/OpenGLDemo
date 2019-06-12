/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
#version 330
layout(location = 0) in vec4 iPosition;

out vec3 dir;

uniform mat4 invViewProjection;


void main(void) {
  // Compute the view direction in world-space by unprojecting the clip space
  // (in this case NDC space) fullscreen quad vertex coordinates by transforming
  // them with the inverse of the view-projection matrix.
  vec4 tmp = invViewProjection* vec4(iPosition.xy, 0.0, 1.0);

  // Do perspective divide to get from homogeneous space to real 3D coordinates.
  dir = tmp.xyz / tmp.w;

  // Simply pass-through the vertex which was already in clip space.
  gl_Position = iPosition;
}