package com.se7en.opengl.material

import com.se7en.opengl.GlUtil
import com.se7en.opengl.Mesh
import com.se7en.opengl.toFloatArray
import org.joml.Matrix4f
import org.lwjgl.opengl.GL41
import org.lwjgl.opengl.GL41.*
import org.lwjgl.system.MemoryUtil

abstract class PbrTexturedMaterial : Material() {

    override fun vertexShader(): String = "shaders/common.vsh"
    override fun fragmentShader(): String = "shaders/pbr_textured.fsh"

    abstract fun normalMap() : String
    abstract fun albedoMap() : String
    abstract fun metallicMap() : String
    abstract fun roughnessMap() : String
    abstract fun aoMap() : String
//    abstract fun displacement() : String

    private var normalTexture = 0
    private var albedoTexture = 0
    private var metallicTexture = 0
    private var roughnessTexture = 0
    private var aoTexture = 0
//    private var displacementTexture = 0
    override var mesh: Mesh?
        get() = super.mesh
        set(value) {
            super.mesh = value

            glBindVertexArray(vao)
            val texCoordArrayBuffer = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, texCoordArrayBuffer)
            glBufferData(GL_ARRAY_BUFFER, value!!.texCoords!!, GL_STATIC_DRAW)
            glEnableVertexAttribArray(2)
            glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, MemoryUtil.NULL)
            glBindBuffer(GL_ARRAY_BUFFER, 0)

            glBindVertexArray(0)

        }

    init {
        enableLighting = true
        normalTexture = GlUtil.createTextureFromResource(normalMap()).textureId
        albedoTexture = GlUtil.createTextureFromResource(albedoMap()).textureId
        if(metallicMap().isNotEmpty())
            metallicTexture = GlUtil.createTextureFromResource(metallicMap()).textureId
        roughnessTexture = GlUtil.createTextureFromResource(roughnessMap()).textureId
//        displacementTexture = GlUtil.createTextureFromResource(displacement()).textureId
        if(aoMap().isNotEmpty())
            aoTexture = GlUtil.createTextureFromResource(aoMap()).textureId

        shader.useProgram()
        shader.setUniform1i("normalMap", 0)
        shader.setUniform1i("albedoMap", 1)
        shader.setUniform1i("metallicMap", 2)
        shader.setUniform1i("roughnessMap", 3)
        shader.setUniform1i("aoMap", 4)
//        shader.setUniform1i("heightMap", 5)

    }
    override fun render(viewMatrix: Matrix4f, projectionMatrix: Matrix4f, modelMatrix: Matrix4f) {
        if (mesh == null) return
        shader.useProgram()
        shader.setUniformMatrix4fv("projectionMatrix", projectionMatrix.get(FloatArray(16)))
        shader.setUniformMatrix4fv("viewMatrix", viewMatrix.get(FloatArray(16)))
        shader.setUniformMatrix4fv("modelMatrix", modelMatrix.get(FloatArray(16)))
//
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, normalTexture)
        glActiveTexture(GL_TEXTURE1)
        glBindTexture(GL_TEXTURE_2D, albedoTexture)
        if(metallicTexture > 0) {
            shader.setUniform1i("useMetallicMap",1)
            glActiveTexture(GL_TEXTURE2)
            glBindTexture(GL_TEXTURE_2D, metallicTexture)
        }else{
            shader.setUniform1i("useMetallicMap",0)
            shader.setUniform1fv("metallicValue",0.0f)
        }
        glActiveTexture(GL_TEXTURE3)
        glBindTexture(GL_TEXTURE_2D, roughnessTexture)
        if(aoTexture > 0) {
            shader.setUniform1i("useAoMap",1)
            glActiveTexture(GL_TEXTURE4)
            glBindTexture(GL_TEXTURE_2D, aoTexture)
        }else{
            shader.setUniform1i("useAoMap",0)
            shader.setUniform1fv("aoValue",1.0f)
        }

        glBindVertexArray(vao)

        glDrawElements(GL_TRIANGLE_STRIP, mesh!!.numVertices, GL_UNSIGNED_INT, 0L)
        glBindVertexArray(0)

        glUseProgram(0)
    }
}