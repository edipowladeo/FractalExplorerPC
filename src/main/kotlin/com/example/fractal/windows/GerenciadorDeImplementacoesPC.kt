package com.example.fractal.windows

import com.example.fractal.*
import com.example.fractal.Relogio

class GerenciadorDeImplementacoesPC : GerenciadorDeImplementacoes {
    override fun relogio(): Relogio = com.example.fractal.windows.Relogio()

    override fun texture(): TextureWrapper{
        TODO("Not yet implemented") //TextureWrapper = MGlTextureWrapper()
    }

    override fun bufferTexture(largura: Int, altura: Int, drawer: (Float, Float) -> TipoCor): TextureWrapper = MGlBufferTextureWrapper(altura, largura, drawer)
    override fun bufferTexture(largura: Int, altura: Int, iteracoes: ArrayIteracoes): TextureWrapper = MGlBufferTextureWrapper(altura, largura, iteracoes)

    override fun uniformBufferObject(): TextureWrapper {
        TODO("Not yet implemented")
    }

}