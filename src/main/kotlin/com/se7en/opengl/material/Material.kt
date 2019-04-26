package com.se7en.opengl.material

import asiainnovations.com.opengles_demo.GlShader
import com.se7en.opengl.Mesh
import com.se7en.opengl.utils.ResourceUtils
import java.nio.FloatBuffer
import java.nio.IntBuffer

abstract class Material {
    val shader: GlShader
    abstract fun vertexShader(): String
    abstract fun fragmentShader(): String
    var mesh: Mesh? = null
        set(value) {
            field = value
            if (value != null) {
                shader.setVertexAttribArray("aPosition", 3, value.vertices!!)
                shader.setVertexAttribArray("aNormal", 3, value.normals!!)

            }
        }

    fun setProjectionMatrix(projectionMatrix: FloatArray) {
        shader.setUniformMatrix4fv("projectionMatrix", projectionMatrix)
    }

    fun setViewMatrix(viewMatrix: FloatArray) {
        shader.setUniformMatrix4fv("viewMatrix", viewMatrix)
    }

    init {
        shader = GlShader(
            ResourceUtils.ioResourceToByteBuffer(vertexShader(), 8192),
            ResourceUtils.ioResourceToByteBuffer(fragmentShader(), 8192)
        )
        shader.useProgram()
    }

    abstract fun render()
}