package com.example.fractal

import com.example.fractal.windows.MGLRGBA8TextureWrapper
import java.util.*

class Paleta(val janela: Janela) {
    var paleta = IntArray(32*32){
        rgbToInt((it%32)*8,(it/32)*4,0)
  //      it -> it//-1024//250//it*128// ((Random().nextFloat()*256*256f*256f*256f).toInt())
    }

    lateinit var texturaPaleta :MGLRGBA8TextureWrapper

    init{
      //  createcolors()
        texturaPaleta = MGLRGBA8TextureWrapper(256,256,paleta)


        janela.TarefasAlocarTextura.add(TarefaCriarTexturaGL(texturaPaleta))
    }


    fun bind(){
        texturaPaleta.bind()
    }

    fun rgbToInt(r: Int, g: Int, b: Int): Int{
        var cor = 0;
        cor += b;
        cor *= 256;
        cor += g;
        cor *= 256
        cor += r
        return cor
    }
}