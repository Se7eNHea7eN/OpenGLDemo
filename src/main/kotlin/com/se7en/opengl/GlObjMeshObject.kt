package com.se7en.opengl

import com.se7en.opengl.obj.ObjData
import com.se7en.opengl.obj.ObjReader
import com.se7en.opengl.obj.ObjUtils

abstract class GlObjMeshObject : GlMeshObject() {
    override fun createMesh(): Mesh {
        val obj = ObjUtils.convertToRenderable(ObjReader.read(javaClass.classLoader.getResourceAsStream(objFilePath())))
        return Mesh().apply {
            vertices = ObjData.getVertices(obj)
            normals = ObjData.getNormals(obj)
            indices = ObjData.getFaceVertexIndices(obj)
        }
    }
    abstract fun objFilePath() : String
}