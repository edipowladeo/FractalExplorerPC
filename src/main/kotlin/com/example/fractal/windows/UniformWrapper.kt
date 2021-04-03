package com.example.fractal.windows

import org.lwjgl.opengl.GL20.glGetAttribLocation
import org.lwjgl.opengl.GL20.glUniform1i
import org.lwjgl.opengl.GL32.glUniform2i

class UniformWrapperI(var uniformName: String, programHandle: Int) {

    val handle = glGetAttribLocation(programHandle, uniformName)

    fun setValue(value: Int){
        glUniform1i(handle, value)
    }

}