package com.se7en.opengl.material

import com.se7en.opengl.toFloatArray
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL41

class Illumination : Material() {
    override fun vertexShader(): String = "shaders/common.vsh"
    override fun fragmentShader(): String = "shaders/illumination.fsh"
    var objColor = Vector3f(1f, 1f, 1f)

    override fun render(
        viewMatrix: Matrix4f,
        projectionMatrix: Matrix4f,
        modelMatrix: Matrix4f
    ) {
        if(mesh == null) return
        shader.useProgram()
        shader.setUniformMatrix4fv("projectionMatrix", projectionMatrix.get(FloatArray(16)))
        shader.setUniformMatrix4fv("viewMatrix", viewMatrix.get(FloatArray(16)))
        shader.setUniformMatrix4fv("modelMatrix",modelMatrix.get(FloatArray(16)))
        shader.setUniform3fv("objColor", objColor.toFloatArray())

        glBindVertexArray(vao)

        glDrawElements(GL_TRIANGLES, mesh!!.numVertices, GL_UNSIGNED_INT, 0L)
        glBindVertexArray(0)

    }
}