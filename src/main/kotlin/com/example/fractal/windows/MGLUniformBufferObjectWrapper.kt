package com.example.fractal.windows

import com.example.fractal.ArrayIteracoes
import org.lwjgl.opengl.GL32

import com.example.fractal.TextureWrapper
import com.example.fractal.TipoCor
import com.example.fractal.dummyTexture
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**x e y da funcao drawer deve ir de -0.5 até 0.5*/
class MGLUniformBufferObjectWrapper(val largura:Int, val altura:Int):TextureWrapper {

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

  //  private var textureHandle = IntArray(1){0}
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
        setDoubleArray( Iteracoes)
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

    fun setDoubleArray( Iteracoes: ArrayIteracoes)
    {
        iteracoes = Iteracoes
    }

    //TODO: muita coisa aqui nem sei pra que serve
    fun createTextureFromBuffer(dataBuffer:ByteBuffer):Int {

  //      GL32.glGenTextures(textureHandle);
        GL32.glGenBuffers(bufferHandle)

     /*   if (textureHandle[0] != 0) {
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
        }else{ throw RuntimeException("Error loading texture.") }*/

        if (bufferHandle[0] != 0) {
            GL32.glBindBuffer(
                    GL32.GL_TEXTURE_BUFFER,
                    bufferHandle[0]
            );

            GL32.glBufferData(
                    GL32.GL_TEXTURE_BUFFER,
                //    largura*altura*entriesPerPixel*bytesPerEntry,
                    dataBuffer,
                    GL32.GL_STATIC_DRAW
            );
        }else{      throw RuntimeException("Error loading Buffer Opengl.")}
        return bufferHandle[0]
    }

    override fun createTextureFromBuffer(){
        dataArray?.let{
            var dataBuffer = ByteBuffer.allocateDirect(it.size * bytesPerEntry*entriesPerPixel)
                    .order(ByteOrder.nativeOrder())
      //      dataBuffer.position(0)
            dataBuffer.put(it).position(0)
            createTextureFromBuffer(dataBuffer)}
        dataArray = null
    }

    override fun populateBufferFromDoubleArray(){
        iteracoes?.let{
            populateBufferFromDoubleArray(it)
        }
    }


    @ExperimentalUnsignedTypes
    fun populateBufferFromDoubleArray(iteracoes: ArrayIteracoes){
        dataArray= ByteArray(largura*altura*entriesPerPixel*bytesPerEntry){0}.also{
            iteracoes.forEachIndexed{indice,valor ->
                val indiceNaTexutraByte = indice * entriesPerPixel
                var valorint = valor.toULong()
                //    valorint +=250u
                it[indiceNaTexutraByte] = valorint.toByte()
                valorint /= 256u
                it[indiceNaTexutraByte+1] = valorint.toByte()
                valorint /= 256u
                it[indiceNaTexutraByte+2] = valorint.toByte()
                valorint /= 256u
                it[indiceNaTexutraByte+3] = valorint.toByte()
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

    override fun getTextureHandle():Int{
        return bufferHandle[0]
    }


    override fun bind(){
      //  GL32.glBindTexture(GL32.GL_TEXTURE_2D, textureHandle[0])
      //  GL32.glBindBuffer(GL32.GL_TEXTURE_BUFFER, bufferHandle[0]);
        GL32.glTexBuffer(GL32.GL_TEXTURE_BUFFER,GL32.GL_R32UI,bufferHandle[0])

    }

    override fun toString():String{
        return "Textura possui Handle GL :" + getTextureHandle().toString()
    }


    override fun liberarRecursos() {
     //   GL32.glDeleteTextures(textureHandle)
        GL32.glDeleteBuffers(bufferHandle)
    }
}
