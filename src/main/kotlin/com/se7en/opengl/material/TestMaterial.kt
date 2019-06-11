package com.se7en.opengl.material

import com.se7en.opengl.toFloatArray
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import org.lwjgl.opengl.GL11.glDrawElements
import org.lwjgl.opengl.GL41

class TestMaterial : Material() {
    init {
        enableLighting = false
    }
    var objColor = Vector3f(1f, 1f, 1f)
    var ambientColor = Vector3f(1f, 1f, 1f)
    var ambientStrength = 0.1f

    override fun vertexShader(): String  = "shaders/common.vsh"
    override fun fragmentShader(): String= "shaders/testLighting.fsh"

    override fun render(viewMatrix: Matrix4f, projectionMatrix: Matrix4f, modelMatrix: Matrix4f) {
        if (mesh == null) return
        shader.useProgram()
        shader.setUniformMatrix4fv("projectionMatrix", projectionMatrix.get(FloatArray(16)))
        shader.setUniformMatrix4fv("viewMatrix", viewMatrix.get(FloatArray(16)))
        shader.setUniformMatrix4fv("modelMatrix",modelMatrix.get(FloatArray(16)))
        shader.setUniform3fv("objColor", objColor.toFloatArray())
        shader.setUniform3fv("ambientColor", ambientColor.toFloatArray())
        shader.setUniform1fv("ambientStrength", ambientStrength)

        shader.setVertexAttribArray("iPosition", 3, mesh!!.vertices!!)
//        shader.setVertexAttribArray("iNormal", 3, mesh!!.normals!!)
        glDrawElements(GL_TRIANGLES, mesh!!.indices!!)
    }
}