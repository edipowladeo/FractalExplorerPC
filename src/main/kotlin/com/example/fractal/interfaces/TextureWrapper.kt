package com.example.fractal.interfaces

import java.nio.ByteBuffer

interface TextureWrapper {

    fun bind()
    fun createOGLTexture()
    fun getHandle():Int
    fun liberarRecursos()
    fun validar()
    fun marcarComoInvalida()
    fun estaValida():Boolean
    fun marcarComoJaDestruiu() // experimental
    fun lersejadestriu():Boolean

}