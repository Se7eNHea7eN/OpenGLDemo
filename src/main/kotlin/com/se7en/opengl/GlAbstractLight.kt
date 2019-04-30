package com.se7en.opengl

import org.joml.Vector3f

abstract class GlAbstractLight :GlObject() {
    var lightColor = Vector3f(1f, 1f, 1f)
    var intensive = 1f
}