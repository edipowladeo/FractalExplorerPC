package com.example.fractal

import com.example.fractal.interfaces.DesenhistaDeCelulas
import com.example.fractal.interfaces.GerenciadorDeImplementacoes
import com.example.fractal.threads.ThreadManipularJanelas
import com.example.fractal.threads.ThreadProcessamento
import org.lwjgl.glfw.GLFW
import java.awt.event.KeyEvent
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.*
import kotlin.math.log

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

    /**VARIAVEIS DE ESTADO vars*/
    //TODO: injeção de dependencia não tá funcionando legal no android

    var cameraAtual = PosicaoCamera(cameraInicial)
    var cameraDesejada = PosicaoCamera(cameraAtual)
    var regiaoDesenhoPlano = RetanguloPlano()

    val poolArrayIteracoes = ObjectPool<TipoArrayIteracoes>(
        1000, 10000, 1L,
        fun(): TipoArrayIteracoes { return TipoArrayIteracoes(tamSprite.x * tamSprite.y) { 0 } })

    /*   val poolTextureWrapper = ObjectPool<TextureWrapper>(
        10,
        fun(): TextureWrapper {return MGlTextureWrapper(100,100,dummyTexture())})
*/

    val tarefasDesalocarTextura = GerenciadorDeTarefas<TarefaDesalocarTexturaGL>()
    val tarefasAlocarTextura = GerenciadorDeTarefas<TarefaCriarTexturaGL>()


    private val numThreadsProcessamento: Int = Runtime.getRuntime().availableProcessors() - 1;
    private val threadsProcessamento = List(numThreadsProcessamento) { ThreadProcessamento(this) }
    private val threadManipularJanelas = ThreadManipularJanelas(this)

    /** key da camada é o valor de magnificação*/
    /** quanto maior, menor é o tamanho aparente da camada e portanto maior resolução  qualidade aparente */
    /** valores menores representam baixa resolucao, e são processados e desenhados primeiro e portanto ficam atrás*/
    var camadas: SortedMap<Int, Camada> = emptyMap<Int, Camada>().toSortedMap()

    init {
        paleta = Paleta(this)
        tarefasAlocarTextura.add(TarefaCriarTexturaGL(texturaPlaceholder))
        atualizaCamadasECelulas()

        threadManipularJanelas.start()
        threadsProcessamento.forEach { it.start() }
        //TODO: Cria thread, porém se o objeto janela sai de escopo, thread fica solta
    }

    fun moverCamera(dXpixels: Float, dYpixels: Float) {
        cameraAtual.x += dXpixels * cameraAtual.delta * FatorScroll
        cameraAtual.y += dYpixels * cameraAtual.delta * FatorScroll
        cameraDesejada = PosicaoCamera(cameraAtual)
        verificarLimites()
    }

    fun moverCameraDesejada(dXpixels: Float, dYpixels: Float) {
        val fatorScroll = 0.4
        cameraDesejada.x += dXpixels * cameraAtual.delta * dimensaoJanelaSaida.x * fatorScroll
        cameraDesejada.y += dYpixels * cameraAtual.delta * dimensaoJanelaSaida.y * fatorScroll
        verificarLimites()
    }

    fun irPara(posicaoCamera: PosicaoCamera) {
        cameraDesejada = PosicaoCamera(posicaoCamera)
        verificarLimites()
    }

    fun dividirDeltaPor(fator: Float) {
        println("DELTA ")
        cameraAtual.delta /= fator.pow(fatorEscala)
        cameraDesejada = PosicaoCamera(cameraAtual)
        verificarLimites()
    }

    fun dividirDeltaDesejado(fator: Float) {
        cameraDesejada.delta /= fator.pow(fatorEscala)
        verificarLimites()

    }
    //todo limitar
    fun verificarLimites(){
        val deltaMax = 3 / dimensaoJanelaSaida.x
        val deltaMin = 10.0.pow(-14) / dimensaoJanelaSaida.x

        if (cameraDesejada.delta > deltaMax) { cameraDesejada.delta = deltaMax }
        if (cameraDesejada.delta < deltaMin) { cameraDesejada.delta = deltaMin }
        if (cameraDesejada.x > +2.0) { cameraDesejada.x = +2.0 }
        if (cameraDesejada.x < -2.0) { cameraDesejada.x = -2.0 }
        if (cameraDesejada.y > +2.0) { cameraDesejada.y = +2.0 }
        if (cameraDesejada.y < -2.0) { cameraDesejada.y = -2.0 }

    }

    //todo fator em funcao do tempo
    private fun atualizaCamera() {
        val logDeltaD = ln(cameraDesejada.delta)
        val logDelta = ln(cameraAtual.delta)

        cameraAtual.delta = exp(logDelta + (logDeltaD - logDelta) * 0.1)

        cameraAtual.x = cameraAtual.x + (cameraDesejada.x - cameraAtual.x) * 0.1
        cameraAtual.y = cameraAtual.y + (cameraDesejada.y - cameraAtual.y) * 0.1
   //         println("delta ${cameraAtual.delta * dimensaoJanelaSaida.x} ")
    }

    fun setDimensaoDaJanelaDeSaida(dimensao: CoordenadasTela) {
        dimensaoJanelaSaida = CoordenadasTela(dimensao)
        verificarLimites()
    }

    fun getDimensaoDaJanelaDeSaida(): CoordenadasTela {
        return CoordenadasTela(dimensaoJanelaSaida)
    }

    fun atualizaCamadasECelulas() {
        lock.lock()
        adicionarRemoverCamadas()
        atualizaIntervaloDesenho()
        camadas.values.forEach { camada ->
            lock.unlock()
            lock.lock()
            camada.atualizaMatrizDeCelulas()
        }
        lock.unlock()
    }


    fun desenharCelulas(desenhista: DesenhistaDeCelulas) {
        lock.lock()
        //atualizaCameraECamadas()
        atualizaCamera();
        //todo :definir um ciclo temporal consistente
        val time = relogio.getCurrentTimeMs() % 10240L
        // val angleInRad = 6.28318530f / 10000.0f * time.toFloat() * velocidadeCircularCores
        desenhista.AtualizaUniforms(time.toFloat(), escalaPaleta)
        paleta.bind()
        camadas.values.forEach { camada ->
            camada.posicionaTodasCelulasNaTela()
            //todo gambiara para nao exibir listras aumentei 2% o tamanho do retangulo
            val escala = camada.delta * tamSprite.x *1.02/ cameraAtual.delta
            camada.celulas.forEachIndexed { i, colunas ->
                colunas.forEachIndexed() { j, linhas ->
                    linhas.run {
                        if (this.possuiTexturaValida()) {
                            if (this.matrizIteracoesEstaCalculada) desenhista.desenharCelula(this, escala.toFloat())
                        }
                    }
                }
            }
        }
        lock.unlock()
    }

    fun executaTarefaDeProcessamentoComMaiorPrioridade(): Boolean {
        lock.lock()
        camadas.values.forEach { camada ->
            camada.TarefasProcessamento.retornarPrimeiraOrNull()?.let {
                lock.unlock()
                it.run()
                return true //executou tarefa
            }
        }
        lock.unlock()
        //println("idle")
        return false //camada não possui tarefas a executar
    }

    private fun atualizaIntervaloDesenho() {
        //todo transformar janela desenho em um retangulo
        val janelaDesenho = CoordenadasTela(
            dimensaoJanelaSaida.x * 0.5 * fator_debug,
            dimensaoJanelaSaida.y * 0.5 * fator_debug
        )

        regiaoDesenhoPlano.max = janelaDesenho.toCoordenadasPlano(cameraAtual)
        janelaDesenho.x = -janelaDesenho.x
        janelaDesenho.y = -janelaDesenho.y
        regiaoDesenhoPlano.min = janelaDesenho.toCoordenadasPlano(cameraAtual)

    }

    private fun adicionarRemoverCamadas() {
        val maiorvalor = log((1.0 / (cameraAtual.delta * minTamanhoAparentePixel)), 2.0).toInt()
        val menorvalor = maiorvalor - quantidadeDeCamadasAlemDaPrincipal

        val camadasDesejadas = IntRange(
            menorvalor,
            maiorvalor
        )

        /** Todas as novas camadas são alinahdas a maior camada*/
        val coordenadasNovasCamadas =
            camadas.values.firstOrNull()?.getCoordenadasPlanoCelulaCantoSupEsq()
                ?: CoordenadasPlano(-0.0, -0.0)

        //TODO:Substituir por comparação direta com o range
        camadasDesejadas.forEach {
            //TODO: usar metodo .in
            if (!camadas.containsKey(it)) {
                val coordenadas = PosicaoCamera(
                    coordenadasNovasCamadas,
                    getDeltaFromIntegerMagnification(it)
                )
                // 1.0/512.0 )
                camadas.put(it, Camada(this, coordenadas))
            }//TODO:consertar delta
        }

        /** cria uma lista de todas as camadas que devem ser apagadas*/
        val camadasApagar = emptyList<Int>().toMutableList()

        camadas.forEach { (key, camada) ->
            if (key !in camadasDesejadas) {
                camadasApagar.add(key)
                camada.liberarRecursos()
            }
        }
        camadasApagar.forEach {
            camadas.remove(it)
        }
    }

    private fun posicionaTodasCelulasNaTela() {
        camadas.values.forEach { camada -> camada.posicionaTodasCelulasNaTela() }
    }

    override fun toString(): String {
        lock.lock()
        val stringB = StringBuilder()
        stringB.append("Dimensoes da Janela: $dimensaoJanelaSaida")
        stringB.append("\nFila Criar Textura ${tarefasAlocarTextura.getQtdeTarefas()}")
        stringB.append("\nFila Desalocar Textura " + tarefasDesalocarTextura.getQtdeTarefas())
        //  textoDebug.append("\nmin:" +coord_min)
        //  textoDebug.append("\nmax:" +coord_max)
        stringB.append("\nCoord Camera Atual " + cameraAtual)
        camadas.forEach { camada ->
            stringB.append("\n\t" + camada.key + ": Fila Processos " + camada.value.TarefasProcessamento.getQtdeTarefas())
        }
        lock.unlock()
        return stringB.toString()
    }

    private fun getDeltaFromIntegerMagnification(mag: Int): TipoDelta {
        return 0.5.pow(mag.toDouble())
    }

    fun liberarRecursos() {
        // TODO("Not yet implemented")
    }

    //todo interface para retornar teclas
    fun handleKeyPress(key: Int) {
        when (key) {
            GLFW.GLFW_KEY_SPACE ->  dividirDeltaDesejado(2f)
            GLFW.GLFW_KEY_Z ->      dividirDeltaDesejado(1 / 2f)
            GLFW.GLFW_KEY_A ->      moverCameraDesejada(-1f,0f)
            GLFW.GLFW_KEY_D ->      moverCameraDesejada(+1f,0f)
            GLFW.GLFW_KEY_W ->      moverCameraDesejada(0f,-1f)
            GLFW.GLFW_KEY_S ->      moverCameraDesejada(0f,+1f)
            GLFW.GLFW_KEY_LEFT ->   moverCameraDesejada(-1f,0f)
            GLFW.GLFW_KEY_RIGHT ->  moverCameraDesejada(+1f,0f)
            GLFW.GLFW_KEY_UP ->     moverCameraDesejada(0f,-1f)
            GLFW.GLFW_KEY_DOWN ->   moverCameraDesejada(0f,+1f)
            GLFW.GLFW_KEY_H ->      irPara(cameraInicial)
        }
    }
}