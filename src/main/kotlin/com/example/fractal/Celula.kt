package com.example.fractal


import java.util.concurrent.locks.ReentrantLock
import kotlin.math.ln


class Celula(val camada: Camada, val coordenadasPlano: CoordenadasPlano, val tamTextura: Cvetor2i) {
    var matrizIteracoesEstaCalculada = false

    //    private var textura= camada.janela.poolTextureWrapper.borrowObject()
    private var textura = camada.janela.texturaPlaceholder

    private val texturaLock = ReentrantLock()

    //TODO: Decidir se vai usar sprite como membro ou como herança, ou se sequer deve existir a classe sprite
    //var sprite: Sprite = Sprite(tamTextura.x, tamTextura.y,camada.janela.texturaPlaceholder)

    //pixels: Origem no centro da tela
    var coordenadasTela = Cvetor2d()

    var iteracoes = camada.janela.poolArrayIteracoes.borrowObject()
    //ArrayIteracoes(tamTextura.x * tamTextura.y) { 0 }

    var marcadaParaDestruicao = false
    var inativa = false
    var texturaEstaProntaParaExibir = false

    init {
        camada.TarefasProcessamento.add(
            TarefaProcessamento(this)
        )
    }

    fun bindTexture(){
        texturaLock.lock()
        textura.bind()
        texturaLock.unlock()
    }

    fun possuiTexturaValida():Boolean
    {
        return textura.estaValida()
    }

    /** usado caso o contexto opencl seja resetado*/
    private fun solicitarGeracaoDeTexturaGL(){
        camada.janela.tarefasAlocarTextura.add(
            TarefaCriarTexturaGL(textura)
        )
    }



    fun processaIteracoesECriaTextura(){
        processaIteracoes()
        trocarTextura(camada.janela.gerenciadorDeImplementacoes.bufferTexture(tamTextura.x,tamTextura.y,iteracoes))
    }

    fun processaIteracoes() {
        val delta = camada.delta
        for (j in 0 until tamTextura.y) {
            for (i in 0 until tamTextura.x) {
                val pidex = i + j * tamTextura.x
                iteracoes[pidex] = itrNormalizada(
                    coordenadasPlano.x + i * delta,
                    coordenadasPlano.y + j * delta
                ).toInt()
            }
        }
    }

    private fun trocarTextura(novaTextura: TextureWrapper){
        texturaLock.lock()
        textura = novaTextura
        textura.marcarComoInvalida()
        texturaLock.unlock()
        solicitarGeracaoDeTexturaGL()
    }

    //todo deveria estar em outra classe
    private fun itrNormalizada(Cx: Double, Cy:Double): Long {
        val limiteDivergencia = camada.janela.limiteDivergencia
        val iMax = camada.janela.maxIteracoes

        var i = 0
        var zx = Cx;
        var zy = Cy;
        var zx2 = zx * zx;
        var zy2 = zy * zy;
        //   if ((zx2+ zy2)>4.0) return 0;
        while (zx2 + zy2 < limiteDivergencia) {
            i++
            zy *= zx;
            zy += zy;
            zy += Cy;
            zx = zx2 - zy2 + Cx;
            zx2 = zx * zx;
            zy2 = zy * zy;
            if (i == iMax) return 0;
        }
        val logZn = ln(zx2 + zy2) / 2.0
        val samplingIteracoes = camada.janela.samplingIteracoes.toDouble()
        val parcial =        samplingIteracoes * ln(logZn / 0.69314718056) / 0.69314718056; //parcela que falta para chegar no proximo i
        // if (parcial >SAMPLING_ITERACOES) parcial=SAMPLING_ITERACOES.toDouble();
        return (i * samplingIteracoes - parcial).toLong();
    }


    fun liberaRecursos(){
        marcadaParaDestruicao = true
        camada.janela.apply {
            //poolTextureWrapper.returnObject(textura)

            tarefasDesalocarTextura.add(
                TarefaDesalocarTexturaGL(textura)
            )
            poolArrayIteracoes.returnObject(iteracoes)
        }
    }


}