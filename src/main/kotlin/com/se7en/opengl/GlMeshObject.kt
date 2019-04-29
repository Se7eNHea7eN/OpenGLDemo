package com.se7en.opengl

import com.se7en.opengl.material.Material

abstract class GlMeshObject : GlRenderObject() {
    var mesh: Mesh

    init {
        mesh = createMesh()
        material.mesh = mesh
    }

    protected abstract fun createMesh(): Mesh

}