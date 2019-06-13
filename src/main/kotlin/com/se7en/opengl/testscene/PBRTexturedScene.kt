package com.se7en.opengl.testscene

import com.se7en.opengl.GlScene
import com.se7en.opengl.WHITE
import com.se7en.opengl.geometry.Sphere
import com.se7en.opengl.input.Input
import com.se7en.opengl.lighting.GlDirectionLight
import com.se7en.opengl.lighting.GlPointLight
import com.se7en.opengl.material.Material
import com.se7en.opengl.material.PBRMaterial
import com.se7en.opengl.material.PbrTexturedMaterial
import com.se7en.opengl.plus
import com.se7en.opengl.times
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW

class PBRTexturedScene : GlScene() {

    private val pointLight1 = object : GlPointLight() {
        init {
            transform.localPosition = Vector3f(0f, 0f, -5f)
            lightColor = WHITE * 40f
        }
    }
//    private val directionLight = object : GlDirectionLight() {
//        init {
//            lightColor = WHITE * 2f
//            transform.localPosition = Vector3f(0f, 10f, -10f)
//            transform.lookAt(Vector3f())
//        }
//
//    }
    val metalSphere =   object : Sphere(){
        override fun createMaterial(): Material = object :PbrTexturedMaterial(){

            override fun normalMap(): String  = "textures/pbr/metal/rustediron2_normal.png"

            override fun albedoMap(): String  = "textures/pbr/metal/rustediron2_basecolor.png"

            override fun metallicMap(): String  = "textures/pbr/metal/rustediron2_metallic.png"

            override fun roughnessMap(): String  = "textures/pbr/metal/rustediron2_roughness.png"

            override fun aoMap(): String = ""
        }
        }.apply {
            transform.localPosition = Vector3f(-2f,0f,0f)
        }

    val clothSphere =   object : Sphere(){
        override fun createMaterial(): Material = object :PbrTexturedMaterial(){

            override fun normalMap(): String  = "textures/pbr/cloth/worn-blue-burlap-Normal-dx.png"

            override fun albedoMap(): String  = "textures/pbr/cloth/worn-blue-burlap-albedo.png"

            override fun metallicMap(): String  = "textures/pbr/cloth/worn-blue-burlap-Metallic.png"

            override fun roughnessMap(): String  = "textures/pbr/cloth/worn-blue-burlap-Roughness.png"

            override fun aoMap(): String = "textures/pbr/cloth/worn-blue-burlap-ao.png"
        }
    }.apply {
        transform.localPosition = Vector3f(1f,0f,0f)
    }

    val stoneSphere =   object : Sphere(){
        override fun createMaterial(): Material = object :PbrTexturedMaterial(){

            override fun normalMap(): String  = "textures/pbr/stone/speckled-granite1-Normal-dx.png"

            override fun albedoMap(): String  = "textures/pbr/stone/speckled-granite1-albedo.png"

            override fun metallicMap(): String  = "textures/pbr/stone/speckled-granite1-Metallic.png"

            override fun roughnessMap(): String  = "textures/pbr/stone/speckled-granite1-Roughness.png"

            override fun aoMap(): String = "textures/pbr/stone/speckled-granite1-ao.png"
        }
    }.apply {
        transform.localPosition = Vector3f(4f,0f,0f)
    }


    init {

        mainCamera.transform.localPosition = Vector3f(2f, 0f, -4f)
        mainCamera.transform.localRotation.rotateY(Math.toRadians(30.0).toFloat())
    }


    var mouseXLastFrame = 0.0
    var mouseYLastFrame = 0.0
    override fun updateControls(deltaTime: Long, input: Input, width: Int, height: Int) {
        super.updateControls(deltaTime, input, width, height)
        if (mouseXLastFrame != 0.0 && mouseYLastFrame != 0.0) {
            val deltaX = input.mouseX - mouseXLastFrame
            val deltaY = input.mouseY - mouseYLastFrame

            mainCamera.transform.localRotation.rotateAxis( 5 *(deltaY / width).toFloat(), mainCamera.transform.left())
            mainCamera.transform.localRotation.rotateAxis(5 *(deltaX / width).toFloat(), mainCamera.transform.up())
        }
        var moveVector : Vector3f = Vector3f()
        if(input.keyDown[GLFW.GLFW_KEY_W]){
            moveVector =  mainCamera.transform.forward()
        }
        if(input.keyDown[GLFW.GLFW_KEY_S]){
            moveVector =  mainCamera.transform.backward()
        }
        if(input.keyDown[GLFW.GLFW_KEY_A]){
            moveVector =  mainCamera.transform.right()
        }
        if(input.keyDown[GLFW.GLFW_KEY_D]){
            moveVector =  mainCamera.transform.left()
        }
        mainCamera.transform.localPosition += moveVector * deltaTime.toFloat() * 0.001f

        mouseXLastFrame = input.mouseX
        mouseYLastFrame = input.mouseY
    }

}