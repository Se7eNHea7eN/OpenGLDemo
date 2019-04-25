package com.opengles_demo

import org.joml.Quaternionf
import org.joml.Vector3f
import org.joml.Matrix4f


class Transform {
    var position = Vector3f(0f, 0f, 0f)
    var rotation = Quaternionf()
    var scale = Vector3f(1f, 1f, 1f)
    //
    fun up(): Vector3f {
        return rotation.normalizedPositiveY(Vector3f())
    }

    fun forward(): Vector3f {
        return rotation.normalizedPositiveZ(Vector3f())
    }

    //
    fun matrix(): Matrix4f {
        return Matrix4f()
            .rotate(rotation)
            .translate(position)
            .scale(scale)
    }

//
//    fun worldPosition(): Vector3 {
//        return position.rotate(rotation) * scale
//    }
}