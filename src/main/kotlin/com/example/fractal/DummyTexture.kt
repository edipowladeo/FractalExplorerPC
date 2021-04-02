package com.example.fractal

import kotlin.math.abs
import kotlin.math.sqrt

//TODO: isto é realmente necessario?

class dummyTexture:(Float, Float)-> TipoCor {
    override fun invoke(x:Float, y:Float): TipoCor {
        var r = 1f
        var g = 1f
        var b = 1f

        if (abs(y)> 0.45) r = 0.5f+ abs(x)
        if (abs(x)> 0.45) g = 0.5f+ abs(y)

        val dist = sqrt(x*x+y*y)

        if (abs(dist-0.35)<0.04){
            r = 0f
            g=0.6f
        }

        return TipoCor(r,g,b)
    }
}