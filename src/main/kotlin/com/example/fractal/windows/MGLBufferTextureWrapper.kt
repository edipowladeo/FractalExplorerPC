package com.example.fractal.windows

import com.example.fractal.ArrayIteracoes
import org.lwjgl.opengl.GL32

import com.example.fractal.TextureWrapper
import com.example.fractal.TipoCor
import com.example.fractal.dummyTexture
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**x e y da funcao drawer deve ir de -0.5 até 0.5*/
class MGlBufferTextureWrapper(val largura:Int, val altura:Int):TextureWrapper {

    var possuiTexturaValida = false;
    var jadestruiu = false


    override fun marcarComoJaDestruiu(){
        jadestruiu = true
    }

    override fun lersejadestriu():Boolean{
        return         jadestruiu }

    override fun fillTextureFromBuffer(buffer: ByteBuffer) {
        TODO("Not yet implemented")
    }

    override fun desalocarTexturasGL() {
        TODO("Not yet implemented")
    }

    var iteracoes: ArrayIteracoes? = null
    private val entriesPerPixel = 4
    private val bytesPerEntry = 1

    var drawer:(Float, Float)-> TipoCor = dummyTexture()


    var dataArray: ByteArray? = null

    private var bufferHandle = IntArray(1){0}

    init {

    }

    constructor(largura:Int,altura:Int, Drawer:(Float,Float)-> TipoCor) : this(largura,altura) {
        drawer = Drawer
        populateByteArrayUsingDrawerFunction()
        //   populateBufferFromByteArray()
        //createTextureFromBuffer()
    }

    constructor(largura:Int,altura:Int,Iteracoes: ArrayIteracoes) : this(largura,altura) {
        iteracoes = Iteracoes;
    }

    override fun invalidar() {
        possuiTexturaValida = false
    }

    override fun validar() {
        possuiTexturaValida = true
    }

    override fun estaValida():Boolean {
        return possuiTexturaValida
    }

    //TODO: muita coisa aqui nem sei pra que serve
    fun createTextureFromBuffer(dataBuffer:IntArray):Int {
        GL32.glGenBuffers(bufferHandle)
        if (bufferHandle[0] != 0) {
            GL32.glActiveTexture(GL32.GL_TEXTURE1)
            GL32.glBindBuffer(
                    GL32.GL_TEXTURE_BUFFER,
                    bufferHandle[0]
            );
            GL32.glBufferData(
                    GL32.GL_TEXTURE_BUFFER,
                    dataBuffer,
                    GL32.GL_STATIC_DRAW
            );
        }else{ throw RuntimeException("Error loading Buffer Opengl.")}
        return bufferHandle[0]
    }

    override fun createTextureFromBuffer() {
        iteracoes?.let {
            createTextureFromBuffer(it)
        }
    }

    override fun populateBufferFromDoubleArray(){

    }

    fun populateByteArrayUsingDrawerFunction(){
        dataArray= ByteArray(largura*altura*entriesPerPixel){0}.also{
            for (i in 0 until largura){
                for (j in 0 until altura){
                    val x = i.toFloat()/largura.toFloat()-0.5f
                    val y = j.toFloat()/altura.toFloat()-0.5f
                    val cor = drawer(x,y)
                    val pixelIndex = i + j*largura
                    val indiceByte = pixelIndex * entriesPerPixel
                    it[indiceByte] = 0


                }
            }}
    }

    override fun getTextureHandle():Int{
        return bufferHandle[0]
    }


    override fun bind(){
//          GL32.glBindBuffer(GL32.GL_TEXTURE_BUFFER, bufferHandle[0]);
        GL32.glActiveTexture(GL32.GL_TEXTURE1)
        GL32.glTexBuffer(GL32.GL_TEXTURE_BUFFER,GL32.GL_R32UI,bufferHandle[0])
    }

    override fun toString():String{
        return "Textura possui Handle GL :" + getTextureHandle().toString()
    }


    override fun liberarRecursos() {
        GL32.glDeleteBuffers(bufferHandle)
    }
}
