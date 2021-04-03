package com.example.fractal


/**
Os vetores coordCelulasX(CX) e Os coordCelulasY(CY) armazendam as coordenadas no plano e na tela das células (c)
é uma otimização para evitar o cálculo repetido destas coordenadas, visto que são iguais para colunas e linhas
As coordenadas no plano são copiadas paras as células por valor , visto que são constantes.


CX0 CX1 CX2 CX3

CY0    c00 c10 c20 c30
CY1    c01 c11 c21 c31
CY2    c02 c12 c22 c32
CX3    c03 c13 c23 c33

A matriz de células é implementada usando uma Lista (colunas) de lista (linhas) de células

Esta classe possui métodos para inserir e remover linhas e colunas,
estes métodos manipulam e os vetores coordCelulas e a matriz de células ao mesmo tempo
 */


class Camada(val janela: Janela, coordenadasIniciais: PosicaoCamera)
{

    //TODO: isto é realmente necessario? classe coordcelula1d, pode ser implementados dois vetores
    private class CoordCelula1D{
        var coordenadaPlano : CoordenadaPlano = 0.0
        var coordenadaTela : CoordenadaTela = 0.0
        constructor(){}
        constructor(CoordenadaPlano : CoordenadaPlano, CoordenadaTela : CoordenadaTela){
            coordenadaPlano = CoordenadaPlano
            coordenadaTela = CoordenadaTela
        }
        constructor(CoordenadaPlano : CoordenadaPlano){
            coordenadaPlano = CoordenadaPlano
        }
    }
    enum class Posicao {
        PRIMEIRA, ULTIMA
    }
    /**lista de tarefas*/
    val TarefasProcessamento = GerenciadorDeTarefas<TarefaProcessamento>()

    /**Vetor de Coordenadas das células*/
    private var coordCelulasX = MutableList<CoordCelula1D>(1){ CoordCelula1D(coordenadasIniciais.x) }
    private var coordCelulasY = MutableList<CoordCelula1D>(1){ CoordCelula1D(coordenadasIniciais.y) }

    internal val delta: TipoDelta = coordenadasIniciais.Delta

    /**Lista (colunas) de lista (linhas) de células*/
    internal var celulas = MutableList<MutableList<Celula>>(1){
        MutableList(1){
            Celula(this,coordenadasIniciais,janela.tamSprite)
        }
    }

    init{
        posicionaTodasCelulasNaTela()
    }

    fun atualizaMatrizDeCelulas() {
        // presume-se que os valores minimos e maximos da janela estão atualizados
        //TODO:usar tipos e operator overload para melhorar a redabilidade
        val tamSpriteNoPlano = CoordenadasPlano(
            janela.tamSprite.x.toDouble() * delta,
            janela.tamSprite.y.toDouble() * delta
        )

        val retanguloAlocar = RetanguloPlano(janela.regiaoDesenhoPlano).apply{
            max.x -= tamSpriteNoPlano.x
            max.y -= tamSpriteNoPlano.y
        }

        val retanguloDesalocar = RetanguloPlano(janela.regiaoDesenhoPlano).apply{
            min.x -= tamSpriteNoPlano.x
            min.y -= tamSpriteNoPlano.y
        }

        //Aloca todas linhas/colunas por chamada
        while (coordCelulasX.first().coordenadaPlano > retanguloAlocar.min.x) adicionarColuna(Posicao.ULTIMA)
        while (coordCelulasX.last() .coordenadaPlano < retanguloAlocar.max.x) adicionarColuna(Posicao.PRIMEIRA)
        while (coordCelulasY.first().coordenadaPlano > retanguloAlocar.min.y) adicionarLinha (Posicao.ULTIMA)
        while (coordCelulasY.last() .coordenadaPlano < retanguloAlocar.max.y) adicionarLinha (Posicao.PRIMEIRA)

        //Desaloca apenas uma linha/coluna por chamada
        if (coordCelulasX.size > 1){
            if (coordCelulasX.first().coordenadaPlano < retanguloDesalocar.min.x) removerColuna(Posicao.ULTIMA)
            if (coordCelulasX.last().coordenadaPlano > retanguloDesalocar.max.x)  removerColuna(Posicao.PRIMEIRA)
        }
        if (coordCelulasY.size > 1){
            if (coordCelulasY.first().coordenadaPlano < retanguloDesalocar.min.y) removerLinha(Posicao.ULTIMA)
            if (coordCelulasY.last().coordenadaPlano > retanguloDesalocar.max.y)  removerLinha(Posicao.PRIMEIRA)
        }
        println("qdte celulas x: ${coordCelulasX.size} y: ${coordCelulasY.size}")
    }

    fun getCoordenadasPlanoCelulaCantoSupEsq(): CoordenadasPlano {
        return CoordenadasPlano(
            coordCelulasX.first().coordenadaPlano,
            coordCelulasY.first().coordenadaPlano
        )
    }

    fun getPosicaoCamera(): PosicaoCamera {
        return PosicaoCamera(getCoordenadasPlanoCelulaCantoSupEsq(),delta)
    }

    private fun posicionaCamadaNaTela() {
        lateinit var coordenadasTela : CoordenadasTela

        val coordenadasPlano = getCoordenadasPlanoCelulaCantoSupEsq()
        janela.cameraAtual.also {
            coordenadasTela = CoordenadasTela(
                (coordenadasPlano.x - it.x) / it.Delta,
                (coordenadasPlano.y - it.y) / it.Delta)
        }

        val razao = delta / janela.cameraAtual.Delta
        coordCelulasX.forEachIndexed{i,it->
            it.coordenadaTela=coordenadasTela.x + i*razao * janela.tamSprite.x}
        coordCelulasY.forEachIndexed{j,it->
            it.coordenadaTela=coordenadasTela.y + j*razao * janela.tamSprite.y}
    }

    private fun getCoordenadaTelaDaCelula(i:Int, j:Int): CoordenadasTela {
        return  CoordenadasTela(
            coordCelulasX[i].coordenadaTela,
            coordCelulasY[j].coordenadaTela)
    }

    //TODO: verificar se é melhor usar este método na camada toda ou chamar o método getCoordenadaTelaDaCelula na hora de renderizar
    fun posicionaTodasCelulasNaTela(){
        posicionaCamadaNaTela()
        celulas.forEachIndexed{ i, colunas ->
            colunas.forEachIndexed{j,linhas ->
                linhas.coordenadasTela = getCoordenadaTelaDaCelula(i,j)
            }
        }
    }

    fun logCells(){
        celulas.forEachIndexed{ i, it ->
            ////Log.i("Debug","x: "+i.toString())
            it.forEachIndexed{j,it ->
                ////Log.i("Debug","y: "+ j.toString())
                ////Log.i("Debug","pos "+it.coordenadasTela.toString())
                ////Log.i("Debug","posP "+it.coordenadasPlano.toString())
            }
        }
    }

    //TODO: Adicionar Coluna e linha não seta coordenadas tela dos vetores, eles ficam em zero durante um frame
    //TODO: refatorar nomes
    private fun adicionarColuna(posicao: Posicao){
        println("adicionou")
        val tamX = coordCelulasX.size

        val indiceX = if (posicao == Posicao.PRIMEIRA) (tamX) else (0)

        val offset = if (posicao == Posicao.PRIMEIRA) (tamX*delta*janela.tamSprite.x) else (-delta*janela.tamSprite.x)

        /** incrementa o vetor de coordenadas */
        val novaCoordenadaX = CoordCelula1D(coordCelulasX.first().coordenadaPlano + offset)
        coordCelulasX.add(indiceX,novaCoordenadaX)

        /**cria uma nova coluna */
        val novaColuna = emptyList<Celula>().toMutableList()

        /**Popula a coluna criada com uma linha para cada coordenada Y*/
        coordCelulasY.forEachIndexed { indiceY, it ->
            val novaCelula = Celula(this, CoordenadasPlano(
                coordCelulasX[indiceX].coordenadaPlano,
                coordCelulasY[indiceY].coordenadaPlano),
                janela.tamSprite)
            novaColuna.add(novaCelula)
        }

        /**Insere nova Coluna na matriz de células */
        celulas.add(indiceX,novaColuna)
    }


    private fun adicionarLinha(posicao: Posicao){
        val tamY = coordCelulasY.size

        val indiceY = if (posicao== Posicao.PRIMEIRA) (tamY) else (0)

        val novacoordenadaOffset = if (posicao== Posicao.PRIMEIRA) (tamY*delta*janela.tamSprite.y) else (-delta*janela.tamSprite.y)

        /** incrementa o vetor de coordenadas */
        val novaCoordenadaY = CoordCelula1D(coordCelulasY.first().coordenadaPlano + novacoordenadaOffset)
        coordCelulasY.add(indiceY, novaCoordenadaY)

        /** para cada coluna ...*/
        coordCelulasX.forEachIndexed{indiceX, it ->
            /** ... cria uma nova célula*/
            val novaCelula = Celula(this,
                CoordenadasPlano(
                    coordCelulasX[indiceX].coordenadaPlano,
                    coordCelulasY[indiceY].coordenadaPlano
                ),
                janela.tamSprite)
            celulas[indiceX].add(indiceY,novaCelula)
        }
    }


    private fun removerColuna(posicao: Posicao){
        println("removeu")
        val tamX = coordCelulasX.size

        val indiceX = if (posicao== Posicao.PRIMEIRA)  (tamX-1) else (0)

        /** remove Coordenadas do vetor*/
        coordCelulasX.removeAt(indiceX)

        /** liberar recursos das células*/
        celulas[indiceX].forEach { celula ->
            celula.liberaRecursos()
        }

        /** remove coluna*/
        celulas.removeAt(indiceX)
    }

    private fun removerLinha(posicao: Posicao) {
        val tamY = coordCelulasY.size

        val indiceY = if (posicao == Posicao.PRIMEIRA) (tamY - 1) else (0)

        /** remove Coordenadas do vetor*/
        coordCelulasY.removeAt(indiceY)

        /** liberar recursos das células*/
        celulas.forEach { linha ->
            linha[indiceY].liberaRecursos()
        }

        /** remove linha*/
        celulas.forEach { linha ->
            linha.removeAt(indiceY)
        }
    }

    fun liberarRecursos(){
        celulas.forEach { linha ->
            linha.forEach{celula->
                celula.liberaRecursos()
            }
        }
    }

    override fun toString(): String {
        return super.toString()
    }


}