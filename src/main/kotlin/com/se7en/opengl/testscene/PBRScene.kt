package com.se7en.opengl.testscene

import com.se7en.opengl.*
import com.se7en.opengl.geometry.Sphere
import com.se7en.opengl.input.Input
import com.se7en.opengl.lighting.GlPointLight
import com.se7en.opengl.material.Illumination
import com.se7en.opengl.material.Material
import com.se7en.opengl.material.PBRMaterial
import com.se7en.opengl.utils.Debug
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW

class PBRScene : GlScene() {

    private val pointLight1 = object : GlPointLight() {
        init {
            transform.localPosition = Vector3f(-4f, -4f, -8f)
            lightColor = WHITE * 10f
        }
    }

    private val pointLight2 = object : GlPointLight() {
        init {
            transform.localPosition = Vector3f(-4f, 4f, -8f)
            lightColor = WHITE * 10f
        }
    }

    private val pointLight3 = object : GlPointLight() {
        init {
            transform.localPosition = Vector3f(4f, -4f, -8f)
            lightColor = WHITE * 10f
        }
    }
    private val pointLight4 = object : GlPointLight() {
        init {
            transform.localPosition = Vector3f(4f, 4f, -8f)
            lightColor = WHITE * 10f
        }
    }



    init {
        for( i in 0..7)
            for(j in 0 ..7){
                object : Sphere(){
                    override fun createMaterial(): Material = PBRMaterial().apply {
                        albedo = Vector3f(0f,0f,0.5f)
                        metallic = j/7f
                        roughness = i/7f
                    }
                }.apply {
                    transform.localScale = Vector3f(0.7f)
                    transform.localPosition = Vector3f(7f - 2*i,7f - 2*j,-5f)
                }
            }
        mainCamera.transform.localPosition = Vector3f(10f, 0f, -25f)
        mainCamera.transform.localRotation.rotateY(Math.toRadians(30.0).toFloat())
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

        if(input.keyDown[GLFW.GLFW_KEY_W]){
            mainCamera.transform.localPosition += mainCamera.transform.forward() * deltaTime.toFloat() * 0.0001f
        }
        mouseXLastFrame = input.mouseX
        mouseYLastFrame = input.mouseY
    }
}

