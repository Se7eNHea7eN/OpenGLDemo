package com.se7en.opengl

import com.se7en.opengl.input.Input
import com.se7en.opengl.test.ShadowTestScene

import org.lwjgl.glfw.Callbacks.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwSwapBuffers

import org.lwjgl.glfw.GLFW.glfwWindowShouldClose

import org.lwjgl.glfw.GLFW.glfwShowWindow
import org.lwjgl.glfw.GLFW.glfwSwapInterval
import org.lwjgl.glfw.GLFW.glfwMakeContextCurrent

import org.lwjgl.glfw.GLFW.glfwSetWindowPos
import org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor
import org.lwjgl.glfw.GLFW.glfwGetVideoMode

import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose
import org.lwjgl.glfw.GLFW.GLFW_RELEASE
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import org.lwjgl.glfw.GLFW.glfwSetKeyCallback
import org.lwjgl.glfw.GLFW.GLFW_TRUE
import org.lwjgl.glfw.GLFW.GLFW_RESIZABLE
import org.lwjgl.glfw.GLFW.glfwWindowHint
import org.lwjgl.glfw.GLFW.GLFW_FALSE
import org.lwjgl.glfw.GLFW.GLFW_VISIBLE
import org.lwjgl.glfw.GLFW.glfwDefaultWindowHints
import org.lwjgl.glfw.GLFW.glfwInit
import org.lwjgl.glfw.GLFWCursorPosCallback
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL.createCapabilities


class GlApp {
    private var window: Long = 0
    private val windowWidth = 1280
    private val windowHeight = 720

    private var width = windowWidth
    private var height = windowHeight

    private var isFullScreen = false

    private var currentScene:GlScene ? = null

    private val input = Input()
    fun run() {
        System.out.println("Se7en's OpenGL Started!")

        init()
        loop()

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window)
        glfwDestroyWindow(window)

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null)?.free()
    }

    private fun init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw IllegalStateException("Unable to initialize GLFW")

        // Configure GLFW
        glfwDefaultWindowHints() // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable
        glfwWindowHint(GLFW_SAMPLES, 4)

        val primaryMonitor = glfwGetPrimaryMonitor()
        val vidmode = glfwGetVideoMode(primaryMonitor)

        // Create the window
        window = if (isFullScreen) {
            width = vidmode!!.width()
            height = vidmode.height()
            glfwCreateWindow(width, height, "OpenGL", primaryMonitor, NULL)
        } else {
            width = windowWidth
            height = windowHeight
            glfwCreateWindow(width, height, "OpenGL", NULL, NULL)
        }

        if (window == NULL)
            throw RuntimeException("Failed to create the GLFW window")

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window) { window, key, scancode, action, mods ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true) // We will detect this in the rendering loop
        }

        // Get the thread stack and push a new frame
        stackPush().use { stack ->
            val pWidth = stack.mallocInt(1) // int*
            val pHeight = stack.mallocInt(1) // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight)

            // Get the resolution of the primary monitor

            // Center the window
            glfwSetWindowPos(
                window,
                (vidmode!!.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            )
        } // the stack frame is popped automatically
        glfwSetKeyCallback(window, object : GLFWKeyCallback() {
            override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
                if (key == GLFW_KEY_UNKNOWN)
                    return
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, true)
                }
                input.keyDown[key] = action == GLFW_PRESS || action == GLFW_REPEAT
            }
        })
        glfwSetCursorPosCallback(window, object : GLFWCursorPosCallback() {
            override fun invoke(window: Long, xpos: Double, ypos: Double) {
                input.mouseX = xpos
                input.mouseY = ypos
            }
        })
        // Make the OpenGL context current
        glfwMakeContextCurrent(window)
        // Enable v-sync
        glfwSwapInterval(1)

        // Make the window visible
        glfwShowWindow(window)
    }

    private fun switchFullScreen() {
        isFullScreen = !isFullScreen

        val primaryMonitor = glfwGetPrimaryMonitor()
        val vidmode = glfwGetVideoMode(primaryMonitor)

        if (isFullScreen) {
            width = vidmode!!.width()
            height = vidmode.height()
            glfwSetWindowMonitor(window, primaryMonitor, 0, 0, width, height, 60)
        } else {
            width = windowWidth
            height = windowHeight
            glfwSetWindowMonitor(window, NULL,
                (vidmode!!.width() - width) / 2,
                (vidmode.height() - height) / 2,
                width, height, 60)
        }
        currentScene?.onWindowSizeChanged(width, height)
    }

    private fun loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        createCapabilities()
        currentScene = createScene()

        currentScene?.onWindowSizeChanged(width, height)
        var lastUpdateTime: Long = 0

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            if (input.keyDown[GLFW_KEY_F11]) {
                input.keyDown[GLFW_KEY_F11] = false
                switchFullScreen()
            }
            val time = System.currentTimeMillis()
            val deltaTime = time - lastUpdateTime
            currentScene?.update(deltaTime)
            currentScene?.updateControls(deltaTime,input,width,height)
            currentScene?.draw()

            glfwSwapBuffers(window) // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents()
            lastUpdateTime = time
        }
        currentScene?.destroy()
        currentScene = null
    }

    protected open fun createScene(): GlScene = ShadowTestScene()
}