package com.se7en.opengl

import org.joml.Vector3f

class TestScene : GlScene() {
    init {
        camera.transform.position = Vector3f(0f,6f,-8f)

        addObject(Bunny())
    }
}