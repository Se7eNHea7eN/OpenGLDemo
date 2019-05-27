package com.se7en.opengl.geometry

object SkyBoxCube {

    val _vertices = floatArrayOf(
        // Bottom face
        1.0f, -1f, -1.0f,    //0
        1.0f, -1f, 1.0f,     //1
        -1.0f, -1f, -1.0f,   //2
        -1.0f, -1f, 1.0f,     //3

        // Top face
        1.0f, 1f, -1.0f,    //0
        1.0f, 1f, 1.0f,     //1
        -1.0f, 1f, -1.0f,   //2
        -1.0f, 1f, 1.0f     //3

        // Left face
        , -1.0f, 1.0f, -1.0f,//4
        -1.0f, -1.0f, -1.0f,//5
        -1.0f, 1.0f, 1.0f,  //6
        -1.0f, -1.0f, 1.0f  //7
        // Right face
        , 1.0f, 1.0f, 1.0f,  //8
        1.0f, -1.0f, 1.0f,  //9
        1.0f, 1.0f, -1.0f,  //10
        1.0f, -1.0f, -1.0f //11
        // Front
        , 1.0f, -1.0f, -1.0f,  //8
        1.0f, 1.0f, -1.0f,  //9
        -1.0f, -1.0f, -1.0f,  //10
        -1.0f, 1.0f, -1.0f //11
    )

    val _normals = floatArrayOf(
        // Bottom face
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f

       , 0.0f, -1.0f, 0.0f,
        0.0f, -1.0f, 0.0f,
        0.0f, -1.0f, 0.0f,
        0.0f, -1.0f, 0.0f
//            // Left face
        , 1.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f
//            // Right face
        , -1.0f, 0.0f, 0.0f,
        -1.0f, 0.0f, 0.0f,
        -1.0f, 0.0f, 0.0f,
        -1.0f, 0.0f, 0.0f
        // Front
        , 0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f
    )

    val _indices = intArrayOf(
        // Bottom face
        0, 1, 2, 1, 3, 2
        , 4, 5, 6, 5, 7, 6
        , 8, 9, 10, 9, 11, 10
        , 12, 13, 14, 13, 15, 14
        , 15 ,16 ,17 ,16 ,18 ,19
    )

    val _texCoords = floatArrayOf(
        1.0f, 0.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        0.0f, 1.0f,

        1.0f, 0.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        0.0f, 1.0f,

        1.0f, 0.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        0.0f, 1.0f,

        1.0f, 0.0f,
        1.0f, 0f,
        0.0f, 0.0f,
        0.0f, 0.0f,

        1.0f, 0.0f,
        1.0f, 0f,
        0.0f, 0.0f,
        0.0f, 0.0f,

        1.0f, 0.0f,
        1.0f, 0f,
        0.0f, 0.0f,
        0.0f, 0.0f
    )

}