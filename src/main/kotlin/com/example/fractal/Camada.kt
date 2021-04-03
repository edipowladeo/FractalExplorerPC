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
    enum class Direcao {
        PRIMEIRA, ULTIMA
    }
    /**lista de tarefas*/
    val TarefasProcessamento = GerenciadorDeTarefas<TarefaProcessamento>()

    /**Vetor de Coordenadas das células*/
    private var coordCelulasX = MutableList<CoordCelula1D>(1){ CoordCelula1D(coordenadasIniciais.x) }
    private var coordCelulasY = MutableList<CoordCelula1D>(1){ CoordCelula1D(coordenadasIniciais.y) }

    internal val Delta: TipoDelta = coordenadasIniciais.Delta

    /**Lista (colunas) de lista (linhas) de células*/
    internal var Celulas = MutableList<MutableList<Celula>>(1){MutableList(1){
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
            janela.tamSprite.x.toDouble() * Delta,
            janela.tamSprite.y.toDouble() * Delta
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
        while (coordCelulasX.first().coordenadaPlano > retanguloAlocar.min.x) adicionarColuna(Direcao.ULTIMA)
        while (coordCelulasX.last() .coordenadaPlano < retanguloAlocar.max.x) adicionarColuna(Direcao.PRIMEIRA)
        while (coordCelulasY.first().coordenadaPlano > retanguloAlocar.min.y) adicionarLinha (Direcao.ULTIMA)
        while (coordCelulasY.last() .coordenadaPlano < retanguloAlocar.max.y) adicionarLinha (Direcao.PRIMEIRA)

        //Desaloca apenas uma linha/coluna por chamada
        if (coordCelulasX.size > 1){
            if (coordCelulasX.first().coordenadaPlano < retanguloDesalocar.min.x) removerColuna(Direcao.ULTIMA)
            if (coordCelulasX.last().coordenadaPlano > retanguloDesalocar.max.x)  removerColuna(Direcao.PRIMEIRA)
        }
        if (coordCelulasY.size > 1){
            if (coordCelulasY.first().coordenadaPlano < retanguloDesalocar.min.y) removerLinha(Direcao.ULTIMA)
            if (coordCelulasY.last().coordenadaPlano > retanguloDesalocar.max.y)  removerLinha(Direcao.PRIMEIRA)
        }

    }

    fun getCoordenadasPlanoCelulaCantoSupEsq(): CoordenadasPlano {
        return CoordenadasPlano(
            coordCelulasX.first().coordenadaPlano,
            coordCelulasY.first().coordenadaPlano
        )
    }

    fun getCoordenadasPlanoEDelta(): PosicaoCamera {
        return PosicaoCamera(getCoordenadasPlanoCelulaCantoSupEsq(),Delta)
    }

    fun PosicionaCamadaNaTela() {
        lateinit var coordenadasTela : CoordenadasTela

        val coordenadasPlano = getCoordenadasPlanoCelulaCantoSupEsq()
        janela.cameraAtual.also {
            coordenadasTela = CoordenadasTela(
                (coordenadasPlano.x - it.x) / it.Delta,
                (coordenadasPlano.y - it.y) / it.Delta)
        }

        val razao = Delta / janela.cameraAtual.Delta
        coordCelulasX.forEachIndexed{i,it->
            it.coordenadaTela=coordenadasTela.x + i*razao * janela.tamSprite.x}
        coordCelulasY.forEachIndexed{j,it->
            it.coordenadaTela=coordenadasTela.y + j*razao * janela.tamSprite.y}
    }

    fun getCoordenadaTelaDaCelula(i:Int,j:Int): CoordenadasTela {
        return  CoordenadasTela(
            coordCelulasX[i].coordenadaTela,
            coordCelulasY[j].coordenadaTela)
    }

    //TODO: verificar se é melhor usar este método na camada toda ou chamar o método getCoordenadaTelaDaCelula na hora de renderizar
    fun posicionaTodasCelulasNaTela(){
        PosicionaCamadaNaTela()
        Celulas.forEachIndexed{i,colunas ->
            colunas.forEachIndexed{j,linhas ->
                linhas.coordenadasTela = getCoordenadaTelaDaCelula(i,j)
            }
        }
    }

    fun logCells(){
        Celulas.forEachIndexed{i,it ->
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
    fun adicionarColuna(direcao: Direcao){
        println("adicionou")
        val tamY = coordCelulasY.size
        val tamX = coordCelulasX.size

        val indiceX = if (direcao== Direcao.PRIMEIRA)  tamX else 0

        val novacoordenadaOffset = if (direcao== Direcao.PRIMEIRA) (tamX*Delta*janela.tamSprite.x) else (-Delta*janela.tamSprite.x)

        /** incrementa o vetor de coordenadas */
        val novaCoordenadaX = CoordCelula1D(coordCelulasX.first().coordenadaPlano + novacoordenadaOffset)
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
        Celulas.add(indiceX,novaColuna)
    }


    fun adicionarLinha(direcao: Direcao){
        val tamY = coordCelulasY.size
        val tamX = coordCelulasX.size

        val indiceY = if (direcao== Direcao.PRIMEIRA)  tamY else 0

        val novacoordenadaOffset = if (direcao== Direcao.PRIMEIRA) (tamY*Delta*janela.tamSprite.y) else (-Delta*janela.tamSprite.y)

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
            Celulas[indiceX].add(indiceY,novaCelula)
        }
    }


    fun removerColuna(direcao: Direcao){
        println("removeu")
        val tamY = coordCelulasY.size
        val tamX = coordCelulasX.size

        val indiceX = if (direcao== Direcao.PRIMEIRA)  tamX-1 else 0

        /** remove Coordenadas do vetor*/
        coordCelulasX.removeAt(indiceX)

        /** liberar recursos das células*/
        Celulas[indiceX].forEach {celula ->
            celula.liberaRecursos()
        }

        /** remove coluna*/
        Celulas.removeAt(indiceX)
    }

    fun removerLinha(direcao: Direcao) {
        val tamY = coordCelulasY.size
        val tamX = coordCelulasX.size

        val indiceY = if (direcao == Direcao.PRIMEIRA) tamY - 1 else 0

        /** remove Coordenadas do vetor*/
        coordCelulasY.removeAt(indiceY)

        /** liberar recursos das células*/
        Celulas.forEach { linha ->
            linha[indiceY].liberaRecursos()
        }

        /** remove linha*/
        Celulas.forEach { linha ->
            linha.removeAt(indiceY)
        }
    }

    fun liberarRecursos(){
        Celulas.forEach { linha ->
            linha.forEach{celula->
                celula.liberaRecursos()
            }
        }
    }

    override fun toString(): String {
        return super.toString()
    }


}