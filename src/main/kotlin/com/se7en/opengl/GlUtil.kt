package com.se7en.opengl

import com.se7en.opengl.utils.Debug
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
import java.lang.Exception
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Some OpenGL static utility functions.
 */
object GlUtil {

    // Assert that no OpenGL ES 2.0 error has been raised.
    fun checkNoGLES2Error(msg: String) {
        val error = glGetError()
        if (error != GL_NO_ERROR) {
            throw Exception("$msg: GLES20 error: $error")
//            Debug.log("GlUtil","$msg: GLES20 error: $error")
        }
    }

    fun createFloatBuffer(coords: FloatArray): FloatBuffer {
        // Allocate a direct ByteBuffer, using 4 bytes per float, and copy coords into it.
        val bb = ByteBuffer.allocateDirect(coords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        val fb = bb.asFloatBuffer()
        fb.put(coords)
        fb.position(0)
        return fb
    }

    /**
     * Generate texture with standard parameters.
     */
    fun generateTexture(target: Int): Int {
        val textureArray = IntArray(1)
        glGenTextures(textureArray)
        val textureId = textureArray[0]
        glBindTexture(target, textureId)
        glTexParameterf(target, GL_TEXTURE_MIN_FILTER, GL_LINEAR.toFloat())
        glTexParameterf(target, GL_TEXTURE_MAG_FILTER, GL_LINEAR.toFloat())
        glTexParameterf(target, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE.toFloat())
        glTexParameterf(target, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE.toFloat())
        checkNoGLES2Error("generateTexture")

        return textureId
    }

    fun generateFrameBuffer(textureId: Int): Int {
        val frameBufferArray = IntArray(1)
        glGenFramebuffers(frameBufferArray)
        val frameBufferId = frameBufferArray[0]
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId, 0)
        val status = glCheckFramebufferStatus(GL_FRAMEBUFFER)

        if (status != GL_FRAMEBUFFER_COMPLETE) {
            return -1
        }

        checkNoGLES2Error("generateFrameBuffer")
        return frameBufferId
    }

//    fun createTextureFromBitmap(bitmap: Bitmap): Int {
//        val textures = IntArray(1)
//        glGenTextures(1, textures, 0)
//        //生成纹理
//        glBindTexture(GL_TEXTURE_2D, textures[0])
//        //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
//        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST.toFloat())
//        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
//        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR.toFloat())
//        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
//        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE.toFloat())
//        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
//        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE.toFloat())
//        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
//        return textures[0]
//    }

//
//    fun initEffectTexture(width: Int, height: Int, textureId: IntArray, type: Int): Int {
//        glGenTextures(1, textureId, 0)
//        glBindTexture(type, textureId[0])
//        glTexParameterf(type, GL_TEXTURE_MAG_FILTER, GL_LINEAR.toFloat())
//        glTexParameterf(type, GL_TEXTURE_MIN_FILTER, GL_LINEAR.toFloat())
//        glTexParameterf(type, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE.toFloat())
//        glTexParameterf(type, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE.toFloat())
//        glTexImage2D(type, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null)
//        return textureId[0]
//    }


    fun bindFrameBuffer(textureId: Int, frameBuffer: Int, width: Int, height: Int) {
        glBindTexture(GL_TEXTURE_2D, textureId)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
            GL_RGBA, GL_UNSIGNED_BYTE,0)
        glTexParameterf(GL_TEXTURE_2D,
            GL_TEXTURE_MAG_FILTER, GL_LINEAR.toFloat())
        glTexParameterf(GL_TEXTURE_2D,
            GL_TEXTURE_MIN_FILTER, GL_LINEAR.toFloat())
        glTexParameterf(GL_TEXTURE_2D,
            GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE.toFloat())
        glTexParameterf(GL_TEXTURE_2D,
            GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE.toFloat())

        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
            GL_TEXTURE_2D, textureId, 0)

    }

}