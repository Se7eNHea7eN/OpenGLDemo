package com.se7en.opengl

import asiainnovations.com.opengles_demo.GlShader
import com.asiainnovations.onlyu.video.gl.TextureRotationUtil
import com.se7en.opengl.utils.ResourceUtils
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL41.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class GlCamera : GlObject() {
    private var width = 1280
    private var height = 720
    var fov = 45f
        set(value) {
            field = value
            recalculateProjectionMatrix()
        }

    var zNear = 0.01f
        set(value) {
            field = value
            recalculateProjectionMatrix()
        }

    var zFar = 100.0f
        set(value) {
            field = value
            recalculateProjectionMatrix()
        }

    var projectionMatrix = Matrix4f()

    var shadowMappingShader = GlShader(
        ResourceUtils.ioResourceToByteBuffer("shaders/shadowMapping.vsh", 8192),
        ResourceUtils.ioResourceToByteBuffer("shaders/shadowMapping.fsh", 8192)
    )

    var rectShader = GlShader(
        ResourceUtils.ioResourceToByteBuffer("shaders/rect.vsh", 8192),
        ResourceUtils.ioResourceToByteBuffer("shaders/rect.fsh", 8192)
    )

    internal val shadowMapSize = 1024

    internal var depthTexture: Int = 0
    internal var fbo: Int = 0

    init {
        /**
         * Create the texture storing the depth values of the light-render.
         */
        depthTexture = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, depthTexture)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexImage2D(
            GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, shadowMapSize, shadowMapSize, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE,
            null as ByteBuffer?
        )
        glBindTexture(GL_TEXTURE_2D, 0)
        /**
         * Create the FBO to render the depth values of the light-render into the
         * depth texture.
         */
        fbo = glGenFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER, fbo)
        glBindTexture(GL_TEXTURE_2D, depthTexture)
        glDrawBuffer(GL_NONE)
        glReadBuffer(GL_NONE)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0)
        val fboStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER)
        if (fboStatus != GL_FRAMEBUFFER_COMPLETE) {
            throw AssertionError("Could not create FBO: $fboStatus")
        }
        glBindTexture(GL_TEXTURE_2D, 0)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun onWindowSizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height
        glClearDepthf(1.0f)
//        glEnable(GL_CULL_FACE)
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)
        recalculateProjectionMatrix()
    }

    private fun recalculateProjectionMatrix() {
        projectionMatrix = Matrix4f()
            .perspective(Math.toRadians(fov.toDouble()).toFloat(), width.toFloat() / height, zNear, zFar)
    }


    fun render(objects: List<GlObject>) {
        renderShadowMap(objects)
        rectShader.useProgram()
        val vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(TextureRotationUtil.CUBE)
                position(0)
            }

        val textureMappingBuffer: FloatBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply {
                put(TextureRotationUtil.getRotation(0, false, false))
                position(0)
            }

        rectShader.setVertexAttribArray("position", 2, vertexBuffer)
        rectShader.setVertexAttribArray("inputTextureCoordinate", 2, textureMappingBuffer)
        //激活纹理单元0
        glActiveTexture(GL_TEXTURE0)
        //绑定纹理
        glBindTexture(GL_TEXTURE_2D, depthTexture)
        glUniform1i(rectShader.getUniformLocation("inputImageTexture"), 0)

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
//        renderScene(objects)
    }

    private fun renderScene(objects: List<GlObject>) {
        glClearColor(0f, 0f, 0f, 0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glViewport(0, 0, width, height)

        val viewMatrix = Matrix4f().setLookAt(
            transform.position, transform.forward(), transform.up()
        )

        objects.forEach {
            if (it is GlRenderObject) {
                it.material.setPointLights(GlScene.currentScene!!.pointLights)
                it.material.setDirectionLights(GlScene.currentScene!!.directionLights)
                it.material.eyePos = transform.position
                it.material.setProjectionMatrix(projectionMatrix.get(FloatArray(16)))
                it.material.setViewMatrix(viewMatrix.get(FloatArray(16)))
                it.render()
            }
        }
    }

    private fun renderShadowMap(objects: List<GlObject>) {
        shadowMappingShader.useProgram()
        glBindFramebuffer(GL_FRAMEBUFFER, fbo)
        glClearColor(1f, 0f, 0f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glViewport(0, 0, shadowMapSize, shadowMapSize)
//
//        objects.forEach {
//            if (it is GlPointLight) {
//                val lightProjectionMatrix =
//                    Matrix4f().setPerspective(Math.toRadians(fov.toDouble()).toFloat(), 1.0f, 0.1f, 60.0f)
//                val lightViewMatrix =
//                    Matrix4f().lookAt(it.transform.position, it.transform.forward(), it.transform.up())
//
//                shadowMappingShader.setUniformMatrix4fv("projectionMatrix", lightProjectionMatrix.get(FloatArray(16)))
//                shadowMappingShader.setUniformMatrix4fv("viewMatrix", lightViewMatrix.get(FloatArray(16)))
//                /* Only clear depth buffer, since we don't have a color draw buffer */
//                objects.forEach { renderObject ->
//                    if (renderObject is GlRenderObject) {
//                        if (renderObject.material.mesh != null) {
//                            shadowMappingShader.setVertexAttribArray(
//                                "aPosition",
//                                3,
//                                renderObject.material.mesh!!.vertices!!
//                            )
//                            glDrawElements(GL_TRIANGLES, renderObject.material.mesh!!.indices!!)
//                        }
//                        renderObject.render()
//                    }
//                }
//            }
//        }

        glBindVertexArray(0)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        glUseProgram(0)
    }

}