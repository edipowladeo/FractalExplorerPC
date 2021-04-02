package com.example.fractal.windows


import org.lwjgl.opengl.GL32 as OGL

//todo: make interface
class MyGLProgramPC(vertexShaderName:String, fragShaderName:String) {

    private val vertexShaderCode = lerArquivoPC(vertexShaderName)
    private val fragmentShaderCode = lerArquivoPC(fragShaderName)

    private var programHandle: Int

    init {
        val vertexShader: Int = loadShader(OGL.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(OGL.GL_FRAGMENT_SHADER, fragmentShaderCode)

        programHandle = OGL.glCreateProgram().also {
            OGL.glAttachShader(it, vertexShader)
            OGL.glAttachShader(it, fragmentShader)
            // creates OpenGL program executables
            OGL.glLinkProgram(it)
        }

        if  (programHandle == 0)
        {
            throw RuntimeException("Error creating program.");
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return OGL.glCreateShader(type).also { shader ->
            OGL.glShaderSource(shader, shaderCode)
            OGL.glCompileShader(shader)
            val error = OGL.glGetError()
           // if (error != 0){
           //     println("Error Compiling Shader")
                val shaderLog = OGL.glGetShaderInfoLog(shader)
                println(shaderLog)

           // }

        }
    }

    fun getProgramHandle(): Int {
        return programHandle
    }

}