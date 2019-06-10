package com.se7en.opengl

import java.nio.FloatBuffer
import java.nio.IntBuffer

class Mesh {
    var vertices: FloatBuffer? = null
    var normals: FloatBuffer? = null
    var indices: IntBuffer? = null
    var texCoords : FloatBuffer? = null
    var numVertices: Int = 0
}