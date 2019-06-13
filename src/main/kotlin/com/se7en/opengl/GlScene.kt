package com.se7en.opengl

import com.se7en.opengl.input.Input

open class GlScene {
    var timeSinceStart = 0L
    companion object {
        var currentScene: GlScene? = null
    }

    val mainCamera = GlCamera()
    protected val objects = ArrayList<GlObject>()

    init {
        currentScene = this
    }

    open fun onWindowSizeChanged(width: Int, height: Int) {
        mainCamera.onWindowSizeChanged(width, height)
    }

    open fun draw() {
        mainCamera.render(objects)
    }

    open fun updateControls(deltaTime: Long, input: Input, width: Int, height: Int) {
    }

    open fun update(deltaTime: Long) {
        timeSinceStart += deltaTime
        objects.forEach {
            it.update(deltaTime)
        }
    }

    fun addObject(obj: GlObject) {
        objects.add(obj)
    }

    open fun destroy() {
        objects.forEach {
            it.onDestroy()
        }
    }
}