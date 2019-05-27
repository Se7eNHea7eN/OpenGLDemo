package com.se7en.opengl

import com.se7en.opengl.geometry.SkyBoxCube
import com.se7en.opengl.material.Material
import com.se7en.opengl.material.SkyBoxMat
import org.joml.Vector3f
import java.nio.ByteBuffer
import java.nio.ByteOrder

abstract class GlSkyBox : GlMeshObject() {

    override fun createMesh(): Mesh = Mesh().apply {
        vertices = ByteBuffer.allocateDirect(SkyBoxCube._vertices.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
            .put(SkyBoxCube._vertices).apply {
                position(0)
            }
        normals = ByteBuffer.allocateDirect(SkyBoxCube._normals.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
            .put(SkyBoxCube._normals).apply {
                position(0)
            }
        indices =
            ByteBuffer.allocateDirect(SkyBoxCube._indices.size * 4).order(ByteOrder.nativeOrder()).asIntBuffer()
                .put(SkyBoxCube._indices)
                .apply {
                    position(0)
                }
        texCoords = ByteBuffer.allocateDirect(SkyBoxCube._texCoords.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
            .put(SkyBoxCube._texCoords).apply {
                position(0)
            }
    }

    abstract fun skyBoxTextures() : Array<String>

    override fun createMaterial(): Material  = object : SkyBoxMat(){
        override fun skyBoxTextures(): Array<String> =  this@GlSkyBox.skyBoxTextures()
    }
}