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
        set(value) {
            field = value
            if (value != null) {
                shader.setVertexAttribArray("aPosition", 3, value.vertices!!)
                shader.setVertexAttribArray("aNormal", 3, value.normals!!)
            }
        }

    fun setEyePosition(eyePos:Vector3f){
        shader.setUniform3fv("eyePos", eyePos.toFloatArray())
    }
    fun setLightPosition(lightPos:Vector3f){
        shader.setUniform3fv("lightPos", lightPos.toFloatArray())
    }
    fun setProjectionMatrix(projectionMatrix: FloatArray) {
        shader.setUniformMatrix4fv("projectionMatrix", projectionMatrix)
    }

    fun setViewMatrix(viewMatrix: FloatArray) {
        shader.setUniformMatrix4fv("viewMatrix", viewMatrix)
    }


    fun setPointLights(pointLights:List<GlPointLight>){
        pointLights.forEachIndexed { index, glPointLight ->
            shader.setUniformInt("pointlights[$index].enable", 1)
            shader.setUniform3fv("pointlights[$index].position", glPointLight.transform.position.toFloatArray())
            shader.setUniform3fv("pointlights[$index].color", glPointLight.lightColor)
            shader.setUniform1fv("pointlights[$index].intensive", glPointLight.intensive)
        }
    }

    init {
        shader = GlShader(
            ResourceUtils.ioResourceToByteBuffer(vertexShader(), 8192),
            ResourceUtils.ioResourceToByteBuffer(fragmentShader(), 8192)
        )
        shader.useProgram()
    }

    open fun render(){
        setPointLights(GlScene.currentScene!!.pointLights)
    }

    fun onDestroy(){
        shader.release()
    }
}