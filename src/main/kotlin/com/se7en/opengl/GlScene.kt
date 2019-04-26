package com.se7en.opengl

import org.lwjgl.opengl.GL11.*

open class GlScene {
    protected val camera = GlCamera()
    protected val objects = ArrayList<GlObject>()
    private var lastDrawTime: Long = 0

    fun onWindowSizeChanged(width: Int, height :Int) {
        camera.onWindowSizeChanged(width,height)
        lastDrawTime = System.currentTimeMillis()
    }

    fun draw() {
        update(System.currentTimeMillis() - lastDrawTime)
        lastDrawTime = System.currentTimeMillis()
        camera.render(objects)
    }

    protected open fun update(deltaTime:Long){
        objects.forEach {
            it.update()
        }
    }

    fun addObject(obj:GlObject){
        objects.add(obj)
    }
}