package com.se7en.opengl.testscene

import com.se7en.opengl.GlScene
import com.se7en.opengl.WHITE
import com.se7en.opengl.geometry.Sphere
import com.se7en.opengl.material.Material
import com.se7en.opengl.material.Phong
import org.joml.Vector3f

class TestScene : GlScene() {
    val sphere  =  object : Sphere(){
        override fun createMaterial(): Material  = object : Phong(){
            init {
                ambientColor = WHITE
            }
            override fun texturePath(): String = "textures/pbr/metal14/Metal14_col.jpg"
        }
    }
    init {
        mainCamera.transform.localPosition = Vector3f(0f,0f,-10f)
    }
}