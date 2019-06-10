package com.se7en.opengl

open class GlObject {
    var isActive = true
    val transform = Transform()
    init {
        GlScene.currentScene?.addObject(this)
    }
    open fun update(deltaTime: Long){
    }

    open fun onDestroy(){
    }
}