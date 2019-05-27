package com.se7en.opengl

import com.se7en.opengl.material.Material
import org.joml.Matrix4f

abstract class GlRenderObject : GlObject() {
    var material: Material
    var castShadow = true
    protected abstract fun createMaterial(): Material

    init {
        material = createMaterial()
    }

    fun render(viewMatrix: Matrix4f, projectionMatrix: Matrix4f) {
        material.shader.useProgram()
        material.render(viewMatrix, projectionMatrix,transform.matrix())
    }

    override fun onDestroy() {
        super.onDestroy()
        material.onDestroy()
    }
}