package com.se7en.opengl.lighting

import asiainnovations.com.opengles_demo.GlShader
import com.se7en.opengl.GlObject
import com.se7en.opengl.GlRenderObject
import com.se7en.opengl.utils.ResourceUtils
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL41.*
import java.nio.ByteBuffer

class GlDirectionLight : GlAbstractLight() {

    internal val shadowMapSize = 2048

    internal var fbo: Int = 0

    internal var depthTexture: Int = 0

    var shadowMappingShader = GlShader(
        ResourceUtils.ioResourceToByteBuffer("shaders/directionLightDepth.vsh", 8192),
        ResourceUtils.ioResourceToByteBuffer("shaders/directionLightDepth.fsh", 8192)
    )

    fun lightVPMatrix() : Matrix4f{
        val lightViewMatrix =
            Matrix4f().lookAt(transform.localPosition, Vector3f(), transform.up())
        return lightProjectionMatrix().mul(lightViewMatrix)
    }

    fun lightProjectionMatrix(): Matrix4f
            = Matrix4f().setOrtho(-10f,10f,-10f,10f, 0.01f, 1000f)

    init {
        /**
         * Create the texture storing the depth values of the light-render.
         */
        depthTexture = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, depthTexture)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER)
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f))

        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_DEPTH_COMPONENT,
            shadowMapSize,
            shadowMapSize,
            0,
            GL_DEPTH_COMPONENT,
            GL_UNSIGNED_BYTE,
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
    override fun renderShadowMap(objects: List<GlObject>) {
        shadowMappingShader.useProgram()
        glBindFramebuffer(GL_FRAMEBUFFER, fbo)
        glClear(GL_DEPTH_BUFFER_BIT)
        glViewport(0, 0, shadowMapSize, shadowMapSize)
        glCullFace(GL_FRONT)
        objects.forEach {
            shadowMappingShader.setUniformMatrix4fv("vpMatrix", lightVPMatrix().get(FloatArray(16)))
            /* Only clear depth buffer, since we don't have a color draw buffer */
            objects.forEach { renderObject ->
                if (renderObject is GlRenderObject) {
                    if (renderObject.castShadow && renderObject.material.mesh != null) {
                        shadowMappingShader.setUniformMatrix4fv(
                            "modelMatrix",
                            renderObject.transform.matrix().get(FloatArray(16))
                        )

                        shadowMappingShader.setVertexAttribArray(
                            "aPosition",
                            3,
                            renderObject.material.mesh!!.vertices!!
                        )
                        glDrawElements(GL_TRIANGLES, renderObject.material.mesh!!.indices!!)
                    }
                }
            }
        }
        glCullFace(GL_BACK)
        glBindVertexArray(0)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        glUseProgram(0)
    }

//    var depthVisualShader =
//        GlShader(
//            ResourceUtils.ioResourceToByteBuffer("shaders/rect.vsh", 8192),
//            ResourceUtils.ioResourceToByteBuffer("shaders/depthTexture.fsh", 8192)
//        )
//
//    fun renderDepthTexture(width : Int,height:Int) {
//        depthVisualShader.useProgram()
//        val vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.size * 4)
//            .order(ByteOrder.nativeOrder())
//            .asFloatBuffer()
//            .apply {
//                put(floatArrayOf(-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f))
//                position(0)
//            }
//
//        val textureMappingBuffer: FloatBuffer =
//            ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.size * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer().apply {
//                    put(TextureRotationUtil.TEXTURE_NO_ROTATION)
//                    position(0)
//                }
//
//        depthVisualShader.setVertexAttribArray("localPosition", 2, vertexBuffer)
//        depthVisualShader.setVertexAttribArray("inputTextureCoordinate", 2, textureMappingBuffer)
//
//        glViewport(0, 0, width, height)
//
//        glBindTexture(GL_TEXTURE_2D, depthTexture)
//        //        glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, shadowMapSize, shadowMapSize);
//        glClearColor(0f, 0f, 0f, 0f)
//        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
//        glEnable(GL_TEXTURE_2D)
//        glActiveTexture(GL_TEXTURE0)
//        glBindTexture(GL_TEXTURE_2D, depthTexture)
//        glUniform1i(depthVisualShader.getUniformLocation("inputImageTexture"), 0)
//
//        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
//    }
}