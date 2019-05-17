package com.se7en.opengl.test

import com.se7en.opengl.GlDirectionLight
import com.se7en.opengl.GlObjMeshObject
import com.se7en.opengl.GlPointLight
import com.se7en.opengl.GlScene
import com.se7en.opengl.material.Material
import com.se7en.opengl.material.Phong
import org.joml.Vector3f

class ShadowTestScene : GlScene() {
//    private val room = RoomObject().apply {
//        transform.scale = Vector3f(10f)
//    }
    private val bunny = object : GlObjMeshObject() {
        override fun objFilePath(): String = "models/bunny.obj"
        override fun createMaterial(): Material = Phong().apply {
            objColor = Vector3f(1f, 0f, 0f)
            specularStrength = 0.5f
        }
    }.apply {
        transform.scale = Vector3f(2f)
        transform.position = Vector3f(-3f, 0f, 0f)
    }
    private val teapot = object : GlObjMeshObject() {
        override fun objFilePath(): String = "models/bunny.obj"
        override fun createMaterial(): Material = Phong().apply {
            objColor = Vector3f(0f, 0f, 1f)
            shininess = 64f
        }
    }.apply {
        transform.scale = Vector3f(2f)
        transform.position = Vector3f(3f, 0f, 0f)
    }

    private val pointLight1 = object :GlPointLight(){
        init {
            transform.position = Vector3f(5f, 5f, 1.5f)
            lightColor = Vector3f(1f, 1f, 1f)
            intensive = 0.75f
        }

        override fun update(deltaTime: Long) {
            super.update(deltaTime)
            transform.position.rotateAxis(3f / 1000f * deltaTime, 0f, 1f, 0f)
        }
    }


    //    private val pointLight2 = object :GlPointLight(){
//        init {
//            transform.position = Vector3f(-5f, 5f, 1.5f)
//            lightColor = Vector3f(0f, 0f, 1f)
//            intensive = 0.75f
//        }
//
//        override fun update(deltaTime: Long) {
//            super.update(deltaTime)
//            transform.position.rotateAxis(2f / 1000f * deltaTime, 0f, 1f, 0f)
//        }
//    }

//    private val directionLight = GlDirectionLight().apply {
//        transform.rotation.rotateX(120f)
//        transform.rotation.rotateY(30f)
//    }

    init {
        mainCamera.transform.position = Vector3f(0f, 4f, 10f)
        mainCamera.transform.rotation.rotateX(-30f)
    }

    override fun update(deltaTime: Long) {
        super.update(deltaTime)
//        directionLight.transform.rotation.rotateX(0.001f*deltaTime)
    }
}