package com.se7en.opengl.testscene

import com.se7en.opengl.GlObjMeshObject
import com.se7en.opengl.lighting.GlPointLight
import com.se7en.opengl.GlScene
import com.se7en.opengl.geometry.RoomObject
import com.se7en.opengl.material.Material
import com.se7en.opengl.material.Phong
import org.joml.Vector3f

class PhongTestScene : GlScene() {

    private val room = RoomObject().apply {
        transform.localScale = Vector3f(10f)
        castShadow = false
    }

    private val bunny = object : GlObjMeshObject() {
        override fun objFilePath(): String = "models/bunny.obj"
        override fun createMaterial(): Material = Phong()
    }.apply {
        transform.localScale = Vector3f(2f)
        transform.localPosition = Vector3f(-3f,0f,0f)
    }
    private val teapot = object : GlObjMeshObject() {
        override fun objFilePath(): String = "models/bunny.obj"
        override fun createMaterial(): Material = Phong()
    }.apply {
        transform.localScale = Vector3f(2f)
        transform.localPosition = Vector3f(3f,0f,0f)
    }

    private val pointLight1 = GlPointLight().apply {
        transform.localPosition = Vector3f(5f, 5f, 1.5f)
        lightColor = Vector3f(1f,0f,0f)
        intensive = 0.75f
    }

    private val pointLight2 = GlPointLight().apply {
        transform.localPosition = Vector3f(-5f, 5f, 1.5f)
        lightColor = Vector3f(0f,0f,1f)
        intensive = 0.75f
    }

    init {
        mainCamera.transform.localPosition = Vector3f(0f, 6f, 10f)
        mainCamera.transform.localRotation.rotateY(Math.toRadians(180.0).toFloat())
        mainCamera.transform.localRotation.rotateX(Math.toRadians(20.0).toFloat())
    }

    override fun update(deltaTime: Long) {
        super.update(deltaTime)
        pointLight1.transform.localPosition.rotateAxis(3f / 1000f * deltaTime, 0f, 1f, 0f)
        pointLight2.transform.localPosition.rotateAxis(2f / 1000f * deltaTime, 0f, 1f, 0f)
    }
}