package com.se7en.opengl.test

import com.se7en.opengl.GlScene
import com.se7en.opengl.GlSkyBox

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

}