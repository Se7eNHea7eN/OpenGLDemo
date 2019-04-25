package com.se7en.opengl

import org.lwjgl.*

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
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.opengl.GL11.*


class GlApp {
    private var window: Long = 0
    private val width = 1280
    private val height = 720
    fun run(){
        System.out.println("Hello LWJGL " + Version.getVersion() + "!")

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

        // Create the window
        window = glfwCreateWindow(1280, 720, "OpenGL", NULL, NULL)
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
            val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

            // Center the window
            glfwSetWindowPos(
                window,
                (vidmode!!.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            )
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window)
        // Enable v-sync
        glfwSwapInterval(1)

        // Make the window visible
        glfwShowWindow(window)
    }

    private fun loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        val scene = GlScene()
        createCapabilities()
        scene.onWindowSizeChanged(width,height)
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            scene.draw()

            glfwSwapBuffers(window) // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents()
        }
    }

}