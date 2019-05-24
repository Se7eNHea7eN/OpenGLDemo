package com.se7en.opengl.geometry

import com.se7en.opengl.GlMeshObject
import com.se7en.opengl.Mesh
import java.lang.Math.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

abstract class Sphere : GlMeshObject() {
    override fun createMesh(): Mesh {
        return Mesh().apply {
            val radius = 1f
            val stackCount = 24
            val sectorCount = 72

            vertices = ByteBuffer.allocateDirect((stackCount +1) * (sectorCount +1) * 3 * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer()
            normals = ByteBuffer.allocateDirect((stackCount +1) * (sectorCount +1) * 3 * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer()
            texCoords =
                ByteBuffer.allocateDirect((stackCount  +1)* (sectorCount +1) * 2 * 4).order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
            indices =
                ByteBuffer.allocateDirect(stackCount * (sectorCount -1) * 3 *2 * 4).order(ByteOrder.nativeOrder())
                    .asIntBuffer()

            val sectorStep = 2 * PI / sectorCount
            val stackStep = PI / stackCount
            var sectorAngle: Double
            val lengthInv = 1.0f / radius
            for (i in 0..stackCount) {
                val stackAngle = PI / 2 - i * stackStep        // starting from pi/2 to -pi/2
                val xy = radius * cos(stackAngle)            // r * cos(u)
                val z = radius * sin(stackAngle).toFloat()               // r * sin(u)
                for (j in 0..sectorCount) {

                    sectorAngle = j * sectorStep           // starting from 0 to 2pi

                    // vertex localPosition (x, y, z)
                    val x = (xy * cos(sectorAngle)).toFloat()         // r * cos(u) * cos(v)
                    val y = (xy * sin(sectorAngle)).toFloat()           // r * cos(u) * sin(v)
                    vertices!!.put(x)
                    vertices!!.put(y)
                    vertices!!.put(z)

                    // normalized vertex normal (nx, ny, nz)
                    val nx = x * lengthInv
                    val ny = y * lengthInv
                    val nz = z * lengthInv
                    normals!!.put(nx)
                    normals!!.put(ny)
                    normals!!.put(nz)

                    // vertex tex coord (s, t) range between [0, 1]
                    val s = j.toFloat() / sectorCount
                    val t = i.toFloat() / stackCount
                    texCoords!!.put(s)
                    texCoords!!.put(t)
                }
            }
            var k1 = 0
            var k2 = 0
            for (i in 0 until stackCount) {
                k1 = i * (sectorCount + 1)     // beginning of current stack
                k2 = k1 + sectorCount + 1      // beginning of next stack

                var j = 0
                while (j < sectorCount) {
                    // 2 triangles per sector excluding first and last stacks
                    // k1 => k2 => k1+1
                    if (i != 0) {
                        indices!!.put(k1)
                        indices!!.put(k2)
                        indices!!.put(k1 + 1)
                    }

                    // k1+1 => k2 => k2+1
                    if (i != stackCount - 1) {
                        indices!!.put(k1 + 1)
                        indices!!.put(k2)
                        indices!!.put(k2 + 1)
                    }
                    ++j
                    ++k1
                    ++k2
                }
            }

            vertices!!.position(0)
            normals!!.position(0)
            texCoords!!.position(0)
            indices!!.position(0)
        }
    }
}
