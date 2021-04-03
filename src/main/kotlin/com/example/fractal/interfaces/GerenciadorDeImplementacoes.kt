package com.example.fractal.interfaces

import com.example.fractal.TipoArrayIteracoes
import com.example.fractal.TipoCor

interface GerenciadorDeImplementacoes {
    fun relogio(): Relogio
    fun texture(): TextureWrapper
    fun bufferTexture(largura: Int, altura: Int, drawer:(Float,Float)-> TipoCor): TextureWrapper
    fun bufferTexture(largura: Int, altura: Int, iteracoes: TipoArrayIteracoes): TextureWrapper
    fun uniformBufferObject(): TextureWrapper
}