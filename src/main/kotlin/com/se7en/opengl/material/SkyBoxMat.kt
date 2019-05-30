package com.se7en.opengl.material

import com.se7en.opengl.GlUtil
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL41.*
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.lwjgl.stb.STBImage.*

abstract class SkyBoxMat : Material() {
    override fun vertexShader(): String = "shaders/skybox.vsh"
    override fun fragmentShader(): String = "shaders/skybox.fsh"

    internal var skyBoxTexture: Int = 0

    abstract fun skyBoxTextures(): Array<String>
    val quadVertices = BufferUtils.createByteBuffer(4 * 2 * 6).apply {
        asFloatBuffer().apply{
            put(-1.0f).put(-1.0f)
            put(1.0f).put(-1.0f)
            put(1.0f).put(1.0f)
            put(1.0f).put(1.0f)
            put(-1.0f).put(1.0f)
            put(-1.0f).put(-1.0f)
        }
    }
    init {
        skyBoxTexture = glGenTextures()
        GlUtil.checkNoGLES2Error("glGenTextures")

        glBindTexture(GL_TEXTURE_CUBE_MAP, skyBoxTexture)
        GlUtil.checkNoGLES2Error("glBindTexture")

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_GENERATE_MIPMAP, GL_TRUE)

        GlUtil.checkNoGLES2Error("glTexParameteri")
        skyBoxTextures().forEachIndexed { index, s ->
            val width = BufferUtils.createIntBuffer(1)
            val height = BufferUtils.createIntBuffer(1)
            val components = BufferUtils.createIntBuffer(1)

            var imgArray = javaClass.classLoader.getResourceAsStream(s).readBytes()

            val imgBuffer = ByteBuffer.allocateDirect(imgArray.size)
                .order(ByteOrder.nativeOrder())
                .apply {
                    put(imgArray)
                    position(0)
                }
            if (!stbi_info_from_memory(imgBuffer, width, height, components))
                throw IOException("Failed to read image information: " + stbi_failure_reason()!!)

            val data = stbi_load_from_memory(
                imgBuffer, width, height, components,
                4
            )

            glTexImage2D(
                GL_TEXTURE_CUBE_MAP_POSITIVE_X + index,
                0,
                GL_RGBA,
                width.get(0),
                height.get(0),
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                data)
            stbi_image_free(data)
        }
        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS)

        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
    }

    override fun render(
        viewMatrix: Matrix4f,
        projectionMatrix: Matrix4f,
        modelMatrix: Matrix4f
    ) {
        shader.useProgram()
        shader.setUniformMatrix4fv("invViewProjection",Matrix4f().set(projectionMatrix).mul(viewMatrix).invert().get(FloatArray(16)))
        glVertexPointer(2, GL_FLOAT, 0, quadVertices)

        glEnable(GL13.GL_TEXTURE_CUBE_MAP)
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_CUBE_MAP, skyBoxTexture)
        shader.setUniformInt("tex",0)

        glDrawArrays(GL_TRIANGLES, 0, 6)
    }
}