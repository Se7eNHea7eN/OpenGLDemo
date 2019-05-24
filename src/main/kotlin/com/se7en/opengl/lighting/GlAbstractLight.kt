package com.se7en.opengl.lighting

import asiainnovations.com.opengles_demo.GlShader
import com.se7en.opengl.GlObject
import com.se7en.opengl.utils.ResourceUtils
import org.joml.Matrix4f
import org.joml.Vector3f

abstract class GlAbstractLight : GlObject() {
    var lightColor = Vector3f(1f, 1f, 1f)
    var intensive = 1f

    abstract fun renderShadowMap(objects: List<GlObject>)
}