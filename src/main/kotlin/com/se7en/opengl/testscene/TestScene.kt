package com.se7en.opengl.testscene

import com.se7en.opengl.GlObjMeshObject
import com.se7en.opengl.GlScene
import com.se7en.opengl.material.Material
import com.se7en.opengl.material.TestMaterial
import org.joml.Vector3f

class TestScene : GlScene() {

    private val bunny = object : GlObjMeshObject() {
        override fun objFilePath(): String = "models/bunny.obj"
        override fun createMaterial(): Material = TestMaterial()
    }.apply {
        transform.localScale = Vector3f(2f)
        transform.localPosition = Vector3f(-3f,0f,0f)
    }

    private val teapot = object : GlObjMeshObject() {
        override fun objFilePath(): String = "models/teapot.obj"
        override fun createMaterial(): Material = TestMaterial()
    }.apply {
        transform.localScale = Vector3f(0.75f)
        transform.localPosition = Vector3f(3f,0f,0f)
    }

    init {
        mainCamera.transform.localPosition = Vector3f(0f, 6f, 10f)
        mainCamera.transform.localRotation.rotateY(Math.toRadians(180.0).toFloat())
        mainCamera.transform.localRotation.rotateX(Math.toRadians(20.0).toFloat())
    }
}