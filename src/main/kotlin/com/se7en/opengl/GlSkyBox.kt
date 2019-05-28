package com.se7en.opengl

import com.se7en.opengl.geometry.SkyBoxCube
import com.se7en.opengl.material.Material
import com.se7en.opengl.material.SkyBoxMat
import org.joml.Vector3f
import java.nio.ByteBuffer
import java.nio.ByteOrder

abstract class GlSkyBox : GlRenderObject() {

    abstract fun skyBoxTextures() : Array<String>

    override fun createMaterial(): Material  = object : SkyBoxMat(){
        override fun skyBoxTextures(): Array<String> =  this@GlSkyBox.skyBoxTextures()
    }
}