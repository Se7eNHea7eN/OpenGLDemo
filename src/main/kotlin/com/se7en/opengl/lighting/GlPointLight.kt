package com.se7en.opengl.lighting

import asiainnovations.com.opengles_demo.GlShader
import com.se7en.opengl.GlObject
import com.se7en.opengl.GlRenderObject
import com.se7en.opengl.plus
import com.se7en.opengl.toFloatArray
import com.se7en.opengl.utils.ResourceUtils
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL41.*
import java.nio.ByteBuffer

open class GlPointLight : GlAbstractLight() {
    internal val shadowMapSize = 2048

    internal var fbo: Int = 0

    internal var depthTexture: Int = 0

    var near = 0.01f
    var far = 1000.0f
    var shadowMappingShader = GlShader(
        ResourceUtils.ioResourceToByteBuffer("shaders/pointLightDepth.vsh", 8192),
        ResourceUtils.ioResourceToByteBuffer("shaders/pointLightDepth.gsh", 8192),
        ResourceUtils.ioResourceToByteBuffer("shaders/pointLightDepth.fsh", 8192)
    )

    val quadVertices = BufferUtils.createByteBuffer(4 * 2 * 6).apply {
        asFloatBuffer().apply {
            put(-1.0f).put(-1.0f)
            put(1.0f).put(-1.0f)
            put(1.0f).put(1.0f)
            put(1.0f).put(1.0f)
            put(-1.0f).put(1.0f)
            put(-1.0f).put(-1.0f)
        }
    }

    init {
        /**
         * Create the texture storing the depth values of the light-render.
         */
        depthTexture = glGenTextures()
        glBindTexture(GL_TEXTURE_CUBE_MAP, depthTexture)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_GENERATE_MIPMAP, GL_TRUE)

        for (i in 0..5) {
            glTexImage2D(
                GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
                0,
                GL_DEPTH_COMPONENT,
                shadowMapSize,
                shadowMapSize,
                0,
                GL_DEPTH_COMPONENT,
                GL_UNSIGNED_BYTE,
                null as ByteBuffer?
            )
        }

        glBindTexture(GL_TEXTURE_2D, 0)

        fbo = glGenFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER, fbo)
        glBindTexture(GL_TEXTURE_CUBE_MAP, depthTexture)
        glDrawBuffer(GL_NONE)
        glReadBuffer(GL_NONE)
        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthTexture, 0)
        val fboStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER)
        if (fboStatus != GL_FRAMEBUFFER_COMPLETE) {
            throw AssertionError("Could not create FBO: $fboStatus")
        }
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }
//
//    override fun lightProjectionMatrix(): Matrix4f =
//        Matrix4f().setPerspective(Math.toRadians(90.0).toFloat(), 1.0f, near, far)

    override fun renderShadowMap(objects: List<GlObject>) {
        shadowMappingShader.useProgram()
        glBindFramebuffer(GL_FRAMEBUFFER, fbo)
        glClear(GL_DEPTH_BUFFER_BIT)
        glViewport(0, 0, shadowMapSize, shadowMapSize)
        val centerDirs = arrayOf(
            Vector3f(1f, 0f, 0f),
            Vector3f(-1f, 0f, 0f),
            Vector3f(0f, 1f, 0f),
            Vector3f(0f, -1f, 0f),
            Vector3f(0f, 0f, 1f),
            Vector3f(0f, 0f, -1f)
        )
        val upDirs = arrayOf(
            Vector3f(0f, -1f, 0f),
            Vector3f(0f, -1f, 0f),
            Vector3f(0f, 0f, 1f),
            Vector3f(0f, 0f, -1f),
            Vector3f(0f, -1f, 0f),
            Vector3f(0f, -1f, 0f)
        )

        for (i in 0..5) {
            shadowMappingShader.setUniformMatrix4fv(
                "shadowMatrices[$i]",
                Matrix4f().lookAt(transform.worldPosition, transform.worldPosition + centerDirs[i], upDirs[i]).get(
                    FloatArray(16)
                )
            )
        }
        shadowMappingShader.setUniform1fv("farPlane", far)
        shadowMappingShader.setUniform3fv("lightPos", transform.worldPosition.toFloatArray())

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
        GL20.glDisableVertexAttribArray(shadowMappingShader.getAttribLocation("aPosition"))
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    var depthVisualShader =
        GlShader(
            ResourceUtils.ioResourceToByteBuffer("shaders/skyBox.vsh", 8192),
            ResourceUtils.ioResourceToByteBuffer("shaders/cubeDepthTexure.fsh", 8192)
        )

    override fun drawDebugShadowMap(
        viewMatrix: Matrix4f,
        projectionMatrix: Matrix4f
    ) {
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)

        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS)
        depthVisualShader.useProgram()
        glVertexPointer(2, GL_FLOAT, 0, quadVertices)

        depthVisualShader.setUniformMatrix4fv(
            "invViewProjection",
            Matrix4f().set(projectionMatrix).mul(viewMatrix).invert().get(FloatArray(16))
        )

        glEnable(GL_TEXTURE_CUBE_MAP)
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_CUBE_MAP, depthTexture)
        depthVisualShader.setUniformInt("tex", 0)

        glDrawArrays(GL_TRIANGLES, 0, 6)
    }

}