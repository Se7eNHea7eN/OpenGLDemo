package com.se7en.opengl.material

import com.se7en.opengl.GlUtil
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.*
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
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_GENERATE_MIPMAP, GL_TRUE)

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

        glEnableClientState(GL_VERTEX_ARRAY)
        glEnable(GL_DEPTH_TEST)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE)

        vao = glGenVertexArrays()
        glBindVertexArray(vao)

        val vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, quadVertices, GL_STATIC_DRAW)
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0L)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)

    }

    override fun render(
        viewMatrix: Matrix4f,
        projectionMatrix: Matrix4f,
        modelMatrix: Matrix4f
    ) {
        glEnable(GL_CULL_FACE)
        shader.useProgram()
        shader.setUniformMatrix4fv("invViewProjection",Matrix4f().set(projectionMatrix).mul(viewMatrix).invert().get(FloatArray(16)))
//        glVertexPointer(2, GL_FLOAT, 0, quadVertices)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_CUBE_MAP, skyBoxTexture)
        shader.setUniform1i("tex",0)
        glBindVertexArray(vao)
        glDrawArrays(GL_TRIANGLES, 0, 6)
        glBindVertexArray(0)
        glDisable(GL_CULL_FACE)
    }
}