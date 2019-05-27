package com.se7en.opengl.material

import com.se7en.opengl.GlUtil
import org.joml.Matrix4f
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL41.*
import java.nio.ByteBuffer

abstract class SkyBoxMat : Material() {
    override fun vertexShader(): String = "shaders/skybox.vsh"
    override fun fragmentShader(): String = "shaders/skybox.fsh"

    internal var skyBoxTexture: Int = 0

    abstract fun skyBoxTextures() : Array<String>

    init {
        skyBoxTexture = glGenTextures()
        glBindTexture(GL_TEXTURE_CUBE_MAP, skyBoxTexture)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)
        val textures = skyBoxTextures().map {
            GlUtil.createTextureFromResource(it,GL_TEXTURE_CUBE_MAP)
        }
        for (i in 0..5){
            glTexImage2D(
                GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
                0,
                GL_DEPTH_COMPONENT,
                textures[i].width,
                textures[i].height,
                0,
                GL_DEPTH_COMPONENT,
                GL_UNSIGNED_BYTE,
                null as ByteBuffer?
            )
        }
        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS)

    }
    override fun render(
        viewMatrix: Matrix4f,
        projectionMatrix: Matrix4f,
        modelMatrix: Matrix4f
    ) {
        if (mesh == null) return
        shader.useProgram()
        shader.setUniformMatrix4fv("projectionMatrix", projectionMatrix.get(FloatArray(16)))
        shader.setUniformMatrix4fv("viewMatrix", viewMatrix.get(FloatArray(16)))

        shader.setVertexAttribArray("position", 3, mesh!!.vertices!!)

        glEnable(GL13.GL_TEXTURE_CUBE_MAP)
        glActiveTexture(GL_TEXTURE_CUBE_MAP)
        glBindTexture(GL_TEXTURE_CUBE_MAP, skyBoxTexture)
        shader.setUniformInt("skybox", 0)
        glDrawElements(GL_TRIANGLES, mesh!!.indices!!)
    }
}