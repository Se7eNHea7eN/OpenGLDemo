package com.se7en.opengl.lighting

import asiainnovations.com.opengles_demo.GlShader
import com.asiainnovations.onlyu.video.gl.TextureRotationUtil
import com.se7en.opengl.*
import com.se7en.opengl.utils.Debug
import com.se7en.opengl.utils.ResourceUtils
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL41.*
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

open class GlDirectionLight : GlAbstractLight() {

    internal val shadowMapSize = 2048

    internal var fbo: Int = 0

    internal var depthTexture: Int = 0

    var shadowMappingShader = GlShader(
        ResourceUtils.ioResourceToByteBuffer("shaders/directionLightDepth.vsh", 8192),
        ResourceUtils.ioResourceToByteBuffer("shaders/directionLightDepth.fsh", 8192)
    )


    fun lightVPMatrix(): Matrix4f {
        val eyePos = transform.position

        val lightViewMatrix =
            Matrix4f().lookAt(eyePos, eyePos + transform.forward(), transform.up())
        return lightProjectionMatrix().mul(lightViewMatrix)
    }

    fun lightProjectionMatrix(): Matrix4f = Matrix4f().setOrtho(-10f, 10f, -10f, 10f, 0.01f, 100f)


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
//        glEnable(GL_CULL_FACE)

//        glCullFace(GL_FRONT)
        /* Only clear depth buffer, since we don't have a color draw buffer */
        objects.forEach { renderObject ->
            if (renderObject is GlRenderObject) {
                if (renderObject.castShadow && renderObject.material.mesh != null) {

                    val vp = lightVPMatrix()
                    shadowMappingShader.setUniformMatrix4fv("vpMatrix", vp.get(FloatArray(16)))

                    shadowMappingShader.setUniformMatrix4fv(
                        "modelMatrix",
                        renderObject.transform.matrix().get(FloatArray(16))
                    )
                    glBindVertexArray(renderObject.material.vao)

                    glDrawElements(GL_TRIANGLES, renderObject.material.mesh!!.numVertices, GL_UNSIGNED_INT, 0L)
                }
            }
        }
//        glCullFace(GL_BACK)
        glBindVertexArray(0)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        glUseProgram(0)
    }

    var depthVisualShader : GlShader ? = null
    var depthVisualVao = 0


    override fun drawDebugShadowMap(viewMatrix: Matrix4f,
                           projectionMatrix: Matrix4f) {
        if(depthVisualShader == null){
            depthVisualShader = GlShader(
                ResourceUtils.ioResourceToByteBuffer("shaders/rect.vsh", 8192),
                ResourceUtils.ioResourceToByteBuffer("shaders/depthTexture.fsh", 8192)
            )

            val vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .apply {
                    put(floatArrayOf(-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f))
                    position(0)
                }

            val textureMappingBuffer: FloatBuffer =
                ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.size * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer().apply {
                        put(TextureRotationUtil.TEXTURE_NO_ROTATION)
                        position(0)
                    }

            depthVisualVao = glGenVertexArrays()
            glBindVertexArray(depthVisualVao)

            val vbo = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, vbo)
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)
            glEnableVertexAttribArray(0)
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, MemoryUtil.NULL)

            val normalArrayBuffer = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, normalArrayBuffer)
            glBufferData(GL_ARRAY_BUFFER, textureMappingBuffer, GL_STATIC_DRAW)
            glEnableVertexAttribArray(1)
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, MemoryUtil.NULL)

            glBindBuffer(GL_ARRAY_BUFFER, 0)
        }
        depthVisualShader!!.useProgram()

        glBindTexture(GL_TEXTURE_2D, depthTexture)
        //        glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, shadowMapSize, shadowMapSize);
        glClearColor(0f, 0f, 0f, 0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, depthTexture)
        glUniform1i(depthVisualShader!!.getUniformLocation("inputImageTexture"), 0)

        glBindVertexArray(depthVisualVao)
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
        glBindVertexArray(0)

    }
}