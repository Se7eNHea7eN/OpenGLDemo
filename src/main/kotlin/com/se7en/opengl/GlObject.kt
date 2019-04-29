package com.se7en.opengl

open class GlObject {
    val transform = Transform()
    init {
        GlScene.currentScene?.addObject(this)
    }
    open fun update(){
    }

    open fun onDestroy(){
    }
}