package com.example.fractal

import com.example.fractal.windows.MGLRGBA8TextureWrapper
import kotlin.math.sin

class Paleta(val janela: Janela) {
    val tamPaleta = 1024
    var paleta = IntArray(tamPaleta){
       cor(it)
    //    rgbToInt((it%32)*8,(it/32)*4,0)
  //      it -> it//-1024//250//it*128// ((Random().nextFloat()*256*256f*256f*256f).toInt())
    }

    lateinit var texturaPaleta :MGLRGBA8TextureWrapper

    init{
      //  createcolors()
        texturaPaleta = MGLRGBA8TextureWrapper(256,256,paleta)


        janela.tarefasAlocarTextura.add(TarefaCriarTexturaGL(texturaPaleta))
    }


    fun bind(){
        texturaPaleta.bind()
    }

    fun cor(iteracoes:Int):Int{

    //    if (iteracoes == 0) return 0
        var iteracoesFloat = (iteracoes).toDouble()
        iteracoesFloat *= (2*3.1416/tamPaleta)

        val r = 127+127*sin(iteracoesFloat);
        val g = 127+127*sin(iteracoesFloat+2.0);
        val b = 127+127*sin(iteracoesFloat-2.0);

        return rgbToInt(r.toInt(),g.toInt(),b.toInt())
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