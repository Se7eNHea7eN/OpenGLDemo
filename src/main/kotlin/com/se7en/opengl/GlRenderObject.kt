package com.se7en.opengl

import com.se7en.opengl.material.Material

abstract class GlRenderObject : GlObject() {
    var material: Material
    protected abstract fun createMaterial(): Material

    init {
        material = createMaterial()
    }
    fun render() {
        material.shader.useProgram()
        material.shader.setUniformMatrix4fv("modelMatrix",transform.matrix().get(FloatArray(16)))
        material.render()
    }

    override fun onDestroy() {
        super.onDestroy()
        material.onDestroy()
    }
}