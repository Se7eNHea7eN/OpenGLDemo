package com.se7en.opengl.test

import com.se7en.opengl.*
import com.se7en.opengl.geometry.RoomObject
import com.se7en.opengl.geometry.Sphere
import com.se7en.opengl.material.Illumination
import com.se7en.opengl.material.Material
import com.se7en.opengl.material.Phong
import org.joml.Vector3f

class ShadowTestScene : GlScene() {
    private val room = RoomObject().apply {
        transform.localScale = Vector3f(10f)
//        projectShadow = false
    }

    private val bunny = object : GlObjMeshObject() {
        override fun objFilePath(): String = "models/bunny.obj"
        override fun createMaterial(): Material = Phong().apply {
            objColor = RED
            specularStrength = 0.5f
        }
    }.apply {
        transform.localScale = Vector3f(2f)
        transform.localPosition = Vector3f(-2f, 0f, 0f)
    }

    private val teapot = object : GlObjMeshObject() {
        override fun objFilePath(): String = "models/teapot.obj"
        override fun createMaterial(): Material = Phong().apply {
            objColor = BLUE
            shininess = 64f
        }
    }.apply {
        transform.localScale = Vector3f(0.75f)
        transform.localPosition = Vector3f(2f, 0f, 0f)
    }


    private val pointLight1 = object : GlPointLight() {
        init {
            transform.localPosition = Vector3f(5f, 5f, 5f)
            lightColor = WHITE
            intensive = 0.5f
        }

        override fun update(deltaTime: Long) {
            super.update(deltaTime)
            transform.localPosition.rotateAxis(3f / 1000f * deltaTime, 0f, 1f, 0f)
        }
    }


    private val pointLight2 = object : GlPointLight() {
        init {
            transform.localPosition = Vector3f(-5f, 5f, 5f)
            lightColor = WHITE

            intensive = 0.5f
        }

        override fun update(deltaTime: Long) {
            super.update(deltaTime)
            transform.localPosition.rotateAxis(2f / 1000f * deltaTime, 0f, 1f, 0f)
        }
    }


    private val sphere1 = object : Sphere(){
        override fun createMaterial(): Material = Illumination().apply {
            objColor = Vector3f(1f, 1f, 1f)
        }
    }.apply {
        projectShadow = false
        transform.localScale = Vector3f(0.5f)
        transform.localPosition = Vector3f(0f, 0f, 0f)
        transform.parent = pointLight1.transform
    }


    private val sphere2 = object : Sphere(){
        override fun createMaterial(): Material = Illumination().apply {
            objColor = Vector3f(1f, 1f, 1f)
        }
    }.apply {
        projectShadow = false
        transform.localScale = Vector3f(0.5f)
        transform.localPosition = Vector3f(0f, 0f, 0f)
        transform.parent = pointLight2.transform
    }

//    private val directionLight = GlDirectionLight().apply {
//        transform.localPosition = Vector3f(0f,5f,0f)
//        transform.lookAt(Vector3f(0f,0f,0f))
////        transform.localRotation.rotateZ(30f)
//    }

    init {
        mainCamera.transform.localPosition = Vector3f(0f, 6f, 10f)
        mainCamera.transform.localRotation.rotateY(Math.toRadians(180.0).toFloat())
        mainCamera.transform.localRotation.rotateX(Math.toRadians(20.0).toFloat())
//        mainCamera.transform.localRotation.rotateX(-90f)
    }

    override fun update(deltaTime: Long) {
        super.update(deltaTime)
//        directionLight.transform.localRotation.rotateY(0.01f*deltaTime)
//        mainCamera.transform.localRotation.rotateAxis(0.001f*deltaTime,1f,0f,0f)
//        mainCamera.transform.localRotation.rotateX(-0.001f*deltaTime)
//        bunny.transform.localRotation.rotateX(0.001f*deltaTime)
//        Debug.log("${mainCamera.transform.forward() }")
    }
}