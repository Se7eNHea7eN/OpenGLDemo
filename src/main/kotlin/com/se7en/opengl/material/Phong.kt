package com.se7en.opengl.material

import com.se7en.opengl.GlUtil.createTextureFromResource
import com.se7en.opengl.toFloatArray
import com.se7en.opengl.utils.Debug
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL41.*

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
        shader.setUniformMatrix4fv("modelMatrix",modelMatrix.get(FloatArray(16)))

        shader.setUniform3fv("objColor", objColor.toFloatArray())
        shader.setUniform3fv("ambientColor", ambientColor.toFloatArray())
        shader.setUniform1fv("ambientStrength", ambientStrength)
        shader.setUniform1fv("specularStrength", specularStrength)
        shader.setUniform1fv("shininess", shininess)
        shader.setUniform3fv("eyePos", eyePos.toFloatArray())

        //shader.setVertexAttribArray("iPosition", 3, mesh!!.vertices!!)
        //shader.setVertexAttribArray("iNormal", 3, mesh!!.normals!!)
        glBindVertexArray(vao)

        if (texture > 0) {
            glEnable(GL_TEXTURE_2D)
            glActiveTexture(GL_TEXTURE20)
            glBindTexture(GL_TEXTURE_2D, texture)
            shader.setVertexAttribArray("iTexCoord",2,mesh!!.texCoords!!)
            shader.setUniformInt("objTexture", 20)
            shader.setUniformInt("useTexture", 1)
        }
        glDrawElements(GL_TRIANGLES, mesh!!.indices!!)
    }
}