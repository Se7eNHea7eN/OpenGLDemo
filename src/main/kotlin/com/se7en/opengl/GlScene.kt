package com.se7en.opengl

open class GlScene {
    companion object {
        var currentScene: GlScene? = null
    }

    val mainCamera = GlCamera()
    protected val objects = ArrayList<GlObject>()

    private var lastDrawTime: Long = 0
    init {
        currentScene = this
    }
    open fun onWindowSizeChanged(width: Int, height: Int) {
        mainCamera.onWindowSizeChanged(width, height)
        lastDrawTime = System.currentTimeMillis()
    }

    open fun draw() {
        update(System.currentTimeMillis() - lastDrawTime)
        lastDrawTime = System.currentTimeMillis()
        mainCamera.render(objects)
    }

    open fun updateControls(keyDown: BooleanArray) {

    }

    protected open fun update(deltaTime: Long) {
        objects.forEach {
            it.update(deltaTime)
        }
    }

    fun addObject(obj: GlObject) {
        objects.add(obj)
    }

    open fun destroy(){
        objects.forEach {
            it.onDestroy()
        }
    }
}