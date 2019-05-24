package com.se7en.opengl.material

import com.se7en.opengl.toFloatArray
import com.se7en.opengl.utils.Debug
import com.se7en.opengl.utils.ResourceUtils.ioResourceToByteBuffer
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL40
import org.lwjgl.opengl.GL40.*
import org.lwjgl.opengl.GL41
import org.lwjgl.opengl.GL41.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.imageio.ImageIO
import org.lwjgl.stb.STBImage.*
import java.io.IOException

open class Phong : Material() {
    override fun vertexShader(): String = "shaders/common.vsh"
    override fun fragmentShader(): String = "shaders/phong.fsh"

    var objColor = Vector3f(1f, 1f, 1f)
    var ambientColor = Vector3f(1f, 1f, 1f)
    var ambientStrength = 0.1f
    var specularStrength = 1.5f
    var shininess = 128f

    protected var texture: Int = -1

    protected open fun texturePath(): String = ""

    init {
        enableLighting = true
        if (texturePath().isNotEmpty()) {
            try {
                texture = glGenTextures()
                glBindTexture(GL_TEXTURE_2D, texture)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)

                val width = BufferUtils.createIntBuffer(1)
                val height = BufferUtils.createIntBuffer(1)
                val components = BufferUtils.createIntBuffer(1)

                var imgArray = javaClass.classLoader.getResourceAsStream(texturePath()).readBytes()

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

                glTexImage2D(GL_TEXTURE_2D,
                    0,
                    GL_RGBA,
                    width.get(0),
                    height.get(0),
                    0,
                    GL_RGBA,
                    GL_UNSIGNED_BYTE,
                    data)
                stbi_image_free(data)

            } catch (e: Exception) {
                Debug.log(e.message)
            }
        }
    }

    override fun render() {
        if (mesh == null) return
        shader.useProgram()
        shader.setUniform3fv("objColor", objColor.toFloatArray())
        shader.setUniform3fv("ambientColor", ambientColor.toFloatArray())
        shader.setUniform1fv("ambientStrength", ambientStrength)
        shader.setUniform1fv("specularStrength", specularStrength)
        shader.setUniform1fv("shininess", shininess)
        shader.setUniform3fv("eyePos", eyePos.toFloatArray())

        shader.setVertexAttribArray("aPosition", 3, mesh!!.vertices!!)
        shader.setVertexAttribArray("aNormal", 3, mesh!!.normals!!)

        if (texture > 0) {
            glEnable(GL_TEXTURE_2D)
            glActiveTexture(GL_TEXTURE20)
            glBindTexture(GL_TEXTURE_2D, texture)
            shader.setVertexAttribArray("aTexCoord",2,mesh!!.texCoords!!)
            shader.setUniformInt("objTexture", 20)
            shader.setUniformInt("useTexture", 1)
        }
        glDrawElements(GL_TRIANGLES, mesh!!.indices!!)
    }
}