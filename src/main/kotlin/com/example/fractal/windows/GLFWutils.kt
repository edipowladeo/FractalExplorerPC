package com.example.fractal.windows

import com.example.fractal.CoordenadasTelai
import com.example.fractal.WindowHandle
import org.lwjgl.glfw.GLFW

fun getDesktopResolution(): CoordenadasTelai{
    val videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())!!    // Get the resolution of the primary monitor

    return CoordenadasTelai(videoMode.width(),videoMode.height())
}

fun getOGLWindowResolution(windowHandle: WindowHandle): CoordenadasTelai{
    val x  = IntArray(1)
    val y  = IntArray(1)

    GLFW.glfwGetWindowSize(windowHandle, x, y)

    return CoordenadasTelai(x[0], y[0])
}