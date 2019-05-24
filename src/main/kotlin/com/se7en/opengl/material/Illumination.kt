package com.se7en.opengl.material

import com.se7en.opengl.toFloatArray
import org.joml.Vector3f
import org.lwjgl.opengl.GL11

class Illumination : Material() {
    override fun vertexShader(): String = "shaders/common.vsh"
    override fun fragmentShader(): String = "shaders/illumination.fsh"
    var objColor = Vector3f(1f, 1f, 1f)

    override fun render() {
        if(mesh == null) return
        shader.useProgram()
        shader.setUniform3fv("objColor", objColor.toFloatArray())
        shader.setVertexAttribArray("aPosition", 3, mesh!!.vertices!!)
        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh!!.indices!!)
    }
}