package com.se7en.opengl.material

import com.se7en.opengl.GlUtil.createTextureFromResource
import com.se7en.opengl.Mesh
import com.se7en.opengl.toFloatArray
import com.se7en.opengl.utils.Debug
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL41.*
import org.lwjgl.system.MemoryUtil

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
    override var mesh: Mesh?
        get() = super.mesh
        set(value) {
            super.mesh = value
            if (value != null &&  texturePath().isNotEmpty()) {

                glBindVertexArray(vao)
                val texCoordArrayBuffer = glGenBuffers()
                glBindBuffer(GL_ARRAY_BUFFER, texCoordArrayBuffer)
                glBufferData(GL_ARRAY_BUFFER, value.texCoords!!, GL_STATIC_DRAW)
                glEnableVertexAttribArray(2)
                glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, MemoryUtil.NULL)
                glBindBuffer(GL_ARRAY_BUFFER, 0)

                glBindVertexArray(0)
            }

        }

    init {
        enableLighting = true
        if (texturePath().isNotEmpty()) {
            try {
                texture = createTextureFromResource(texturePath()).textureId
            } catch (e: Exception) {
                Debug.log(e.message)
            }

        }

    }

    override fun render(
        viewMatrix: Matrix4f,
        projectionMatrix: Matrix4f,
        modelMatrix: Matrix4f
    ) {
        if (mesh == null) return
        shader.useProgram()
        shader.setUniformMatrix4fv("projectionMatrix", projectionMatrix.get(FloatArray(16)))
        shader.setUniformMatrix4fv("viewMatrix", viewMatrix.get(FloatArray(16)))
        shader.setUniformMatrix4fv("modelMatrix", modelMatrix.get(FloatArray(16)))

        shader.setUniform3fv("objColor", objColor.toFloatArray())
        shader.setUniform3fv("ambientColor", ambientColor.toFloatArray())
        shader.setUniform1fv("ambientStrength", ambientStrength)
        shader.setUniform1fv("specularStrength", specularStrength)
        shader.setUniform1fv("shininess", shininess)
        shader.setUniform3fv("eyePos", eyePos.toFloatArray())

        if (texture > 0) {
            glActiveTexture(GL_TEXTURE20)
            glBindTexture(GL_TEXTURE_2D, texture)
            shader.setUniform1i("objTexture", 20)
            shader.setUniform1i("useTexture", 1)
        }
        glBindVertexArray(vao)

        glDrawElements(GL_TRIANGLES, mesh!!.numVertices, GL_UNSIGNED_INT, 0L)
        glBindVertexArray(0)

        glUseProgram(0)
    }
}