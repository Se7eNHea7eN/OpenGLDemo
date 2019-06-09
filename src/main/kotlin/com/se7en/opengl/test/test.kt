package com.se7en.opengl.test

import com.se7en.opengl.GlApp
import com.se7en.opengl.Transform
import com.se7en.opengl.utils.Debug
import org.joml.Vector3f


fun main(args: Array<String>) {
    val parent = Transform()
    val transform = Transform()
    transform.localPosition = Vector3f(0f,100f,0f)
    transform.parent = parent;
    parent.localRotation.rotateLocalZ(Math.toRadians(180.0).toFloat())
    Debug.log(transform.position.toString())
}