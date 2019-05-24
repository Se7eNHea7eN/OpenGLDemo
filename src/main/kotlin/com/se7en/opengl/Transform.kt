package com.se7en.opengl

import org.joml.Quaternionf
import org.joml.Vector3f
import org.joml.Matrix4f

class Transform {
    var parent: Transform? = null

    var localPosition = Vector3f(0f, 0f, 0f)
    var localRotation = Quaternionf()
    var localScale = Vector3f(1f, 1f, 1f)
    //
    fun up(): Vector3f {
        return localRotation.normalizedPositiveY(Vector3f())
    }

    fun down(): Vector3f {
        return up().negate()
    }

    fun forward(): Vector3f {
        return localRotation.normalizedPositiveZ(Vector3f())
    }

    fun backward(): Vector3f {
        return forward().negate()
    }

    fun right(): Vector3f {
        return localRotation.normalizedPositiveX(Vector3f())
    }

    fun left(): Vector3f {
        return right().negate()
    }

    fun lookAt(target: Vector3f) {
        localRotation = localRotation.rotationTo(Vector3f(0f, 0f, 1f), localPosition - target)
    }

    //
    fun matrix(): Matrix4f {
        val baseMatrix = if (parent != null) parent!!.matrix() else Matrix4f()

        return baseMatrix
            .rotate(localRotation)
            .translate(localPosition)
            .scale(localScale)
    }

}