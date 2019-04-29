package com.se7en.opengl.material

import asiainnovations.com.opengles_demo.GlShader
import com.se7en.opengl.GlPointLight
import com.se7en.opengl.GlScene
import com.se7en.opengl.Mesh
import com.se7en.opengl.toFloatArray
import com.se7en.opengl.utils.ResourceUtils
import org.joml.Vector3f
import java.nio.FloatBuffer
import java.nio.IntBuffer

abstract class Material {
    val shader: GlShader
    abstract fun vertexShader(): String
    abstract fun fragmentShader(): String
    var mesh: Mesh? = null

    var eyePos = Vector3f()

    fun setProjectionMatrix(projectionMatrix: FloatArray) {
        shader.useProgram()
        shader.setUniformMatrix4fv("projectionMatrix", projectionMatrix)
    }

    fun setViewMatrix(viewMatrix: FloatArray) {
        shader.useProgram()
        shader.setUniformMatrix4fv("viewMatrix", viewMatrix)
    }

    fun setPointLights(pointLights:List<GlPointLight>){
        shader.useProgram()
        pointLights.forEachIndexed { index, glPointLight ->
            shader.setUniformInt("pointlights[$index].enable", 1)
            shader.setUniform3fv("pointlights[$index].position", glPointLight.transform.position.toFloatArray())
            shader.setUniform3fv("pointlights[$index].color", glPointLight.lightColor.toFloatArray())
            shader.setUniform1fv("pointlights[$index].intensive", glPointLight.intensive)
        }
    }

    init {
        shader = GlShader(
            ResourceUtils.ioResourceToByteBuffer(vertexShader(), 8192),
            ResourceUtils.ioResourceToByteBuffer(fragmentShader(), 8192)
        )
    }

    abstract fun render()

    fun onDestroy(){
        shader.release()
    }
}