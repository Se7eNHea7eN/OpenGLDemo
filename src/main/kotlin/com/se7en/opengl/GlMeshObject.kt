package com.se7en.opengl

abstract class GlMeshObject : GlRenderObject() {
    var mesh: Mesh
    init {
        mesh = createMesh()
        material.mesh = mesh
    }

    protected abstract fun createMesh(): Mesh
}