package com.se7en.opengl.test

import com.se7en.opengl.GlScene
import com.se7en.opengl.GlSkyBox
import org.lwjgl.opengl.GL11.*

class SkyBoxTest : GlScene() {
    private val skybox = object : GlSkyBox() {
        override fun skyBoxTextures(): Array<String> =
            arrayOf(
                "textures/space/space_right1.jpg",
                "textures/space/space_left2.jpg",
                "textures/space/space_top3.jpg",
                "textures/space/space_bottom4.jpg",
                "textures/space/space_front5.jpg",
                "textures/space/space_back6.jpg"
            )
    }
    init {

        glEnableClientState(GL_VERTEX_ARRAY)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE)
    }
}