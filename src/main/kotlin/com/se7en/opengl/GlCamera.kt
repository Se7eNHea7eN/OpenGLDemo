package com.se7en.opengl

import asiainnovations.com.opengles_demo.GlShader
import com.asiainnovations.onlyu.video.gl.TextureRotationUtil
import com.se7en.opengl.utils.ResourceUtils
import org.joml.Matrix4f
import org.lwjgl.opengl.GL41.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

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

    var zFar = 1000.0f
        set(value) {
            field = value
            recalculateProjectionMatrix()
        }

    var projectionMatrix = Matrix4f()

    init {

    }

    fun onWindowSizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height
        glClearDepthf(1.0f)
//        glEnable(GL_CULL_FACE)
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)
        recalculateProjectionMatrix()
    }

    private fun recalculateProjectionMatrix() {
        projectionMatrix = Matrix4f()
            .perspective(Math.toRadians(fov.toDouble()).toFloat(), width.toFloat() / height, zNear, zFar)
    }

    fun render(objects: List<GlObject>) {
        renderScene(objects)
    }

    private fun renderScene(objects: List<GlObject>) {
        glClearColor(0f, 0f, 0f, 0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glViewport(0, 0, width, height)

        val viewMatrix = Matrix4f().setLookAt(
            transform.position, transform.forward(), transform.up()
        )

        objects.forEach {
            if (it is GlRenderObject) {
                it.material.setLights(objects.filter { o -> o is GlAbstractLight } as List<GlAbstractLight>)
                it.material.eyePos = transform.position
                it.material.setProjectionMatrix(projectionMatrix.get(FloatArray(16)))
                it.material.setViewMatrix(viewMatrix.get(FloatArray(16)))
                it.render()
            }
        }
    }

}