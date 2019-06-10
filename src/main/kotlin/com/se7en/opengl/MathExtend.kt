package com.se7en.opengl

import org.joml.Quaternionf
import org.joml.Vector3f

fun Vector3f.toFloatArray(): FloatArray = floatArrayOf(x, y, z)

operator fun Vector3f.plus(other: Vector3f): Vector3f = this.add(other, Vector3f())
operator fun Vector3f.minus(other: Vector3f): Vector3f = this.sub(other,Vector3f())

operator fun Vector3f.times(factor: Float): Vector3f = this.mul(factor,Vector3f())

operator fun Vector3f.times(other: Vector3f): Vector3f = this.mul(other,Vector3f())

operator fun Float.times(vector3f: Vector3f): Vector3f = vector3f * this