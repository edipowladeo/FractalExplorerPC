package com.example.fractal

import com.example.fractal.android.ThreadProcessamento
import com.example.fractal.windows.ThreadManipularJanelas
import java.lang.Math.pow
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.log
import kotlin.math.pow

//TODO: thread Safe
/** Lock janela thread
 * locks nas filas de tarefas
 * tarefa deve conter todas as informacoes para realizar
 * tarefas sao executadas por outras trheads
 * lock dentro da célula nos buffers de entrada e resultado do processamento, textura???
 * lock nas camadas caso queira criar thread exclusiva de adicionar/remover ,
 * somente caso esteja muito lento (profiling) seria a ultima coisa a fazer
  */
class Janela(
        val gerenciadorDeImplementacoes: GerenciadorDeImplementacoes,
        private var dimensaoJanelaSaida: CoordenadasTela
) : JanelaPropriedades() {
    private val lock = ReentrantLock()

    private var paleta: Paleta

    val relogio = gerenciadorDeImplementacoes.relogio()

    val texturaPlaceholder = gerenciadorDeImplementacoes.bufferTexture(100, 100, dummyTexture())

    var regiaoDesenhoPlano = RetanguloPlano()

    /**VARIAVEIS DE ESTADO vars*/
    //TODO: injeção de dependencia não tá funcionando legal

    //var camadasDesejadas = 7..7 // passei para local no metodo adicionarRemoverCamadas
    var PosicaoCameraAtual = cameraInicial
    var PosicaoCameraDesejada = PosicaoCameraAtual


    val poolArrayIteracoes = ObjectPool<TipoArrayIteracoes>(
        10, 200, 1L,
        fun(): TipoArrayIteracoes { return TipoArrayIteracoes(tamSprite.x * tamSprite.y) { 0 } })

    /*   val poolTextureWrapper = ObjectPool<TextureWrapper>(
        10,
        fun(): TextureWrapper {return MGlTextureWrapper(100,100,dummyTexture())})
*/

    val tarefasDesalocarTextura = GerenciadorDeTarefas<TarefaDesalocarTexturaGL>()
    val tarefasAlocarTextura = GerenciadorDeTarefas<TarefaCriarTexturaGL>()

    private val threadsProcessamento = List<ThreadProcessamento>(12){ ThreadProcessamento(this) }
    private val threadManipularJanelas = ThreadManipularJanelas(this)

    /** key da camada é o valor de magnificação*/
    /** quanto maior, menor é o tamanho aparente da camada e portanto maior resolução  qualidade aparente */
    /** valores menores representam baixa resolucao, e são processados e desenhados primeiro e portanto ficam atrás*/
    var camadas: SortedMap<Int, Camada> = emptyMap<Int, Camada>().toSortedMap()

    init {
        paleta = Paleta(this)
        tarefasAlocarTextura.add(TarefaCriarTexturaGL(texturaPlaceholder))
        atualizaCameraECamadas()

        threadManipularJanelas.start()
        threadsProcessamento.forEach{it.start()}
        //TODO: Cria thread, porém se o objeto janela sai de escopo, thread fica solta
    }

    fun moverCamera(dXpixels: Float, dYpixels: Float) {
        PosicaoCameraAtual.x += dXpixels * PosicaoCameraAtual.Delta * FatorScroll
        PosicaoCameraAtual.y += dYpixels * PosicaoCameraAtual.Delta * FatorScroll
        PosicaoCameraDesejada = PosicaoCameraAtual;

    }

    fun dividirDeltaPor(fator: Float) {
        PosicaoCameraAtual.Delta /= fator.pow(fatorEscala)
        PosicaoCameraDesejada = PosicaoCameraAtual
    }

    fun setDimensaoDaJanelaDeSaida(dimensao: CoordenadasTela) {
  //      var dimensaoold = dimensaoJanelaSaida
        dimensaoJanelaSaida = dimensao


       // println("Janela Redimensioanda $dimensaoJanelaSaida")
    }

    fun getDimensaoDaJanelaDeSaida(): CoordenadasTela {
        return dimensaoJanelaSaida
    }

    fun getCoordenadasPlano(coordenadasTela: CoordenadasTela): CoordenadasPlano {
        return CoordenadasPlano(
            PosicaoCameraAtual.x + coordenadasTela.x * PosicaoCameraAtual.Delta * 2,
            PosicaoCameraAtual.y + coordenadasTela.y * PosicaoCameraAtual.Delta * 2
        )
    }

    fun atualizaCameraECamadas() {
        lock.lock()
        adicionarRemoverCamadas()
        atualizaIntervaloDesenho()
        camadas.values.forEach { camada ->
            lock.unlock()
            lock.lock()
           camada.atualizaMatrizDeCelulas()
        }
      //  println("Ncamadas: ${this.camadas.count()}")
        lock.unlock()
    }


    fun desenharCelulas(desenhista: DesenhistaDeCelulas){
        lock.lock()
     //   atualizaCameraECamadas()
        val time = relogio.getCurrentTimeMs() % 10240L
       // val angleInRad = 6.28318530f / 10000.0f * time.toFloat() * velocidadeCircularCores
        desenhista.AtualizaUniforms(time.toFloat(),escalaPaleta)
        paleta.bind()
        camadas.values.forEach { camada ->
            camada.posicionaTodasCelulasNaTela()
            val escala =camada.Delta * tamSprite.x / PosicaoCameraAtual.Delta
            camada.Celulas.forEachIndexed { i, colunas ->
                colunas.forEachIndexed() { j, linhas ->
                    linhas.run {
                        if (this.possuiTexturaValida()) {
                          if (this.matrizIteracoesEstaCalculada)  desenhista.desenharCelula(this,escala.toFloat())
                        }
                    }
                }
            }
        }
    }

    fun executaTarefaDeProcessamentoComMaiorPrioridade():Boolean{
        lock.lock()
        camadas.values.forEach { camada ->
            camada.TarefasProcessamento.retornarPrimeiraOrNull()?.let {
                lock.unlock()
                it.run()
                return true //executou tarefa
            }
        }
        lock.unlock();
        return false //camada não possui tarefas a executar
    }

    private fun atualizaIntervaloDesenho(){
        var janela_desenho = CoordenadasTela(
        dimensaoJanelaSaida.x*0.5*fator_debug,
        dimensaoJanelaSaida.y*0.5*fator_debug)

        regiaoDesenhoPlano.max = getCoordenadasPlano(janela_desenho)
        janela_desenho.x=- janela_desenho.x
        janela_desenho.y=- janela_desenho.y
        regiaoDesenhoPlano.min = getCoordenadasPlano(janela_desenho)
    //    println("coord max $coord_max coord min $coord_min")
    }

      private fun adicionarRemoverCamadas() {
        val maiorvalor = log((1.0/(PosicaoCameraAtual.Delta*minTamanhoAparentePixel)),2.0).toInt()
        val menorvalor = maiorvalor - quantidadeDeCamadasAlemDaPrincipal

        val camadasDesejadas = IntRange(
            menorvalor,
            maiorvalor
        )

        /** Todas as novas camadas são alinahdas a maior camada*/
        var CoordenadasNovasCamadas = CoordenadasPlano(-0.0,-0.0)
        camadas.values.firstOrNull()?.let {
            CoordenadasNovasCamadas = it.getCoordenadasPlanoCelulaCantoSupEsq()
        }

        //TODO:Substituir por comparação direta com o range
        camadasDesejadas.forEach {
            //TODO: usar metodo .in
            if (!camadas.containsKey(it)){
                val coordenadas = PosicaoCamera(
                    CoordenadasNovasCamadas,
                   getDeltaFromIntegerMagnification(it))
                   // 1.0/512.0 )
                camadas.put(it, Camada(this,coordenadas))
            }//TODO:consertar delta
        }

        /** cria uma lista de todas as camadas que devem ser apagadas*/
        var camadasApagar = emptyList<Int>().toMutableList()

        camadas.forEach{ key, camada ->
            if (key !in camadasDesejadas) {
                camadasApagar.add(key)
                camada.liberarRecursos()
            }
        }
            camadasApagar.forEach {
                camadas.remove(it)
        }

    }

    private fun posicionaTodasCelulasNaTela(){
        camadas.forEach { chave, camada ->camada.posicionaTodasCelulasNaTela() }
    }

    override fun toString(): String {
        lock.lock()
        val stringB = StringBuilder()
        stringB.append("Dimensoes da Janela: " + dimensaoJanelaSaida)
        stringB.append("\nFila Criar Textura " + tarefasAlocarTextura.getQtdeTarefas() )
        stringB.append("\nFila Desalocar Textura " + tarefasDesalocarTextura.getQtdeTarefas() )
        //  textoDebug.append("\nmin:" +coord_min)
        //  textoDebug.append("\nmax:" +coord_max)
        stringB.append("\nCoord Camera Atual " + PosicaoCameraAtual)
        camadas.forEach{camada ->
            stringB.append("\n\t" +camada.key+": Fila Processos "+camada.value.TarefasProcessamento.getQtdeTarefas())}
        lock.unlock()
        return stringB.toString()
    }

    private fun getDeltaFromIntegerMagnification(mag:Int): TipoDelta {
       return pow(0.5,mag.toDouble())
    }

    fun liberarRecursos() {
       // TODO("Not yet implemented")
    }
}