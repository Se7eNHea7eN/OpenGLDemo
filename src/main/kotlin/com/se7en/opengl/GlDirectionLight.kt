package com.se7en.opengl

import org.joml.Matrix4f

class GlDirectionLight : GlAbstractLight() {
    override fun lightProjectionMatrix(): Matrix4f
            = Matrix4f().setOrtho(-10f,10f,-10f,10f, 0.01f, 1000f)
}