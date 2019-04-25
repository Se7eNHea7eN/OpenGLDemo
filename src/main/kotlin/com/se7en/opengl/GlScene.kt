package com.se7en.opengl

import org.lwjgl.opengl.GL11.*

class GlScene {
    val camera  = GlCamera()
    fun onWindowSizeChanged(width: Int, height :Int) {
        camera.onWindowSizeChanged(width,height)
    }

    fun draw() {
        // Set the clear color
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) // clear the framebuffer
    }
}