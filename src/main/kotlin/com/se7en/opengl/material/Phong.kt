package com.se7en.opengl.material

import org.lwjgl.opengl.GL11.*

class Phong: Material() {
    override fun vertexShader(): String = "shaders/phong/phong.vsh"
    override fun fragmentShader(): String = "shaders/phong/phong.fsh"
    private var objColor = floatArrayOf(1f, 0f, 0f)
    private var lightColor = floatArrayOf(1f, 1f, 1f)
    private var ambientStrength = 0.1f
    private var specularStrength = 1.5f
    private var shininess = 128f
    override fun render() {
        if(mesh == null) return
        shader.useProgram()
        shader.setUniform3fv("objColor", objColor)
        shader.setUniform3fv("lightColor", lightColor)
        shader.setUniform1fv("ambientStrength", ambientStrength)
//        shader.setUniform3fv("lightPos", light.transform.worldPosition().asFloatArray())
//        shader.setUniform3fv("eyePos", camera.transform.worldPosition().asFloatArray())
        shader.setUniform1fv("specularStrength", specularStrength)
        shader.setUniform1fv("shininess", shininess)
        glDrawElements(GL_TRIANGLES,  mesh!!.indices!!)
    }
}