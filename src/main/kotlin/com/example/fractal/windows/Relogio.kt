package com.example.fractal.windows

import com.example.fractal.interfaces.Relogio

class Relogio : Relogio {
    override fun getCurrentTimeMs(): Long {
     return System.currentTimeMillis()
    }
}