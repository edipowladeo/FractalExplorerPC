package com.example.fractal.windows

import com.example.fractal.TipoArrayIteracoes
import org.lwjgl.opengl.GL32

import com.example.fractal.interfaces.TextureWrapper
import com.example.fractal.TipoCor
import com.example.fractal.dummyTexture
import java.lang.Math.sin
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**x e y da funcao drawer deve ir de -0.5 até 0.5*/
class MGlTextureWrapper(val largura:Int, val altura:Int): TextureWrapper {

    var possuiTexturaValida = false;
    var jadestruiu = false


    override fun marcarComoJaDestruiu(){
        jadestruiu = true
    }

    override fun lersejadestriu():Boolean{
        return         jadestruiu }


    var iteracoes: TipoArrayIteracoes? = null
    private val entriesPerPixel = 4
    private val bytesPerEntry = 1

    var drawer:(Float, Float)-> TipoCor = dummyTexture()


    var dataArray: ByteArray? = null

    private var textureHandle = IntArray(1){0}

    init {

    }

    constructor(largura:Int,altura:Int, Drawer:(Float,Float)-> TipoCor) : this(largura,altura) {
        drawer = Drawer
        populateByteArrayUsingDrawerFunction()
        //   populateBufferFromByteArray()
        //createTextureFromBuffer()
    }

    constructor(largura:Int,altura:Int,Iteracoes: TipoArrayIteracoes) : this(largura,altura) {
        setDoubleArray( Iteracoes)
    }

    override fun marcarComoInvalida() {
        possuiTexturaValida = false
    }

    override fun validar() {
        possuiTexturaValida = true
    }

    override fun estaValida():Boolean {
        return possuiTexturaValida
    }

    fun setDoubleArray( Iteracoes: TipoArrayIteracoes)
    {
        iteracoes = Iteracoes
    }

    //TODO: muita coisa aqui nem sei pra que serve
    fun createTextureFromBuffer(dataBuffer:ByteBuffer):Int {

        GL32.glGenTextures(textureHandle);


        if (textureHandle[0] != 0) {
            GL32.glBindTexture(
                    GL32.GL_TEXTURE_2D,
                    textureHandle[0]
            )
            GL32.glTexImage2D(
                    GL32.GL_TEXTURE_2D,
                    0,
                    GL32.GL_RGBA,
                    largura,
                    altura,
                    0,
                    GL32.GL_RGBA,
                    GL32.GL_UNSIGNED_BYTE,
                    dataBuffer
            )
        }else{ throw RuntimeException("Error loading texture.") }

        return textureHandle[0]
    }

    override fun createOGLTexture(){
        populateBufferFromDoubleArray()
        dataArray?.let{
            var dataBuffer = ByteBuffer.allocateDirect(it.size * bytesPerEntry*entriesPerPixel)
                    .order(ByteOrder.nativeOrder())
      //      dataBuffer.position(0)
            dataBuffer.put(it).position(0)
            createTextureFromBuffer(dataBuffer)}
        dataArray = null
    }

    @ExperimentalUnsignedTypes
    fun populateBufferFromDoubleArray(){
        iteracoes?.let{
            populateBufferFromDoubleArray(it)
        }
    }


    @ExperimentalUnsignedTypes
    fun populateBufferFromDoubleArray(iteracoes: TipoArrayIteracoes){
        dataArray= ByteArray(largura*altura*entriesPerPixel*bytesPerEntry){0}.also{
            iteracoes.forEachIndexed{indice,valor ->
                val indiceNaTexutraByte = indice * entriesPerPixel
                val valorint = valor.toULong()

                var iteracoesFloat = valorint.toDouble()

                it[indiceNaTexutraByte] = (128.0f + 128f*sin(iteracoesFloat)).toInt().toByte()
                iteracoesFloat += 1f
                it[indiceNaTexutraByte+1] = (128.0f + 128f*sin(iteracoesFloat)).toInt().toByte()
                iteracoesFloat += 1f
                it[indiceNaTexutraByte+2] = (128.0f + 128f*sin(iteracoesFloat)).toInt().toByte()
                iteracoesFloat += 1f
                it[indiceNaTexutraByte+3] = (128.0f + 128f*sin(iteracoesFloat)).toInt().toByte()




/*                it[indiceNaTexutraByte] = valorint.toByte()
                it[indiceNaTexutraByte+1] = valorint.shr(8).toByte()
                it[indiceNaTexutraByte+2] = valorint.shr(16).toByte()
                it[indiceNaTexutraByte+3] = valorint.shr(24).toByte() */




            }
        }
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

    override fun getHandle():Int{
        return textureHandle[0]
    }


    override fun bind(){
        GL32.glBindTexture(GL32.GL_TEXTURE_2D, textureHandle[0])
    }

    override fun toString():String{
        return "Textura possui Handle GL :" + getHandle().toString()
    }


    override fun liberarRecursos() {
        GL32.glDeleteTextures(textureHandle)
    }
}
