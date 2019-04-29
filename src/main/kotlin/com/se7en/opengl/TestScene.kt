package com.se7en.opengl

import com.se7en.opengl.material.Material
import com.se7en.opengl.material.Phong
import com.se7en.opengl.obj.ObjData
import com.se7en.opengl.obj.ObjReader
import com.se7en.opengl.obj.ObjUtils
import org.joml.Vector3f

class TestScene : GlScene() {
    //    private val bunny = Bunny()
    private val teapot = object : GlMeshObject() {
        override fun createMesh(): Mesh = Mesh().apply {
            val obj =
                ObjUtils.convertToRenderable(ObjReader.read(javaClass.classLoader.getResourceAsStream("models/teapot.obj")))
            vertices = ObjData.getVertices(obj)
            normals = ObjData.getNormals(obj)
            indices = ObjData.getFaceVertexIndices(obj)
        }

        override fun createMaterial(): Material = Phong()
    }
    private val pointLight = GlPointLight()

    init {
        mainCamera.lookAtPosition = Vector3f(0f,2f,0f)
        mainCamera.transform.position = Vector3f(0f, 4f, -8f)
        pointLight.transform.position = Vector3f(5f, 5f, 1.5f)
    }

    override fun update(deltaTime: Long) {
        super.update(deltaTime)
        pointLight.transform.position = pointLight.transform.position.rotateAxis(2f / 1000f * deltaTime, 0f, 1f, 0f)
    }
}