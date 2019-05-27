package com.se7en.opengl.lighting

import asiainnovations.com.opengles_demo.GlShader
import com.se7en.opengl.GlObject
import com.se7en.opengl.GlRenderObject
import com.se7en.opengl.plus
import com.se7en.opengl.toFloatArray
import com.se7en.opengl.utils.ResourceUtils
import org.joml.Matrix4f
import org.joml.Vector3f
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
    init {

        /**
         * Create the texture storing the depth values of the light-render.
         */
        depthTexture = glGenTextures()
        glBindTexture(GL_TEXTURE_CUBE_MAP, depthTexture)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)

        for (i in 0..5){
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
        /**
         * Create the FBO to render the depth values of the light-render into the
         * depth texture.
         */
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
        glCullFace(GL_FRONT)
        val centerDirs =  arrayOf(
            Vector3f(1f,0f,0f),
            Vector3f(-1f,0f,0f),
            Vector3f(0f,1f,0f),
            Vector3f(0f,-1f,0f),
            Vector3f(0f,0f,1f),
            Vector3f(0f,0f,-1f)
        )
        val upDirs =  arrayOf(
            Vector3f(0f,-1f,0f),
            Vector3f(0f,-1f,0f),
            Vector3f(0f,0f,1f),
            Vector3f(0f,0f,-1f),
            Vector3f(0f,-1f,0f),
            Vector3f(0f,-1f,0f)
        )

        for (i in 0 .. 5){
            shadowMappingShader.setUniformMatrix4fv("shadowMatrices[$i]",
                Matrix4f().lookAt(transform.worldPosition,transform.worldPosition + centerDirs[i],upDirs[i]).get(FloatArray(16))
            )
        }
        shadowMappingShader.setUniform1fv("farPlane",far)
        shadowMappingShader.setUniform3fv("lightPos",transform.worldPosition.toFloatArray())
        objects.forEach {
//            shadowMappingShader.setUniformMatrix4fv("vpMatrix", lightVPMatrix().get(FloatArray(16)))
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
}