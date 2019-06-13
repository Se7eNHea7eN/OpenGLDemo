package com.se7en.opengl.testscene

import com.se7en.opengl.*
import com.se7en.opengl.geometry.RoomObject
import com.se7en.opengl.geometry.Sphere
import com.se7en.opengl.input.Input
import com.se7en.opengl.lighting.GlDirectionLight
import com.se7en.opengl.lighting.GlPointLight
import com.se7en.opengl.material.Illumination
import com.se7en.opengl.material.Material
import com.se7en.opengl.material.Phong
import com.se7en.opengl.utils.Debug
import org.joml.Vector3f
import org.lwjgl.opengl.GL11.GL_CULL_FACE
import org.lwjgl.opengl.GL11.glEnable

class ShadowTestScene : GlScene() {
    private val room = RoomObject().apply {
        transform.localScale = Vector3f(10f)
        castShadow = false
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
//        doRender = false
        transform.localScale = Vector3f(0.75f)
        transform.localPosition = Vector3f(2f, 0f, 0f)
//        transform.localRotation.rotateX(-Math.toRadians(90.0).toFloat())
    }


    private val pointLight1 = object : GlPointLight() {
        init {
            transform.localPosition = Vector3f(2f, 3f, 2f)
            lightColor = WHITE
            intensive = 0.5f
        }

        override fun update(deltaTime: Long) {
            super.update(deltaTime)
            transform.localPosition.rotateAxis(3f / 1000f * deltaTime, 0f, 1f, 0f)
        }
    }.run {
        object : Sphere() {
            override fun createMaterial(): Material = Illumination().apply {
                objColor = Vector3f(1f, 1f, 1f)
            }

        }.apply {
            castShadow = false
            transform.parent = this@run.transform

            transform.localScale = Vector3f(0.5f)
            transform.localPosition = Vector3f(0f, 0f, 0f)
        }

    }
    private val pointLight2 = object : GlPointLight() {
        init {
            transform.localPosition = Vector3f(-2f, 3f, 2f)
            lightColor = WHITE
            intensive = 0.5f
        }

        override fun update(deltaTime: Long) {
            super.update(deltaTime)
            transform.localPosition.rotateAxis(2f / 1000f * deltaTime, 0f, 1f, 0f)
        }
    }.run {
        object : Sphere() {
            override fun createMaterial(): Material = Illumination().apply {
                objColor = Vector3f(1f, 1f, 1f)
            }

        }.apply {
            castShadow = false
            transform.parent = this@run.transform

            transform.localScale = Vector3f(0.5f)
            transform.localPosition = Vector3f(0f, 0f, 0f)
        }

    }

//    private val directionLight = object : GlDirectionLight() {
//        init {
//            transform.localPosition = Vector3f(0f, 50f, 0f)
//            transform.lookAt(Vector3f())
//        }
//        var rotationZ = 0f
//        var rotationZDir = 1f
//        override fun update(deltaTime: Long) {
//            super.update(deltaTime)
//            if (Math.toDegrees(rotationZ.toDouble()) > 45f)
//                rotationZDir = -1f
//            else if (Math.toDegrees(rotationZ.toDouble()) < -45f)
//                rotationZDir = 1f
//            var rot = rotationZDir * 0.0002f * deltaTime.toFloat()
//            rotationZ += rot
//
//            transform.localPosition.rotateZ(rot)
//            transform.lookAt(Vector3f())
//        }
//    }
    init {
        mainCamera.transform.localPosition = Vector3f(0f, 6f, 10f)
        mainCamera.transform.localRotation.rotateY(Math.toRadians(180.0).toFloat())
        mainCamera.transform.localRotation.rotateX(Math.toRadians(20.0).toFloat())
    }

    var mouseXLastFrame = 0.0
    var mouseYLastFrame = 0.0
    override fun updateControls(deltaTime: Long, input: Input, width: Int, height: Int) {
        super.updateControls(deltaTime, input, width, height)
        if (mouseXLastFrame != 0.0 && mouseYLastFrame != 0.0) {
            val deltaX = input.mouseX - mouseXLastFrame
            val deltaY = input.mouseY - mouseYLastFrame

            mainCamera.transform.localRotation.rotateAxis((deltaY / width).toFloat(), mainCamera.transform.left())
            mainCamera.transform.localRotation.rotateAxis((deltaX / width).toFloat(), mainCamera.transform.up())
        }

        mouseXLastFrame = input.mouseX
        mouseYLastFrame = input.mouseY
    }
}