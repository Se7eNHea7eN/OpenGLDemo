package com.se7en.opengl

import com.se7en.opengl.material.Material
import com.se7en.opengl.material.Phong
import com.se7en.opengl.obj.ObjData
import com.se7en.opengl.obj.ObjReader
import com.se7en.opengl.obj.ObjUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class Bunny : GlMeshObject() {
    override fun createMesh(): Mesh {
        val obj = ObjUtils.convertToRenderable(ObjReader.read(this@Bunny.javaClass.classLoader.getResourceAsStream("models/bunny.obj")))
        return Mesh().apply {
            vertices = ObjData.getVertices(obj)
            normals = ObjData.getNormals(obj)
            indices = ObjData.getFaceVertexIndices(obj)
        }
    }

    override fun createMaterial(): Material = Phong()
}