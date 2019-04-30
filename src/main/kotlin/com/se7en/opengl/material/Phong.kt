package com.se7en.opengl.material

import com.se7en.opengl.toFloatArray
import org.joml.Vector3f
import org.lwjgl.opengl.GL11.*

class Phong: Material() {
    override fun vertexShader(): String = "shaders/common.vsh"
    override fun fragmentShader(): String = "shaders/phong.fsh"
    var objColor = Vector3f(1f, 1f, 1f)
    var ambientColor = Vector3f(1f, 1f, 1f)
    var ambientStrength = 0.1f
    var specularStrength = 1.5f
    var shininess = 128f
    override fun render() {
        if(mesh == null) return
        shader.useProgram()
        shader.setUniform3fv("objColor", objColor.toFloatArray())
        shader.setUniform3fv("ambientColor", ambientColor.toFloatArray())
        shader.setUniform1fv("ambientStrength", ambientStrength)
        shader.setUniform1fv("specularStrength", specularStrength)
        shader.setUniform1fv("shininess", shininess)
        shader.setUniform3fv("eyePos", eyePos.toFloatArray())

        shader.setVertexAttribArray("aPosition", 3, mesh!!.vertices!!)
        shader.setVertexAttribArray("aNormal", 3, mesh!!.normals!!)
        glDrawElements(GL_TRIANGLES,  mesh!!.indices!!)
    }
}