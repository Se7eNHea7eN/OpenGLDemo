package com.se7en.opengl.material

import org.lwjgl.opengl.GL11.*

class Phong: Material() {
    override fun vertexShader(): String = "shaders/phong.vsh"
    override fun fragmentShader(): String = "shaders/phong.fsh"
    private var objColor = floatArrayOf(1f, 1f, 1f)
    private var ambientColor = floatArrayOf(1f, 1f, 1f)
    private var ambientStrength = 0.1f
    private var specularStrength = 1.5f
    private var shininess = 128f
    override fun render() {
        super.render()
        if(mesh == null) return
        shader.useProgram()
        shader.setUniform3fv("objColor", objColor)
        shader.setUniform3fv("ambientColor", ambientColor)
        shader.setUniform1fv("ambientStrength", ambientStrength)
        shader.setUniform1fv("specularStrength", specularStrength)
        shader.setUniform1fv("shininess", shininess)
        glDrawElements(GL_TRIANGLES,  mesh!!.indices!!)
    }
}