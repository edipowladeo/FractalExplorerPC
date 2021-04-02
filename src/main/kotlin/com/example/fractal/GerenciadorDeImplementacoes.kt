package com.example.fractal

interface GerenciadorDeImplementacoes {
    fun relogio():Relogio
    fun texture():TextureWrapper
    fun bufferTexture(largura: Int, altura: Int, drawer:(Float,Float)-> TipoCor):TextureWrapper
    fun bufferTexture(largura: Int, altura: Int, iteracoes: ArrayIteracoes):TextureWrapper
    fun uniformBufferObject():TextureWrapper
}