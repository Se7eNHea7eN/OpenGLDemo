package com.se7en.opengl

import com.se7en.opengl.material.Material
import com.se7en.opengl.material.Phong
import org.joml.Vector3f

class TestScene : GlScene() {

    private val bunny = object : GlObjMeshObject() {
        override fun objFilePath(): String = "models/bunny.obj"
        override fun createMaterial(): Material = Phong()
    }.apply {
        transform.scale = Vector3f(2f)
        transform.position = Vector3f(-3f,0f,0f)
    }
    private val teapot = object : GlObjMeshObject() {
        override fun objFilePath(): String = "models/teapot.obj"
        override fun createMaterial(): Material = Phong()
    }.apply {
        transform.position = Vector3f(3f,0f,0f)
    }

    private val pointLight = GlPointLight()

    init {
        mainCamera.transform.position = Vector3f(0f, 4f, 10f)
        pointLight.transform.position = Vector3f(5f, 5f, 1.5f)
    }

    override fun update(deltaTime: Long) {
        super.update(deltaTime)
        pointLight.transform.position = pointLight.transform.position.rotateAxis(2f / 1000f * deltaTime, 0f, 1f, 0f)
    }
}