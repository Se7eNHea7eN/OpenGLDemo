package com.se7en.opengl.material

import com.se7en.opengl.toFloatArray
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL41

class PBRMaterial : Material() {
    override fun vertexShader(): String = "shaders/common.vsh"
    override fun fragmentShader(): String = "shaders/pbr.fsh"


    var albedo = Vector3f(0.5f,0.0f,0.0f)
    var ao = 1.0f
    var metallic = 0.70f
    var roughness = 0.30f
    init {
        enableLighting = true

    }
    override fun render(viewMatrix: Matrix4f, projectionMatrix: Matrix4f, modelMatrix: Matrix4f) {

        if (mesh == null) return
        shader.useProgram()
        shader.setUniformMatrix4fv("projectionMatrix", projectionMatrix.get(FloatArray(16)))
        shader.setUniformMatrix4fv("viewMatrix", viewMatrix.get(FloatArray(16)))
        shader.setUniformMatrix4fv("modelMatrix", modelMatrix.get(FloatArray(16)))

        shader.setUniform3fv("albedo", albedo.toFloatArray())
        shader.setUniform1fv("ao", ao)
        shader.setUniform1fv("metallic", metallic)
        shader.setUniform1fv("roughness", roughness)

        GL41.glBindVertexArray(vao)

        GL41.glDrawElements(GL41.GL_TRIANGLES, mesh!!.numVertices, GL41.GL_UNSIGNED_INT, 0L)
        GL41.glBindVertexArray(0)

        GL41.glUseProgram(0)
    }
}