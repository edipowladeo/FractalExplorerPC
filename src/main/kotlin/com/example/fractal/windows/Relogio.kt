package com.example.fractal.windows

import com.example.fractal.Relogio

class Relogio : Relogio {
    override fun getCurrentTimeMs(): Long {
     return System.currentTimeMillis()
    }
}