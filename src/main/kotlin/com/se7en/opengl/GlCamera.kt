package com.se7en.opengl

import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL41.glClearDepthf

class GlCamera : GlObject() {
    private var width = 1280
    private var height = 720

    fun onWindowSizeChanged(width: Int, height :Int) {
        this.width = width
        this.height = height
        glClearDepthf(1.0f)
//        glEnable(GL_CULL_FACE)
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)
        glViewport(0, 0, width, height)
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

    fun render(objects:List<GlObject>){
        glClearColor(0f, 0f, 0f, 0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        val forward = transform.forward()
        val up = transform.up()
        objects.forEach {
            if(it is GlMeshObject){
                it.material.setProjectionMatrix(Matrix4f()
                    .perspective(Math.toRadians(45.0).toFloat(), width.toFloat()/height, 0.01f, 100.0f)
                    .get(FloatArray(16)))
                it.material.setViewMatrix(Matrix4f().lookAt(
                    transform.position.x, transform.position.y, transform.position.z,
                    forward.x, forward.y, forward.z,
                    up.x, up.y, up.z
                ).get(FloatArray(16)))
                it.render()
            }
        }
    }

}