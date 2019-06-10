package com.se7en.opengl.material

import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*

class Lambert : Material() {
    override fun vertexShader(): String = "shaders/common.vsh"

    override fun fragmentShader(): String = "shaders/lambert.fsh"

    var objColor = floatArrayOf(1f, 1f, 1f)
    var ambientColor = floatArrayOf(1f, 1f, 1f)
    var ambientStrength = 0.1f
    init {
        enableLighting = true
    }
    override fun render(
        viewMatrix: Matrix4f,
        projectionMatrix: Matrix4f,
        modelMatrix: Matrix4f
    ) {
        if(mesh == null) return
        shader.setUniformMatrix4fv("projectionMatrix", projectionMatrix.get(FloatArray(16)))
        shader.setUniformMatrix4fv("viewMatrix", viewMatrix.get(FloatArray(16)))
        shader.setUniformMatrix4fv("modelMatrix",modelMatrix.get(FloatArray(16)))

        shader.useProgram()
        shader.setUniform3fv("objColor", objColor)
        shader.setUniform3fv("ambientColor", ambientColor)
        shader.setUniform1fv("ambientStrength", ambientStrength)
        shader.setVertexAttribArray("aPosition", 3, mesh!!.vertices!!)
        shader.setVertexAttribArray("aNormal", 3, mesh!!.normals!!)
        glDrawElements(GL_TRIANGLES, mesh!!.indices!!)
    }
}