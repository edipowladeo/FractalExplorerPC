package com.example.fractal.interfaces

import com.example.fractal.Celula

interface DesenhistaDeCelulas {
    fun desenharCelula(celula: Celula, escala:Float)
    fun AtualizaUniforms(tempo:Float,escalaPaleta:Float)
}