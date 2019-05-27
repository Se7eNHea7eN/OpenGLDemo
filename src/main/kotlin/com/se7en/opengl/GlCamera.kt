package com.se7en.opengl

import com.se7en.opengl.lighting.GlAbstractLight
import org.joml.Matrix4f
import org.lwjgl.opengl.GL41.*


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
//        objects.filter { it is GlAbstractLight }.forEach {
//            (it as GlAbstractLight).renderDepthTexture(width,height)
//        }
    }

    private fun renderScene(objects: List<GlObject>) {
        val viewMatrix = Matrix4f().setLookAt(
            transform.localPosition, transform.localPosition + transform.forward() , transform.up()
        )

        objects.filter { it is GlAbstractLight }.forEach {
            (it as GlAbstractLight).renderShadowMap(objects)
        }

        glClearColor(0f, 0f, 0f, 0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glViewport(0, 0, width, height)

        objects.forEach {
            if (it is GlRenderObject) {
                if(it.material.enableLighting)
                    it.material.setLights(objects.filter { o -> o is GlAbstractLight } as List<GlAbstractLight>)
                it.material.eyePos = transform.localPosition

                it.render(viewMatrix,projectionMatrix)
            }
        }
    }
}