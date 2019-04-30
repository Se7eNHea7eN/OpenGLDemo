package com.se7en.opengl.test

import com.se7en.opengl.GlObjMeshObject
import com.se7en.opengl.GlPointLight
import com.se7en.opengl.GlScene
import com.se7en.opengl.material.Lambert
import com.se7en.opengl.material.Material
import com.se7en.opengl.material.Phong
import org.joml.Vector3f

class PhongTestScene : GlScene() {

    private val bunny = object : GlObjMeshObject() {
        override fun objFilePath(): String = "models/bunny.obj"
        override fun createMaterial(): Material = Phong()
    }.apply {
        transform.scale = Vector3f(2f)
        transform.position = Vector3f(-3f,0f,0f)
    }
    private val teapot = object : GlObjMeshObject() {
        override fun objFilePath(): String = "models/bunny.obj"
        override fun createMaterial(): Material = Lambert()
    }.apply {
        transform.scale = Vector3f(2f)
        transform.position = Vector3f(3f,0f,0f)
    }

    private val pointLight1 = GlPointLight().apply {
        transform.position = Vector3f(5f, 5f, 1.5f)
        lightColor = Vector3f(1f,0f,0f)
        intensive = 0.75f
    }

    private val pointLight2 = GlPointLight().apply {
        transform.position = Vector3f(-5f, 5f, 1.5f)
        lightColor = Vector3f(0f,0f,1f)
        intensive = 0.75f
    }

    init {
        mainCamera.transform.position = Vector3f(0f, 4f, 10f)
        mainCamera.transform.rotation.rotateX(-30f)
    }

    override fun update(deltaTime: Long) {
        super.update(deltaTime)
        pointLight1.transform.position.rotateAxis(3f / 1000f * deltaTime, 0f, 1f, 0f)
        pointLight2.transform.position.rotateAxis(2f / 1000f * deltaTime, 0f, 1f, 0f)
    }
}