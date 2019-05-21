package com.se7en.opengl

import org.joml.Matrix4f
import org.joml.Vector3f

open class GlPointLight : GlAbstractLight() {
    override fun lightProjectionMatrix(): Matrix4f = Matrix4f().setPerspective(Math.toRadians(90.0).toFloat(), 1.0f, 0.01f, 1000f)
}