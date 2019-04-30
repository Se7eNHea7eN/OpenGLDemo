package com.se7en.opengl

open class GlScene {
    companion object {
        var currentScene: GlScene? = null
    }

    val mainCamera = GlCamera()
    protected val objects = ArrayList<GlObject>()
    val pointLights = ArrayList<GlPointLight>()
    val directionLights = ArrayList<GlDirectionLight>()
    private var lastDrawTime: Long = 0
    init {
        currentScene = this
    }
    fun onWindowSizeChanged(width: Int, height: Int) {
        mainCamera.onWindowSizeChanged(width, height)
        lastDrawTime = System.currentTimeMillis()
    }

    fun draw() {
        update(System.currentTimeMillis() - lastDrawTime)
        lastDrawTime = System.currentTimeMillis()
        mainCamera.render(objects)
    }

    protected open fun update(deltaTime: Long) {
        objects.forEach {
            it.update(deltaTime)
        }
    }

    fun addObject(obj: GlObject) {
        objects.add(obj)
        if(obj is GlPointLight)
            pointLights.add(obj)
        else if (obj is GlDirectionLight)
            directionLights.add(obj)
    }

    fun destroy(){
        objects.forEach {
            it.onDestroy()
        }
    }
}