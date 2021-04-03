package com.example.fractal.windows

import com.example.fractal.CoordenadasTelai
import com.example.fractal.WindowHandle
import org.lwjgl.glfw.GLFW
import org.lwjgl.system.MemoryStack
import java.nio.IntBuffer

fun getDesktopResolution(): CoordenadasTelai{
    val vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())!!    // Get the resolution of the primary monitor

    return CoordenadasTelai(vidmode.width(),vidmode.height())
}

fun getOGLWindowResolution(windowHandle: WindowHandle): CoordenadasTelai{
    var x :IntArray = IntArray(1)
    var y :IntArray = IntArray(1)

    GLFW.glfwGetWindowSize(windowHandle, x, y)

    return CoordenadasTelai(x?.get(0)!!, y?.get(0)!!)
}