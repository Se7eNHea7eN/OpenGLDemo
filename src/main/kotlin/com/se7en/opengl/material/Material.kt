package com.se7en.opengl.material

import asiainnovations.com.opengles_demo.GlShader
import com.se7en.opengl.*
import com.se7en.opengl.lighting.GlAbstractLight
import com.se7en.opengl.lighting.GlDirectionLight
import com.se7en.opengl.lighting.GlPointLight
import com.se7en.opengl.utils.ResourceUtils
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20.*

abstract class Material {
    val shader: GlShader
    var enableLighting = false
    abstract fun vertexShader(): String
    abstract fun fragmentShader(): String
    open fun geometryShader(): String? = null
    var mesh: Mesh? = null

    var eyePos = Vector3f()


    fun setViewMatrix(viewMatrix: FloatArray) {
        shader.useProgram()
        shader.setUniformMatrix4fv("viewMatrix", viewMatrix)
    }

    fun setLights(lights: List<GlAbstractLight>) {
        shader.useProgram()
        shader.setUniformInt("pointLightCount", lights.count { it is GlPointLight })
        shader.setUniformInt("directionLightCount", lights.count { it is GlDirectionLight })
        glEnable(GL_TEXTURE_2D)
        lights.forEachIndexed { index, light ->
            if (light is GlPointLight) {
                shader.setUniform3fv("pointLights[$index].position", light.transform.position.toFloatArray())
                shader.setUniform3fv("pointLights[$index].color", light.lightColor.toFloatArray())
                shader.setUniform1fv("pointLights[$index].intensive", light.intensive)
                shader.setUniform1fv("pointLights[$index].farPlane", light.far)

                glActiveTexture(GL_TEXTURE0 + index)
                glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, light.depthTexture)
                glUniform1i(shader.getUniformLocation("pointLightCubeShadows[$index]"), index)
            } else if (light is GlDirectionLight) {
                shader.setUniform3fv("directionLights[$index].direction", light.transform.forward().toFloatArray())
                shader.setUniform3fv("directionLights[$index].color", light.lightColor.toFloatArray())
                shader.setUniform1fv("directionLights[$index].intensive", light.intensive)
                shader.setUniformMatrix4fv("directionLights[$index].matrix", light.lightVPMatrix().get(FloatArray(16)))

                glActiveTexture(GL_TEXTURE0 + index)
                glBindTexture(GL_TEXTURE_2D, light.depthTexture)
                glUniform1i(shader.getUniformLocation("directionLightShadows[$index]"), index)
            }
        }
    }


    init {
        shader = GlShader(
            ResourceUtils.ioResourceToByteBuffer(vertexShader(), 8192),
            ResourceUtils.ioResourceToByteBuffer(fragmentShader(), 8192)
        )
    }

    abstract fun render(
        viewMatrix: Matrix4f,
        projectionMatrix: Matrix4f,
        modelMatrix: Matrix4f
    )

    fun onDestroy() {
        shader.release()
    }
}