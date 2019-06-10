package com.se7en.opengl.testscene

import com.se7en.opengl.GlScene
import com.se7en.opengl.GlSkyBox
import com.se7en.opengl.input.Input
import org.lwjgl.opengl.GL11.*

class SkyBoxTest : GlScene() {
    private val skybox = object : GlSkyBox() {
        override fun skyBoxTextures(): Array<String> =
            arrayOf(
                "textures/space/space_right1.jpg",
                "textures/space/space_left2.jpg",
                "textures/space/space_top3.jpg",
                "textures/space/space_bottom4.jpg",
                "textures/space/space_front5.jpg",
                "textures/space/space_back6.jpg"
            )
    }
    init {

        glEnableClientState(GL_VERTEX_ARRAY)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE)
    }
    var mouseXLastFrame = 0.0
    var mouseYLastFrame = 0.0
    override fun updateControls(deltaTime: Long, input: Input, width: Int, height: Int) {
        super.updateControls(deltaTime, input, width, height)
        if (mouseXLastFrame != 0.0 && mouseYLastFrame != 0.0) {
            val deltaX = input.mouseX - mouseXLastFrame
            val deltaY = input.mouseY - mouseYLastFrame

            mainCamera.transform.localRotation.rotateAxis((deltaY / width).toFloat(), mainCamera.transform.left())
            mainCamera.transform.localRotation.rotateAxis((deltaX / width).toFloat(), mainCamera.transform.up())
//            mainCamera.transform.localRotation.getEulerAnglesXYZ(Vector3f()).apply {
//                mainCamera.transform.localRotation.rotateAxis(z,mainCamera.transform.backward())
//            }
        }

        mouseXLastFrame = input.mouseX
        mouseYLastFrame = input.mouseY
    }
}