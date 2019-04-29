package com.se7en.opengl

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL41.glClearDepthf

class GlCamera : GlObject() {
    private var width = 1280
    private var height = 720
    var fov = 45f
        set(value) {
            field = value
            recalculateProjectionMatrix()
        }

    var zNear = 0.01f
        set(value) {
            field = value
            recalculateProjectionMatrix()
        }

    var zFar = 100.0f
        set(value) {
            field = value
            recalculateProjectionMatrix()
        }

    var projectionMatrix = Matrix4f()

    fun onWindowSizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height
        glClearDepthf(1.0f)
//        glEnable(GL_CULL_FACE)
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)
//        glViewport(0, 0, width, height)
        recalculateProjectionMatrix()
    }
    private fun recalculateProjectionMatrix(){
        projectionMatrix = Matrix4f()
            .perspective(Math.toRadians(fov.toDouble()).toFloat(), width.toFloat() / height, zNear, zFar)
    }

    fun render(objects: List<GlObject>) {
        glClearColor(0f, 0f, 0f, 0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        val up = transform.up()
        val forward = transform.forward()
        val viewMatrix = Matrix4f().setLookAt(
            transform.position.x, transform.position.y, transform.position.z,
            forward.x, forward.y, forward.z,
            up.x, up.y, up.z
        )
        objects.forEach {
            if (it is GlRenderObject) {
                it.material.setPointLights(GlScene.currentScene!!.pointLights)
                it.material.setEyePosition(transform.position)
                it.material.setProjectionMatrix(projectionMatrix.get(FloatArray(16)))
                it.material.setViewMatrix(viewMatrix.get(FloatArray(16)))
                it.render()
            }
        }
    }
}