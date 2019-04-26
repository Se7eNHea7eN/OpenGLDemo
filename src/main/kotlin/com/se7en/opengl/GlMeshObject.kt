package com.se7en.opengl

import com.se7en.opengl.material.Material

abstract class GlMeshObject : GlObject() {
    var material: Material

    var mesh: Mesh

    init {
        material = createMaterial()
        mesh = createMesh()
        material.mesh = mesh
    }

    protected abstract fun createMaterial(): Material
    protected abstract fun createMesh(): Mesh

    fun render() {
        material.render()
    }
}