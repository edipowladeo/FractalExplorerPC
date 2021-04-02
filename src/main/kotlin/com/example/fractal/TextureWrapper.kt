package com.example.fractal

import java.nio.ByteBuffer

interface TextureWrapper {

    fun bind()
//    fun setDoubleArray( Iteracoes:ArrayIteracoes)
    fun populateBufferFromDoubleArray()
 //   fun populateBufferFromDoubleArray(iteracoes: ArrayIteracoes)
    fun createTextureFromBuffer()
    fun getTextureHandle():Int
    fun liberarRecursos()
    fun validar()
    fun invalidar()
    fun estaValida():Boolean
    fun marcarComoJaDestruiu() // experimental
   fun   lersejadestriu():Boolean



    fun fillTextureFromBuffer(buffer: ByteBuffer)
    fun desalocarTexturasGL()


}