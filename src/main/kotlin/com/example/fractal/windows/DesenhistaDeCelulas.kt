package com.example.fractal.windows

import com.example.fractal.Celula
import org.lwjgl.opengl.GL32 as OGL
import java.nio.ByteBuffer.allocateDirect
import java.nio.ByteOrder
import java.nio.FloatBuffer
import com.example.fractal.DesenhistaDeCelulas


/** Invoca o GLELS20 e desenha um retângulo na tela, correspondente a uma célula*/
open class DesenhistaDeCelulas : DesenhistaDeCelulas {
    private val mVertices: FloatBuffer
    private val mBytesPerFloat = 4
    private val mStrideBytes = 2 * mBytesPerFloat
    private val mPositionOffset = 0
    private val mValuesPerVertice = 2

    internal var handlePosicaoVertices = 0
    internal var handlePosicaoCelulas = 0
    internal var handleTempo = 0
    internal var handleEscalaDaPaleta = 0

    init {
        val verticesData = floatArrayOf(
                // X, Y, Z,
                0.0f, 0.0f,
                0.0f, -1.0f,
                1.0f, 0.0f,
                1.0f, -1.0f
        )
        mVertices = allocateDirect(verticesData.size * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertices.put(verticesData)!!.position(0)
    }

    internal fun drawRectangle() {
        // Pass in the position information
        mVertices.position(mPositionOffset)
        OGL.glVertexAttribPointer(
                handlePosicaoVertices, mValuesPerVertice, OGL.GL_FLOAT, false,
                mStrideBytes, mVertices
        )
        OGL.glEnableVertexAttribArray(handlePosicaoVertices)
        OGL.glDrawArrays(OGL.GL_TRIANGLE_STRIP, 0, 4)
    }

    /*
    override fun desenharCelula(celula:Celula, escala:Float){
        celula.BindTexture()
        OGL.glUniform3f(
                handlePosicaoCelulas,
                celula.coordenadasTela.x.toFloat(),
                -celula.coordenadasTela.y.toFloat(),
                escala)
        drawRectangle()
    }*/

    override fun desenharCelula(celula: Celula, escala:Float){
        celula.BindTexture()
        OGL.glUniform3f(
                handlePosicaoCelulas,
                celula.coordenadasTela.x.toFloat(),
                -celula.coordenadasTela.y.toFloat(),
                escala)
        drawRectangle()
    }


    override fun AtualizaUniforms(tempo:Float,escalaPaleta:Float){
        OGL.glUniform1f(handleEscalaDaPaleta,escalaPaleta)
        OGL.glUniform1f(handleTempo,tempo)
    }

}