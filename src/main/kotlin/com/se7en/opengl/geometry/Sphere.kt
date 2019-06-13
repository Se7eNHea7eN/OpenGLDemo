package com.se7en.opengl.geometry

import com.se7en.opengl.GlMeshObject
import com.se7en.opengl.Mesh
import com.se7en.opengl.toFloatArray
import com.se7en.opengl.utils.Debug
import org.joml.Vector3f
import java.lang.Math.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

abstract class Sphere : GlMeshObject() {
    override fun createMesh(): Mesh {
        return Mesh().apply {

            val X_SEGMENTS = 64
            val Y_SEGMENTS = 64

            numVertices = (Y_SEGMENTS+1) * (X_SEGMENTS +1)

            vertices = ByteBuffer.allocateDirect(numVertices * 3 * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer()
            normals = ByteBuffer.allocateDirect(numVertices * 3 * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer()
            texCoords =
                ByteBuffer.allocateDirect(numVertices * 2 * 4).order(ByteOrder.nativeOrder())
                    .asFloatBuffer()


            indices =
                ByteBuffer.allocateDirect(Y_SEGMENTS *(X_SEGMENTS +1) * 2 * 4).order(ByteOrder.nativeOrder())
                    .asIntBuffer()

            numVertices *= 4
            for(y in 0 ..Y_SEGMENTS )
                for (x in 0..X_SEGMENTS){
                    val xSegment = x.toFloat() / X_SEGMENTS
                    val ySegment = y.toFloat() / Y_SEGMENTS
                    val xPos = (cos(xSegment * 2.0f * PI) * sin(ySegment * PI)).toFloat()
                    val yPos = cos(ySegment * PI).toFloat()
                    val zPos = (sin(xSegment * 2.0f * PI) * sin(ySegment * PI)).toFloat()
                    vertices!!.put(xPos)
                    vertices!!.put(yPos)
                    vertices!!.put(zPos)
                    texCoords!!.put(xSegment)
                    texCoords!!.put(ySegment)
                    normals!!.put(xPos)
                    normals!!.put(yPos)
                    normals!!.put(zPos)
                }
            var oddRow = false
            for(y in 0 until Y_SEGMENTS){
                if (!oddRow) // even rows: y == 0, y == 2; and so on
                {
                    for(x in 0 .. X_SEGMENTS){
                        indices!!.put(y       * (X_SEGMENTS + 1) + x)
                        indices!!.put((y + 1) * (X_SEGMENTS + 1) + x)
                    }
                }
                else
                {
                    for (x in X_SEGMENTS downTo  0){
                        indices!!.put((y + 1) * (X_SEGMENTS + 1) + x)
                        indices!!.put(y       * (X_SEGMENTS + 1) + x)

                    }
                }
                oddRow = !oddRow
            }
            vertices!!.position(0)
            normals!!.position(0)
            texCoords!!.position(0)
            indices!!.position(0)
        }
    }
}
