package com.se7en.opengl.input

import org.lwjgl.glfw.GLFW

class Input {
    val keyDown = BooleanArray(GLFW.GLFW_KEY_LAST)
    var mouseX: Double = 0.0
    var mouseY: Double = 0.0
}