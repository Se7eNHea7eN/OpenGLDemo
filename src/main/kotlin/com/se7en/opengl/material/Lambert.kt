package com.se7en.opengl.material

import org.lwjgl.opengl.GL11.*

class Lambert : Material() {
    override fun vertexShader(): String = "shaders/common.vsh"

    override fun fragmentShader(): String = "shaders/lambert.fsh"

    private var objColor = floatArrayOf(1f, 1f, 1f)
    private var ambientColor = floatArrayOf(1f, 1f, 1f)
    private var ambientStrength = 0.1f

    override fun render() {
        if(mesh == null) return
        shader.useProgram()
        shader.setUniform3fv("objColor", objColor)
        shader.setUniform3fv("ambientColor", ambientColor)
        shader.setUniform1fv("ambientStrength", ambientStrength)
        shader.setVertexAttribArray("aPosition", 3, mesh!!.vertices!!)
        shader.setVertexAttribArray("aNormal", 3, mesh!!.normals!!)
        glDrawElements(GL_TRIANGLES, mesh!!.indices!!)
    }
}