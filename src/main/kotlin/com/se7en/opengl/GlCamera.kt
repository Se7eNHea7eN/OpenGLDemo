package com.se7en.opengl

import com.opengles_demo.GlObject
import org.joml.Matrix4f

class GlCamera : GlObject() {
    private var width = 1280
    private var height = 720

    fun onWindowSizeChanged(width: Int, height :Int) {
        this.width = width
        this.height = height
    }

    fun matrix(): Matrix4f {
        val up = transform.up()
        val forward = transform.forward()

        return Matrix4f()
            .perspective(Math.toRadians(45.0).toFloat(), width.toFloat()/height, 0.01f, 100.0f)
            .lookAt(
                transform.position.x, transform.position.y, transform.position.z,
                forward.x, forward.y, forward.z,
                up.x, up.y, up.z
            )
    }

}