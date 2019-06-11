package com.se7en.opengl.material

import asiainnovations.com.opengles_demo.GlShader
import com.se7en.opengl.*
import com.se7en.opengl.lighting.GlAbstractLight
import com.se7en.opengl.lighting.GlDirectionLight
import com.se7en.opengl.lighting.GlPointLight
import com.se7en.opengl.utils.ResourceUtils
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil.memAddress
import org.lwjgl.system.MemoryUtil.memCopy

abstract class Material {
    val shader: GlShader
    var enableLighting = false
    abstract fun vertexShader(): String
    abstract fun fragmentShader(): String
    open fun geometryShader(): String? = null
    var mesh: Mesh? = null
        set(value) {
            field = value
            if(field != null){
                vao = glGenVertexArrays()
                glBindVertexArray(vao)
                val vbo = glGenBuffers()
                glBindBuffer(GL_ARRAY_BUFFER, vbo)
                val vertices = field!!.vertices!!
                val verticesBytes = 4 * 3 * vertices.remaining()
                val verticesFB = BufferUtils.createByteBuffer(verticesBytes).asFloatBuffer()
                memCopy(vertices.address, memAddress(verticesFB), verticesBytes.toLong())
                nglBufferData(GL_ARRAY_BUFFER, verticesBytes.toLong(), vertices.address, GL_STATIC_DRAW)
                glEnableVertexAttribArray(0)
                glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0L)
                normalArrayBuffer = glGenBuffers()
                glBindBuffer(GL_ARRAY_BUFFER, normalArrayBuffer)
                val normals = mesh.mNormals()
                val normalsBytes = 4 * 3 * normals!!.remaining()
                normalsFB = BufferUtils.createByteBuffer(normalsBytes).asFloatBuffer()
                memCopy(normals!!.address(), memAddress(normalsFB), normalsBytes.toLong())
                nglBufferData(GL_ARRAY_BUFFER, normalsBytes.toLong(), normals!!.address(), GL_STATIC_DRAW)
                glEnableVertexAttribArray(1)
                glVertexAttribPointer(1, 3, GL_FLOAT, true, 0, 0L)
                val faceCount = mesh.mNumFaces()
                elementCount = faceCount * 3
                indicesIB = BufferUtils.createIntBuffer(elementCount)
                val facesBuffer = mesh.mFaces()
                for (i in 0 until faceCount) {
                    val face = facesBuffer.get(i)
                    if (face.mNumIndices() != 3)
                        throw IllegalStateException("AIFace.mNumIndices() != 3")
                    indicesIB.put(face.mIndices())
                }
                indicesIB.flip()
                elementArrayBuffer = glGenBuffers()
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementArrayBuffer)
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesIB, GL_STATIC_DRAW)
                glBindVertexArray(0)

            }

        }
    var vao = 0

    var eyePos = Vector3f()


    fun setViewMatrix(viewMatrix: FloatArray) {
        shader.useProgram()
        shader.setUniformMatrix4fv("viewMatrix", viewMatrix)
    }

    fun setLights(lights: List<GlAbstractLight>) {
        shader.useProgram()
        shader.setUniformInt("pointLightCount", lights.count { it is GlPointLight })
        shader.setUniformInt("directionLightCount", lights.count { it is GlDirectionLight })
        glEnable(GL_TEXTURE_2D)
        lights.forEachIndexed { index, light ->
            if (light is GlPointLight) {
                shader.setUniform3fv("pointLights[$index].position", light.transform.position.toFloatArray())
                shader.setUniform3fv("pointLights[$index].color", light.lightColor.toFloatArray())
                shader.setUniform1fv("pointLights[$index].intensive", light.intensive)
                shader.setUniform1fv("pointLights[$index].farPlane", light.far)

                glActiveTexture(GL_TEXTURE0 + index)
                glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, light.depthTexture)
                glUniform1i(shader.getUniformLocation("pointLightCubeShadows[$index]"), index)
            } else if (light is GlDirectionLight) {
                shader.setUniform3fv("directionLights[$index].direction", light.transform.forward().toFloatArray())
                shader.setUniform3fv("directionLights[$index].color", light.lightColor.toFloatArray())
                shader.setUniform1fv("directionLights[$index].intensive", light.intensive)
                shader.setUniformMatrix4fv("directionLights[$index].matrix", light.lightVPMatrix().get(FloatArray(16)))

                glActiveTexture(GL_TEXTURE0 + index)
                glBindTexture(GL_TEXTURE_2D, light.depthTexture)
                glUniform1i(shader.getUniformLocation("directionLightShadows[$index]"), index)
            }
        }
    }


    init {
        shader = GlShader(
            ResourceUtils.ioResourceToByteBuffer(vertexShader(), 8192),
            ResourceUtils.ioResourceToByteBuffer(fragmentShader(), 8192)
        )
    }

    abstract fun render(
        viewMatrix: Matrix4f,
        projectionMatrix: Matrix4f,
        modelMatrix: Matrix4f
    )

    fun onDestroy() {
        shader.release()
    }
}