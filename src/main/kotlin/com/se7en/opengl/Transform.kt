package com.se7en.opengl

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

    fun down(): Vector3f {
        return up().negate()
    }

    fun forward(): Vector3f {
        return rotation.normalizedPositiveZ(Vector3f())
    }

    fun backward(): Vector3f{
        return forward().negate()
    }

    fun right(): Vector3f {
        return rotation.normalizedPositiveX(Vector3f())
    }

    fun left(): Vector3f{
        return right().negate()
    }

    fun lookAt(target: Vector3f) {
        rotation = rotation.rotationTo(Vector3f(0f, 0f, 1f), (position - target).normalize())
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