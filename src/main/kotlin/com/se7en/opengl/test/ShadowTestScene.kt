package com.se7en.opengl.test

import com.se7en.opengl.*
import com.se7en.opengl.material.Material
import com.se7en.opengl.material.Phong
import com.se7en.opengl.utils.Debug
import org.joml.Vector3f
import org.lwjgl.system.MathUtil

class ShadowTestScene : GlScene() {
    private val room = RoomObject().apply {
        transform.scale = Vector3f(10f)
        projectShadow = false
    }

    private val bunny = object : GlObjMeshObject() {
        override fun objFilePath(): String = "models/bunny.obj"
        override fun createMaterial(): Material = Phong().apply {
            objColor = Vector3f(1f, 0f, 0f)
            specularStrength = 0.5f
        }
    }.apply {
        transform.scale = Vector3f(2f)
        transform.position = Vector3f(-2f, 0f, 0f)
    }

    private val teapot = object : GlObjMeshObject() {
        override fun objFilePath(): String = "models/teapot.obj"
        override fun createMaterial(): Material = Phong().apply {
            objColor = Vector3f(0f, 0f, 1f)
            shininess = 64f
        }
    }.apply {
        transform.scale = Vector3f(0.75f)
        transform.position = Vector3f(2f, 0f, 0f)
    }

    private val pointLight1 = object : GlPointLight() {
        init {
            transform.position = Vector3f(5f, 5f, 5f)
            lightColor = Vector3f(1f, 1f, 1f)
            intensive = 0.5f
        }

        override fun update(deltaTime: Long) {
            super.update(deltaTime)
            transform.position.rotateAxis(3f / 1000f * deltaTime, 0f, 1f, 0f)
        }
    }


    private val pointLight2 = object : GlPointLight() {
        init {
            transform.position = Vector3f(-5f, 5f, 5f)
            lightColor = Vector3f(1f, 1f, 1f)

            intensive = 0.5f
        }

        override fun update(deltaTime: Long) {
            super.update(deltaTime)
            transform.position.rotateAxis(2f / 1000f * deltaTime, 0f, 1f, 0f)
        }
    }

//    private val directionLight = GlDirectionLight().apply {
//        transform.position = Vector3f(0f,5f,0f)
//        transform.lookAt(Vector3f(0f,0f,0f))
////        transform.rotation.rotateZ(30f)
//    }

    init {
        mainCamera.transform.position = Vector3f(0f, 6f, 10f)
        mainCamera.transform.rotation.rotateY(Math.toRadians(180.0).toFloat())
        mainCamera.transform.rotation.rotateX(Math.toRadians(20.0).toFloat())
//        mainCamera.transform.rotation.rotateX(-90f)
    }

    override fun update(deltaTime: Long) {
        super.update(deltaTime)
//        directionLight.transform.rotation.rotateY(0.01f*deltaTime)
//        mainCamera.transform.rotation.rotateAxis(0.001f*deltaTime,1f,0f,0f)
//        mainCamera.transform.rotation.rotateX(-0.001f*deltaTime)
//        bunny.transform.rotation.rotateX(0.001f*deltaTime)
//        Debug.log("${mainCamera.transform.forward() }")
    }
}