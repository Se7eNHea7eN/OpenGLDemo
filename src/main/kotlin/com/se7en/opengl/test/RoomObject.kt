package com.se7en.opengl.test

import com.se7en.opengl.GlMeshObject
import com.se7en.opengl.Mesh
import com.se7en.opengl.material.Lambert
import com.se7en.opengl.material.Material
import com.se7en.opengl.material.Phong
import java.nio.ByteBuffer
import java.nio.ByteOrder

class RoomObject : GlMeshObject() {
    override fun createMesh(): Mesh {
        val _vertices = floatArrayOf(
            // Bottom face
            1.0f, 0f, -1.0f,    //0
            1.0f, 0f, 1.0f,     //1
            -1.0f, 0f, -1.0f,   //2
            -1.0f, 0f, 1.0f     //3
//
//            // Left face
//            ,-1.0f, 1.0f, -1.0f,//4
//            -1.0f, -1.0f, -1.0f,//5
//            -1.0f, 1.0f, 1.0f,  //6
//            -1.0f, -1.0f, 1.0f  //7
//
//
//            // Right face
//            ,1.0f, 1.0f, 1.0f,  //8
//            1.0f, -1.0f, 1.0f,  //9
//            1.0f, 1.0f, -1.0f,  //10
//            1.0f, -1.0f, -1.0f //11
        )

        val _normals = floatArrayOf(
            // Bottom face
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f
//            // Left face
//            ,1.0f, 0.0f, 0.0f,
//            1.0f, 0.0f, 0.0f,
//            1.0f, 0.0f, 0.0f,
//            1.0f, 0.0f, 0.0f
//
//            // Right face
//            ,-1.0f, 0.0f, 0.0f,
//            -1.0f, 0.0f, 0.0f,
//            -1.0f, 0.0f, 0.0f,
//            -1.0f, 0.0f, 0.0f
        )

        val _indices = intArrayOf(
            // Bottom face
            0,1,2,1,3,2
//            ,4,5,6,5,7,6
//            ,8,9,10,9,11,10
        )

        val _texCoords = floatArrayOf(
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
            )
        return Mesh().apply {
            vertices = ByteBuffer.allocateDirect(_vertices.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(_vertices).apply {
                position(0)
            }
            normals = ByteBuffer.allocateDirect(_normals.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(_normals).apply {
                position(0)
            }
            indices = ByteBuffer.allocateDirect(_indices.size*4).order(ByteOrder.nativeOrder()).asIntBuffer().put(_indices).apply {
                position(0)
            }
            texCoords =  ByteBuffer.allocateDirect(_texCoords.size*4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(_texCoords).apply {
                position(0)
            }
        }
    }
    override fun createMaterial(): Material = object : Phong(){
        override fun texturePath(): String =  "textures/wood.jpg"
    }
}